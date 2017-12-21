package de.cinovo.cloudconductor.server.rest.ui;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.cinovo.cloudconductor.api.interfaces.ILinks;
import de.cinovo.cloudconductor.api.model.AdditionalLink;
import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.model.EAdditionalLinks;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.restutils.RESTAssert;

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
		
		EAdditionalLinks existing = this.additionalLinksDAO.findByLabel(link.getLabel());
		
		RESTAssert.assertTrue(existing == null, Status.CONFLICT);
		
		EAdditionalLinks model = new EAdditionalLinks();
		model.setLabel(link.getLabel());
		model.setUrl(link.getUrl());
		EAdditionalLinks saved = this.additionalLinksDAO.save(model);
		return Response.created(URI.create("/api/links/" + saved.getId())).entity(saved.toApi()).build();
	}
	
	@Override
	@Transactional
	public AdditionalLink updateLink(AdditionalLink link) {
		RESTAssert.assertNotNull(link);
		RESTAssert.assertNotNull(link.getLabel());
		RESTAssert.assertNotNull(link.getUrl());
		
		EAdditionalLinks model = this.additionalLinksDAO.findById(link.getId());
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
