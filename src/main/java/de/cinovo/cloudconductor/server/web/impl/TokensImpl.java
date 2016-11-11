package de.cinovo.cloudconductor.server.web.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.cinovo.cloudconductor.server.web.CSViewModel;
import de.cinovo.cloudconductor.server.web.helper.AWebPage;
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
		CSViewModel view = this.createView();
		view.addModel("TOKENS", tokens);
		return view.render();
	}
	
	@Override
	public RenderedUI newTokenView() {
		CSViewModel modal = this.createModal("mAddToken");
		return modal.render();
	}
	
	@Override
	public RenderedUI revokeTokenView(String token) {
		RESTAssert.assertNotEmpty(token);
		CSViewModel modal = this.createModal("mModToken");
		modal.addModel("TOKEN", this.dToken.findByToken(token));
		return modal.render();
	}
	
}
