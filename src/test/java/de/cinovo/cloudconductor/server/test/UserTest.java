package de.cinovo.cloudconductor.server.test;

import java.util.Set;

import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.UserHandler;
import de.cinovo.cloudconductor.api.model.AuthToken;
import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.APITest;

@ExtendWith(SpringDaemonExtension.class)
class UserTest extends APITest {
	
	@Test
	void test() throws CloudConductorException {
		UserHandler h = new UserHandler(this.getCSApi(), this.getToken());
		{
			Set<User> set = h.get();
			Assertions.assertEquals(2, set.size());
		}
		{
			User admin = h.get("admin");
			Assertions.assertEquals("admin", admin.getLoginName());
			Assertions.assertEquals("Admin", admin.getDisplayName());
		}
		{
			User test = new User();
			test.setLoginName("test");
			test.setDisplayName("Test User");
			test.setPassword("password");
			test.setActive(true);
			
			h.save(test);
			
			Set<User> set = h.get();
			Assertions.assertEquals(3, set.size());
		}
		{
			h.createAuthToken("test");
			User u = h.get("test");
			Assertions.assertEquals(1, u.getAuthTokens().size());
			
			AuthToken token = u.getAuthTokens().iterator().next();
			Assertions.assertNull(token.getRevoked());
			
			h.revokeAuthToken("test", token.getToken());
			
			u = h.get("test");
			Assertions.assertEquals(1, u.getAuthTokens().size());
			
			token = u.getAuthTokens().iterator().next();
			Assertions.assertNotNull(token.getRevoked());
		}
	}
}
