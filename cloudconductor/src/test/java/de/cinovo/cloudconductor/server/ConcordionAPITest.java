package de.cinovo.cloudconductor.server;

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

import java.io.File;
import java.io.IOException;

import org.concordion.api.ResultSummary;
import org.concordion.api.extension.Extensions;
import org.concordion.ext.TimestampFormatterExtension;
import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.FileTarget;
import org.concordion.internal.extension.FixtureExtensionLoader;
import org.junit.Test;

/**
 * 
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author hoegertn
 * 
 */
@Extensions({TimestampFormatterExtension.class})
public abstract class ConcordionAPITest extends APITest {
	
	/**
	 * run the according Concordion test and write HTML to target folder
	 * 
	 * @throws IOException on file error
	 */
	@Test
	public void runConcordion() throws IOException {
		Object fixture = this;
		ConcordionBuilder concordionBuilder = new ConcordionBuilder().withFixture(fixture);
		concordionBuilder.withTarget(new FileTarget(new File("target/concordion")));
		new FixtureExtensionLoader().addExtensions(fixture, concordionBuilder);
		ResultSummary resultSummary = concordionBuilder.build().process(fixture);
		resultSummary.print(System.out, fixture);
		resultSummary.assertIsSatisfied(fixture);
	}
	
}
