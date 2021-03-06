/* Licensed to Aduna under one or more contributor license agreements.  
 * See the NOTICE.txt file distributed with this work for additional 
 * information regarding copyright ownership. Aduna licenses this file
 * to you under the terms of the Aduna BSD License (the "License"); 
 * you may not use this file except in compliance with the License. See 
 * the LICENSE.txt file distributed with this work for the full License.
 *
 * Unless required by applicable law or agreed to in writing,software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.  See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.openrdf.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.util.iterators.Iterators;

/**
 * Utility functions for working with {@link Model}s and other {@link Statement}
 * collections.
 * 
 * @author Jeen Broekstra
 * @author Arjohn Kampman
 * @since 2.8.0
 * @see org.openrdf.model.Model
 */
public class Models {

	/*
	 * hidden constructor to avoid instantiation
	 */
	protected Models() {
	}

	/**
	 * Retrieves an object {@link Value} from the statements in the given model.
	 * If more than one possible object value exists, any one value is picked and
	 * returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object value.
	 * @return an object value from the given model, or {@link Optional#empty()}
	 *         if no such value exists.
	 * @since 4.0
	 */
	public static Optional<Value> object(Model m) {
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			return Optional.of(objects.iterator().next());
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #object(Model)} instead.
	 */
	@Deprecated
	public static Value anyObject(Model m) {
		return object(m).orElse(null);
	}

	/**
	 * Retrieves an object {@link Literal} value from the statements in the given
	 * model. If more than one possible Literal value exists, any one Literal
	 * value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object Literal value.
	 * @return an object Literal value from the given model, or
	 *         {@link Optional#empty()} if no such value exists.
	 * @since 4.0
	 */
	public static Optional<Literal> objectLiteral(Model m) {
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof Literal) {
					return Optional.of((Literal)v);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #objectLiteral(Model)} instead.
	 */
	@Deprecated
	public static Literal anyObjectLiteral(Model m) {
		return objectLiteral(m).orElse(null);
	}

	/**
	 * Retrieves an object {@link Resource} value from the statements in the
	 * given model. If more than one possible Resource value exists, any one
	 * Resource value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object Resource value.
	 * @return an {@link Optional} object Resource value from the given model,
	 *         which will be {@link Optional#empty() empty} if no such value
	 *         exists.
	 * @since 4.0
	 */
	public static Optional<Resource> objectResource(Model m) {
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof Resource) {
					return Optional.of((Resource)v);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #objectResource(Model)} instead.
	 */
	@Deprecated
	public static Resource anyObjectResource(Model m) {
		return objectResource(m).orElse(null);
	}

	/**
	 * Retrieves an object {@link URI} value from the statements in the given
	 * model. If more than one possible URI value exists, any one value is picked
	 * and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve an object IRI value.
	 * @return an {@link Optional} object URI value from the given model, which
	 *         will be {@link Optional#empty() empty} if no such value exists.
	 * @since 4.0
	 */
	public static Optional<URI> objectURI(Model m) {
		final Set<Value> objects = m.objects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof IRI) {
					return Optional.of((URI)v);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #objectURI(Model)} instead.
	 */
	@Deprecated
	public static URI anyObjectURI(Model m) {
		return objectURI(m).orElse(null);
	}

	/**
	 * Retrieves a subject {@link Resource} from the statements in the given
	 * model. If more than one possible resource value exists, any one resource
	 * value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a subject Resource.
	 * @return an {@link Optional} subject resource from the given model, which
	 *         will be {@link Optional#empty() empty} if no such value exists.
	 * @since 4.0
	 */
	public static Optional<Resource> subject(Model m) {
		final Set<Resource> subjects = m.subjects();
		if (subjects != null && !subjects.isEmpty()) {
			return Optional.of(subjects.iterator().next());
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #subject(Model)} instead.
	 */
	@Deprecated
	public static Resource anySubject(Model m) {
		return subject(m).orElse(null);
	}

	/**
	 * Retrieves a subject {@link URI} from the statements in the given model. If
	 * more than one possible URI value exists, any one URI value is picked and
	 * returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a subject IRI value.
	 * @return an {@link Optional} subject URI value from the given model, which
	 *         will be {@link Optional#empty() empty} if no such value exists.
	 */
	public static Optional<URI> subjectURI(Model m) {
		final Set<Resource> objects = m.subjects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof IRI) {
					return Optional.of((URI)v);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #subjectURI(Model)} instead.
	 */
	@Deprecated
	public static URI anySubjectURI(Model m) {
		return subjectURI(m).orElse(null);
	}

	/**
	 * Retrieves a subject {@link BNode} from the statements in the given model.
	 * If more than one possible blank node value exists, any one blank node
	 * value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a subject BNode value.
	 * @return an {@link Optional} subject BNode value from the given model,
	 *         which will be {@link Optional#empty() empty} if no such value
	 *         exists.
	 * @since 4.0
	 */
	public static Optional<BNode> subjectBNode(Model m) {
		final Set<Resource> objects = m.subjects();
		if (objects != null && !objects.isEmpty()) {
			for (Value v : objects) {
				if (v instanceof BNode) {
					return Optional.of((BNode)v);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #subjectBNode(Model)} instead.
	 */
	@Deprecated
	public static BNode anySubjectBNode(Model m) {
		return subjectBNode(m).orElse(null);
	}

	/**
	 * Retrieves a predicate from the statements in the given model. If more than
	 * one possible predicate value exists, any one value is picked and returned.
	 * 
	 * @param m
	 *        the model from which to retrieve a predicate value.
	 * @return an {@link Optional} predicate value from the given model, which
	 *         will be {@link Optional#empty() empty} if no such value exists.
	 * @since 4.0
	 */
	public static Optional<IRI> predicate(Model m) {
		final Set<IRI> predicates = m.predicates();
		if (predicates != null && !predicates.isEmpty()) {
			return Optional.of(predicates.iterator().next());
		}
		return Optional.empty();
	}

	/**
	 * @deprecated since 4.0. Use {@link #predicate(Model)} instead.
	 */
	@Deprecated
	public static URI anyPredicate(Model m) {
		return predicate(m).orElse(null);
	}

	/**
	 * Sets the property value for the given subject to the given object value,
	 * replacing any existing value(s) for the subject's property. This method
	 * updates the original input Model and then returns that same Model object.
	 * 
	 * @param m
	 *        the model in which to set the property value. May not be null.
	 * @param subject
	 *        the subject for which to set/replace the property value. May not be
	 *        null.
	 * @param property
	 *        the property for which to set/replace the value. May not be null.
	 * @param value
	 *        the value to set for the given subject and property. May not be
	 *        null.
	 * @param contexts
	 *        the context(s) in which to set/replace the property value. Optional
	 *        vararg argument. If not specified the operations works on the
	 *        entire Model.
	 * @return the Model object, containing the updated property value.
	 * @since 2.8.0
	 */
	public static Model setProperty(Model m, Resource subject, IRI property, Value value, Resource... contexts)
	{
		Objects.requireNonNull(m, "model may not be null");
		Objects.requireNonNull(subject, "subject may not be null");
		Objects.requireNonNull(property, "property may not be null");
		Objects.requireNonNull(value, "value may not be null");

		if (m.contains(subject, property, null, contexts)) {
			m.remove(subject, property, null, contexts);
		}
		m.add(subject, property, value, contexts);
		return m;
	}

	/**
	 * Compares two RDF models, and returns <tt>true</tt> if they contain
	 * isomorphic graphs. RDF models are isomorphic graphs if statements from one
	 * model can be mapped 1:1 on to statements in the other model. In this
	 * mapping, blank nodes are not considered mapped when having an identical
	 * internal id, but are mapped from one model to the other by looking at the
	 * statements in which the blank nodes occur.
	 * 
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#graph-isomorphism">RDF
	 *      Concepts &amp; Abstract Syntax, section 3.6 (Graph Comparison)</a>
	 * @since 2.8.0
	 */
	public static boolean isomorphic(Iterable<? extends Statement> model1, Iterable<? extends Statement> model2)
	{
		Set<? extends Statement> set1 = toSet(model1);
		Set<? extends Statement> set2 = toSet(model2);
		// Compare the number of statements in both sets
		if (set1.size() != set2.size()) {
			return false;
		}

		return isSubsetInternal(set1, set2);
	}

	/**
	 * Compares two RDF models, defined by two statement collections, and returns
	 * <tt>true</tt> if they are equal. Models are equal if they contain the same
	 * set of statements. Blank node IDs are not relevant for model equality,
	 * they are mapped from one model to the other by using the attached
	 * properties.
	 * 
	 * @deprecated since 2.8.0. Use {@link Models#isomorphic(Iterable, Iterable)}
	 *             instead.
	 */
	@Deprecated
	public static boolean equals(Iterable<? extends Statement> model1, Iterable<? extends Statement> model2) {
		return isomorphic(model1, model2);
	}

	/**
	 * Compares two RDF models, and returns <tt>true</tt> if the first model is a
	 * subset of the second model, using graph isomorphism to map statements
	 * between models.
	 */
	public static boolean isSubset(Iterable<? extends Statement> model1, Iterable<? extends Statement> model2)
	{
		// Filter duplicates
		Set<? extends Statement> set1 = toSet(model1);
		Set<? extends Statement> set2 = toSet(model2);

		return isSubset(set1, set2);
	}

	/**
	 * Compares two RDF models, and returns <tt>true</tt> if the first model is a
	 * subset of the second model, using graph isomorphism to map statements
	 * between models.
	 */
	public static boolean isSubset(Set<? extends Statement> model1, Set<? extends Statement> model2) {
		// Compare the number of statements in both sets
		if (model1.size() > model2.size()) {
			return false;
		}

		return isSubsetInternal(model1, model2);
	}

	private static boolean isSubsetInternal(Set<? extends Statement> model1, Set<? extends Statement> model2) {
		// try to create a full blank node mapping
		return matchModels(model1, model2);
	}

	private static boolean matchModels(Set<? extends Statement> model1, Set<? extends Statement> model2) {
		// Compare statements without blank nodes first, save the rest for later
		List<Statement> model1BNodes = new ArrayList<Statement>(model1.size());

		for (Statement st : model1) {
			if (st.getSubject() instanceof BNode || st.getObject() instanceof BNode) {
				model1BNodes.add(st);
			}
			else {
				if (!model2.contains(st)) {
					return false;
				}
			}
		}

		return matchModels(model1BNodes, model2, new HashMap<BNode, BNode>(), 0);
	}

	/**
	 * A recursive method for finding a complete mapping between blank nodes in
	 * model1 and blank nodes in model2. The algorithm does a depth-first search
	 * trying to establish a mapping for each blank node occurring in model1.
	 * 
	 * @param model1
	 * @param model2
	 * @param bNodeMapping
	 * @param idx
	 * @return true if a complete mapping has been found, false otherwise.
	 */
	private static boolean matchModels(List<? extends Statement> model1, Iterable<? extends Statement> model2,
			Map<BNode, BNode> bNodeMapping, int idx)
	{
		boolean result = false;

		if (idx < model1.size()) {
			Statement st1 = model1.get(idx);

			List<Statement> matchingStats = findMatchingStatements(st1, model2, bNodeMapping);

			for (Statement st2 : matchingStats) {
				// Map bNodes in st1 to bNodes in st2
				Map<BNode, BNode> newBNodeMapping = new HashMap<BNode, BNode>(bNodeMapping);

				if (st1.getSubject() instanceof BNode && st2.getSubject() instanceof BNode) {
					newBNodeMapping.put((BNode)st1.getSubject(), (BNode)st2.getSubject());
				}

				if (st1.getObject() instanceof BNode && st2.getObject() instanceof BNode) {
					newBNodeMapping.put((BNode)st1.getObject(), (BNode)st2.getObject());
				}

				// FIXME: this recursive implementation has a high risk of
				// triggering a stack overflow

				// Enter recursion
				result = matchModels(model1, model2, newBNodeMapping, idx + 1);

				if (result == true) {
					// models match, look no further
					break;
				}
			}
		}
		else {
			// All statements have been mapped successfully
			result = true;
		}

		return result;
	}

	private static List<Statement> findMatchingStatements(Statement st, Iterable<? extends Statement> model,
			Map<BNode, BNode> bNodeMapping)
	{
		List<Statement> result = new ArrayList<Statement>();

		for (Statement modelSt : model) {
			if (statementsMatch(st, modelSt, bNodeMapping)) {
				// All components possibly match
				result.add(modelSt);
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

		if (subj1 instanceof BNode && subj2 instanceof BNode) {
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
		else {
			// subjects are not (both) bNodes
			if (!subj1.equals(subj2)) {
				return false;
			}
		}

		Value obj1 = st1.getObject();
		Value obj2 = st2.getObject();

		if (obj1 instanceof BNode && obj2 instanceof BNode) {
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
		else {
			// objects are not (both) bNodes
			if (!obj1.equals(obj2)) {
				return false;
			}
		}

		return true;
	}

	private static <S extends Statement> Set<S> toSet(Iterable<S> iterable) {
		Set<S> set = null;
		if (iterable instanceof Set) {
			set = (Set<S>)iterable;
		}
		else {
			// Filter duplicates
			set = new HashSet<S>();
			Iterators.addAll(iterable.iterator(), set);
		}
		return set;
	}
}