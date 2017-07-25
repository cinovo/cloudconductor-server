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
	String ROOT = "/tokens";
	
	/** */
	String EDIT_TOKEN = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_EDIT;
	
	/** */
	String NEW_TOKEN = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_NEW;
	
	/**
	 * 
	 */
	String REVOKE_TOKEN_VIEW = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_REVOKE + "Token";
	/**
	 * 
	 */
	String REVOKE_TOKEN = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_REVOKE;
	
	/**
	 * 
	 */
	String UPDATE_TOKEN = "/{" + IWebPath.VAR_NAME + "}" + IWebPath.ACTION_UPDATE;
	
	
	/**
	 * @return the view
	 */
	@GET
	@Path(IWebPath.DEFAULTVIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	RenderedUI view();
	
	/**
	 * @return the modal content
	 */
	@GET
	@Path(IWebPath.ACTION_ADD)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	RenderedUI newTokenView();
	
	/**
	 * @return the modal content
	 * @param tokenId the token id
	 */
	@GET
	@Path(IToken.EDIT_TOKEN)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	RenderedUI editTokenView(@PathParam(IWebPath.VAR_NAME) Long tokenId);
	
	/**
	 * @param agents the agent list to associate with the token
	 * @return an ajax answer
	 */
	@POST
	@Path(IToken.NEW_TOKEN)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	AjaxAnswer generateNewToken(@FormParam("agents") String[] agents);
	
	/**
	 * @param tokenId the token id
	 * @param agents the agents to remove from the token
	 * @param nagents the agents (which are not token-agents yet) to add to the token
	 * @return an ajax answer
	 */
	@POST
	@Path(IToken.UPDATE_TOKEN)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	AjaxAnswer updateToken(@PathParam(IWebPath.VAR_NAME) Long tokenId, @FormParam("agents") String[] agents, @FormParam("nagents") String[] nagents);
	
	/**
	 * @param tokenId the id of the token
	 * @return the modal content
	 */
	@GET
	@Path(IToken.REVOKE_TOKEN_VIEW)
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	RenderedUI revokeTokenView(@PathParam(IWebPath.VAR_NAME) Long tokenId);
	
	/**
	 * @param tokenId the id of the token
	 * @param comment the comment
	 * @return an ajax answer
	 */
	@POST
	@Path(IToken.REVOKE_TOKEN)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	AjaxAnswer revokeToken(@PathParam(IWebPath.VAR_NAME) Long tokenId, @FormParam("revokeComment") String comment);
}
