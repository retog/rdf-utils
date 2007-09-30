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
package org.wymiwyg.rdf.graphs.fgnodes.impl.ifpont;

import org.wymiwyg.commons.jena.JenaUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author reto
 *
 */
public class MakeCombiOnt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model fullCombi = ModelFactory.createDefaultModel();
		fullCombi.read("http://www.w3.org/2002/07/owl#");
		fullCombi.read("http://www.w3.org/2004/02/skos/core");
		fullCombi.read("http://www.w3.org/2004/02/skos/mapping");
		fullCombi.read("http://www.w3.org/2004/02/skos/extensions");
		//TODO add GVS-access-Control ontology
		fullCombi.read("http://www.mindswap.org/2003/owl/geo/geoFeatures20040307.owl#");
		fullCombi.read(MakeCombiOnt.class.getResource("foaf.rdf").toString());
		fullCombi.add(RDF.rest, RDF.type, OWL.InverseFunctionalProperty);
		Model result = ModelFactory.createDefaultModel();
		ResIterator resIter = fullCombi.listSubjectsWithProperty(RDF.type, OWL.InverseFunctionalProperty);
		while (resIter.hasNext()) {
			result.add(JenaUtil.getExpandedResource(resIter.nextResource(), 4));
		}
		resIter.close();
		resIter = fullCombi.listSubjectsWithProperty(RDF.type, OWL.FunctionalProperty);
		while (resIter.hasNext()) {
			result.add(JenaUtil.getExpandedResource(resIter.nextResource(), 4));
		}
		result.write(System.out);
		
	}

}
