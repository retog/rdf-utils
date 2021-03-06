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
package org.wymiwyg.rdf.molecules.impl;

import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.molecules.Molecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;

/**
 * @author reto
 * @deprecated use {@link TerminalMoleculeImpl}
 */
@Deprecated
public class SimpleTerminalMolecule extends SimpleGraph implements
		TerminalMolecule {

	/**
	 * 
	 */
	public SimpleTerminalMolecule() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.Molecule#getUnionWith(org.wymiwyg.rdf.molecules.Molecule)
	 */
	public Molecule getUnionWith(Molecule graph) {
		throw new RuntimeException("Not implemented");
	}

}
