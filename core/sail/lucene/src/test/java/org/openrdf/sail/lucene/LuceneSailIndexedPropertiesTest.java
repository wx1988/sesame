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
package org.openrdf.sail.lucene;

import static org.openrdf.sail.lucene.LuceneSailSchema.MATCHES;
import static org.openrdf.sail.lucene.LuceneSailSchema.PROPERTY;
import static org.openrdf.sail.lucene.LuceneSailSchema.QUERY;
import static org.openrdf.sail.lucene.LuceneSailSchema.SCORE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import junit.framework.TestCase;

import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleLiteral;
import org.openrdf.model.impl.SimpleIRI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class LuceneSailIndexedPropertiesTest extends TestCase {

	protected LuceneSail sail;

	protected Repository repository;

	protected RepositoryConnection connection;

	public static final IRI SUBJECT_1 = new SimpleIRI("urn:subject1");

	public static final IRI SUBJECT_2 = new SimpleIRI("urn:subject2");

	public static final IRI SUBJECT_3 = new SimpleIRI("urn:subject3");

	public static final IRI SUBJECT_4 = new SimpleIRI("urn:subject4");

	public static final IRI SUBJECT_5 = new SimpleIRI("urn:subject5");

	public static final IRI CONTEXT_1 = new SimpleIRI("urn:context1");

	public static final IRI CONTEXT_2 = new SimpleIRI("urn:context2");

	public static final IRI CONTEXT_3 = new SimpleIRI("urn:context3");

	public static final IRI RDFSLABEL = RDFS.LABEL;

	public static final IRI RDFSCOMMENT = RDFS.COMMENT;

	public static final IRI FOAFNAME = new SimpleIRI("http://xmlns.com/foaf/0.1/name");

	public static final IRI FOAFPLAN = new SimpleIRI("http://xmlns.com/foaf/0.1/plan");

	@Override
	public void setUp()
		throws IOException, RepositoryException
	{
		// setup a LuceneSail
		MemoryStore memoryStore = new MemoryStore();
		// enable lock tracking
		info.aduna.concurrent.locks.Properties.setLockTrackingEnabled(true);
		sail = new LuceneSail();
		Properties indexedFields = new Properties();
		indexedFields.setProperty("index.1", RDFSLABEL.toString());
		indexedFields.setProperty("index.2", RDFSCOMMENT.toString());
		indexedFields.setProperty(FOAFNAME.toString(), RDFS.LABEL.toString());
		ByteArrayOutputStream indexedFieldsString = new ByteArrayOutputStream();
		indexedFields.store(indexedFieldsString, "For testing");
		sail.setParameter(LuceneSail.INDEXEDFIELDS, indexedFieldsString.toString());
		sail.setParameter(LuceneSail.LUCENE_RAMDIR_KEY, "true");
		sail.setBaseSail(memoryStore);

		// create a Repository wrapping the LuceneSail
		repository = new SailRepository(sail);
		repository.initialize();

		// add some statements to it
		connection = repository.getConnection();
		connection.setAutoCommit(false);
		connection.add(SUBJECT_1, RDFSLABEL, new SimpleLiteral("the first resource"));
		connection.add(SUBJECT_1, RDFSCOMMENT, new SimpleLiteral(
				"Groucho Marx is going to cut away the first part of the first party of the contract."));
		connection.add(SUBJECT_1, FOAFNAME, new SimpleLiteral("groucho and harpo"));

		connection.add(SUBJECT_2, FOAFNAME, new SimpleLiteral("the second resource"));
		connection.add(SUBJECT_2, RDFSCOMMENT, new SimpleLiteral(
				"in the night at the opera, groucho is in a cabin on a ship."));

		connection.add(SUBJECT_3, RDFSLABEL, new SimpleLiteral("the third resource"));
		connection.add(SUBJECT_3, RDFSCOMMENT, new SimpleLiteral(
				"a not well known fact, groucho marx was not a smoker"));
		connection.add(SUBJECT_3, FOAFPLAN, new SimpleLiteral("groucho did not smoke cigars nor cigarillos")); // this
																																				// should
																																				// not
																																				// be
																																				// indexed
		connection.commit();
	}

	@Override
	public void tearDown()
		throws RepositoryException
	{
		connection.close();
		repository.shutDown();
	}

	public void testTriplesStored()
		throws Exception
	{
		// are the triples stored in the underlying sail?

		assertTrue(connection.hasStatement(SUBJECT_1, RDFSLABEL, new SimpleLiteral("the first resource"), false));
		assertTrue(connection.hasStatement(SUBJECT_1, RDFSCOMMENT, new SimpleLiteral(
				"Groucho Marx is going to cut away the first part of the first party of the contract."), false));
		assertTrue(connection.hasStatement(SUBJECT_1, FOAFNAME, new SimpleLiteral("groucho and harpo"), false));

		assertTrue(connection.hasStatement(SUBJECT_2, FOAFNAME, new SimpleLiteral("the second resource"), false));
		assertTrue(connection.hasStatement(SUBJECT_2, RDFSCOMMENT, new SimpleLiteral(
				"in the night at the opera, groucho is in a cabin on a ship."), false));

		assertTrue(connection.hasStatement(SUBJECT_3, RDFSLABEL, new SimpleLiteral("the third resource"), false));
		assertTrue(connection.hasStatement(SUBJECT_3, RDFSCOMMENT, new SimpleLiteral(
				"a not well known fact, groucho marx was not a smoker"), false));
		assertTrue(connection.hasStatement(SUBJECT_3, FOAFPLAN, new SimpleLiteral(
				"groucho did not smoke cigars nor cigarillos"), false)); // this
																							// should
																							// not be
																							// indexed
	}

	public void testRegularQuery()
		throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		// fire a query for all subjects with a given term
		String queryString = "SELECT Subject, Score " + "FROM {Subject} <" + MATCHES + "> {} " + " <" + QUERY
				+ "> {Query}; " + " <" + PROPERTY + "> {Property}; " + " <" + SCORE + "> {Score} ";
		{
			TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SERQL, queryString);
			query.setBinding("Query", new SimpleLiteral("resource"));
			query.setBinding("Property", RDFSLABEL);
			TupleQueryResult result = query.evaluate();
			// check the results
			ArrayList<IRI> uris = new ArrayList<IRI>();

			BindingSet bindings = null;
			while (result.hasNext()) {
				bindings = result.next();
				uris.add((IRI)bindings.getValue("Subject"));
				assertNotNull(bindings.getValue("Score"));
			}
			result.close();
			assertEquals(3, uris.size());
			assertTrue(uris.contains(SUBJECT_1));
			assertTrue(uris.contains(SUBJECT_2));
			assertTrue(uris.contains(SUBJECT_3));
		}
		{
			TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SERQL, queryString);
			query.setBinding("Query", new SimpleLiteral("groucho"));
			query.setBinding("Property", RDFSLABEL);
			TupleQueryResult result = query.evaluate();
			// check the results
			ArrayList<IRI> uris = new ArrayList<IRI>();

			BindingSet bindings = null;
			while (result.hasNext()) {
				bindings = result.next();
				uris.add((IRI)bindings.getValue("Subject"));
				assertNotNull(bindings.getValue("Score"));
			}
			result.close();
			assertEquals(1, uris.size());
			assertTrue(uris.contains(SUBJECT_1));
		}
		{
			TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SERQL, queryString);
			query.setBinding("Query", new SimpleLiteral("cigarillos"));
			query.setBinding("Property", FOAFPLAN);
			TupleQueryResult result = query.evaluate();
			// check the results
			assertFalse(result.hasNext());
			result.close();
		}
	}

}
