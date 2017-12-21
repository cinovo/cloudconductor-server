package de.cinovo.cloudconductor.server.test;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.UserHandler;
import de.cinovo.cloudconductor.api.model.AuthToken;
import de.cinovo.cloudconductor.api.model.User;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonTestRunner;

@RunWith(SpringDaemonTestRunner.class)
@SuppressWarnings("javadoc")
public class UserTest extends APITest {
	
	@Test
	public void test() throws CloudConductorException {
		UserHandler h = new UserHandler(this.getCSApi(), this.getToken());
		{
			Set<User> set = h.get();
			Assert.assertEquals(2, set.size());
		}
		{
			User admin = h.get("admin");
			Assert.assertEquals("admin", admin.getLoginName());
			Assert.assertEquals("Admin", admin.getDisplayName());
		}
		{
			User test = new User();
			test.setLoginName("test");
			test.setDisplayName("Test User");
			test.setPassword("password");
			test.setActive(true);
			
			h.save(test);
			
			Set<User> set = h.get();
			Assert.assertEquals(3, set.size());
		}
		{
			h.createAuthToken("test");
			User u = h.get("test");
			Assert.assertEquals(1, u.getAuthTokens().size());
			
			AuthToken token = u.getAuthTokens().iterator().next();
			Assert.assertNull(token.getRevoked());
			
			h.revokeAuthToken("test", token.getToken());
			
			u = h.get("test");
			Assert.assertEquals(1, u.getAuthTokens().size());
			
			token = u.getAuthTokens().iterator().next();
			Assert.assertNotNull(token.getRevoked());
		}
	}
}
