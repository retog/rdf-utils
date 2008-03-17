/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.rdf.graphs.fgnodes.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.rdf.graphs.LiteralNode;
import org.wymiwyg.rdf.graphs.NamedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PlainLiteralNode;
import org.wymiwyg.rdf.graphs.Triple;
import org.wymiwyg.rdf.graphs.TypedLiteralNode;
import org.wymiwyg.rdf.graphs.fgnodes.FunctionallyGroundedNode;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * Abstract implementation of FunctionallyGroundedNode providing the equals and
 * the (strong) hashcode methods.
 * 
 * Subclasses will invoke markFinalized when the fg-node is finalized.
 * 
 * @author reto
 * 
 */
public abstract class FunctionallyGroundedNodeBase implements
		FunctionallyGroundedNode {

	private static int instanceCount = 0;
	{
		instanceCount++;
	}

	private final int instanceNumber = instanceCount;

	private static final Log log = LogFactory
			.getLog(FunctionallyGroundedNodeBase.class);

	/**
	 * utility method for writing strings escaped as in n-triples
	 */
	private static void writeString(String s, Writer writer) throws IOException {

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\\' || c == '"') {
				writer.append('\\');
				writer.append(c);
			} else if (c == '\n') {
				writer.append("\\n");
			} else if (c == '\r') {
				writer.append("\\r");
			} else if (c == '\t') {
				writer.append("\\t");
			} else if (c >= 32 && c < 127) {
				writer.append(c);
			} else {
				String hexstr = Integer.toHexString(c).toUpperCase();
				int pad = 4 - hexstr.length();
				writer.append("\\u");
				for (; pad > 0; pad--)
					writer.append("0");
				writer.append(hexstr);
			}
		}
	}

	private boolean finalized;

	private int hash;

	private boolean hashComputed;

	byte[] strongHashCode;

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof FunctionallyGroundedNode)) {
			return false;
		}

		FunctionallyGroundedNode other = (FunctionallyGroundedNode) obj;
		if (!isFinalized() || !other.isFinalized()) {
			return false;
		}
		byte[] otherStronhHash = other.strongHashCode();
		return Arrays.equals(strongHashCode(), otherStronhHash);
	}

	/**
	 * After finalization the hashCode is a hash is based on the strong
	 * hashcode, before the hashCode of the superclass (Object) is returned.
	 * 
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		if (finalized) {
			if (!hashComputed) {
				synchronized (this) {
					if (!hashComputed) {
						hash = Arrays.hashCode(strongHashCode());
						hashComputed = true;						
					}
				}
			}
			return hash;
		}
		log.warn("requesting hash for unfinalized fg-node");
		return super.hashCode();
	}

	/**
	 * @return
	 */
	public boolean isFinalized() {
		return finalized;
	}

	public byte[] strongHashCode() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("sha1 not supported by platform");
		}
		try {
			strongHashCode = md.digest(serialize(this, new ArrayList<FunctionallyGroundedNode>()).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("utf-8 not supported by platform");
		}
		// if (!finalized) {
		// log.warn("requesting strong hash for unfinalized fg-node");
		// return computeStrongHashCode();
		// }
		// if (strongHashCode == null) {
		// synchronized (this) {
		// if (strongHashCode == null) {
		// strongHashCode = computeStrongHashCode();
		//
		// }
		// }
		// }
		return strongHashCode;
	}

	/**
	 * @param commonResultPart
	 * @param node
	 * @throws IOException
	 */
	private static void appendLiteral(Writer writer, LiteralNode node)
			throws IOException {
		writer.append('"');
		writeString(node.getLexicalForm(), writer);
		writer.append('"');
		if (node instanceof TypedLiteralNode) {
			String dt = ((TypedLiteralNode) node).getDataType().toString();
			if (dt != null && !dt.equals(""))
				writer.append("^^<");
			writer.append(dt);
			writer.append(">");
		} else {
			Locale locale = ((PlainLiteralNode) node).getLocale();
			if (locale != null) {
				writer.append("@" + locale.toString());
			}
		}

	}

	/**
	 * subclasses invoke this method when this fg-node is finalized
	 */
	public void markFinalized() {
		finalized = true;
	}

	
	
	public String toString() {
		return (finalized ? "" : "unfinalized, ")+"instance nr "+instanceNumber+"\n"+serialize(this, new ArrayList<FunctionallyGroundedNode>());
	}

	/**
	 * @param sout
	 * @param name
	 */
	private static String serialize(FunctionallyGroundedNode node,
			ArrayList<FunctionallyGroundedNode> visitedFgNodes) {
		StringWriter writer = new StringWriter();
		writer.write("[");
		for (int i = 0; i < visitedFgNodes.size(); i++) {
			FunctionallyGroundedNode visitedFgNode = visitedFgNodes.get(i);
			// TODO deal with the case they are not the same instance
			if (visitedFgNode == node) {
				writer.write(Integer.toString(i));
				writer.write("]");
				return writer.toString();
			}
		}
		visitedFgNodes.add(node);
		SortedSet<String> moleculeStrings = new TreeSet<String>();
		for (NonTerminalMolecule molecule : node.getGroundingMolecules()) {
			moleculeStrings.add(serializeMolcule(molecule,
					new ArrayList<FunctionallyGroundedNode>(visitedFgNodes)));
		}
		for (String moleculeString : moleculeStrings) {
			writer.write(moleculeString);
			writer.write("\n");
		}
		writer.write("]");
		return writer.toString();
	}

	/**
	 * @param writer
	 * @param molecule
	 * @param name
	 */
	private static String serializeMolcule(NonTerminalMolecule molecule,
			ArrayList<FunctionallyGroundedNode> visitedFgNodes) {
		StringWriter writer = new StringWriter();
		Triple triple = molecule.iterator().next();
		Node groundingNode;
		if (triple.getSubject() != NonTerminalMolecule.GROUNDED_NODE) {
			writer.append('i');
			groundingNode = triple.getSubject();
		} else {
			groundingNode = triple.getObject();
		}
		writer.append('<');
		writer.append(triple.getPredicate().getURIRef());
		writer.append('>');
		writer.append(' ');
		if (groundingNode instanceof FunctionallyGroundedNode) {
			writer.append(serialize((FunctionallyGroundedNode) groundingNode,
					visitedFgNodes));
		} else {
			if (groundingNode instanceof LiteralNode) {
				try {
					appendLiteral(writer, (LiteralNode) groundingNode);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				if (groundingNode instanceof NamedNode) {
					writer.append('<');
					writer.append(((NamedNode) groundingNode).getURIRef());
					writer.append('>');
				} else {
					// e.g. ModelGroundedNode
					writer.append('_');
					writer.append(groundingNode.toString());
					writer.append('_');
				}
			}
		}
		return writer.toString();

	}

}
