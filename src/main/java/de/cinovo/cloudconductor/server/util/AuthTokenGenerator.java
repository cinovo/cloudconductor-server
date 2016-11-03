package de.cinovo.cloudconductor.server.util;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
public class AuthTokenGenerator {
	
	/**
	 * the AuthTokenGenerator singleton
	 */
	public static AuthTokenGenerator instance = new AuthTokenGenerator();
	
	private SecureRandom random = new SecureRandom();
	
	@Autowired
	private IAgentAuthTokenDAO dauthtoken;
	
	
	/**
	 * Generates a unique AuthToken and saves it in the database.
	 * 
	 * @return a generated AuthToken if generated successful, or null if something went wrong
	 */
	public synchronized String generateAuthToken(int tokenLength) {
		String generatedToken = "";
		// Try generating token, until you generated a unique one
		while (!this.dauthtoken.isTokenUnique(generatedToken) && !generatedToken.equals("") && (generatedToken.length() == tokenLength)) {
			generatedToken = new BigInteger(tokenLength * 5, this.random).toString(32);
		}
		EAgentAuthToken token = new EAgentAuthToken();
		token.setToken(generatedToken);
		token.setCreationDate(new DateTime());
		this.dauthtoken.save(token);
		return generatedToken;
	}
}
