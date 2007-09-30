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
package org.wymiwyg.rdf.graphs.matcher.test;

import junit.framework.TestCase;

import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.graphs.matcher.SubGraphMatcher;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author reto
 * 
 */
public class SubgraphTest extends TestCase {

	public void testSubGraph() {
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		IdentityTest.createSameContextualMolecules(model1, model2, 9);
		IdentityTest.createCircle(model1, 4);
		IdentityTest.createCircle(model2, 4);
		model2.add(model2.createResource("http://foo/0"), RDFS.seeAlso, model2
				.createResource("http://foo/1"));
		assertTrue("model1 subgraph of model2", SubGraphMatcher
				.getValidMapping(JenaUtil.getGraphFromModel(model1, false), JenaUtil
						.getGraphFromModel(model2, false)) != null);
		IdentityTest.createCircle(model2, 5);
		assertTrue("model1 subgraph of model2", SubGraphMatcher
				.getValidMapping(JenaUtil.getGraphFromModel(model1, false), JenaUtil
						.getGraphFromModel(model2, false)) != null);
		assertFalse("model2 is not a subgraph of model1", SubGraphMatcher
				.getValidMapping(JenaUtil.getGraphFromModel(model2, false), JenaUtil
						.getGraphFromModel(model1, false)) != null);

	}

}
