package de.cinovo.cloudconductor.server.repo.indexer;

import de.cinovo.cloudconductor.api.enums.DependencyType;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.server.repo.RepoEntry;
import de.cinovo.cloudconductor.server.repo.provider.IRepoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Copyright 2014 Hoegernet<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public class RPMIndexer implements IRepoIndexer {
	private static final String REPO_INDEX = "repodata/repomd.xml";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Set<PackageVersion> getRepoIndex(IRepoProvider provider) {
		RepoEntry entry = provider.getEntry(RPMIndexer.REPO_INDEX);
		if(entry != null) {
			Document repoXML = this.xmlDOM(provider.getEntryStream(RPMIndexer.REPO_INDEX));
			XPath xpath = XPathFactory.newInstance().newXPath();
			try {
				String primaryHREF = xpath.evaluate("/repomd/data[@type='primary']/location/@href", repoXML);
				GZIPInputStream gzipInputStream = new GZIPInputStream(provider.getEntryStream(primaryHREF));
				RPMPrimaryParser handler = new RPMPrimaryParser(provider.getRepoName());
				this.xmlSAX(gzipInputStream, handler);
				return handler.versions;
			} catch(XPathExpressionException e) {
				throw new RuntimeException("Failed to parse repomd.xml", e);
			} catch(IOException e) {
				throw new RuntimeException("Failed to read repodata", e);
			}
		}
		return null;
	}

	@Override
	public RepoEntry getRepoEntry(IRepoProvider provider) {
		return provider.getEntry(RPMIndexer.REPO_INDEX);
	}

	private Document xmlDOM(InputStream xmlStream) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlStream);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void xmlSAX(InputStream xmlStream, DefaultHandler handler) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.newSAXParser().parse(xmlStream, handler);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private enum RPMPrimaryState {
		Repo, Package, Requires, Provides, Conflicts;

		public static EnumSet<RPMPrimaryState> depStates = EnumSet.of(RPMPrimaryState.Requires, RPMPrimaryState.Provides, RPMPrimaryState.Conflicts);
	}

	private static class RPMPrimaryParser extends DefaultHandler {

		private Set<PackageVersion> versions = new HashSet<>();

		private String name;
		private String version;
		private Set<Dependency> dependencies;

		private String tmpValue;

		private RPMPrimaryState state = RPMPrimaryState.Repo;

		private String repoName;


		RPMPrimaryParser(String repoName) {
			this.repoName = repoName;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if((this.state == RPMPrimaryState.Repo) && qName.equals("package") && attributes.getValue("type").equals("rpm")) {
				this.state = RPMPrimaryState.Package;
				this.name = null;
				this.version = null;
				this.dependencies = new HashSet<>();
			} else if((this.state == RPMPrimaryState.Package) && qName.equals("version")) {
				this.version = String.format("%s-%s", attributes.getValue("ver"), attributes.getValue("rel"));
			} else if((this.state == RPMPrimaryState.Package) && qName.equals("rpm:requires")) {
				this.state = RPMPrimaryState.Requires;
			} else if((this.state == RPMPrimaryState.Package) && qName.equals("rpm:provides")) {
				this.state = RPMPrimaryState.Provides;
			} else if((this.state == RPMPrimaryState.Package) && qName.equals("rpm:conflicts")) {
				this.state = RPMPrimaryState.Conflicts;
			} else if(RPMPrimaryState.depStates.contains(this.state) && qName.equals("rpm:entry")) {
				// example: <rpm:entry name="jdk" flags="GE" epoch="0" ver="1.7"/>
				String depVersion = attributes.getValue("ver");
				if(depVersion == null) {
					depVersion = "";
				}
				Dependency dep = new Dependency();
				dep.setName(attributes.getValue("name"));
				dep.setVersion(depVersion);
				dep.setOperator(this.parseOperator(attributes.getValue("flags")));
				dep.setType(this.convertDepType(this.state));
				this.dependencies.add(dep);
			}
		}

		private String parseOperator(String flag) {
			if(flag == null) {
				return "";
			}
			switch(flag) {
				case "GE":
					return ">=";
				case "EQ":
					return "=";
				case "LE":
					return "<=";
				default:
					return "";
			}
		}

		private DependencyType convertDepType(RPMPrimaryState depState) {
			switch(depState) {
				case Conflicts:
					return DependencyType.CONFLICTS;
				case Provides:
					return DependencyType.PROVIDES;
				case Requires:
					return DependencyType.REQUIRES;
				default:
					return null;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if((this.state == RPMPrimaryState.Package) && qName.equals("name")) {
				this.name = this.tmpValue;
			} else if((this.state == RPMPrimaryState.Package) && qName.equals("package")) {

				PackageVersion pv = new PackageVersion();
				pv.setName(this.name);
				pv.setVersion(this.version);
				pv.setDependencies(this.dependencies);
				pv.setRepos(new HashSet<String>());
				pv.getRepos().add(this.repoName);
				this.versions.add(pv);
				this.state = RPMPrimaryState.Repo;
			} else if((this.state == RPMPrimaryState.Requires) && qName.equals("rpm:requires")) {
				this.state = RPMPrimaryState.Package;
			} else if((this.state == RPMPrimaryState.Provides) && qName.equals("rpm:provides")) {
				this.state = RPMPrimaryState.Package;
			} else if((this.state == RPMPrimaryState.Conflicts) && qName.equals("rpm:conflicts")) {
				this.state = RPMPrimaryState.Package;
			}
		}

		@Override
		public void endDocument() throws SAXException {
			if(this.state != RPMPrimaryState.Repo) {
				throw new SAXException("Invalid end state: " + this.state);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			this.tmpValue = new String(ch, start, length);
		}

	}
}
