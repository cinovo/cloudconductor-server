package de.cinovo.cloudconductor.server.util;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;

/**
 * Copyright 2016 Cinovo AG<br>
 * <br>
 * 
 * @author ablehm
 * 
 */
@Service
public class AuthTokenGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenGenerator.class);
	@Autowired
	private IAgentAuthTokenDAO dToken;
	
	
	/**
	 * Generates a unique AuthToken and saves it in the database.
	 * 
	 * @param tokenLength the length of the token to generate
	 * 
	 * @return a generated AuthToken if generated successful, or null if something went wrong
	 * @throws TokenGenerationException - when generation of unqiue token failed in loop after 10 attempts
	 */
	public EAgentAuthToken generateAuthToken(int tokenLength) throws TokenGenerationException {
		String generatedToken = "";
		// Try generating token, until you generated a unique one
		int count = 0;
		while (!this.dToken.isTokenUnique(generatedToken) || generatedToken.trim().isEmpty()) {
			if (count > 10) {
				String errorMsg = "Failed to generate unique token.";
				AuthTokenGenerator.LOGGER.error(errorMsg);
				throw new TokenGenerationException(errorMsg);
			}
			generatedToken = new BigInteger(tokenLength * 5, new SecureRandom()).toString(tokenLength);
			count++;
		}
		EAgentAuthToken token = new EAgentAuthToken();
		token.setToken(generatedToken);
		token.setCreationDate(new DateTime().getMillis());
		token = this.dToken.save(token);
		return token;
	}
}
