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
 * 
 * Note that the comparison algorithm used here does not aim to faithfully imitate the one used by the RPM Package Manager. It serves as a
 * simple implementation that should work with most reasonable version naming schemes.
 * 
 * @author mhilbert
 */
public class VersionStringComparator implements Comparator<String> {
	
	@Override
	public int compare(String version1, String version2) {
		// Split version and release.
		String[] parts1 = version1.split("-", 2);
		parts1 = (parts1.length == 2) && !parts1[1].isEmpty() ? parts1 : new String[] {parts1[0], "1"};
		String[] parts2 = version2.split("-", 2);
		parts2 = (parts2.length == 2) && !parts2[1].isEmpty() ? parts2 : new String[] {parts2[0], "1"};
		
		// Compare version. Empty string is less than non-empty string.
		if (version1.isEmpty() != version2.isEmpty()) {
			return Boolean.compare(!version1.isEmpty(), !version2.isEmpty());
		}
		String[] v1 = parts1[0].split("\\.");
		String[] v2 = parts2[0].split("\\.");
		for (int i = 0; i < Math.max(v1.length, v2.length); ++i) {
			String x1 = i < v1.length ? v1[i] : "0";
			String x2 = i < v2.length ? v2[i] : "0";
			int c = this.compareVersionParts(x1, x2);
			if (c != 0) {
				return c;
			}
		}
		
		// Compare release.
		return this.compareVersionParts(parts1[1], parts2[1]);
	}
	
	private int compareVersionParts(String input1, String input2) {
		// Split parts at digit-to-non-digit boundaries, i.e. into parts that are either numeric or non-numeric strings. The individual
		// parts of both inputs will be compared pairwise from beginning to end until a pair is not equal, which will decide the comparison.
		// If no such pair is found the two inputs are considered equal.
		String[] r1 = this.splitAtDigitBoundaries(input1);
		String[] r2 = this.splitAtDigitBoundaries(input2);
		
		for (int i = 0; i < Math.max(r1.length, r2.length); ++i) {
			// Get string part for this index (default is empty string).
			String s1 = i < r1.length ? r1[i] : "";
			String s2 = i < r2.length ? r2[i] : "";
			
			// Empty string is less than non-empty string.
			if (s1.isEmpty() != s2.isEmpty()) {
				return Boolean.compare(!s1.isEmpty(), !s2.isEmpty());
			}
			
			// Check if the string parts begin with digits (if so, they only contain digits, because we split at digit-to-non-digit
			// boundaries).
			boolean b1 = this.beginsWithDigit(s1);
			boolean b2 = this.beginsWithDigit(s2);
			
			if ((b1 == true) && (b2 == true)) { // both parts are numbers
				// Compare length of the numbers (without leading zeros).
				String ss1 = this.stripLeadingZeros(s1);
				String ss2 = this.stripLeadingZeros(s2);
				int c = Integer.compare(ss1.length(), ss2.length());
				if (c != 0) {
					return c;
				}
				
				// The numbers are of equal lengths. Perform a lexicographic comparison.
				c = ss1.compareTo(ss2);
				if (c != 0) {
					return c;
				}
				
				// The numbers are the same without leading zeros. Let the one with more leading zeros be greater, i.e. compare lengths
				// without removing leading zeros.
				c = Integer.compare(s1.length(), s2.length());
				if (c != 0) {
					return c;
				}
			} else if ((b1 == false) && (b2 == false)) { // neither of the parts is a number
				// Perform a lexicographic comparison.
				int c = s1.compareTo(s2);
				if (c != 0) {
					return c;
				}
			} else { // one part is a number, the other is not
				// Number trumps non-number.
				return Boolean.compare(b1, b2);
			}
		}
		
		// No pairwise difference found. The two inputs are considered equal.
		return 0;
	}
	
	private String[] splitAtDigitBoundaries(String str) {
		Matcher matcher = Pattern.compile("\\d+").matcher(str);
		List<String> parts = new ArrayList<String>();
		int pos = 0;
		while (matcher.find()) {
			if (matcher.start() > 0) {
				parts.add(str.substring(pos, matcher.start()));
			}
			parts.add(matcher.group());
			pos = matcher.end();
		}
		if (pos < str.length()) {
			parts.add(str.substring(pos, str.length()));
		}
		return parts.toArray(new String[parts.size()]);
	}
	
	private boolean beginsWithDigit(String str) {
		return str.substring(0, 1).matches("\\d");
	}
	
	private String stripLeadingZeros(String str) {
		int i = 0;
		while ((i < str.length()) && (str.charAt(i) == '0')) {
			i++;
		}
		return str.substring(i, str.length());
	}
}
