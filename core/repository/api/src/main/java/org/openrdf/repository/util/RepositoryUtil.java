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
package org.openrdf.repository.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import info.aduna.iteration.Iterations;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.util.Models;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Utility methods for comparing sets of statements (graphs) with each other.
 * The supplied comparison operations map bnodes in the two supplied models on
 * to each other and thus define a graph isomorphism.
 * 
 * @author jeen
 * @author Arjohn Kampman
 */
public class RepositoryUtil {

	/**
	 * Compares the models in the default contexts of the two supplied
	 * repositories and returns true if they are equal. Models are equal if they
	 * contain the same set of statements. bNodes IDs are not relevant for model
	 * equality, they are mapped from one model to the other by using the
	 * attached properties. Note that the method pulls the entire default context
	 * of both repositories into main memory. Use with caution.
	 */
	public static boolean equals(Repository rep1, Repository rep2)
		throws RepositoryException
	{
		// Fetch statements from rep1 and rep2
		Set<Statement> model1, model2;

		RepositoryConnection con1 = rep1.getConnection();
		try {
			model1 = Iterations.asSet(con1.getStatements(null, null, null, true));
		}
		finally {
			con1.close();
		}

		RepositoryConnection con2 = rep2.getConnection();
		try {
			model2 = Iterations.asSet(con2.getStatements(null, null, null, true));
		}
		finally {
			con2.close();
		}

		return Models.isomorphic(model1, model2);
	}

	/**
	 * Compares the models of the default context of two repositories and returns
	 * true if rep1 is a subset of rep2. Note that the method pulls the entire
	 * default context of both repositories into main memory. Use with caution.
	 */
	public static boolean isSubset(Repository rep1, Repository rep2)
		throws RepositoryException
	{
		Set<Statement> model1, model2;

		RepositoryConnection con1 = rep1.getConnection();
		try {
			model1 = Iterations.asSet(con1.getStatements(null, null, null, true));
		}
		finally {
			con1.close();
		}

		RepositoryConnection con2 = rep2.getConnection();
		try {
			model2 = Iterations.asSet(con2.getStatements(null, null, null, true));
		}
		finally {
			con2.close();
		}

		return Models.isSubset(model1, model2);
	}

	/**
	 * Compares two models defined by the default context of two repositories and
	 * returns the difference between the first and the second model (that is,
	 * all statements that are present in rep1 but not in rep2). Blank node IDs
	 * are not relevant for model equality, they are mapped from one model to the
	 * other by using the attached properties. Note that the method pulls the
	 * entire default context of both repositories into main memory. Use with
	 * caution.
	 * <p>
	 * <b>NOTE: this algorithm is currently broken; it doesn't actually map blank
	 * nodes between the two models.</b>
	 * 
	 * @return The collection of statements that is the difference between rep1
	 *         and rep2.
	 */
	public static Collection<? extends Statement> difference(Repository rep1, Repository rep2)
		throws RepositoryException
	{
		Collection<Statement> model1 = new HashSet<Statement>();
		Collection<Statement> model2 = new HashSet<Statement>();

		RepositoryConnection con1 = rep1.getConnection();
		try {
			Iterations.addAll(con1.getStatements(null, null, null, false), model1);
		}
		finally {
			con1.close();
		}

		RepositoryConnection con2 = rep2.getConnection();
		try {
			Iterations.addAll(con2.getStatements(null, null, null, false), model2);
		}
		finally {
			con2.close();
		}

		return difference(model1, model2);
	}

	/**
	 * Compares two models, defined by two statement collections, and returns the
	 * difference between the first and the second model (that is, all statements
	 * that are present in model1 but not in model2). Blank node IDs are not
	 * relevant for model equality, they are mapped from one model to the other
	 * by using the attached properties. *
	 * <p>
	 * <b>NOTE: this algorithm is currently broken; it doesn't actually map blank
	 * nodes between the two models.</b>
	 * 
	 * @return The collection of statements that is the difference between model1
	 *         and model2.
	 */
	public static Collection<? extends Statement> difference(Collection<? extends Statement> model1,
			Collection<? extends Statement> model2)
	{
		// Create working copies
		LinkedList<Statement> copy1 = new LinkedList<Statement>(model1);
		LinkedList<Statement> copy2 = new LinkedList<Statement>(model2);

		Collection<Statement> result = new ArrayList<Statement>();

		// Compare statements that don't contain bNodes
		Iterator<Statement> iter1 = copy1.iterator();
		while (iter1.hasNext()) {
			Statement st = iter1.next();

			if (st.getSubject() instanceof BNode || st.getObject() instanceof BNode) {
				// One or more of the statement's components is a bNode,
				// these statements are handled later
				continue;
			}

			// Try to remove the statement from model2
			boolean removed = copy2.remove(st);
			if (!removed) {
				// statement was not present in model2 and is part of the difference
				result.add(st);
			}
			iter1.remove();
		}

		// FIXME: this algorithm is broken: bNodeMapping is assumed to contain a
		// bnode mapping while in reallity it is an empty map

		HashMap<BNode, BNode> bNodeMapping = new HashMap<BNode, BNode>();
		// mapBlankNodes(copy1, copy2, bNodeMapping, 0);

		for (Statement st1 : copy1) {
			boolean foundMatch = false;

			for (Statement st2 : copy2) {
				if (statementsMatch(st1, st2, bNodeMapping)) {
					// Found a matching statement
					foundMatch = true;
					break;
				}
			}

			if (!foundMatch) {
				// No statement matching st1 was found in model2, st1 is part of
				// the difference.
				result.add(st1);
			}
		}

		return result;
	}

	private static boolean statementsMatch(Statement st1, Statement st2, Map<BNode, BNode> bNodeMapping) {
		IRI pred1 = st1.getPredicate();
		IRI pred2 = st2.getPredicate();

		if (!pred1.equals(pred2)) {
			// predicates don't match
			return false;
		}

		Resource subj1 = st1.getSubject();
		Resource subj2 = st2.getSubject();

		if (!(subj1 instanceof BNode)) {
			if (!subj1.equals(subj2)) {
				// subjects are not bNodes and don't match
				return false;
			}
		}
		else { // subj1 instanceof BNode
			BNode mappedBNode = bNodeMapping.get(subj1);

			if (mappedBNode != null) {
				// bNode 'subj1' was already mapped to some other bNode
				if (!subj2.equals(mappedBNode)) {
					// 'subj1' and 'subj2' do not match
					return false;
				}
			}
			else {
				// 'subj1' was not yet mapped. we need to check if 'subj2' is a
				// possible mapping candidate
				if (bNodeMapping.containsValue(subj2)) {
					// 'subj2' is already mapped to some other value.
					return false;
				}
			}
		}

		Value obj1 = st1.getObject();
		Value obj2 = st2.getObject();

		if (!(obj1 instanceof BNode)) {
			if (!obj1.equals(obj2)) {
				// objects are not bNodes and don't match
				return false;
			}
		}
		else { // obj1 instanceof BNode
			BNode mappedBNode = bNodeMapping.get(obj1);

			if (mappedBNode != null) {
				// bNode 'obj1' was already mapped to some other bNode
				if (!obj2.equals(mappedBNode)) {
					// 'obj1' and 'obj2' do not match
					return false;
				}
			}
			else {
				// 'obj1' was not yet mapped. we need to check if 'obj2' is a
				// possible mapping candidate
				if (bNodeMapping.containsValue(obj2)) {
					// 'obj2' is already mapped to some other value.
					return false;
				}
			}
		}

		return true;
	}
}
