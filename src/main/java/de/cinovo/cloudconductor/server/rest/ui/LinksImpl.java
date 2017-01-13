package de.cinovo.cloudconductor.server.rest.ui;

import com.google.common.collect.Lists;
import de.cinovo.cloudconductor.api.interfaces.ILinks;
import de.cinovo.cloudconductor.api.model.AdditionalLink;
import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.model.EAdditionalLinks;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 		
 */
@JaxRsComponent
public class LinksImpl implements ILinks {
	
	@Autowired
	private IAdditionalLinksDAO additionalLinksDAO;
	
	
	@Override
	@Transactional
	public List<AdditionalLink> getLinks() {
		List<AdditionalLink> links = Lists.newArrayList();
		for (EAdditionalLinks link : this.additionalLinksDAO.findList()) {
			links.add(link.toApi());
		}
		return links;
	}
	
	@Override
	@Transactional
	public Response createLink(AdditionalLink link) {
		RESTAssert.assertNotNull(link);
		RESTAssert.assertNotNull(link.getLabel());
		RESTAssert.assertNotNull(link.getUrl());
		
		EAdditionalLinks model = new EAdditionalLinks();
		model.setLabel(link.getLabel());
		model.setUrl(link.getUrl());
		EAdditionalLinks saved = this.additionalLinksDAO.save(model);
		return Response.created(URI.create("/api/links/" + saved.getId())).entity(saved.toApi()).build();
	}
	
	@Override
	@Transactional
	public AdditionalLink getLink(long id) {
		EAdditionalLinks model = this.additionalLinksDAO.findById(id);
		RESTAssert.assertNotNull(model);
		return model.toApi();
	}
	
	@Override
	@Transactional
	public AdditionalLink updateLink(long id, AdditionalLink link) {
		RESTAssert.assertNotNull(link);
		RESTAssert.assertNotNull(link.getLabel());
		RESTAssert.assertNotNull(link.getUrl());
		
		EAdditionalLinks model = this.additionalLinksDAO.findById(id);
		RESTAssert.assertNotNull(model);

		model.setLabel(link.getLabel());
		model.setUrl(link.getUrl());
		EAdditionalLinks saved = this.additionalLinksDAO.save(model);
		return saved.toApi();
	}
	
	@Override
	@Transactional
	public void deleteLink(long id) {
		this.additionalLinksDAO.deleteById(id);
	}
	
}
