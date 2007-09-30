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
package org.wymiwyg.rdf.utils.jena.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaModelGraph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiffImpl;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;
import org.wymiwyg.rdf.utils.jena.LeanDiffPatch;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class MinimumFailingGraphDetector {

	public static class TripleInfo {

		/**
		 * @param triple2
		 */
		public TripleInfo(Triple triple) {
			this.triple = triple;
		}

		Triple triple;

		States status = States.undefined;

	}

	public static enum States {
		undefined, needed, unneeded
	}
	
	/** This returns subgraph of a graph, in order to find a minimal sufficient graph
	 * the class attempts to remove triples one by one, if a subgraph was not sufficient
	 * the triple missing in that graph will not be removed again, if it was found to be sufficient
	 * it will allways be remove.
	 * 
	 * @author reto
	 *
	 */
	class SubGraphCreator {
		Set<TripleInfo> tripleInfos = new HashSet<TripleInfo>();
		private int omitUndefined = 1;
		private int qualified = 0;
		private int recentSuccessCount = 0;
		private double reduceFactor = 0.5;
		SubGraphCreator(Graph g) {
			for (Triple triple : g) {
				tripleInfos.add(new TripleInfo(triple));
			}
		}
		
		Graph getFirstSubgraph() {
			return getNextSubGraph();
		}
		
		/** returns a new subgraph or null if no new one is available
		 * 
		 * @return
		 */
		Graph getNewSubgraph(boolean previousSubGraphWasSufficient) {
			int markCount = 0;
			for (TripleInfo info : tripleInfos) {
				if (info.status == States.undefined) {
					if (previousSubGraphWasSufficient) {
						info.status = States.unneeded;
						qualified++;
						markCount++;
					} else {
						if (omitUndefined == 1) {
							info.status = States.needed;
							qualified++;
							break;
						}
					}
					if (markCount == omitUndefined) {
						break;
					}
				}
			}
			if (previousSubGraphWasSufficient) {
				recentSuccessCount++;
				omitUndefined = ((int) ((tripleInfos.size()-qualified) * (reduceFactor* recentSuccessCount)))+1;
				if (omitUndefined < 5) {
					omitUndefined = 1;
				}
				reduceFactor *= 2;
			} else {
				recentSuccessCount = 0;
				omitUndefined = 1;
				reduceFactor *= 0.2;
			}
			return getNextSubGraph();
		}
		private Graph getNextSubGraph() {
			int count  = 0;
			Graph currentGraph = new SimpleGraph();
			for (TripleInfo info : tripleInfos) {
				switch (info.status) {
				case undefined:
					if (count < omitUndefined) {
						count++;
						break;
					} else {
						currentGraph.add(info.triple);
					}
					;
					break;
				case needed:
					currentGraph.add(info.triple);
				}
			}
			if (count == 0) {
				return null;
			}
			System.out.println("returning subgraph of size "+currentGraph.size());
			return currentGraph;
		}
	}
	
	private Graph minimumFailingG1;
	private Graph minimumFailingG2;

	/**
	 * 
	 */
	public MinimumFailingGraphDetector(Graph g1, Graph g2) {
		if (diffPathTest(g1,true, g2, true)) {
			throw new RuntimeException("test with original graphs succed!");
		}
		createMimimumFailingG1(g1, g2);
		createMimimumFailingG2(g2);
	}

	/**
	 * @param g1
	 * @param g2
	 */
	private void createMimimumFailingG1(Graph g1, Graph g2) {
		SubGraphCreator subGraphCreator = new SubGraphCreator(g1);
		boolean lastOneFailed; //note that we are looking for failing graphs
		Graph lastFailingGraph = g1; 
//		int roundCount = 0;
		for (Graph currentGraph = subGraphCreator.getFirstSubgraph(); currentGraph != null; currentGraph = subGraphCreator.getNewSubgraph(lastOneFailed)) {
			lastOneFailed = !diffPathTest(currentGraph, false, g2, true);
			if (lastOneFailed) {
				lastFailingGraph = currentGraph;
//				if (roundCount++ % 100 ==0){
//					try {
//						JenaUtil.getModelFromGraph(currentGraph).write(new FileWriter("g1-"+roundCount),"N-TRIPLES");
//					} catch (IOException e) {
//						throw new RuntimeException(e);
//					}
//				}
			}
		}
		minimumFailingG1 = lastFailingGraph;		
	}
	
	/**
	 * @param g1
	 * @param g2
	 */
	private void createMimimumFailingG2( Graph g2) {
		SubGraphCreator subGraphCreator = new SubGraphCreator(g2);
		boolean lastOneFailed; //note that we are looking for failing graphs
		Graph lastFailingGraph = g2; //we don't verify
//		int roundCount = 0;
		for (Graph currentGraph = subGraphCreator.getFirstSubgraph(); currentGraph != null; currentGraph = subGraphCreator.getNewSubgraph(lastOneFailed)) {
			lastOneFailed = !diffPathTest(minimumFailingG1, true, currentGraph, false);
			if (lastOneFailed) {
				lastFailingGraph = currentGraph;
//				if (roundCount++ % 100 ==0){
//					try {
//						JenaUtil.getModelFromGraph(currentGraph).write(new FileWriter("g2-"+roundCount),"N-TRIPLES");
//					} catch (IOException e) {
//						throw new RuntimeException(e);
//					}
//				}
			}
		}
		minimumFailingG2 = lastFailingGraph;		
	}

	/**
	 * @param g1
	 * @param g2
	 * @return
	 */
	private boolean diffPathTest(Graph g1, boolean isLean1, Graph g2, boolean isLean2) {
		if (!isLean1) {
			System.out.println("leanifying g1");
			g1 = MoleculeBasedLeanifier.getLeanVersionOf(g1);
		}
		if (!isLean2) {
			System.out.println("leanifying g2");
			g2 = MoleculeBasedLeanifier.getLeanVersionOf(g2);
		}
		System.out.println("testing g1 of size "+g1.size()+" against g2 of size "+g2.size());
		try {
			MoleculeDiff diff = new MoleculeDiffImpl(
					new ReferenceGroundedDecompositionImpl(
							new ModelReferencingDecompositionImpl(g1)),
					new ReferenceGroundedDecompositionImpl(
							new ModelReferencingDecompositionImpl(g2)));
			File file = File.createTempFile("minimum-failing",".zip");
			diff.serialize(new FileOutputStream(file));
			MoleculeDiff diffRec =  LeanDiffPatch.deserializeDiff(file);
			ReferenceGroundedDecomposition dec2rec = diffRec
					.patch(new ReferenceGroundedDecompositionImpl(
							new ModelReferencingDecompositionImpl(g1)));
			Graph g2rec = ReferenceGroundedUtil.reconstructGraph(dec2rec);
			//TODO find out why test 20 fails without this line
			g2rec = JenaUtil.getGraphFromModel(JenaUtil.getModelFromGraph(g2rec), true);
			g2rec = MoleculeBasedLeanifier.getLeanVersionOf(g2rec);
			Model m2 = JenaUtil.getModelFromGraph(g2);
			Model m2rec = JenaUtil.getModelFromGraph(g2rec);
			System.out.println(g2.size());
			System.out.println(g2rec.size());
			System.out.println(m2.size());
			System.out.println(m2rec.size());
			//m2rec = LeanDiffPatch.leanify(m2rec);
			return m2.isIsomorphicWith(m2rec);
		} catch (Exception e) {
			System.out.println("Failed with exception " + e);
			return false;
		}
		// return g2.equals(g2rec);
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		//m1.read(new File("problematic-model1.nt").toURL().toString(), "N-TRIPLE");
		//m2.read(new File("problematic-model2.nt").toURL().toString(), "N-TRIPLE");
		m1.read(MinimumFailingGraphDetector.class.getResource("test14-1.nt").toString(), "N-TRIPLE");
		m2.read(MinimumFailingGraphDetector.class.getResource("test14-2.nt").toString(), "N-TRIPLE");
		m1 = LeanDiffPatch.leanify(m1);
		m2 = LeanDiffPatch.leanify(m2);
		Graph g1 = new JenaModelGraph(m1, true);
		Graph g2 = new JenaModelGraph(m2, true);
		MinimumFailingGraphDetector detector = new MinimumFailingGraphDetector(g1, g2); 
		System.out.println("minimum failing g1 (of size "+detector.minimumFailingG1.size()+") :");
		JenaUtil.getModelFromGraph(
				detector.minimumFailingG1).write(
				System.out, "N-TRIPLE");
		System.out.println();
		System.out.println();
		System.out.println("minimum failing g2 (of size "+detector.minimumFailingG2.size()+") :");
		JenaUtil.getModelFromGraph(
				detector.minimumFailingG2).write(
				System.out, "N-TRIPLE");
		JenaUtil.getModelFromGraph(
				detector.minimumFailingG1).write(
				new FileOutputStream("minimum-failing1.nt"), "N-TRIPLE");
		JenaUtil.getModelFromGraph(
				detector.minimumFailingG2).write(
				new FileOutputStream("minimum-failing2.nt"), "N-TRIPLE");
	}

	public Graph getMinimumFailingG1() {
		return minimumFailingG1;
	}

	public Graph getMinimumFailingG2() {
		return minimumFailingG2;
	}

}
