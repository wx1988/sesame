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
package org.openrdf.http.protocol;

import org.openrdf.OpenRDFUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

public abstract class Protocol {

	/**
	 * Defines the action a particular transaction update is executing.  
	 *
	 * @author Jeen Broekstra
	 */
	public static enum Action {
		/** adding data */
		ADD,
		/** deleting data */
		DELETE,
		/** getStatements or exportStatements */
		GET,
		/** retrieving repository size */
		SIZE,
		/** SPARQL or SeRQL query */
		QUERY,
		/** SPARQL Update */
		UPDATE,
		/** commit */
		COMMIT,
		/** rollback */
		ROLLBACK;
	}

	/**
	 * Protocol version.
	 */
	public static final String VERSION = "7";

	/**
	 * Parameter name for the 'subject' parameter of a statement query.
	 */
	public static final String SUBJECT_PARAM_NAME = "subj";

	/**
	 * Parameter name for the 'predicate' parameter of a statement query.
	 */
	public static final String PREDICATE_PARAM_NAME = "pred";

	/**
	 * Parameter name for the 'object' parameter of statement query.
	 */
	public static final String OBJECT_PARAM_NAME = "obj";

	/**
	 * Parameter name for the 'includeInferred' parameter.
	 */
	public static final String INCLUDE_INFERRED_PARAM_NAME = "infer";

	/**
	 * Parameter name for the context parameter.
	 */
	public static final String CONTEXT_PARAM_NAME = "context";

	/**
	 * Parameter value for the NULL context.
	 */
	public static final String NULL_PARAM_VALUE = "null";

	/**
	 * Parameter name for the graph parameter.
	 */
	public static final String GRAPH_PARAM_NAME = "graph";

	/**
	 * Parameter name for the update parameter.
	 */
	public static final String UPDATE_PARAM_NAME = "update";

	/**
	 * Parameter name for the base-URI parameter.
	 */
	public static final String BASEURI_PARAM_NAME = "baseURI";

	/**
	 * Parameter name for the query parameter.
	 */
	public static final String QUERY_PARAM_NAME = "query";

	/**
	 * Parameter name for the query language parameter.
	 */
	public static final String QUERY_LANGUAGE_PARAM_NAME = "queryLn";

	public static final String TIMEOUT_PARAM_NAME = "timeout";

	/**
	 * Parameter name for the default remove graph URI parameter.
	 */
	public static final String REMOVE_GRAPH_PARAM_NAME = "remove-graph-uri";

	/**
	 * Parameter name for the default insert graph URI parameter.
	 */
	public static final String INSERT_GRAPH_PARAM_NAME = "insert-graph-uri";

	/**
	 * Parameter name for the default graph URI parameter for update.
	 */
	public static final String USING_GRAPH_PARAM_NAME = "using-graph-uri";

	/**
	 * Parameter name for the named graph URI parameter for update.
	 */
	public static final String USING_NAMED_GRAPH_PARAM_NAME = "using-named-graph-uri";

	/**
	 * Parameter name for the default graph URI parameter.
	 */
	public static final String DEFAULT_GRAPH_PARAM_NAME = "default-graph-uri";

	/**
	 * Parameter name for the named graph URI parameter.
	 */
	public static final String NAMED_GRAPH_PARAM_NAME = "named-graph-uri";

	/**
	 * Parameter name for the Accept parameter (may also be used as the name of
	 * the Accept HTTP header).
	 */
	public static final String ACCEPT_PARAM_NAME = "Accept";

	/**
	 * Parameter name for the isolation level used in transactions.
	 */
	public static final String ISOLATION_LEVEL_PARAM_NAME = "isolation-level";
	
	/**
	 * Parameter name for the action parameter used in transactions.
	 */
	public static final String ACTION_PARAM_NAME = "action";

	/**
	 * Relative location of the protocol resource.
	 */
	public static final String PROTOCOL = "protocol";

	/**
	 * Relative location of the config resource.
	 */
	public static final String CONFIG = "config";

	/**
	 * Relative location of the repository list resource.
	 */
	public static final String REPOSITORIES = "repositories";

	/**
	 * Relative location of the statement list resource of a repository.
	 */
	public static final String STATEMENTS = "statements";

	/**
	 * Relative location of the transaction resources of a repository.
	 */
	public static final String TRANSACTIONS = "transactions";

	/**
	 * Relative location of the context list resource of a repository.
	 */
	public static final String CONTEXTS = "contexts";

	/**
	 * Relative location of the namespaces list resource of a repository.
	 */
	public static final String NAMESPACES = "namespaces";

	/**
	 * Parameter prefix for query-external variable bindings.
	 */
	public static final String BINDING_PREFIX = "$";

	/**
	 * Relative location of the 'size' resource of a repository.
	 */
	public static final String SIZE = "size";

