package org.wymiwyg.rdf.molecules;

import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;


public interface NonTerminalMolecule extends Molecule, Graph {

	//the active-functionally grounded node
	static final Node GROUNDED_NODE = new NodeImpl() {
		public String toString() {
			return "GROUNDED_NODE";
		}
	};
	/**
	 * 
	 * @return the active-fuctionally-grounded node of the molecule
	 */
	//public abstract Node getAfgn();

}