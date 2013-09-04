/* 
 * Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. 
 *
 * Aduna licenses this file to you under the terms of the Aduna BSD 
 * License (the "License"); you may not use this file except in compliance 
 * with the License. See the LICENSE.txt file distributed with this work 
 * for the full License.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.openrdf.sail.nativerdf;

import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import info.aduna.io.FileUtil;

import org.openrdf.query.QueryLanguage;
import org.openrdf.sail.CustomGraphQueryInferencerTest;
import org.openrdf.sail.NotifyingSail;

public class NativeStoreCustomInferencingTest extends CustomGraphQueryInferencerTest {

	private File dataDir;

	public NativeStoreCustomInferencingTest(String resourceFolder, Expectation testData, QueryLanguage language)
	{
		super(resourceFolder, testData, language);
	}

	@Before
	public void setUp()
		throws IOException
	{
		dataDir = FileUtil.createTempDir("nativestore");
		assumeNotNull("could not create datadir", dataDir);
	}

	@After
	public void tearDown()
		throws IOException
	{
		if (dataDir != null)
			FileUtil.deleteDir(dataDir);
	}

	@Override
	protected NotifyingSail newSail() {
		return new NativeStore(dataDir, "spoc,posc");
	}
}
