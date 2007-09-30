package org.wymiwyg.rdf.molecules.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.wymiwyg.rdf.graphs.AbstractGraph;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.molecules.Molecule;

public abstract class AbstractMolecule extends AbstractGraph implements Molecule {

	//private Set statements = null;

	public String toString() {
		StringWriter out = new StringWriter();
		PrintWriter pout = new PrintWriter(out);
		for (Iterator iter = iterator(); iter.hasNext();) {
			Triple statement = (Triple) iter.next();
			pout.println(statement);
		}
		pout.flush();
		return out.toString();
	}

}