	/**
	 * MIME type for transactions: <tt>application/x-rdftransaction</tt>.
	 */
	public static final String TXN_MIME_TYPE = "application/x-rdftransaction";

	/**
	 * MIME type for www forms: <tt>application/x-www-form-urlencoded</tt>.
	 */
	public static final String FORM_MIME_TYPE = "application/x-www-form-urlencoded";

	/**
	 * Parameter for server instruction to preserve blank node ids when parsing
	 * request data.
	 * 
	 * @since 2.8.0
	 */
	public static final String PRESERVE_BNODE_ID_PARAM_NAME = "preserveNodeId";

	private static String getServerDir(String serverLocation) {
		if (serverLocation.endsWith("/")) {
			return serverLocation;
		}
		else {
			return serverLocation + "/";
		}
	}

	/**
	 * Get the location of the protocol resource on the specified server.
	 * 
	 * @param serverLocation
	 *        the base location of a server implementing this REST protocol.
	 * @return the location of the protocol resource on the specified server
	 */
	public static final String getProtocolLocation(String serverLocation) {
		return getServerDir(serverLocation) + PROTOCOL;
	}

	/**
	 * Get the location of the server configuration resource on the specified
	 * server.
	 * 
	 * @param serverLocation
	 *        the base location of a server implementing this REST protocol.
	 * @return the location of the server configuration resource on the specified
	 *         server
	 */
	public static final String getConfigLocation(String serverLocation) {
		return getServerDir(serverLocation) + CONFIG;
	}

	/**
	 * Get the location of the repository list resource on the specified server.
	 * 
	 * @param serverLocation
	 *        the base location of a server implementing this REST protocol.
	 * @return the location of the repository list resource on the specified
	 *         server
	 */
	public static final String getRepositoriesLocation(String serverLocation) {
		return getServerDir(serverLocation) + REPOSITORIES;
	}

	/**
	 * Get the location of a specific repository resource on the specified
	 * server.
	 * 
	 * @param serverLocation
	 *        the base location of a server implementing this REST protocol.
	 * @param repositoryID
	 *        the ID of the repository
	 * @return the location of a specific repository resource on the specified
	 *         server
	 */
	public static final String getRepositoryLocation(String serverLocation, String repositoryID) {
		return getRepositoriesLocation(serverLocation) + "/" + repositoryID;
	}

	/**
	 * Get the location of the statements resource for a specific repository.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @return the location of the statements resource for the specified
	 *         repository
	 */
	public static final String getStatementsLocation(String repositoryLocation) {
		return repositoryLocation + "/" + STATEMENTS;
	}

	/**
	 * Get the location of the transaction resources for a specific repository.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @return the location of the transaction resources for the specified
	 *         repository
	 * @since 2.8.0
	 */
	public static final String getTransactionsLocation(String repositoryLocation) {
		return repositoryLocation + "/" + TRANSACTIONS;
	}

	/**
	 * Extracts the server location from the repository location.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @return the location of the server resource for the specified repository.
	 */
	public static final String getServerLocation(String repositoryLocation) {
		String serverLocation = repositoryLocation.substring(0, repositoryLocation.lastIndexOf('/'));
		serverLocation = serverLocation.substring(0, serverLocation.lastIndexOf('/'));
		return serverLocation;
	}

	/**
	 * Extracts the repository ID from the repository location.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @return the ID of the repository.
	 */
	public static final String getRepositoryID(String repositoryLocation) {
		String repositoryID = repositoryLocation.substring(repositoryLocation.lastIndexOf('/') + 1);
		return repositoryID;
	}

	/**
	 * Get the location of the contexts lists resource for a specific repository.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @return the location of the contexts lists resource for the specified
	 *         repository
	 */
	public static final String getContextsLocation(String repositoryLocation) {
		return repositoryLocation + "/" + CONTEXTS;
	}

	/**
	 * Get the location of the namespaces lists resource for a specific
	 * repository on the specified server.
	 * 
	 * @param repositoryLocation
	 *        the base location of a server implementing this REST protocol.
	 * @return the location of the namespaces lists resource for a specific
	 *         repository on the specified server
	 */
	public static final String getNamespacesLocation(String repositoryLocation) {
		return repositoryLocation + "/" + NAMESPACES;
	}

	/**
	 * Get the location of the namespace with the specified prefix for a specific
	 * repository on the specified server.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @param prefix
	 *        the namespace prefix
	 * @return the location of the the namespace with the specified prefix for a
	 *         specific repository on the specified server
	 */
	public static final String getNamespacePrefixLocation(String repositoryLocation, String prefix) {
		return getNamespacesLocation(repositoryLocation) + "/" + prefix;
	}

