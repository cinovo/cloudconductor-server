package de.cinovo.cloudconductor.server.web.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IAgentDAO;
import de.cinovo.cloudconductor.server.model.EAgent;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.cinovo.cloudconductor.server.util.AuthTokenGenerator;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
import de.cinovo.cloudconductor.server.web.helper.AjaxAnswer;
import de.cinovo.cloudconductor.server.web.helper.NavbarHardLinks;
import de.cinovo.cloudconductor.server.web.interfaces.IToken;
import de.cinovo.cloudconductor.server.web.interfaces.IWebPath;
import de.taimos.cxf_renderer.model.RenderedUI;
import de.taimos.restutils.RESTAssert;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
public class TokensImpl extends AWebPage implements IToken {
	
	@Autowired
	private IAgentAuthTokenDAO dToken;
	@Autowired
	private IAgentDAO dAgent;
	@Autowired
	private AuthTokenGenerator tokenGen;
	
	
	@Override
	protected String getTemplateFolder() {
		return "tokens";
	}
	
	@Override
	protected void init() {
		this.navRegistry.registerSubMenu(NavbarHardLinks.config, this.getNavElementName(), IToken.ROOT);
		this.addBreadCrumb(IWebPath.WEBROOT + IToken.ROOT, this.getNavElementName());
		this.addTopAction(IWebPath.WEBROOT + IToken.ROOT + IWebPath.ACTION_ADD, "Create new Token");
	}
	
	@Override
	protected String getNavElementName() {
		return "Auth-Tokens";
	}
	
	@Override
	@Transactional
	public RenderedUI view() {
		List<EAgentAuthToken> tokens = this.dToken.findList();
		Collections.sort(tokens);
		CSViewModel view = this.createView();
		view.addModel("TOKENS", tokens);
		// send token-agent map to view
		List<EAgent> agents = this.dAgent.findList();
		Multimap<Long, String> tokenAgentMap = HashMultimap.create();
		for (EAgent agent : agents) {
			if (agent.getToken() != null) {
				tokenAgentMap.put(agent.getToken().getId(), agent.getName());
			}
		}
		view.addModel("TOKENAGENTMAP", tokenAgentMap);
		return view.render();
	}
	
	@Override
	@Transactional
	public RenderedUI newTokenView() {
		CSViewModel modal = this.createModal("mAddToken");
		List<EAgent> agentsWithoutToken = this.dAgent.getAgentsWithoutToken();
		modal.addModel("AGENTSWITHOUTTOKEN", agentsWithoutToken);
		return modal.render();
	}
	
	@Override
	@Transactional
	public RenderedUI editTokenView(Long tokenId) {
		RESTAssert.assertNotNull(tokenId);
		CSViewModel modal = this.createModal("mModToken");
		EAgentAuthToken token = this.dToken.findById(tokenId);
		modal.addModel("TOKEN", token);
		List<EAgent> agents = this.dAgent.findList();
		List<EAgent> agentList = new ArrayList<EAgent>();
		List<EAgent> notAgentList = new ArrayList<EAgent>();
		for (EAgent agent : agents) {
			if (agent.getToken() != null) {
				if (agent.getToken().getId().equals(tokenId)) {
					agentList.add(agent);
				} else if (agent.getToken().getId() < 0) {
					notAgentList.add(agent);
				}
			} else {
				if (agent.getToken() == null) {
					notAgentList.add(agent);
				}
			}
		}
		modal.addModel("AGENTLIST", agentList);
		modal.addModel("NOTAGENTLIST", notAgentList);
		return modal.render();
	}
	
	@Override
	@Transactional
	public AjaxAnswer generateNewToken(String[] agents) {
		try {
			EAgentAuthToken generatedToken = this.tokenGen.generateAuthToken(Integer.parseInt(System.getProperty("cloudconductor.tokenlength", "32")));
			for (String agentId : agents) {
				EAgent agent = this.dAgent.findById(Long.parseLong(agentId));
				agent.setToken(generatedToken);
				this.dAgent.save(agent);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IToken.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxAnswer updateToken(Long tokenId, String[] agents, String[] nagents) {
		EAgentAuthToken tokenToUpdate = this.dToken.findById(tokenId);
		for (String agentId : agents) {
			// remove agents from token
			EAgent agent = this.dAgent.findById(Long.parseLong(agentId));
			agent.setToken(null);
			this.dAgent.save(agent);
		}
		for (String nAgentId : nagents) {
			// add agents to token
			EAgent nagent = this.dAgent.findById(Long.parseLong(nAgentId));
			nagent.setToken(tokenToUpdate);
			this.dAgent.save(nagent);
		}
		return new AjaxAnswer(IWebPath.WEBROOT + IToken.ROOT);
	}
	
	@Override
	@Transactional
	public AjaxAnswer revokeToken(Long tokenId) {
		RESTAssert.assertNotNull(tokenId);
		EAgentAuthToken token = this.dToken.findById(tokenId);
		token.setRevoked((new DateTime()).getMillis());
		this.dToken.save(token);
		return new AjaxAnswer(IWebPath.WEBROOT + IToken.ROOT);
	}
	
}
