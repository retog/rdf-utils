package org.wymiwyg.rdf.molecules.examples;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class JenaAnonNodes {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		Resource anonRes = model.createResource();
		anonRes.addProperty(RDFS.label, "A node");
		Resource anonRes2 = model.createResource();
		anonRes2.addProperty(RDFS.label, "A node");
		model.write(System.out);
		//make nodes distinct
		anonRes.addProperty(RDFS.comment,"A person met yesterday");
		anonRes2.addProperty(RDFS.comment,"A person met today");
		
	}

}
