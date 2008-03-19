/*
 * Copyright  2002-2005 WYMIWYG (http://wymiwyg.org)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.wymiwyg.rdf.leanifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;


/**
 * @author reto
 * 
 */
public class GraphLeanifier {

	private static final Log log = LogFactory.getLog(GraphLeanifier.class);
	
	/**
	 * @param unleanifiedModel
	 */
	public static void makeLean(Graph graph) {
		if (log.isDebugEnabled()) {
			log.debug("Leanifying graph with following triples");
			for (Triple triple : graph) {
				log.debug(triple);
			}
		}
		Set<Node> fullyCheckedCandidateImplicators = new HashSet<Node>();
		Set<Node> checkedCandidateImplicatedForCurrentCandidateImplicator = new HashSet<Node>();
		// MultiMap otherChecked = new MultiHashMap();
		Node currentCandidateImplicator = null;
		Set<Node[]> knownNotImplying = new HashSet<Node[]>();
		while (true) {
			if (currentCandidateImplicator == null) {
				currentCandidateImplicator = getNewCandidateImplicator(graph,
						fullyCheckedCandidateImplicators);
			}
			if (currentCandidateImplicator == null) {
				return;
			}

			Node currentCandidateImplicated = geNewCandidateImplicated(
					graph,
					checkedCandidateImplicatedForCurrentCandidateImplicator,
					currentCandidateImplicator);
			if (currentCandidateImplicated == null) {
				fullyCheckedCandidateImplicators
						.add(currentCandidateImplicator);
				currentCandidateImplicator = null;
				checkedCandidateImplicatedForCurrentCandidateImplicator = new HashSet<Node>();
				continue;
			}
			if (!removeNodeifImplied(graph, currentCandidateImplicator,
					currentCandidateImplicated, knownNotImplying)) {
				checkedCandidateImplicatedForCurrentCandidateImplicator
						.add(currentCandidateImplicated);
			}
		}
	}

	/**
	 * @param currentCandidateImplicator
	 * @param currentCandidateImplicated
	 * @param knownNotImplying
	 * @return
	 */
	private static boolean removeNodeifImplied(Graph graph, Node n1,
			Node n2, Set<Node[]> knownNotImplying) {
		List<Node> history1 = new ArrayList<Node>();
		List<Node> history2 = new ArrayList<Node>();
		Set<Node[]> conditionalImplications = new HashSet<Node[]>(); // a new set of Node[2];
		if (implies(graph, n1, n2, history1, history2, knownNotImplying,
				conditionalImplications)) {
			for (Iterator<Node[]> iter = conditionalImplications.iterator(); iter
					.hasNext();) {
				Node[] current = iter.next();
				deleteStatementsWith(graph, current[1]);
			}
			return true;
		}
		return false;
	}

	/**
	 * @param graph
	 * @param resource
	 */
	private static void deleteStatementsWith(Graph graph, Node resource) {
		Iterator stmtIter = graph.iterator();
			while (stmtIter.hasNext()) {
				Triple statement = (Triple) stmtIter.next();
				Node subject = statement.getSubject();
				if (resource.equals(subject)) {
					stmtIter.remove();
					continue;
				}
				Node object = statement.getObject();
				if (resource.equals(object)) {
					stmtIter.remove();
					continue;
				}

			}

	}