	/**
	 * Get the location of the 'size' resource for a specific repository on the
	 * specified server.
	 * 
	 * @param repositoryLocation
	 *        the location of a repository implementing this REST protocol.
	 * @return the location of the 'size' resource for a specific repository on
	 *         the specified server
	 */
	public static final String getSizeLocation(String repositoryLocation) {
		return repositoryLocation + "/" + SIZE;
	}

	/**
	 * Encodes a value in a canonical serialized string format, for use in a URL query parameter.
	 * 
	 * @param value
	 *        The value to encode, possibly <tt>null</tt>.
	 * @return The protocol-serialized representation of the supplied value, or
	 *         {@link #NULL_PARAM_VALUE} if the supplied value was <tt>null</tt>.
	 */
	public static String encodeValue(Value value) {
		if (value instanceof BNode) {
			// SES-2129 special treatment of blank node names to avoid problems with round-tripping.
			return "_:" + ((BNode)value).getID();
		}
		
		// for everything else we just use N-Triples serialization.
		return NTriplesUtil.toNTriplesString(value);
	}

	/**
	 * Decode a previously encoded value.
	 * 
	 * @param encodedValue
	 *        the encoded value
	 * @param valueFactory
	 *        the factory to use for constructing the Value
	 * @return the decoded Value
	 * @see #encodeValue(Value)
	 */
	public static Value decodeValue(String encodedValue, ValueFactory valueFactory) {
		if (encodedValue != null) {
			return NTriplesUtil.parseValue(encodedValue, valueFactory);
		}

		return null;
	}

	/**
	 * Decode a previously encoded Resource.
	 * 
	 * @param encodedValue
	 *        the encoded value
	 * @param valueFactory
	 *        the factory to use for constructing the Resource
	 * @return the decoded Resource
	 * @see #encodeValue(Value)
	 */
	public static Resource decodeResource(String encodedValue, ValueFactory valueFactory) {
		if (encodedValue != null) {
			return NTriplesUtil.parseResource(encodedValue, valueFactory);
		}

		return null;
	}

	/**
	 * Decode a previously encoded URI.
	 * 
	 * @param encodedValue
	 *        the encoded value
	 * @param valueFactory
	 *        the factory to use for constructing the URI
	 * @return the decoded URI
	 * @see #encodeValue(Value)
	 */
	public static IRI decodeURI(String encodedValue, ValueFactory valueFactory) {
		if (encodedValue != null) {
			return NTriplesUtil.parseURI(encodedValue, valueFactory);
		}

		return null;
	}

	/**
	 * Encodes a context resource for use in a URL.
	 * 
	 * @param context
	 *        The context to encode, possibly <tt>null</tt>.
	 * @return The protocol-serialized representation of the supplied context, or
	 *         {@link #NULL_PARAM_VALUE} if the supplied value was <tt>null</tt>.
	 */
	public static String encodeContext(Resource context) {
		if (context == null) {
			return Protocol.NULL_PARAM_VALUE;
		}
		else {
			return encodeValue(context);
		}
	}

	/**
	 * Decode a previously encoded context Resource.
	 * 
	 * @param encodedValue
	 *        the encoded value
	 * @param valueFactory
	 *        the factory to use for constructing the Resource
	 * @return the decoded Resource, or null if the encoded values was null or
	 *         equal to {@link #NULL_PARAM_VALUE}
	 */
	public static Resource decodeContext(String encodedValue, ValueFactory valueFactory) {
		if (encodedValue == null) {
			return null;
		}
		else if (NULL_PARAM_VALUE.equals(encodedValue)) {
			return null;
		}
		else {
			return decodeResource(encodedValue, valueFactory);
		}
	}

	/**
	 * Encode context resources for use in a URL.
	 * 
	 * @param contexts
	 *        the contexts to encode, must not be <tt>null</tt>.
	 * @return the encoded contexts
	 * @throws IllegalArgumentException
	 *         If the <tt>contexts</tt> is <tt>null</tt>.
	 */
	public static String[] encodeContexts(Resource... contexts) {
		OpenRDFUtil.verifyContextNotNull(contexts);

		String[] result = new String[contexts.length];
		for (int index = 0; index < contexts.length; index++) {
			result[index] = encodeContext(contexts[index]);
		}

		return result;
	}

	/**
	 * Decode previously encoded contexts.
	 * 
	 * @param encodedValues
	 *        the encoded values
	 * @param valueFactory
	 *        the factory to use for constructing the Resources
	 * @return the decoded Resources, or an empty array if the supplied
	 *         <tt>encodedValues</tt> was <tt>null</tt>.
	 */
	public static Resource[] decodeContexts(String[] encodedValues, ValueFactory valueFactory) {
		Resource[] result;

		if (encodedValues == null) {
			result = new Resource[0];
		}
		else {
			result = new Resource[encodedValues.length];
			for (int index = 0; index < encodedValues.length; index++) {
				result[index] = decodeContext(encodedValues[index], valueFactory);
			}
		}

		return result;
	}
}