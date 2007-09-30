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

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.commons.util.io.IndentWriter;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedNodeBase;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedNodeImpl;
import org.wymiwyg.rdf.graphs.fgnodes.impl.HashFreeSet;
import org.wymiwyg.rdf.graphs.impl.NodeImpl;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * @author reto
 *
 */
public class CrossGraphFgNode extends FunctionallyGroundedNodeBase {

	//TODO other method to fill in stuff, return inmutableSets, interrnaly use HashSet (markFinalized needed?)
	private Set<FunctionallyGroundedNode> nodesIn1 = new HashFreeSet<FunctionallyGroundedNode>();
	private Set<FunctionallyGroundedNode> nodesIn2 = new HashFreeSet<FunctionallyGroundedNode>();
	private String id;
	private Node anonymousNode;
	
	public CrossGraphFgNode() {
		id = "_:cgn-"+Util.createRandomString(8);
		anonymousNode = new NodeImpl();
	}

	public Set<FunctionallyGroundedNode> getNodesIn1() {
		return nodesIn1;
	}
	
	public Set<FunctionallyGroundedNode> getNodesIn2() {
		return nodesIn2;
	}
	public void print(PrintWriter out) {
		out.println("CrossGraphFgNode, that will be referenced as "+id);
		out.println("Versions in 1: "+nodesIn1.size());
		PrintWriter iOut = new PrintWriter(new IndentWriter(out));
		for (Iterator<FunctionallyGroundedNode> iter = nodesIn1.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode current =  iter.next();
			iOut.print("-");
			iOut.println(current);
		}
		iOut.flush();
		
		out.println();
		out.println("Versions in 2: "+nodesIn2.size());
		PrintWriter iOut2 = new PrintWriter(new IndentWriter(out));
		for (Iterator<FunctionallyGroundedNode> iter = nodesIn2.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode current =  iter.next();
			iOut2.print("-");
			iOut2.println(current);
		}
		iOut2.flush();
		out.println();
	}

	@Override
	public String toString() {
		return id;
		/*StringWriter swriter = new StringWriter();
		PrintWriter pwriter = new PrintWriter(swriter);
		print(pwriter);
		pwriter.flush();
		return swriter.toString();*/
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode#getGroundingMolecules()
	 */
	public Set<NonTerminalMolecule> getGroundingMolecules() {
		Set<NonTerminalMolecule> result = new HashSet<NonTerminalMolecule>();
		for (Iterator<FunctionallyGroundedNode> iter = nodesIn1.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode currentFgNode = iter.next();
			result.addAll(currentFgNode.getGroundingMolecules());
		}
		for (Iterator<FunctionallyGroundedNode> iter = nodesIn2.iterator(); iter.hasNext();) {
			FunctionallyGroundedNode currentFgNode = iter.next();
			result.addAll(currentFgNode.getGroundingMolecules());
		}
		return result;
	}



	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode#getOriginalNode()
	 */
	public Node getAnonymousNode() {
		return anonymousNode;
	}

	/**
	 * @return
	 */
	public boolean is1To1() {
		return (nodesIn1.size() == 1) && (nodesIn2.size() == 1);
	}



}
