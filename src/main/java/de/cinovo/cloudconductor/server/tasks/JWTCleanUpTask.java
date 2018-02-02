package de.cinovo.cloudconductor.server.tasks;

import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EServerOptions;
import de.taimos.dvalin.jaxrs.security.jwt.AuthenticatedUser;
import de.taimos.dvalin.jaxrs.security.jwt.JWTAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class JWTCleanUpTask implements IServerTasks {

	@Value("${jwt-cleanup-hours:2}")
	private int cleanUpTimer;

	private TimeUnit cleanupTimerUnit = TimeUnit.HOURS;

	@Autowired
	private IJWTTokenDAO jwtTokenDAO;
	@Autowired
	private JWTAuth jwtAuth;

	@Override
	public String getTaskIdentifier() {
		return "JWTCleanUpTask";
	}

	@Override
	public Integer getTimer() {
		return this.cleanUpTimer;
	}

	@Override
	public TimeUnit getTimerUnit() {
		return this.cleanupTimerUnit;
	}

	@Override
	public Integer getDelay() {
		return 0;
	}

	@Override
	public TaskStateChange checkStateChange(EServerOptions oldSettings, EServerOptions newSettings) {
		return TaskStateChange.START;
	}


	@Override
	public void run() {
		for(EJWTToken jwtToken : this.jwtTokenDAO.findList()) {
			try {
				AuthenticatedUser authenticatedUser = this.jwtAuth.validateToken(jwtToken.getToken());
				//every token which is not valid anymore can be deleted
				if(authenticatedUser == null) {
					this.jwtTokenDAO.delete(jwtToken);
				}
			} catch(ParseException e) {
				//this token is realy broken, we get rid of it;
				this.jwtTokenDAO.delete(jwtToken);
			}
		}
	}
}
