package de.cinovo.cloudconductor.server.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.AgentHandler;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonTestRunner;

@RunWith(SpringDaemonTestRunner.class)
@SuppressWarnings("javadoc")
public class AgentTest extends APITest {
	
	private static final String TEMPLATE = "dev";
	
	private static final String HOST_A = "HOST_A";
	private static final String HOST_B = "HOST_B";
	private static final String HOST_C = "HOST_C";
	
	
	@Test
	public void testPackagesBasic() throws CloudConductorException {
		AgentHandler agent = new AgentHandler(this.getCSApi());
		{
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_C, this.getPartiallyInstalled());
			Assert.assertEquals(4, result.getToInstall().size());
			Assert.assertTrue(!result.getToErase().isEmpty());
			Assert.assertTrue(!result.getToUpdate().isEmpty());
		}
	}
	
	@Test
	public void testPackageMultiHost() throws CloudConductorException {
		AgentHandler agent = new AgentHandler(this.getCSApi());
		{
			// block the second host on update
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_A, this.getPartiallyInstalled());
			result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_B, this.getPartiallyInstalled());
			Assert.assertTrue(result.getToInstall().isEmpty());
			Assert.assertTrue(result.getToErase().isEmpty());
			Assert.assertTrue(result.getToUpdate().isEmpty());
		}
		{
			// finalize update on host a
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_A, this.getAllInstalled());
			Assert.assertTrue(result.getToInstall().isEmpty());
			Assert.assertTrue(result.getToUpdate().isEmpty());
			Assert.assertTrue(result.getToErase().isEmpty());
			agent.notifyServiceState(AgentTest.TEMPLATE, AgentTest.HOST_A, new ServiceStates(new ArrayList<String>()));
		}
		{
			// finally start update on host b
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_B, this.getPartiallyInstalled());
			Assert.assertEquals(4, result.getToInstall().size());
			Assert.assertTrue(!result.getToErase().isEmpty());
			Assert.assertTrue(!result.getToUpdate().isEmpty());
		}
		{
			// finalize update on b
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_B, this.getAllInstalled());
			Assert.assertTrue(result.getToInstall().isEmpty());
			Assert.assertTrue(result.getToUpdate().isEmpty());
			Assert.assertTrue(result.getToErase().isEmpty());
		}
	}
	
	private PackageState getAllInstalled() {
		List<PackageVersion> pkgs = new ArrayList<>();
		pkgs.add(new PackageVersion("nginx", "1.5.3-1", null, "TESTREPO"));
		pkgs.add(new PackageVersion("postgresql92", "9.2.4-1PGDG.rhel6", null, "TESTREPO"));
		pkgs.add(new PackageVersion("postgresql92-libs", "9.2.4-1PGDG.rhel6", null, "TESTREPO"));
		pkgs.add(new PackageVersion("postgresql92-server", "9.2.4-1PGDG.rhel6", null, "TESTREPO"));
		pkgs.add(new PackageVersion("jdk", "1.7.0_45-fcs", null, "TESTREPO"));
		return new PackageState(pkgs);
	}
	
	private PackageState getPartiallyInstalled() {
		List<PackageVersion> pkgs = new ArrayList<>();
		pkgs.add(new PackageVersion("nginx", "1.5.2-1", null, "TESTREPO"));
		pkgs.add(new PackageVersion("nodejs", "0.10.12-1", null, "TESTREPO"));
		return new PackageState(pkgs);
	}
}
