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
package org.openrdf.rio.datatypes;

import org.openrdf.model.Literal;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.LiteralUtilException;
import org.openrdf.rio.DatatypeHandler;

/**
 * An implementation of a datatype handler that can process GeoSPARQL datatypes.
 * 
 * @author Peter Ansell
 * @since 2.7.4
 */
public class GeoSPARQLDatatypeHandler implements DatatypeHandler {

	/**
	 * Default constructor.
	 */
	public GeoSPARQLDatatypeHandler() {
	}

	@Override
	public boolean isRecognizedDatatype(IRI datatypeUri) {
		if (datatypeUri == null) {
			throw new NullPointerException("Datatype URI cannot be null");
		}

		return datatypeUri.stringValue().startsWith("http://www.opengis.net/ont/geosparql#");
	}

	@Override
	public boolean verifyDatatype(String literalValue, IRI datatypeUri)
		throws LiteralUtilException
	{
		if (isRecognizedDatatype(datatypeUri)) {
			if (literalValue == null) {
				throw new NullPointerException("Literal value cannot be null");
			}

			// TODO: Implement verification
			return true;
		}

		throw new LiteralUtilException("Could not verify DBPedia literal");
	}

	@Override
	public Literal normalizeDatatype(String literalValue, IRI datatypeUri, ValueFactory valueFactory)
		throws LiteralUtilException
	{
		if (isRecognizedDatatype(datatypeUri)) {
			if (literalValue == null) {
				throw new NullPointerException("Literal value cannot be null");
			}

			return valueFactory.createLiteral(literalValue, datatypeUri);
		}

		throw new LiteralUtilException("Could not normalise DBPedia literal");
	}

	@Override
	public String getKey() {
		return DatatypeHandler.GEOSPARQL;
	}
}
