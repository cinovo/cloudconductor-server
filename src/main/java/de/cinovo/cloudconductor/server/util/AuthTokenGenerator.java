package de.cinovo.cloudconductor.server.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.cinovo.cloudconductor.server.dao.IAgentAuthTokenDAO;
import de.cinovo.cloudconductor.server.model.EAgentAuthToken;
import de.cinovo.cloudconductor.server.util.exception.TokenGenerationException;

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
		String generatedToken = null;
		
		int count = 0;
		while (!this.dToken.isTokenUnique(generatedToken)) {
			if (count > 10) {
				String errorMsg = "Failed to generate unique token.";
				AuthTokenGenerator.LOGGER.error(errorMsg);
				throw new TokenGenerationException(errorMsg);
			}
			count++;
			generatedToken = this.generateToken(tokenLength);
		}
		
		EAgentAuthToken token = new EAgentAuthToken();
		token.setToken(generatedToken);
		token.setCreationDate(new DateTime().getMillis());
		token = this.dToken.save(token);
		return token;
	}
	
	protected String generateToken(int tokenLength) {
		String generatedToken = new BigInteger(tokenLength * 5, new SecureRandom()).toString(32);
		generatedToken = this.generatePartialUppercasedToken(tokenLength, generatedToken);
		return this.shuffleWithFisherYates(generatedToken);
	}
	
	private String generatePartialUppercasedToken(int tokenLength, String currentToken) {
		StringBuilder tokenStringToShuffle = new StringBuilder();
		tokenStringToShuffle.append(currentToken.substring(0, tokenLength / 2).toUpperCase());
		tokenStringToShuffle.append(currentToken.substring(tokenLength / 2, tokenLength));
		return tokenStringToShuffle.toString();
	}
	
	private String shuffleWithFisherYates(String tokenStringToShuffle) {
		char[] shuffleArray = tokenStringToShuffle.toCharArray();
		int tokenLength = tokenStringToShuffle.length();
		for (int i = 0; i < (tokenLength - 2); i++) {
			int j = ThreadLocalRandom.current().nextInt(i, tokenLength);
			shuffleArray = this.swap(shuffleArray, i, j);
		}
		return new String(shuffleArray);
	}
	
	private char[] swap(char[] toSwapIn, int i, int j) {
		char[] toReturn = toSwapIn;
		char temp = toReturn[i];
		toReturn[i] = toReturn[j];
		toReturn[j] = temp;
		return toReturn;
	}
}
