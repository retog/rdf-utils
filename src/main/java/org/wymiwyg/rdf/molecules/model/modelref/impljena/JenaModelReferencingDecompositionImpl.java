package org.wymiwyg.rdf.molecules.model.modelref.impljena;

import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;

import com.hp.hpl.jena.rdf.model.Model;

public class JenaModelReferencingDecompositionImpl extends
		org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl {
	public JenaModelReferencingDecompositionImpl(Model model, Model ontology) {
		super(JenaUtil.getGraphFromModel(model, ontology));
	}
	

}
