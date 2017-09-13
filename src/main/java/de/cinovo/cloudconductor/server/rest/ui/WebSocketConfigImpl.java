package de.cinovo.cloudconductor.server.rest.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import de.cinovo.cloudconductor.api.interfaces.IWebSocketConfig;
import de.cinovo.cloudconductor.api.model.WebSocketConfig;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.URLUtils;
import de.taimos.dvalin.jaxrs.URLUtils.SplitURL;
import de.taimos.dvalin.jaxrs.context.DvalinRSContext;

/**
 * 
 * Copyright 2017 Cinovo AG<br>
 * <br>
 * 
 * @author mweise
 *
 */
@JaxRsComponent
public class WebSocketConfigImpl implements IWebSocketConfig {
	
	@Autowired
	DvalinRSContext context;
	
	@Value("${websocket.baseuri:/websocket}")
	private String baseURI;
	
	
	@Override
	public WebSocketConfig getConfig() {
		SplitURL split = URLUtils.splitURL(this.context.getServerURL());
		String webSocketBasePath = "ws://" + split.getHost() + ":" + split.getPort() + this.baseURI;
		return new WebSocketConfig(webSocketBasePath);
	}
	
}
