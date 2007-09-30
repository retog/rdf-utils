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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.ZipFile;

import org.wymiwyg.commons.util.MalformedURIException;
import org.wymiwyg.commons.util.URI;
import org.wymiwyg.commons.util.dirbrowser.PathNode;
import org.wymiwyg.commons.util.dirbrowser.ZipPathNode;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedBuilder;
import org.wymiwyg.rdf.graphs.fgnodes.impl.FunctionallyGroundedNodeImpl;
import org.wymiwyg.rdf.graphs.fgnodes.impl.HashFreeSet;
import org.wymiwyg.rdf.graphs.impl.GraphUtil;
import org.wymiwyg.rdf.graphs.impl.NamedNodeImpl;
import org.wymiwyg.rdf.graphs.impl.PropertyNodeImpl;
import org.wymiwyg.rdf.graphs.impl.SimpleGraph;
import org.wymiwyg.rdf.graphs.impl.SourceNodeNotFoundException;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.MaximumContextualMolecule;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;
import org.wymiwyg.rdf.molecules.TerminalMolecule;
import org.wymiwyg.rdf.molecules.diff.CrossGraphFgNode;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiffBase;
import org.wymiwyg.rdf.molecules.diff.vocabulary.MODELDIFF;
import org.wymiwyg.rdf.molecules.impl.SimpleContextualMolecule;
import org.wymiwyg.rdf.molecules.impl.SimpleTerminalMolecule;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class MoleculeDiffDeserialized extends MoleculeDiffBase {

	private Set<MaximumContextualMolecule> contextualMoleculesOnlyIn1 = new HashSet<MaximumContextualMolecule>();

	private Set<MaximumContextualMolecule> contextualMoleculesOnlyIn2 = new HashSet<MaximumContextualMolecule>();

	private Set<TerminalMolecule> terminalMoleculesOnlyIn1 = new HashSet<TerminalMolecule>();

	private Set<TerminalMolecule> terminalMoleculesOnlyIn2 = new HashSet<TerminalMolecule>();

	private Set<FunctionallyGroundedNode> commonFgNodesInDiffMolecules = new HashSet<FunctionallyGroundedNode>();

	private Set<CrossGraphFgNode> crossGraphFgNodes = new HashSet<CrossGraphFgNode>();

	private Set<FunctionallyGroundedNode> fgNodesOnlyIn1 = new HashFreeSet<FunctionallyGroundedNode>();

	private Set<FunctionallyGroundedNode> fgNodesOnlyIn2 = new HashFreeSet<FunctionallyGroundedNode>();

	private Map<NamedNode, FunctionallyGroundedNode> descriptionPathToFgNode = new HashMap<NamedNode, FunctionallyGroundedNode>();
	
	private FunctionallyGroundedBuilder functionallyGroundedBuilder = new FunctionallyGroundedBuilder();

	/**
	 * @throws IOException
	 * 
	 */
	public MoleculeDiffDeserialized(File file) throws IOException {
		ZipFile zipFile = new ZipFile(file);
		// PathNode zipRoot = new ZipPathNode(zipFile, "");
		PathNode commonFgNodesPath = new ZipPathNode(zipFile,
				"fgNodes/used-common/");// zipRoot.getSubPath("fgnodes/onlyIn1/");
		deserializeFgNodes(zipFile, commonFgNodesPath,
				commonFgNodesInDiffMolecules);

		PathNode fgNodesOnlyIn1Path = new ZipPathNode(zipFile,
				"fgNodes/onlyIn1/");// zipRoot.getSubPath("fgnodes/onlyIn1/");
		deserializeFgNodes(zipFile, fgNodesOnlyIn1Path, fgNodesOnlyIn1);
		PathNode fgNodesOnlyIn2Path = new ZipPathNode(zipFile,
				"fgNodes/onlyIn2/");// zipRoot.getSubPath("fgnodes/onlyIn2/");
		deserializeFgNodes(zipFile, fgNodesOnlyIn2Path, fgNodesOnlyIn2);

		
		
		PathNode cgFgNodesPath = new ZipPathNode(zipFile,
				"fgNodes/crossGraphFgNodes/");
		deserializeCgFgNodes(zipFile, cgFgNodesPath);
		functionallyGroundedBuilder.release();
		PathNode moleculesOnlyIn1Path = new ZipPathNode(zipFile,
				"contextual-molecules/onlyIn1/");
		deserializeMolecules(moleculesOnlyIn1Path, contextualMoleculesOnlyIn1);
		PathNode moleculesOnlyIn2Path = new ZipPathNode(zipFile,
				"contextual-molecules/onlyIn2/");
		deserializeMolecules(moleculesOnlyIn2Path, contextualMoleculesOnlyIn2);
		PathNode terminalMoleculesOnlyIn1Path = new ZipPathNode(zipFile,
				"terminal-molecules/onlyIn1/");
		deserializeTerminalMolecules(terminalMoleculesOnlyIn1Path,
				terminalMoleculesOnlyIn1);
		PathNode terminalMoleculesOnlyIn2Path = new ZipPathNode(zipFile,
				"terminal-molecules/onlyIn2/");
		deserializeTerminalMolecules(terminalMoleculesOnlyIn2Path,
				terminalMoleculesOnlyIn2);
		fgNodesOnlyIn1 = new HashSet<FunctionallyGroundedNode>(fgNodesOnlyIn1);
		fgNodesOnlyIn2 = new HashSet<FunctionallyGroundedNode>(fgNodesOnlyIn2);
	}

	/**
	 * @param moleculesOnlyIn1Path
	 * @param moleculesOnlyIn12
	 * @throws IOException
	 */
	private void deserializeMolecules(PathNode moleculesPath,
			Set<MaximumContextualMolecule> targetMoleculesSet)
			throws IOException {
		String[] names = moleculesPath.list();
		for (String name : names) {
			targetMoleculesSet.add(getContextualMoleculeFromPathNode(
					moleculesPath, name));
		}
	}

	/**
	 * @param name
	 * @param currentPath
	 * @return
	 * @throws IOException
	 */
	private MaximumContextualMolecule getContextualMoleculeFromPathNode(
			PathNode dirPathNode, String name) throws IOException {

		SimpleContextualMolecule molecule = new SimpleContextualMolecule();
		molecule.addAll(getTriplesFromPathNode(dirPathNode, name));
		molecule.markFinalized();
		return molecule;
	}

	/**
	 * @param moleculesOnlyIn1Path
	 * @param moleculesOnlyIn12
	 * @throws IOException
	 */
	private void deserializeTerminalMolecules(PathNode moleculesPath,
			Set<TerminalMolecule> targetMoleculesSet) throws IOException {
		String[] names = moleculesPath.list();
		for (String name : names) {
			targetMoleculesSet.add(getTerminalMoleculeFromPathNode(
					moleculesPath, name));
		}
	}

	/**
	 * @param name
	 * @param currentPath
	 * @return
	 * @throws IOException
	 */
	private TerminalMolecule getTerminalMoleculeFromPathNode(
			PathNode dirPathNode, String name) throws IOException {

		SimpleTerminalMolecule molecule = new SimpleTerminalMolecule();
		molecule.addAll(getTriplesFromPathNode(dirPathNode, name));
		molecule.markFinalized();
		return molecule;
	}

	private Graph getTriplesFromPathNode(PathNode dirPathNode, String name)
			throws IOException {
		PathNode moleculePathNode = dirPathNode.getSubPath(name);
		Model model = ModelFactory.createDefaultModel();
		model.read(moleculePathNode.getInputStream(),
				ReferencingNaturalizer.rootURL + moleculePathNode.getPath());
		Graph graph = JenaUtil.getGraphFromModel(model, false);
		SimpleGraph simpleGraph = new SimpleGraph();
		//Collection<Node> origFgNodes = new HashSet<Node>();
		Map<Node, NamedNode> node2descriptionDocMap = new HashMap<Node, NamedNode>();
		//NamedNode descriptionDoc = null;
		for (Triple triple : graph) {
			if (triple.getPredicate().equals(
					new PropertyNodeImpl(MODELDIFF.functionallyGroundedIn
							.getURI()))) {
				NamedNode descriptionDoc  = (NamedNode) triple.getObject();
				node2descriptionDocMap.put(triple.getSubject(), descriptionDoc);
			} else {
				simpleGraph.add(triple);
			}
		}
		simpleGraph.markFinalized();
		SimpleGraph tripleSet = simpleGraph;
		for (Entry<Node, NamedNode> entry : node2descriptionDocMap.entrySet()) {
			tripleSet = replaceNode(entry.getKey(), descriptionPathToFgNode
					.get(entry.getValue()), tripleSet);
			tripleSet.markFinalized();
		}
		return tripleSet;
	}

	/**
	 * @param zipFile 
	 * @param cgFgNodesPath
	 * @param fgNodesOnlyIn22
	 * @throws IOException
	 */
	private void deserializeCgFgNodes(ZipFile zipFile, PathNode cgFgNodesPath)
			throws IOException {
		String[] names = cgFgNodesPath.list();
		for (String name : names) {
			PathNode currentPath = cgFgNodesPath.getSubPath(name);
			crossGraphFgNodes.add(getCgFgNodeFromPathNode(zipFile, currentPath));
		}
	}

	/**
	 * @param zipFile 
	 * @param currentPath
	 * @return
	 * @throws IOException
	 */
	private CrossGraphFgNode getCgFgNodeFromPathNode(ZipFile zipFile, PathNode currentPath)
			throws IOException {
		NamedNode describingResource = new NamedNodeImpl(
				ReferencingNaturalizer.rootURL + currentPath.getPath());
		CrossGraphFgNode result = new CrossGraphFgNode();
		PathNode nodesIn1Path = currentPath.getSubPath("onlyIn1/");
		deserializeFgNodes(zipFile, nodesIn1Path, result.getNodesIn1());
		PathNode nodesIn2Path = currentPath.getSubPath("onlyIn2/");
		deserializeFgNodes(zipFile, nodesIn2Path, result.getNodesIn2());
		descriptionPathToFgNode.put(describingResource, result);
		return result;
	}

	/**
	 * @param zipFile
	 * @param fgNodesPath
	 * @param fgNodesTargetSet
	 * @throws IOException
	 */
	private void deserializeFgNodes(ZipFile zipFile, PathNode fgNodesPath,
			Set<FunctionallyGroundedNode> fgNodesTargetSet) throws IOException {
		String[] names = fgNodesPath.list();
		for (String name : names) {
			PathNode currentPath = fgNodesPath.getSubPath(name);
			fgNodesTargetSet.add(getFgNodeFromPathNode(zipFile, currentPath));
		}
	}

	private FunctionallyGroundedNode getFgNodeFromDescribingNode(
			ZipFile zipFile, NamedNode describingResource) throws IOException {
		FunctionallyGroundedNode existing = descriptionPathToFgNode.get(describingResource);
		if (existing != null) {
			return existing;
		}
		try {
			String relativePath = new URI(ReferencingNaturalizer.rootURL)
					.relativize(describingResource.getURIRef(), URI.SAMEDOCUMENT
							| URI.ABSOLUTE | URI.RELATIVE | URI.PARENT);
			PathNode pathNode = new ZipPathNode(zipFile, relativePath);
			//FunctionallyGroundedNode result = 
			return getFgNodeFromPathNode(zipFile, pathNode, describingResource);
		} catch (MalformedURIException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param zipFile
	 * @param pathNode
	 * @return
	 * @throws IOException
	 */
	private FunctionallyGroundedNode getFgNodeFromPathNode(ZipFile zipFile,
			PathNode pathNode) throws IOException {
		
		NamedNode describingResource = new NamedNodeImpl(
				ReferencingNaturalizer.rootURL + pathNode.getPath());
		FunctionallyGroundedNode existing = descriptionPathToFgNode.get(describingResource);
		if (existing != null) {
			return existing;
		}
		return getFgNodeFromPathNode(zipFile, pathNode, describingResource);
	}
	
	private FunctionallyGroundedNode getFgNodeFromPathNode(ZipFile zipFile,
			PathNode pathNode, NamedNode describingResource) throws IOException {
		Node afgn = null;
		FunctionallyGroundedNodeImpl result = functionallyGroundedBuilder.createFGNode();
		descriptionPathToFgNode.put(describingResource, result);
		for (String moleculeNodeName : pathNode.list()) {
			PathNode moleculePathNode = pathNode.getSubPath(moleculeNodeName);
			Model model = ModelFactory.createDefaultModel();
			model
					.read(moleculePathNode.getInputStream(),
							ReferencingNaturalizer.rootURL
									+ moleculePathNode.getPath());
			/*
			 * StmtIterator specialStmtIter = model.listStatements(null,
			 * MODELDIFF.functionallyGroundedIn, (Resource)null); Statement
			 * specialStmt = specialStmtIter.nextStatement();
			 * specialStmtIter.close(); Resource groundedNodedRes =
			 * specialStmt.getSubject(); specialStmt.remove();
			 */
			Graph graph = JenaUtil.getGraphFromModel(model, false);
			Graph tripleSet = new SimpleGraph();
			Node currentAfgn = null;
			Map<Node, FunctionallyGroundedNode> innerReplacements = new HashMap<Node, FunctionallyGroundedNode>();
			for (Triple triple : graph) {
				if (triple.getPredicate().equals(
						new PropertyNodeImpl(MODELDIFF.functionallyGroundedIn
								.getURI()))) {
					Node subject = triple.getSubject();
					if (!describingResource.equals(triple.getObject())) {
						// throw new RuntimeException("invalid diff");
						// now legal as fg-nodes may reference to others in
						// their nt-molecules
						//infinite recursion by cache
						FunctionallyGroundedNode replacement = getFgNodeFromDescribingNode(zipFile,
								(NamedNode) triple.getObject());
						innerReplacements.put(subject, replacement);
						
					} else {
						currentAfgn = subject;
					}
				} else {
					tripleSet.add(triple);
				}
			}
			if (afgn == null) {
				afgn = currentAfgn;
			} else {
				tripleSet = replaceNode(currentAfgn, afgn, tripleSet);
			}
			for (Entry<Node, FunctionallyGroundedNode> entry : innerReplacements.entrySet()) {
				tripleSet = replaceNode(entry.getKey(), entry.getValue(), tripleSet);
			}
			NonTerminalMolecule ntMolecule = functionallyGroundedBuilder.createNTMolecule(afgn);
			ntMolecule.addAll(tripleSet);
			result.addMolecule(ntMolecule);
		}
		
		return result;
	}

	/**
	 * @param currentAfgn
	 * @param afgn
	 * @param tripleSet
	 */
	private <T extends Collection<? extends Triple>> SimpleGraph replaceNode(
			Node currentAfgn, Node afgn, T tripleSet) {
		try {
			return new GraphUtil<SimpleGraph>().replaceNode(tripleSet, currentAfgn, afgn, new SimpleGraph());
		} catch (SourceNodeNotFoundException e) {
			SimpleGraph result = new SimpleGraph();
			result.addAll(tripleSet);
			
			return result;
		}
		/*
		 * Set<Triple> result = new HashSet<Triple>(); for (Triple triple :
		 * tripleSet) { boolean modified = false; Node subject =
		 * triple.getSubject(); if (subject.equals(currentAfgn)) { subject =
		 * afgn; modified = true; } Node object = triple.getObject(); if
		 * (object.equals(currentAfgn)) { object = afgn; modified = true; } if
		 * (modified) { result .add(new TripleImp(subject,
		 * triple.getPredicate(), object)); } else { result.add(triple); } }
		 * return result;
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getCrossGraphFgNodes()
	 */
	public Set<CrossGraphFgNode> getCrossGraphFgNodes() {
		return crossGraphFgNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getCommonFgNodes()
	 */
	public Set<FunctionallyGroundedNode> getCommonFgNodesInDiffMolecules() {
		return commonFgNodesInDiffMolecules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getFgNodesOnlyIn1()
	 */
	public Set<FunctionallyGroundedNode> getFgNodesOnlyIn1() {
		return fgNodesOnlyIn1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getFgNodesOnlyIn2()
	 */
	public Set<FunctionallyGroundedNode> getFgNodesOnlyIn2() {
		return fgNodesOnlyIn2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.rdf.molecules.diff.MoleculeDiff#getContainingCrossGrapgFgNode(org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode)
	 */
	public CrossGraphFgNode getContainingCrossGrapgFgNode(
			FunctionallyGroundedNode fgNode) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<MaximumContextualMolecule> getContextualMoleculesOnlyIn1() {
		return contextualMoleculesOnlyIn1;
	}

	public Set<MaximumContextualMolecule> getContextualMoleculesOnlyIn2() {
		return contextualMoleculesOnlyIn2;
	}

	public Set<TerminalMolecule> getTerminalMoleculesOnlyIn1() {
		return terminalMoleculesOnlyIn1;
	}

	public Set<TerminalMolecule> getTerminalMoleculesOnlyIn2() {
		return terminalMoleculesOnlyIn2;
	}

}
