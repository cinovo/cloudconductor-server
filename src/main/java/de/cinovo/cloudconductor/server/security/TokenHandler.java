package de.cinovo.cloudconductor.server.security;

import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.enums.AuthType;
import de.cinovo.cloudconductor.server.security.exception.TokenGenerationException;
import de.taimos.dvalin.jaxrs.security.jwt.AuthenticatedUser;
import de.taimos.dvalin.jaxrs.security.jwt.JWTAuth;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class TokenHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandler.class);
	
	@Value("${cloudconductor.tokenlength:32}")
	private int TOKEN_LENGTH;
	
	@Autowired
	private IAuthTokenDAO authTokenDao;
	@Autowired
	private IJWTTokenDAO jwtTokenDao;
	@Autowired
	private JWTAuth jwtAuth;
	
	
	/**
	 * @param user the user to generate the jwttoken for
	 * @param type the authentication type
	 * @param refToken the referenced login token
	 * @return the token
	 * @throws TokenGenerationException on token generation errors
	 */
	public EJWTToken generateJWTToken(EUser user, AuthType type, String refToken) {
		if (user == null) {
			return null;
		}
		EAuthToken referenceToken = null;
		if (type == AuthType.AGENT) {
			if ((refToken == null) || refToken.isEmpty()) {
				return null;
			}
			referenceToken = this.authTokenDao.findByToken(refToken);
			if (referenceToken == null) {
				return null;
			}
		}
		
		AuthenticatedUser newUser = new AuthenticatedUser();
		newUser.setUsername(user.getUsername());
		newUser.setDisplayName(user.getDisplayName());
		newUser.setId(String.valueOf(user.getId()));
		newUser.setRoles(user.getRoles());
		String token = this.jwtAuth.signToken(newUser);
		EJWTToken existing = this.jwtTokenDao.findByToken(token);
		if(existing != null) {
			return this.generateJWTToken(user, type, refToken);
		}

		EJWTToken ejwtToken = new EJWTToken();
		ejwtToken.setActive(true);
		ejwtToken.setToken(token);
		ejwtToken.setUser(user);
		ejwtToken.setAuthType(type);
		ejwtToken.setRefToken(referenceToken);
		return this.jwtTokenDao.save(ejwtToken);
	}
	
	/**
	 * @param user the user to revoke the tokens for
	 */
	public void revokeJWTTokens(EUser user) {
		if (user == null) {
			return;
		}
		for (EJWTToken jwtToken : user.getJwtTokens()) {
			if (jwtToken == null) {
				return;
			}
			this.jwtTokenDao.delete(jwtToken);
		}
	}
	
	/**
	 * @param token the token to revoke
	 */
	public void revokeJWTToken(String token) {
		if (token == null) {
			return;
		}
		EJWTToken jwtToken = this.jwtTokenDao.findByToken(token);
		if (jwtToken == null) {
			return;
		}
		this.jwtTokenDao.delete(jwtToken);
	}
	
	private void revokeJWTToken(EUser user, EAuthToken token) {
		for (EJWTToken jwtToken : this.jwtTokenDao.findByRefToken(user, token)) {
			this.jwtTokenDao.delete(jwtToken);
		}
	}
	
	/**
	 * Generates a unique AuthToken and saves it in the database.
	 *
	 * @param user the user the toke is created for
	 * @return a generated AuthToken if generated successful, or null if something went wrong
	 * @throws TokenGenerationException - when generation of unqiue token failed in loop after 10 attempts
	 */
	public EAuthToken generateAuthToken(EUser user) {
		String generatedToken = null;
		
		int count = 0;
		while (!this.authTokenDao.isTokenUnique(generatedToken)) {
			if (count > 10) {
				String errorMsg = "Failed to generate unique token.";
				TokenHandler.LOGGER.error(errorMsg);
				return null;
			}
			count++;
			generatedToken = this.generateToken();
		}
		EAuthToken token = new EAuthToken();
		token.setCreationDate(DateTime.now());
		token.setToken(generatedToken);
		token.setUser(user);
		return this.authTokenDao.save(token);
	}
	
	/**
	 * @param user the user to revoke the token for
	 * @param token the token to revoke
	 * @return true if revoking was successful, false otherwise
	 */
	public boolean revokeAuthToken(EUser user, String token) {
		EAuthToken authToken = this.authTokenDao.findByToken(token);
		if (authToken == null) {
			return false;
		}
		boolean found = false;
		for (EAuthToken eAuthToken : user.getAuthTokens()) {
			if (eAuthToken.getToken().equals(token)) {
				found = true;
				break;
			}
		}
		if (found) {
			authToken.setRevokeDate(DateTime.now());
			EAuthToken save = this.authTokenDao.save(authToken);
			this.revokeJWTToken(user, save);
			return true;
		}
		return false;
	}
	
	private String generateToken() {
		String generatedToken = new BigInteger(this.TOKEN_LENGTH * 5, new SecureRandom()).toString(32);
		generatedToken = this.generatePartialUppercasedToken(this.TOKEN_LENGTH, generatedToken);
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
		char temp = toSwapIn[i];
		toSwapIn[i] = toSwapIn[j];
		toSwapIn[j] = temp;
		return toSwapIn;
	}
}