	private static boolean implies(Graph graph, Node n1, Node n2,
			List<Node> history1, List<Node> history2, Set<Node[]> knownNotImplying,
			Set<Node[]> conditionalImplications) {
		if (log.isDebugEnabled()) {
			log.debug("checking if "+n1+" implies "+n2);
		}
		boolean result = implies_(graph, n1, n2, history1, history2, knownNotImplying, conditionalImplications);
		if (log.isDebugEnabled()) {
			log.debug("checking if "+n1+" implies "+n2+" results to "+result);
		}
		return result;
	}
	/**
	 * @param n1
	 * @param n2
	 * @param history1
	 * @param history2
	 * @param knownNotImplying
	 * @param conditionalImplications
	 * @return
	 */
	private static boolean implies_(Graph graph, Node n1, Node n2,
			List<Node> history1, List<Node> history2, Set<Node[]> knownNotImplying,
			Set<Node[]> conditionalImplications) {
		
		if (n1.equals(n2))
			return true; // == is true if they have the same node id;
		if (n2 instanceof GroundedNode) {
			return false;
		}
		Node[] currentPair = new Node[2];
		currentPair[0] = n1;
		currentPair[1] = n2;
		if (knownNotImplying.contains(currentPair))
			return false;
		if (conditionalImplications.contains(currentPair))
			return true;
		// if history1 contains n2 return false;
		if (history1.contains(n2))
			return false;

		// if there is a position pos in history2 so that history2[pos] == n2 {
		if (history2.contains(n2)) {
			int pos = history2.indexOf(n2);
			if (history1.get(pos).equals(n1)) {
				conditionalImplications.add(currentPair);
				return true;
			}
			// if removeNodeifImplied(n1, history1[pos], knownNotImplying)
			if (removeNodeifImplied(graph, n1, history1.get(pos),
					knownNotImplying)) {
				conditionalImplications.add(currentPair);
				return true;
			}
			return false; // do not add to knownNotImplying as there may be
			// another path/history leading to these nodes
		}
		Set<Triple> p1 = getPropertySet(graph, n1); // the set of statements which
											// contain n1;
		Set<Triple> p2 = getPropertySet(graph, n2); // the set of statements wich
											// contain n2;
		// for every statement in p2 with no other anonymous node than n2 check
		// if there is an identical statement in p1, otherwise add {n1, n2} to
		// knownNotImpying an return false;
		P2_ITER: for (Iterator<Triple> iterP2 = p2.iterator(); iterP2.hasNext();) {
			Triple currentP2 = iterP2.next();
			if (!containsNoOtherAnon(currentP2, n2)) {
				for (Iterator<Triple> iterP1 = p1.iterator(); iterP1.hasNext();) {
					Triple currentP1 = iterP1.next();
					Triple currentP1Replaced = replaceInStmt(currentP1, n1,
							n2);
					if (currentP1Replaced.equals(currentP2)) {
						iterP2.remove();
						continue P2_ITER;
					}
				}
				knownNotImplying.add(currentPair);
				return false;
			}
		}
		history1.add(n1);
		history2.add(n2);
		// for every other statement (n2, p, o2) or (o2, p, n2) in p2 check if
		// there is a statement (n1, p, o1) respectively (o1, p, n1) in p1 with
		// the same predicate and for which implies(o1, o2,
		// history1.clone(),history2.clone(), knownNotImplying,
		// conditionalImplications) is true, otherwise add {n1, n2} to
		// knownNotImpying an return false;

		// first round: just check direction and property-type, add checkPairs
		// to
		// will fail if for any entry of the set, all pairs in the contained set do not matchs
		Set<Set<Node[]>> checkingSets = new HashSet<Set<Node[]>>();
		
		P2_ITER: for (Iterator<Triple> iterP2 = p2.iterator(); iterP2.hasNext();) {
			Triple currentP2 = iterP2.next();;
			boolean forwardP2 = currentP2.getSubject().equals(n2);
			Set<Node[]> pairsToCheck = new HashSet<Node[]>();
			for (Iterator<Triple> iterP1 = p1.iterator(); iterP1.hasNext();) {
				Triple currentP1 = iterP1.next();
				boolean forwardP1 = currentP1.getSubject().equals(n1);
				if ((forwardP1 == forwardP2)
						&& currentP1.getPredicate().equals(
								currentP2.getPredicate())) {
					Node[] currentPairToCheck = new Node[2];
					if (forwardP1) {
						currentPairToCheck[0] = currentP1.getObject();
						currentPairToCheck[1] = currentP2.getObject();
					} else {
						currentPairToCheck[0] = currentP1.getSubject();
						currentPairToCheck[1] = currentP2.getSubject();
					}
					//Fixing: if p1 contains multiple statements with same property and direction this may not be the right match
					pairsToCheck.add(currentPairToCheck);
				}
			}
			if (pairsToCheck.size() > 0) {
				checkingSets.add(pairsToCheck);
			} else {
				knownNotImplying.add(currentPair);
				return false;
			}
		}
		CHECKINGSETS: for (Set<Node[]> pairsToCheck : checkingSets) {	
			for (Iterator<Node[]> iter = pairsToCheck.iterator(); iter.hasNext();) {
				Node[] currentPairToCheck = iter.next();
				Set<Node[]> conditionalImplicationsOrig = new HashSet<Node[]>(
						conditionalImplications);
				if ((!(currentPairToCheck[1] instanceof Node))
						|| !implies(graph, currentPairToCheck[0],
								(Node) currentPairToCheck[1], new ArrayList<Node>(
										history1), new ArrayList<Node>(history2),
								knownNotImplying, conditionalImplications)) {
					conditionalImplications.clear();
					conditionalImplications.addAll(conditionalImplicationsOrig);
				} else {
					continue CHECKINGSETS;
				}
			}
			knownNotImplying.add(currentPair);
			return false;
			
		}
		conditionalImplications.add(currentPair);
		return true;
	}

