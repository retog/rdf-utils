package org.wymiwyg.rdf.molecules.diff;

import org.wymiwyg.rdf.molecules.Molecule;

/**
 * @author reto
 *
 */
public interface MoleculeFactory<T extends Molecule> {

	T createMolecule();
	
	void release(T molecule);


}