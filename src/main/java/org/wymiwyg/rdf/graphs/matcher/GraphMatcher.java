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
package org.wymiwyg.rdf.graphs.matcher;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaModelGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author reto
 * 
 */
public class GraphMatcher {

	static private Random random = new Random(0);

	private final static Log log = LogFactory.getLog(GraphMatcher.class);

	/**
	 * get a mapping from g1 to g2 or null if they are not isomorphic. Grounded
	 * (not only NaturallyGrounded) are mapped to each other if the are
	 * .equals().
	 * 
	 * Ungrounded nodes are not mapped to grounded nodes
	 * 
	 * @param g1
	 * @param g2
	 * @return a Set of NodePairs
	 */
	public static Map<Node, Node> getValidMapping(Graph g1, Graph g2) {
		ContextGraph cg1 = new ContextGraph(g1);
		ContextGraph cg2 = new ContextGraph(g2);
		IntHashMap<Collection[]> resultMap = new IntHashMap<Collection[]>();
		try {
			if (!getPossibleMappingClasses(cg1, cg2, resultMap)) {
				resultMap = recursivelyRefine(cg1, cg2, resultMap);
			}
		} catch (NoMappingException e) {
			return null;
		}
		if (log.isDebugEnabled()) {
			log.debug("Have " + resultMap.keySet().size()
					+ " unique mappings. B-nodes in cg1: "
					+ cg1.getContextNodes().size());
		}
		Map<Node, Node> result = new HashMap<Node, Node>();
		for (IntIterator iter = resultMap.keySet().intIterator(); iter
				.hasNext();) {
			int currentHash = iter.nextInt();
			Collection[] mapping = (Collection[]) resultMap.get(currentHash);
			ContextNode n1 = (ContextNode) mapping[0].iterator().next();
			ContextNode n2 = (ContextNode) mapping[1].iterator().next();
			result.put(n1.getNode(), n2.getNode());
		}

		if (resultMap.keySet().size() != cg1.getContextNodes().size()) {
			throw new RuntimeException(
					"more nodes than mapping-classes. hash collision?");
			// for (Iterator iter = cg1.getStatements().iterator();
			// iter.hasNext();) {
			// Triple element = (Triple) iter.next();
			// System.out.println(element);
			// }
			// for (Iterator iter = result.keySet().iterator(); iter.hasNext();)
			// {
			// Node keyNode = (Node) iter.next();
			// Node valueNode = (Node) result.get(keyNode);
			// log.info(keyNode+" to "+valueNode);
			// }
		}
		if (isValidMapping(result, g1, g2)) {
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Get a set containing Collection[2] elements where each element of one
	 * collection could possibly be mapped to an element of the other
	 * collection.
	 * 
	 * @param g1
	 * @param g2
	 * @param baseMappingClasses
	 * @param unmappable
	 * @return true if all mapping classes contain 1 elemnt per graph
	 */
	/*
	 * On the first round we take the triple pattern without considering other
	 * b-nodes (all hash = 0), on the next round the hash of the bnodes is the
	 * hash of triple-pattern of the previuos round, etc.
	 * 
	 * When we guess, we set two b-nodes to equal hashes. If a guess fails the
	 * original hashes have to be reset, conservatively on all b-nodes. This is
	 * done by each bnodes keeping a list of hashes, and the operations
	 * refineHash() and resetHash(). (recalculation of hashes for the same level
	 * could be reduced the containing contextual molecule.
	 * 
	 */
	private static boolean getPossibleMappingClasses(ContextGraph g1,
			ContextGraph g2, IntHashMap<Collection[]> targetMap) throws NoMappingException {

		boolean result = true;
		IntHashMap<Collection<ContextNode>> map1 = getHashClasses(g1, null);
		IntSet keySet = map1.keySet();
		IntHashMap<Collection<ContextNode>> map2 = getHashClasses(g2, keySet);
		if (map2 == null) {
			throw new NoMappingException();
		}
		for (IntIterator iter = keySet.intIterator(); iter.hasNext();) {
			int currentHash = iter.nextInt();
			Collection[] mappingClasses = new Collection[2];
			mappingClasses[0] = (Collection) map1.get(currentHash);
			mappingClasses[1] = (Collection) map2.get(currentHash);
			if (mappingClasses[1] == null) {
				throw new NoMappingException();
			}
			int size1 = mappingClasses[0].size();
			if (size1 != mappingClasses[1].size()) {
				throw new NoMappingException();
			}
			if (size1 > 1) {
				result = false;
			}
			targetMap.put(currentHash, mappingClasses);
		}
		return result;
	}

	/**
	 * Returns a mapping classes with exactly one element per graph or null if
	 * it doesn't exist
	 * 
	 * @param cg1
	 * @param cg2
	 * @param initialMapping
	 * @return
	 * @throws NoMappingException
	 */
	private static IntHashMap<Collection[]> recursivelyRefine(ContextGraph cg1,
			ContextGraph cg2, IntHashMap<Collection[]> initialMapping)
			throws NoMappingException {
		IntHashMap<Collection[]> currentMapping = initialMapping;
		while (true) {
			RefiningResult refRes = refineMappingClasses(cg1, cg2,
					currentMapping);
			currentMapping = refRes.mappingClasses;
			if (refRes.allMappingsUnique) {
				return currentMapping;
			}
			if (!refRes.newClassesDifferent) {
				// gotta guess
				Collection[] smallestNoUniqueMapping = refRes.smallestNoUniqueMapping;
				for (Iterator iter1 = smallestNoUniqueMapping[0].iterator(); iter1
						.hasNext();) {
					ContextNode current1 = (ContextNode) iter1.next();
					for (Iterator iter2 = smallestNoUniqueMapping[1].iterator(); iter2
							.hasNext();) {
						ContextNode current2 = (ContextNode) iter2.next();
						// make the two nodes look equal
						int fakeHash = random.nextInt();
						current1.setFakeHash(fakeHash);
						current2.setFakeHash(fakeHash);
						log.debug("added assumption " + fakeHash);
						try {
							return recursivelyRefine(cg1, cg2, currentMapping);
						} catch (NoMappingException ex) {
							// go on trying
						}
						current1.removeFakeHash();
						current2.removeFakeHash();
						log.debug("revoked assumption " + fakeHash);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param g1
	 * @param g2
	 * @param targetMap
	 * @return
	 * @throws NoMappingException
	 */
	private static RefiningResult refineMappingClasses(ContextGraph g1,
			ContextGraph g2, IntHashMap<Collection[]> classesMap) throws NoMappingException {
		IntHashMap<Collection[]> resultMap = new IntHashMap<Collection[]>();
		boolean allUnique = true;
		boolean newClassesDifferent = false;
		Collection[] smallestNonUnique = null;
		int currentSmallest = Integer.MAX_VALUE;
		g1.refineNodeHashes();
		g2.refineNodeHashes();
		for (IntIterator iter = classesMap.keySet().intIterator(); iter
				.hasNext();) {
			int currentHash = iter.nextInt();
			Collection[] mappingClasses = (Collection[]) classesMap
					.get(currentHash);
			IntHashMap<Collection<ContextNode>> newClasses1 = getHashClasses(mappingClasses[0], null);
			IntSet keySet1 = newClasses1.keySet();
			IntHashMap<Collection<ContextNode>> newClasses2 = getHashClasses(mappingClasses[1], keySet1);
			int size1 = keySet1.size();
			if ((newClasses2 == null) || (size1 != newClasses2.size())) {
				g1.resetNodeHashes();
				g2.resetNodeHashes();
				throw new NoMappingException();
			}
			if (size1 > 1) {
				newClassesDifferent = true;
			}

			for (IntIterator iter2 = keySet1.intIterator(); iter2.hasNext();) {
				int currentNewHash = iter2.nextInt();
				Collection[] newMappingClasses = new Collection[2];
				newMappingClasses[0] = (Collection) newClasses1
						.get(currentNewHash);
				newMappingClasses[1] = (Collection) newClasses2
						.get(currentNewHash);
				int newSize1 = newMappingClasses[0].size();
				int newSize2 = newMappingClasses[1].size();
				if (newSize1 != newSize2) {
					throw new NoMappingException();
				}
				if (newSize1 > 1) {
					allUnique = false;
					if (newSize1 < currentSmallest) {
						smallestNonUnique = newMappingClasses;
						currentSmallest = newSize1;
					}
				}
				// if (resultMap.containsKey(currentNewHash)) {
				// log.warn("Hash collision!");
				// }
				resultMap.put(currentNewHash, newMappingClasses);
			}
		}
		log.debug("refinded from " + classesMap.size() + " to "
				+ resultMap.size());
		return new RefiningResult(resultMap, allUnique, newClassesDifferent,
				smallestNonUnique);
	}

	/**
	 * @param keySet
	 *            return if keySet doesn't contain Hash
	 * @param g1
	 * @return
	 */
	private static IntHashMap<Collection<ContextNode>> getHashClasses(ContextGraph graph, IntSet keySet) {
		List<ContextNode> nodes = graph.getContextNodes();
		return getHashClasses(nodes, keySet);
	}

	private static IntHashMap<Collection<ContextNode>> getHashClasses(Collection<ContextNode> contextNodes,
			IntSet keySet) {
		IntHashMap<Collection<ContextNode>> map = new IntHashMap<Collection<ContextNode>>();
		for (Iterator iter = contextNodes.iterator(); iter.hasNext();) {
			// IntHashMap debugSubMap = new IntHashMap();
			ContextNode current = (ContextNode) iter.next();
			int hash = current.contextHash();
			if ((keySet != null) && !keySet.contains(hash)) {
				return null;
			}
			Collection<ContextNode> hashClass = map.get(hash);
			if (hashClass == null) {
				hashClass = new HashSet<ContextNode>();
				map.put(hash, hashClass);
			}
			/*
			 * if (!debugSubMap.containsKey(hash)) { if (hashC) }
			 */
			hashClass.add(current);
		}
		return map;
	}

	public static boolean isValidMapping(Map<Node, Node> mapping, Graph g1, Graph g2) {
		for (Iterator iter = g1.iterator(); iter.hasNext();) {
			Triple triple = (Triple) iter.next();
			Node subject = triple.getSubject();
			PropertyNode predicate = triple.getPredicate();
			Node object = triple.getObject();
			if (!(subject instanceof GroundedNode)) {
				subject = mapping.get(subject);
			}
			if (!(object instanceof GroundedNode)) {
				object = mapping.get(object);
			}
			Triple mappedTriple = new TripleImpl(subject, predicate, object);
			if (!g2.contains(mappedTriple)) {
				return false;
			}
		}
		return true;
	}

	public static Graph applyMapping(Graph graph, Map mapping) {
		SimpleGraph result = new SimpleGraph();
		for (Iterator iter = graph.iterator(); iter.hasNext();) {
			Triple triple = (Triple) iter.next();
			Node subject = triple.getSubject();
			PropertyNode predicate = triple.getPredicate();
			Node object = triple.getObject();
			if (!(subject instanceof GroundedNode)) {
				subject = (Node) mapping.get(subject);
			}
			if (!(object instanceof GroundedNode)) {
				object = (Node) mapping.get(object);
			}
			Triple mappedTriple = new TripleImpl(subject, predicate, object);
			result.add(mappedTriple);
		}
		result.markFinalized();
		return result;
	}

	public static void main(String[] args) {

		String str = "_:A52687b39X3aX107aecea80bX3aXX2dX79c6 <http://ex/b> _:A52687b39X3aX107aecea80bX3aXX2dX79ca ."
				+ "_:A52687b39X3aX107aecea80bX3aXX2dX79c8 <http://ex/b> _:A52687b39X3aX107aecea80bX3aXX2dX79ca .";

		// "_:26117441 <http://ex/i> _:1708953 ."
		// +"_:30311876 <http://ex/t> _:25442933 ."
		// +" _:1708953 <http://ex/t> _:25442933 ."
		// +"_:25442933 <http://ex/n> _:17708501 ."
		// +"_:13301441 <http://ex/o> _:25442933 ."
		// +"_:25442933 <http://ex/o> <urn:urn-5:x0xC5btRsVBW61v7UfiIR42scXU=>
		// ."
		// +"<urn:urn-5:AGuuuSS1EoZfXEl9Flrn42HQGPE=> <http://ex/o> _:25442933
		// ."
		// +"_:25442933 <http://ex/g> _:32519825 ."
		// +"<urn:urn-5:D0j3nyksREmMynC2H23qmEr62oQ=> <http://ex/b> _:25442933
		// ."
		// +"_:25442933 <http://ex/t> _:33341602 .";

		/*
		 * String str = "_:AX2dX318298fcX3aX107aeb11205X3aXX2dX7f0a
		 * <http://ex/d> _:AX2dX318298fcX3aX107aeb11205X3aXX2dX7f06 ." +
		 * "_:AX2dX318298fcX3aX107aeb11205X3aXX2dX7f08 <http://ex/d>
		 * _:AX2dX318298fcX3aX107aeb11205X3aXX2dX7f0a .";
		 */
		Model model = ModelFactory.createDefaultModel();
		model.read(new StringReader(str), "", "N-TRIPLE");
		StmtIterator modelIter = model.listStatements();
		while (modelIter.hasNext()) {
			Statement stmt = modelIter.nextStatement();
			System.out.println(stmt);
		}
		getValidMapping(new JenaModelGraph(model, false), new JenaModelGraph(model, false));
	}
}
