package org.wymiwyg.rdf.molecules;

import org.wymiwyg.rdf.graphs.Graph;

/**
 * Terminal Molecule (T-molecule). A terminal molecule only uses grounded nodes
 * and/or functionally grounded BNodes, and all its BNodes are close. A BNode bn
 * in a molecule n has two states, namely ‘open’ and ‘close’. bn is said ‘close’
 * if it is functionally grounded and being used by exact one more triple in m,
 * otherwise it is ‘open’.
 */

public interface TerminalMolecule extends Molecule, Graph {

}