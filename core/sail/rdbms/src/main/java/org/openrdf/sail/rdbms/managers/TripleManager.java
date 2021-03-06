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
package org.openrdf.sail.rdbms.managers;

import java.sql.SQLException;

import org.openrdf.sail.rdbms.managers.base.ManagerBase;

/**
 * 
 * @author James Leigh
 */
public class TripleManager extends ManagerBase {

	public static TripleManager instance;

	private TransTableManager statements;

	public TripleManager() {
		instance = this;
	}

	public void setTransTableManager(TransTableManager statements) {
		this.statements = statements;
	}

	@Override
	public void close()
		throws SQLException
	{
		super.close();
		statements.close();
	}

	public void insert(Number ctx, Number subj, Number pred, Number obj)
		throws SQLException, InterruptedException
	{
		statements.insert(ctx, subj, pred, obj);
	}

}
