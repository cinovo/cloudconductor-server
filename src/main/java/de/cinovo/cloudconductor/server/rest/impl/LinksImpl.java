package de.cinovo.cloudconductor.server.rest.impl;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.cinovo.cloudconductor.api.interfaces.ILinks;
import de.cinovo.cloudconductor.api.model.AdditionalLink;
import de.cinovo.cloudconductor.server.dao.IAdditionalLinksDAO;
import de.cinovo.cloudconductor.server.model.EAdditionalLinks;
import de.cinovo.cloudconductor.server.rest.helper.MAConverter;
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
public class LinksImpl extends ImplHelper implements ILinks {
	
	@Autowired
	private IAdditionalLinksDAO dlinks;
	
	
	@Override
	@Transactional
	public List<AdditionalLink> getLinks() {
		List<AdditionalLink> links = Lists.newArrayList();
		for (EAdditionalLinks link : this.dlinks.findList()) {
			links.add(MAConverter.fromModel(link));
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
		EAdditionalLinks saved = this.dlinks.save(model);
		return Response.created(URI.create("/api/links/" + saved.getId())).entity(MAConverter.fromModel(saved)).build();
	}
	
	@Override
	@Transactional
	public AdditionalLink getLink(long id) {
		EAdditionalLinks model = this.dlinks.findById(id);
		this.assertModelFound(model);
		return MAConverter.fromModel(model);
	}
	
	@Override
	@Transactional
	public AdditionalLink updateLink(long id, AdditionalLink link) {
		RESTAssert.assertNotNull(link);
		RESTAssert.assertNotNull(link.getLabel());
		RESTAssert.assertNotNull(link.getUrl());
		
		EAdditionalLinks model = this.dlinks.findById(id);
		this.assertModelFound(model);
		model.setLabel(link.getLabel());
		model.setUrl(link.getUrl());
		EAdditionalLinks saved = this.dlinks.save(model);
		return MAConverter.fromModel(saved);
	}
	
	@Override
	@Transactional
	public void deleteLink(long id) {
		this.dlinks.deleteById(id);
	}
	
}
