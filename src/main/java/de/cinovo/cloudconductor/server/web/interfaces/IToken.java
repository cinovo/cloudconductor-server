package de.cinovo.cloudconductor.server.web.interfaces;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.api.MediaType;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.taimos.cxf_renderer.model.RenderedUI;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
@Path(IToken.ROOT)
public interface IToken {
	
	/** the root */
	public static final String ROOT = "/tokens";
	
	/** */
	public static final String REVOKE_TOKEN = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_UPDATE;
	
	/** */
	public static final String NEW_TOKEN = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_NEW;
	
	
	/**
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedUI view();
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedUI newTokenView();
	
	/**
	 * @param service the service name
	 * @return the modal content
	 */
	@GET
	@Path(IToken.REVOKE_TOKEN)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public abstract RenderedUI editTokenView(@PathParam(IWebPath.VAR_NAME) String token);
	
	/**
	 * @return an ajax answer
	 */
	@POST
	@Path(IToken.NEW_TOKEN)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public abstract AjaxAnswer generateNewToken(@FormParam("agents") String[] agents);
}
