package de.cinovo.cloudconductor.server.installer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;

import de.taimos.daemon.DaemonStarter;
import de.taimos.springcxfdaemon.SpringDaemonAdapter;

/**
 * Copyright 2014 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public class InstallationAdapter extends SpringDaemonAdapter {
	
	@Override
	protected void doBeforeSpringStart() {
		// In dev mode all classes related to the config server should log on the level DEBUG.
		if (DaemonStarter.isDevelopmentMode()) {
			Logger.getLogger("de.cinovo.cloudconductor.server").setLevel(Level.DEBUG);
		}
		Velocity.setProperty("runtime.log.logsystem.log4j.logger", "org.apache.velocity");
		super.doBeforeSpringStart();
	}

	@Override
	protected String getSpringResource() {
		return "spring/installation.xml";
	}
}
