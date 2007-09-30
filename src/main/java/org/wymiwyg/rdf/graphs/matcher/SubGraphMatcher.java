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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.matcher.sublists.ListsIterator;
import org.wymiwyg.rdf.graphs.matcher.sublists.MultiSublistsSet;
import org.wymiwyg.rdf.graphs.matcher.sublists.SubListsSet;

/**
 * @author reto
 * 
 */
public class SubGraphMatcher {

	/**
	 * @author reto
	 * 
	 */
	public static class PossibleSubGraphsIterator {

		ListsIterator multiSubllistsIter;

		/**
		 * @param patternTripleMap
		 * @throws NoMappingException
		 */
		public PossibleSubGraphsIterator(Map<TrivialTriplePattern, Set[]> patternTripleMap)
				throws NoMappingException {
			ArrayList<SubListsSet> subListsSetList = new ArrayList<SubListsSet>();
			for (Iterator<TrivialTriplePattern> iter = patternTripleMap.keySet().iterator(); iter
					.hasNext();) {
				TrivialTriplePattern pattern = iter
						.next();
				Set[] matchings = patternTripleMap.get(pattern);
				if (matchings[0].size() > matchings[1].size()) {
					throw new NoMappingException();
				}
				ArrayList baseList = new ArrayList(matchings[1]);
				subListsSetList.add(new SubListsSet(baseList, matchings[0]
						.size()));
				/*
				 * if (matchings[0].size() == matchings[1].size()) {
				 * safeMatches.add(matchings[1]); } else {
				 * guessingMatches.add(new SelectionVariants(matchings[1],
				 * matchings[0].size())); }
				 */
			}
			multiSubllistsIter = (ListsIterator) new MultiSublistsSet(
					(SubListsSet[]) subListsSetList
							.toArray(new SubListsSet[subListsSetList.size()]))
					.iterator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new RuntimeException("not supported");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return multiSubllistsIter.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		public Graph nextGraph() {
			SimpleGraph result = new SimpleGraph();
			result.addAll(multiSubllistsIter.nextList());
			result.markFinalized();
			return result;
		}

	}

	/**
	 * get a mapping from g1 to g2 or null if not every node of g1 can be mapped
	 * to a node of g2. Grounded (not only NaturallyGrounded) are mapped to each
	 * other if the are .equals().
	 * 
	 * Ungrounded nodes are not mapped to grounded nodes
	 * 
	 * @param g1
	 * @param g2
	 * @return a Set of NodePairs
	 */
	/*
	 * This method checks for set of statements in g2 with map g1 and then
	 * checks if g1 is isomorphic with this subgraph of g2.
	 * 
	 * To find possible matches
	 * 
	 */
	public static Map<Node, Node> getValidMapping(Graph g1, Graph g2) {
		// maps a triple-pattern to Collection[2]
		Map<TrivialTriplePattern, Set[]> patternTripleMap = new HashMap<TrivialTriplePattern, Set[]>();
		for (Iterator iter = g1.iterator(); iter.hasNext();) {
			Triple current = (Triple) iter.next();
			TrivialTriplePattern pattern = new TrivialTriplePattern(current);
			Set[] matchings = patternTripleMap.get(pattern);
			if (matchings == null) {
				matchings = new HashSet[2];
				matchings[0] = new HashSet();
				matchings[1] = new HashSet();
				patternTripleMap.put(pattern, matchings);
			}
			matchings[0].add(current);
		}
		for (Iterator iter = g2.iterator(); iter.hasNext();) {
			Triple current = (Triple) iter.next();
			TrivialTriplePattern pattern = new TrivialTriplePattern(current);
			Set[] matchings = patternTripleMap.get(pattern);
			if (matchings == null) {
				continue;
			}
			matchings[1].add(current);
		}
		PossibleSubGraphsIterator possibleGraphs;
		try {
			possibleGraphs = new PossibleSubGraphsIterator(patternTripleMap);
		} catch (NoMappingException e) {
			return null;
		}
		while (possibleGraphs.hasNext()) {
			Map<Node, Node> mapping = GraphMatcher.getValidMapping(g1, possibleGraphs.nextGraph());
			if (mapping != null) {
				return mapping;
			}
		}
		return null;
	}

}
