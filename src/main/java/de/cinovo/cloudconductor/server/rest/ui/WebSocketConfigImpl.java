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
	
	@Value("${ws.host:localhost}")
	private String host;
	
	@Value("${ws.baseuri:/websocket}")
	private String baseURI;
	
	@Value("${ws.timeout:60000}")
	private long websocketTimeout;
	
	
	@Override
	public WebSocketConfig getConfig() {
		SplitURL split = URLUtils.splitURL(this.context.getServerURL());
		String webSocketBasePath = "ws://" + this.host + ":" + split.getPort() + this.baseURI;
		return new WebSocketConfig(webSocketBasePath, this.websocketTimeout);
	}
	
}
