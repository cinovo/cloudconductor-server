package de.cinovo.cloudconductor.server.comparators;

/*
 * #%L
 * cloudconductor-server
 * %%
 * Copyright (C) 2013 - 2014 Cinovo AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * #L%
 */

import java.util.Comparator;

import de.cinovo.cloudconductor.server.model.EPackageVersion;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Comparator for comparing two package versions.
 * 
 * @author psigloch
 */
public class PackageVersionComparator implements Comparator<EPackageVersion> {
	
	private static final VersionStringComparator versionStringComparator = new VersionStringComparator();
	
	
	@Override
	public int compare(EPackageVersion v1, EPackageVersion v2) {
		int pc = v1.getPkg().getName().compareTo(v2.getPkg().getName());
		if (pc != 0) {
			return pc;
		}
		return PackageVersionComparator.versionStringComparator.compare(v1.getVersion(), v2.getVersion());
	}
	
}
