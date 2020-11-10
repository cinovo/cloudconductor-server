package de.cinovo.cloudconductor.server.util.comparators;

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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * Comparator for comparing version strings.
 * <p>
 * Note that the comparison algorithm used here does not aim to faithfully imitate the one used by the RPM Package Manager. It serves as a
 * simple implementation that should work with most reasonable version naming schemes.
 *
 * @author mhilbert
 */
public class VersionStringComparator implements Comparator<String> {

	@Override
	public int compare(String versionAIn, String versionBIn) {
		// Compare version. Empty string is less than non-empty string.
		if(versionAIn.isEmpty() != versionBIn.isEmpty()) {
			return Boolean.compare(!versionAIn.isEmpty(), !versionBIn.isEmpty());
		}

		// Split version and release.
		String[] versionA = this.getVersion(versionAIn);
		String[] versionB = this.getVersion(versionBIn);

		for(int i = 0; i < Math.max(versionA.length, versionB.length); ++i) {
			String xA = i < versionA.length ? versionA[i] : "0";
			String xB = i < versionB.length ? versionB[i] : "0";
			int versionCompResult = this.compareVersions(xA, xB);
			if(versionCompResult != 0) {
				return versionCompResult;
			}
		}
		// Compare release.
		return this.compareVersions(this.getRelease(versionAIn), this.getRelease(versionBIn));
	}

	private String[] getVersion(String version) {
		String[] parts = version.split("-", 2);
		return parts[0].split("\\.");
	}

	private String getRelease(String version) {
		String[] parts = version.split("-", 2);
		if((parts.length == 2) && !parts[1].isEmpty()) {
			return parts[1];
		}
		return "1";
	}

	private int compareVersions(String inputA, String inputB) {
		// Split parts at digit-to-non-digit boundaries, i.e. into parts that are either numeric or non-numeric strings. The individual
		// parts of both inputs will be compared pairwise from beginning to end until a pair is not equal, which will decide the comparison.
		// If no such pair is found the two inputs are considered equal.
		String[] inputASplit = this.splitAtDigitBoundaries(inputA);
		String[] inputBSplit = this.splitAtDigitBoundaries(inputB);

		for(int i = 0; i < Math.max(inputASplit.length, inputBSplit.length); ++i) {
			// Get string part for this index (default is empty string).
			String curPartA = i < inputASplit.length ? inputASplit[i] : "";
			String curPartB = i < inputBSplit.length ? inputBSplit[i] : "";

			// Empty string is less than non-empty string.
			if(curPartA.isEmpty() != curPartB.isEmpty()) {
				return Boolean.compare(!curPartA.isEmpty(), !curPartB.isEmpty());
			}

			// Check if the string parts begin with digits (if so, they only contain digits, because we split at digit-to-non-digit
			// boundaries).
			boolean inputAStartsWithDigit = this.beginsWithDigit(curPartA);
			boolean inputBStartsWithDigit = this.beginsWithDigit(curPartB);

			if((inputAStartsWithDigit) && (inputBStartsWithDigit)) { // both parts are numbers
				int compareResult = this.compareDigits(curPartA, curPartB);
				if(compareResult != 0) {
					return compareResult;
				}
			} else if((!inputAStartsWithDigit) && (!inputBStartsWithDigit)) { // neither of the parts is a number
				// Perform a lexicographic comparison.
				int c = curPartA.compareTo(curPartB);
				if(c != 0) {
					return c;
				}
			} else { // one part is a number, the other is not
				// Number trumps non-number.
				return Boolean.compare(inputAStartsWithDigit, inputBStartsWithDigit);
			}
		}
		// No pairwise difference found. The two inputs are considered equal.
		return 0;
	}

	private int compareDigits(String inputA, String inputB) {
		// Compare length of the numbers (without leading zeros).
		String ss1 = this.stripLeadingZeros(inputA);
		String ss2 = this.stripLeadingZeros(inputB);
		int c = Integer.compare(ss1.length(), ss2.length());
		if(c != 0) {
			return c;
		}
		// The numbers are of equal lengths. Perform a lexicographic comparison.
		c = ss1.compareTo(ss2);
		if(c != 0) {
			return c;
		}
		// The numbers are the same without leading zeros. Let the one with more leading zeros be greater, i.e. compare lengths
		// without removing leading zeros.
		c = Integer.compare(inputA.length(), inputB.length());
		return c;
	}

	private String[] splitAtDigitBoundaries(String str) {
		Matcher matcher = Pattern.compile("\\d+").matcher(str);
		List<String> parts = new ArrayList<>();
		int pos = 0;
		while(matcher.find()) {
			if(matcher.start() > 0) {
				parts.add(str.substring(pos, matcher.start()));
			}
			parts.add(matcher.group());
			pos = matcher.end();
		}
		if(pos < str.length()) {
			parts.add(str.substring(pos));
		}
		return parts.toArray(new String[0]);
	}

	private boolean beginsWithDigit(String str) {
		return str.substring(0, 1).matches("\\d");
	}

	private String stripLeadingZeros(String str) {
		int i = 0;
		while((i < str.length()) && (str.charAt(i) == '0')) {
			i++;
		}
		return str.substring(i);
	}
}
