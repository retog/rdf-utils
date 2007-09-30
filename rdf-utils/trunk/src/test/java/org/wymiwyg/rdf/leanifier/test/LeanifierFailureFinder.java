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
package org.wymiwyg.rdf.leanifier.test;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;
import org.wymiwyg.rdf.utils.jena.test.ModelCreationUtil;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author reto
 *
 */
public class LeanifierFailureFinder {

	/**
	 * 
	 */
	public LeanifierFailureFinder() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model randomModel = null;
		Graph randomGraph;
		try {
		for (int i = 0; i < 50000; i++) {
			randomModel = ModelCreationUtil.createRandomModel((int) (Math.random()*1000));
			randomGraph = JenaUtil.getGraphFromModel(randomModel, true);
			Graph leanifiedGraph = MoleculeBasedLeanifier.getLeanVersionOf(randomGraph);
			Graph doubleLeanifiedGraph = MoleculeBasedLeanifier.getLeanVersionOf(leanifiedGraph);
			boolean equals = leanifiedGraph.equals(doubleLeanifiedGraph);
			if (!equals) {
				randomModel.write(System.out);
				return;
			}
			
		}
		} catch (RuntimeException e) {
			randomModel.write(System.out);
			throw e;
		}
	}

}
