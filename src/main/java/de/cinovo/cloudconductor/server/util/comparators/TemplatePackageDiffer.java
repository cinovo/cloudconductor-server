package de.cinovo.cloudconductor.server.util.comparators;

import de.cinovo.cloudconductor.api.model.PackageDiff;
import de.cinovo.cloudconductor.server.dao.IPackageVersionDAO;
import de.cinovo.cloudconductor.server.dao.ITemplateDAO;
import de.cinovo.cloudconductor.server.model.EPackageVersion;
import de.cinovo.cloudconductor.server.model.ETemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Service
public class TemplatePackageDiffer {
	
	private static final VersionStringComparator versionStringComparator = new VersionStringComparator();
	
	@Autowired
	private ITemplateDAO templateDAO;
	@Autowired
	private IPackageVersionDAO packageVersionDAO;
	
	/**
	 * @param templateNameA template a
	 * @param templateNameB template b
	 * @return array of pacakges difference between the given templates for the biggest installed version of the package
	 */
	public PackageDiff[] compare(String templateNameA, String templateNameB) {
		ETemplate templateA = this.templateDAO.findByName(templateNameA);
		ETemplate templateB = this.templateDAO.findByName(templateNameB);
		if (templateA == null && templateB == null) {
			return new PackageDiff[0];
		}
		if (templateA == null || templateA.getPackageVersions() == null || templateA.getPackageVersions().isEmpty()) {
			return this.packageVersionToPackageDiff(this.packageVersionDAO.findByIds(templateB.getPackageVersions()));
		}
		if (templateB == null || templateB.getPackageVersions() == null || templateB.getPackageVersions().isEmpty()) {
			return this.packageVersionToPackageDiff(this.packageVersionDAO.findByIds(templateA.getPackageVersions()));
		}
		
		Set<PackageDiff> res = new HashSet<>();
		List<EPackageVersion> pvsTemplateA = this.packageVersionDAO.findByIds(templateA.getPackageVersions());
		Set<EPackageVersion> missingFromA = new HashSet<>(pvsTemplateA);
		
		for (EPackageVersion ePackageVersionB : this.packageVersionDAO.findByIds(templateB.getPackageVersions())) {
			boolean found = false;
			for (EPackageVersion ePackageVersionA : pvsTemplateA) {
				if (ePackageVersionB.getPkgId().equals(ePackageVersionA.getPkgId())) {
					missingFromA.remove(ePackageVersionA);
					int vComp = TemplatePackageDiffer.versionStringComparator.compare(ePackageVersionA.getVersion(), ePackageVersionB.getVersion());
					if (vComp > 0) {
						res.add(this.packageVersionToPackageDiff(ePackageVersionA));
					} else if (vComp < 0) {
						res.add(this.packageVersionToPackageDiff(ePackageVersionB));
					}
					found = true;
				}
			}
			if (!found) {
				res.add(this.packageVersionToPackageDiff(ePackageVersionB));
			}
		}
		
		for (EPackageVersion ePackageVersionA : missingFromA) {
			res.add(this.packageVersionToPackageDiff(ePackageVersionA));
		}
		
		return res.toArray(new PackageDiff[0]);
	}
	
	private PackageDiff packageVersionToPackageDiff(EPackageVersion pkgVersion) {
		PackageDiff res = new PackageDiff();
		res.setName(pkgVersion.getPkgName());
		res.setVersion(pkgVersion.getVersion());
		return res;
	}
	
	private PackageDiff[] packageVersionToPackageDiff(List<EPackageVersion> packageVersions) {
		return packageVersions.stream().map(this::packageVersionToPackageDiff).toArray(PackageDiff[]::new);
	}
}
