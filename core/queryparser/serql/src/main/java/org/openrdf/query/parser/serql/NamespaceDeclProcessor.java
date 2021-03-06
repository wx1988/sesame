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
package org.openrdf.query.parser.serql;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SESAME;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.serql.ast.ASTNamespaceDecl;
import org.openrdf.query.parser.serql.ast.ASTQName;
import org.openrdf.query.parser.serql.ast.ASTQueryContainer;
import org.openrdf.query.parser.serql.ast.ASTURI;
import org.openrdf.query.parser.serql.ast.SyntaxTreeBuilderTreeConstants;
import org.openrdf.query.parser.serql.ast.VisitorException;

/**
 * Processes the namespace declarations in a SeRQL query model.
 * 
 * @author Arjohn Kampman
 */
class NamespaceDeclProcessor extends ASTVisitorBase {

	/**
	 * Processes prefix declarations in queries. This method collects all
	 * prefixes that are declared in the supplied query, verifies that prefixes
	 * are not redefined and replaces any {@link ASTQName} nodes in the query
	 * with equivalent {@link ASTIRI} nodes.
	 * 
	 * @param qc
	 *        The query that needs to be processed.
	 * @return A map containing the prefixes that are declared in the query (key)
	 *         and the namespace they map to (value).
	 * @throws MalformedQueryException
	 *         If the query contains redefined prefixes or qnames that use
	 *         undefined prefixes.
	 */
	public static Map<String, String> process(ASTQueryContainer qc)
		throws MalformedQueryException
	{
		List<ASTNamespaceDecl> nsDeclList = qc.getNamespaceDeclList();

		// Build a prefix --> URI map
		Map<String, String> nsMap = new LinkedHashMap<String, String>();
		for (ASTNamespaceDecl nsDecl : nsDeclList) {
			String prefix = nsDecl.getPrefix();
			String uri = nsDecl.getURI().getValue();

			if (nsMap.containsKey(prefix)) {
				// Prefix already defined
				if (nsMap.get(prefix).equals(uri)) {
					// duplicate, ignore
				}
				else {
					throw new MalformedQueryException("Multiple namespace declarations for prefix '" + prefix
							+ "'");
				}
			}
			else {
				nsMap.put(prefix, uri);
			}
		}

		// Use default namespace prefixes when not defined explicitly
		if (!nsMap.containsKey("rdf")) {
			nsMap.put("rdf", RDF.NAMESPACE);
		}
		if (!nsMap.containsKey("rdfs")) {
			nsMap.put("rdfs", RDFS.NAMESPACE);
		}
		if (!nsMap.containsKey("xsd")) {
			nsMap.put("xsd", XMLSchema.NAMESPACE);
		}
		if (!nsMap.containsKey("owl")) {
			nsMap.put("owl", OWL.NAMESPACE);
		}
		if (!nsMap.containsKey("sesame")) {
			nsMap.put("sesame", SESAME.NAMESPACE);
		}

		// For backwards compatibility:
		Map<String, String> extendedNsMap = new HashMap<String, String>(nsMap);
		if (!extendedNsMap.containsKey("serql")) {
			extendedNsMap.put("serql", SESAME.NAMESPACE);
		}

		// Replace all qnames with URIs
		QNameProcessor visitor = new QNameProcessor(extendedNsMap);
		try {
			qc.jjtAccept(visitor, null);
		}
		catch (VisitorException e) {
			throw new MalformedQueryException(e.getMessage(), e);
		}

		return nsMap;
	}

	private static class QNameProcessor extends ASTVisitorBase {

		private Map<String, String> prefixMap;

		public QNameProcessor(Map<String, String> prefixMap) {
			this.prefixMap = prefixMap;
		}

		@Override
		public Object visit(ASTQName qnameNode, Object data)
			throws VisitorException
		{
			String qname = qnameNode.getValue();

			int colonIdx = qname.indexOf(':');
			assert colonIdx >= 0 : "colonIdx should be >= 0: " + colonIdx;

			String prefix = qname.substring(0, colonIdx);
			String localName = qname.substring(colonIdx + 1);

			String namespace = prefixMap.get(prefix);
			if (namespace == null) {
				throw new VisitorException("QName '" + qname + "' uses an undefined prefix");
			}

			// Replace the qname node with a new IRI node in the parent node
			ASTURI uriNode = new ASTURI(SyntaxTreeBuilderTreeConstants.JJTURI);
			uriNode.setValue(namespace + localName);
			qnameNode.jjtReplaceWith(uriNode);

			return null;
		}
	}
}
