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
package org.wymiwyg.rdf.molecules.functref.test;



import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;

import com.hp.hpl.jena.rdf.model.Model;
import org.wymiwyg.rdf.molecules.functref.impl2.ReferenceGroundedDecompositionImpl2;

/**
 * @author reto
 * 
 */
public class ReferenceGroundedDecomposition2Test extends ReferenceGroundedDecompositionTestBase {

	/**
	 * @param arg0
	 */
	public ReferenceGroundedDecomposition2Test(String arg0) {
		super(arg0);
	}
	
	@Override
	protected ReferenceGroundedDecomposition getDecomposition(Model model, boolean useDefaultOntology) {
		 return new ReferenceGroundedDecompositionImpl2(JenaUtil
						.getGraphFromModel(model, useDefaultOntology));
	}

}
