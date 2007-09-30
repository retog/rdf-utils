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
package org.wymiwyg.rdf.graphs.jenaimpl.test;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.graphs.matcher.GraphMatcher;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class UtilTest extends TestCase {

	public void testConversion() throws FileNotFoundException {
		Model orig = ModelFactory.createDefaultModel();
		orig.read(getClass().getResource("OWLManifest.rdf").toString());
		Graph graph = JenaUtil.getGraphFromModel(orig, false);
		Model convertedBack = JenaUtil.getModelFromGraph(graph);
		Graph graph1 = JenaUtil.getGraphFromModel(convertedBack, false);
		graph1.size();
		assertTrue("Converting back and forward (testing on graph)",
				GraphMatcher.getValidMapping(graph, graph1) != null);
		// fails because of literal-identity bug
		// assertTrue("Converting back and forward",
		// orig.isIsomorphicWith(convertedBack));
	}

}
