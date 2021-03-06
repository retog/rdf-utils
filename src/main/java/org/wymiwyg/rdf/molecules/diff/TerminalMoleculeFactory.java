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
package org.wymiwyg.rdf.molecules.diff;

import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.impl.SimpleTerminalMolecule;

/**
 * @author reto
 *
 */
public class TerminalMoleculeFactory implements MoleculeFactory<TerminalMolecule> {

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeFactory#createMolecule()
	 */
	public TerminalMolecule createMolecule() {
		return new SimpleTerminalMolecule();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.molecules.impl.MoleculeFactory#release(org.wymiwyg.rdf.molecules.Molecule)
	 */
	public void release(TerminalMolecule molecule) {
		((SimpleTerminalMolecule)molecule).markFinalized();
	}


}
