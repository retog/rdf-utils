/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.rdf.graphs;

import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;

import junit.framework.TestCase;

/**
 * @author reto
 *
 */
public abstract class GraphTestBase extends ModelTestBase {
	/**
	 * @param arg0
	 */
	public GraphTestBase(String arg0) {
		super(arg0);
	}

	protected Model getModelFromResource(String resName) {
		Model model = ModelFactory.createDefaultModel();
		model.read(getClass().getResource(resName).toString());
		return model;
	}

	protected Graph getGraphFromResource(String resName) {
		return JenaUtil.getGraphFromModel(getModelFromResource(resName), true);
	}
}
