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
package org.openrdf.workbench.base;

import java.io.BufferedWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import info.aduna.iteration.Iterations;

import org.openrdf.model.Namespace;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.workbench.util.TupleResultBuilder;
import org.openrdf.workbench.util.WorkbenchRequest;

public abstract class TupleServlet extends TransformationServlet {
	protected String xsl;
	protected String[] variables;

	public TupleServlet(String xsl, String... variables) {
		super();
		this.xsl = xsl;
		this.variables = variables;
	}

	@Override
	protected void service(WorkbenchRequest req, HttpServletResponse resp,
			String xslPath) throws Exception {
		resp.setContentType("application/xml");
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(resp.getWriter()));
		TupleResultBuilder builder = new TupleResultBuilder(writer);
		if (xsl != null) {
			builder.transform(xslPath, xsl);
		}
		RepositoryConnection con = repository.getConnection();
		try {
			for (Namespace ns : Iterations.asList(con.getNamespaces())) {
				builder.prefix(ns.getPrefix(), ns.getName());
			}
			builder.start(variables);
			builder.link("info");
			this.service(req, resp, builder, con);
			builder.end();
			writer.flush();
		} finally {
			con.close();
		}
	}

	protected void service(WorkbenchRequest req, HttpServletResponse resp,
			TupleResultBuilder builder, RepositoryConnection con) 
					throws Exception {
		service(builder, con);
	}

	protected void service(TupleResultBuilder builder, 
			RepositoryConnection con)
			throws Exception {
	}
}