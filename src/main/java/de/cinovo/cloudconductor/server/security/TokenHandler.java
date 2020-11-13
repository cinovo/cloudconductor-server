package de.cinovo.cloudconductor.server.security;

import de.cinovo.cloudconductor.server.dao.IAuthTokenDAO;
import de.cinovo.cloudconductor.server.dao.IJWTTokenDAO;
import de.cinovo.cloudconductor.server.dao.IUserGroupDAO;
import de.cinovo.cloudconductor.server.model.EAuthToken;
import de.cinovo.cloudconductor.server.model.EJWTToken;
import de.cinovo.cloudconductor.server.model.EUser;
import de.cinovo.cloudconductor.server.model.EUserGroup;
import de.cinovo.cloudconductor.server.model.enums.AuthType;
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
import java.util.Set;
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
	@Autowired
	private IUserGroupDAO userGroupDAO;
	
	/**
	 * @param user     the user to generate the JWT for
	 * @param type     the authentication type
	 * @param refToken the referenced login token
	 * @return the token
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
		newUser.setUsername(user.getLoginName());
		newUser.setDisplayName(user.getDisplayName());
		newUser.setId(String.valueOf(user.getId()));
		newUser.setRoles(this.userGroupDAO.findByIds(user.getUserGroup()).stream().map(EUserGroup::getPermissionsAsString).distinct().flatMap(Set::stream).toArray(String[]::new));
		
		String token = this.jwtAuth.signToken(newUser);
		EJWTToken existing = this.jwtTokenDao.findByToken(token);
		if (existing != null) {
			return this.generateJWTToken(user, type, refToken);
		}
		
		EJWTToken ejwtToken = new EJWTToken();
		ejwtToken.setActive(true);
		ejwtToken.setToken(token);
		ejwtToken.setUserId(user.getId());
		ejwtToken.setAuthType(type);
		if (referenceToken != null) {
			ejwtToken.setRefToken(referenceToken.getId());
		}
		return this.jwtTokenDao.save(ejwtToken);
	}
	
	/**
	 * @param user the user to revoke the tokens for
	 */
	public void revokeJWTTokens(EUser user) {
		if (user == null) {
			return;
		}
		this.jwtTokenDao.deleteByUser(user);
	}
	
	/**
	 * @param token the token to revoke
	 */
	public void revokeJWTToken(String token) {
		if (token == null) {
			return;
		}
		this.jwtTokenDao.deleteByToken(token);
	}
	
	/**
	 * Generates a unique AuthToken and saves it in the database.
	 *
	 * @param user the user the toke is created for
	 */
	public void generateAuthToken(EUser user) {
		String generatedToken = null;
		
		int count = 0;
		while (!this.authTokenDao.isTokenUnique(generatedToken)) {
			if (count > 10) {
				String errorMsg = "Failed to generate unique token.";
				TokenHandler.LOGGER.error(errorMsg);
				return;
			}
			count++;
			generatedToken = this.generateToken();
		}
		EAuthToken token = new EAuthToken();
		token.setCreationDate(DateTime.now());
		token.setToken(generatedToken);
		token.setUserid(user.getId());
		this.authTokenDao.save(token);
	}
	
	/**
	 * @param user  the user to revoke the token for
	 * @param token the token to revoke
	 * @return true if revoking was successful, false otherwise
	 */
	public boolean revokeAuthToken(EUser user, String token) {
		EAuthToken authToken = this.authTokenDao.findByUserAndToken(user, token);
		if (authToken == null) {
			return false;
		}
		
		authToken.setRevokeDate(DateTime.now());
		EAuthToken savedAuthToken = this.authTokenDao.save(authToken);
		this.jwtTokenDao.deleteByRefToken(savedAuthToken);
		return true;
	}
	
	private String generateToken() {
		String generatedToken = new BigInteger(this.TOKEN_LENGTH * 5, new SecureRandom()).toString(32);
		generatedToken = this.generatePartialUppercasedToken(this.TOKEN_LENGTH, generatedToken);
		return this.shuffleWithFisherYates(generatedToken);
	}
	
	private String generatePartialUppercasedToken(int tokenLength, String currentToken) {
		return currentToken.toUpperCase().substring(0, tokenLength / 2) + currentToken.substring(tokenLength / 2, tokenLength - 1);
	}
	
	private String shuffleWithFisherYates(String tokenStringToShuffle) {
		char[] shuffleArray = tokenStringToShuffle.toCharArray();
		int tokenLength = tokenStringToShuffle.length();
		for (int i = 0; i < (tokenLength - 2); i++) {
			int j = ThreadLocalRandom.current().nextInt(i, tokenLength);
			this.swap(shuffleArray, i, j);
		}
		return new String(shuffleArray);
	}
	
	private void swap(char[] toSwapIn, int i, int j) {
		char temp = toSwapIn[i];
		toSwapIn[i] = toSwapIn[j];
		toSwapIn[j] = temp;
	}
}