	/**
	 * @param currentP1
	 * @param n1
	 * @param n2
	 * @return
	 */
	private static Triple replaceInStmt(Triple statement, Node n1,
			Node n2) {

		Node subject = statement.getSubject();
		if ((subject.equals(n1))) {
			subject = (Node) n2;
		}
		Node object = statement.getObject();
		if ((object.equals(n1))) {
			object = n2;
		}
		return new TripleImpl(subject, statement.getPredicate(), object);
	}

	/**
	 * @param statement
	 * @param n2
	 * @return
	 */
	private static boolean containsNoOtherAnon(Triple statement,
			Node node) {
		Node subject = statement.getSubject();
		if (!(subject instanceof GroundedNode) && (!subject.equals(node))) {
			return true;
		}
		Node object = statement.getObject();
		if (!(subject instanceof GroundedNode) && (!object.equals(node))) {
			return true;
		}
		return false;
	}

	/**
	 * @param graph
	 * @param n1
	 * @return
	 */
	private static Set<Triple> getPropertySet(Graph graph, Node node) {
		Set<Triple> result = new HashSet<Triple>();
		Iterator stmtIter = graph.iterator();
			while (stmtIter.hasNext()) {
				Triple statement = (Triple) stmtIter.next();
				Node subject = statement.getSubject();
				if ((node.hashCode() == subject.hashCode()) && subject.equals(node)) {
					result.add(statement);
					continue;
				}
				Node object = statement.getObject();
				if ((node.hashCode() == object.hashCode()) && object.equals(node)) {
					result.add(statement);
				}

			}
		return result;
	}
	/**
	 * @param graph
	 * @param exclude
	 * @param currentCandidateImplicator
	 * @return
	 */
	private static Node geNewCandidateImplicated(Graph graph, Set<Node> exclude,
			Node currentCandidateImplicator) {
		Iterator stmtIter = graph.iterator();
		while (stmtIter.hasNext()) {
			Triple statement = (Triple) stmtIter.next();
			Node subject = statement.getSubject();
			if ((!(subject instanceof GroundedNode))
					&& !exclude.contains(subject)
					&& !subject.equals(currentCandidateImplicator)) {
				return subject;
			}
			Node object = statement.getObject();
			if ((!(object instanceof GroundedNode))
					&& !exclude.contains(object)
					&& !object.equals(currentCandidateImplicator)) {
				return object;
			}

		}

		return null;
	}

	/**
	 * @param graph
	 * @param exclude
	 * @return
	 */
	private static Node getNewCandidateImplicator(Graph graph, Set<Node> exclude) {
		Iterator stmtIter = graph.iterator();
		while (stmtIter.hasNext()) {
			Triple statement = (Triple) stmtIter.next();
			Node subject = statement.getSubject();
			if (!exclude.contains(subject)) {
				return subject;
			}
			Node object = statement.getObject();
			if (!exclude.contains(object)) {
				return object;
			}

		}

		return null;

	}

}
