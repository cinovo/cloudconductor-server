package de.cinovo.cloudconductor.server.test;

import de.cinovo.cloudconductor.api.lib.exceptions.CloudConductorException;
import de.cinovo.cloudconductor.api.lib.manager.AgentHandler;
import de.cinovo.cloudconductor.api.model.AgentOption;
import de.cinovo.cloudconductor.api.model.Dependency;
import de.cinovo.cloudconductor.api.model.PackageState;
import de.cinovo.cloudconductor.api.model.PackageStateChanges;
import de.cinovo.cloudconductor.api.model.PackageVersion;
import de.cinovo.cloudconductor.api.model.ServiceStates;
import de.cinovo.cloudconductor.server.APITest;
import de.taimos.daemon.spring.SpringDaemonTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringDaemonTestRunner.class)
public class AgentTest extends APITest {
	
	private static final String TEMPLATE = "dev";
	
	private static final String HOST_A = "HOST_A";
	private static final String HOST_B = "HOST_B";
	private static final String HOST_C = "HOST_C";
	
	
	@Test
	public void testPackagesBasic() throws CloudConductorException {
		AgentHandler agent = new AgentHandler(this.getCSApi(), this.getToken());
		AgentOption optionsC = agent.heartBeat(AgentTest.TEMPLATE, AgentTest.HOST_C, null, "asd");
		{
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_C, this.getPartiallyInstalled(), optionsC.getUuid());
			Assert.assertEquals(4, result.getToInstall().size());
			Assert.assertFalse(result.getToErase().isEmpty());
			Assert.assertFalse(result.getToUpdate().isEmpty());
		}
	}
	
	@Test
	public void testPackageMultiHost() throws CloudConductorException {
		AgentHandler agent = new AgentHandler(this.getCSApi(), this.getToken());
		AgentOption optionsA = agent.heartBeat(AgentTest.TEMPLATE, AgentTest.HOST_A, null, "asd");
		AgentOption optionsB = agent.heartBeat(AgentTest.TEMPLATE, AgentTest.HOST_B, null, "asd2");
		
		{
			// block the second host on update
			agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_A, this.getPartiallyInstalled(), optionsA.getUuid());
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_B, this.getPartiallyInstalled(), optionsB.getUuid());
			Assert.assertTrue(result.getToInstall().isEmpty());
			Assert.assertTrue(result.getToErase().isEmpty());
			Assert.assertTrue(result.getToUpdate().isEmpty());
		}
		{
			// finalize update on host a
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_A, this.getAllInstalled(), optionsA.getUuid());
			Assert.assertTrue(result.getToInstall().isEmpty());
			Assert.assertTrue(result.getToUpdate().isEmpty());
			Assert.assertTrue(result.getToErase().isEmpty());
			agent.notifyServiceState(AgentTest.TEMPLATE, AgentTest.HOST_A, new ServiceStates(new ArrayList<>()), optionsA.getUuid());
		}
		{
			// finally start update on host b
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_B, this.getPartiallyInstalled(), optionsB.getUuid());
			Assert.assertEquals(4, result.getToInstall().size());
			Assert.assertFalse(result.getToErase().isEmpty());
			Assert.assertFalse(result.getToUpdate().isEmpty());
		}
		{
			// finalize update on b
			PackageStateChanges result = agent.notifyPackageState(AgentTest.TEMPLATE, AgentTest.HOST_B, this.getAllInstalled(), optionsB.getUuid());
			Assert.assertTrue(result.getToInstall().isEmpty());
			Assert.assertTrue(result.getToUpdate().isEmpty());
			Assert.assertTrue(result.getToErase().isEmpty());
		}
	}
	
	private PackageState getAllInstalled() {
		List<PackageVersion> pkgs = new ArrayList<>();
		pkgs.add(this.createPackageState("nginx", "1.5.3-1", null, "TESTREPO"));
		pkgs.add(this.createPackageState("postgresql92", "9.2.4-1PGDG.rhel6", null, "TESTREPO"));
		pkgs.add(this.createPackageState("postgresql92-libs", "9.2.4-1PGDG.rhel6", null, "TESTREPO"));
		pkgs.add(this.createPackageState("postgresql92-server", "9.2.4-1PGDG.rhel6", null, "TESTREPO"));
		pkgs.add(this.createPackageState("jdk", "1.7.0_45-fcs", null, "TESTREPO"));
		return new PackageState(pkgs);
	}
	
	private PackageState getPartiallyInstalled() {
		List<PackageVersion> pkgs = new ArrayList<>();
		pkgs.add(this.createPackageState("nginx", "1.5.2-1", null, "TESTREPO"));
		pkgs.add(this.createPackageState("nodejs", "0.10.12-1", null, "TESTREPO"));
		return new PackageState(pkgs);
	}
	
	@SuppressWarnings("SameParameterValue")
	private PackageVersion createPackageState(String name, String version, Set<Dependency> dep, String psg) {
		PackageVersion packageVersion = new PackageVersion();
		packageVersion.setName(name);
		packageVersion.setVersion(version);
		packageVersion.setDependencies(dep);
		packageVersion.setRepos(new HashSet<String>());
		packageVersion.getRepos().add(psg);
		return packageVersion;
	}
	
}
