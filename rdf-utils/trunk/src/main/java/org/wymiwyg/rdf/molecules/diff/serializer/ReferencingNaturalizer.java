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
package org.wymiwyg.rdf.molecules.diff.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.Naturalizer;
import org.wymiwyg.rdf.graphs.impl.NamedNodeImpl;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;
import org.wymiwyg.rdf.graphs.impl.PropertyNodeImpl;
import org.wymiwyg.rdf.graphs.impl.TripleImpl;
import org.wymiwyg.rdf.molecules.diff.CrossGraphFgNode;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.diff.vocabulary.MODELDIFF;

/**
 * @author reto
 * 
 */
public class ReferencingNaturalizer implements Naturalizer {

	static final String rootURL = "http://diff-root.localhost/";

	Map<FunctionallyGroundedNode, NamedNode> groundedInMap = new HashMap<FunctionallyGroundedNode, NamedNode>();

	//private String currentGraphLabel;

	//private MoleculeDiff diff;

	/**
	 * @param diff
	 */
	public ReferencingNaturalizer(MoleculeDiff diff) {
		//this.diff = diff;
		String baseURL = rootURL+"fgNodes/";
		prepareNames(diff.getCommonFgNodesInDiffMolecules(), "used-common", baseURL);
		prepareNames(diff.getFgNodesOnlyIn1(), "onlyIn1", baseURL);
		prepareNames(diff.getFgNodesOnlyIn2(), "onlyIn2", baseURL);
		prepareNames(diff.getCrossGraphFgNodes(), "crossGraphFgNodes", baseURL);
		for (CrossGraphFgNode cgNode : diff.getCrossGraphFgNodes()) {
			prepareNames(cgNode.getNodesIn1(), "onlyIn1", groundedInMap.get(cgNode).getURIRef());
			prepareNames(cgNode.getNodesIn2(), "onlyIn2", groundedInMap.get(cgNode).getURIRef());
		}
	}

	/**
	 * @param commonFgNodesInDiffMolecules
	 * @param string
	 */
	private void prepareNames(Set<? extends FunctionallyGroundedNode> fgNodes,
			String categoryLabel,  String baseURL) {
		for (FunctionallyGroundedNode fgNode : fgNodes) {
			NamedNode descriptionDoc = getDescriptionDoc(categoryLabel, baseURL);
			groundedInMap.put(fgNode, descriptionDoc);
		}

	}

	/**
	 * @param categoryLabel
	 * @return
	 */
	private NamedNode getDescriptionDoc(String categoryLabel, String baseURL) {
		StringBuffer relativePath = new StringBuffer();
		relativePath.append(baseURL);
		relativePath.append(categoryLabel);
		relativePath.append('/');
		relativePath.append(Util.createRandomString(8));
		relativePath.append('/');
		return new NamedNodeImpl(relativePath.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.graphs.fgnodes.impl.Naturalizer#naturalize(org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode,
	 *      org.wymiwyg.rdf.graphs.Graph)
	 */
	public Node naturalize(FunctionallyGroundedNode fgNode, Graph graph) {
		Node anonymousNode = new NodeImpl();
		graph.add(new TripleImpl(anonymousNode, new PropertyNodeImpl(
				MODELDIFF.functionallyGroundedIn.getURI()),
				getGroundedIn(fgNode)));
		return anonymousNode;
	}

	/**
	 * @param fgNode
	 * @return
	 */
	NamedNode getGroundedIn(FunctionallyGroundedNode fgNode) {
		NamedNode result = groundedInMap.get(fgNode);
		if (result != null) {
			return result;
		} else {
			throw new RuntimeException("reference to fgNode which was not present in diff "+fgNode);
		}
	}


	/**
	 * @param string
	 */
	/*public void setCurrentGraphLabel(String string) {
		currentGraphLabel = string;

	}*/

}
