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
package org.wymiwyg.rdf.utils.jena.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.commons.util.Util;
import org.wymiwyg.commons.vocabulary.FOAF;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author reto
 *
 */
public class ModelCreationUtil {

	private static final Log log = LogFactory.getLog(ModelCreationUtil.class);
	
	private static Model m_model = ModelFactory.createDefaultModel();
	
	private static List<Resource> resourceCandidates = new ArrayList<Resource>();
	static {
		for (int i = 0; i < 5000; i++) {
			if (Math.random() > 0.2) {
				resourceCandidates.add(m_model.createResource());
			} else {
				resourceCandidates.add(m_model.createResource(Util.createURN5()));
			}
		}
	}
	
	private static List<Property> predicateCandidates = new ArrayList<Property>();
	static {
		predicateCandidates.add(FOAF.mbox);
		predicateCandidates.add(FOAF.mbox_sha1sum);
		predicateCandidates.add(FOAF.homepage);
		predicateCandidates.add(FOAF.interest);
		predicateCandidates.add(FOAF.primaryTopic);
		predicateCandidates.add(FOAF.gender);
		for (int i = 0; i < 30; i++) {
			predicateCandidates.add(m_model.createProperty("http://"+Util.createRandomString(8)+"/"+Util.createRandomString(4)));
		}
	}
	private static List<Literal> literalCandidates = new ArrayList<Literal>();
	static {
		for (int i = 0; i < 200; i++) {
			StringBuffer buffer = new StringBuffer();
			while (Math.random() > 0.2) {
				buffer.append(Util.createRandomString((int) (Math.random()*50)));
				if (Math.random() > 0.1) {
					buffer.append(' ');
				}
			}
			literalCandidates.add(m_model.createLiteral(buffer.toString()));
		}
	}
	
	
	public static Model createRandomModel(int size) {
		Model model = ModelFactory.createDefaultModel();
		log.debug("trans: "+model.supportsTransactions());
		List<Resource> usedResources =new ArrayList<Resource>((int) (1.2*size)); 
		for (int i = 0; i < size; i++) {
			addRandomTriple(model, usedResources);
			if ((i > 0) && (i % 100000 == 0)) {
				log.info("pos  "+i);
			}
		}
		return model;
	}

	/**
	 * @param model
	 */
	private static void addRandomTriple(Model model, List<Resource> usedResources) {
		Resource subject = getRandomSubject(usedResources);
		Property predicate = getRandomPredicate();
		RDFNode object = getRandomObject(usedResources);
		model.add(subject, predicate, object);
	}

	/**
	 * @return
	 */
	private static RDFNode getRandomObject(List<Resource> usedResources) {
		if ((usedResources.size() < 3) || (Math.random() > 0.8)) {
			if (Math.random() > 0.6) {
				return getRandomFromList(literalCandidates);
			} else {
				Resource resource = getRandomFromList(resourceCandidates);
				usedResources.add(resource);
				return resource;
			}
			
		} else {
			return getRandomFromList(usedResources);
		}
		
	}

	/**
	 * @return
	 */
	private static Property getRandomPredicate() {
		return getRandomFromList(predicateCandidates);
	}

	/**
	 * @param usedResources 
	 * @return
	 */
	private static Resource getRandomSubject(List<Resource> usedResources) {
		if ((usedResources.size() < 3) || (Math.random() > 0.8)) {
			Resource resource = getRandomFromList(resourceCandidates);
			usedResources.add(resource);
			return resource;
		} else {
			return getRandomFromList(usedResources);
		}
	}

	/**
	 * @param subjectCandidates2
	 */
	private static <T> T getRandomFromList(List<T> list) {
		int position = (int) (Math.random() * (list.size()-1));
		return list.get(position);
		
	}

}
