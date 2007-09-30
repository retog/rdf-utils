package org.wymiwyg.rdf.graphs.fgnodes;

import java.util.Set;

import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * A FunctionallyGroundedNode aka fg-node is a node which has a
 * context-independent identity thanks to one or more functional or inverse
 * functional properties, these statements are called NonTerminalMolecule (or
 * nt-molecules).
 * 
 * An fg-node may contain nt-molecules pointing back to the fg-node, it must
 * however contain at least one nt-molecule which does not point back to the
 * fg-node, i.e. which is (directly or indirectly) grounded by a
 * NaturallyGroundedNode.
 * 
 * @author reto
 * 
 */
public interface FunctionallyGroundedNode extends GroundedNode {

	/**
	 * @return the set of nt-molecules grounding this node, this node is the
	 *         active functionally grounded node of all molecules in the set
	 */
	public Set<NonTerminalMolecule> getGroundingMolecules();

	/**
	 * two instances are equal iff they are both finalized and their sets of
	 * Grounding Molecules are equal or if they are ==
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return true iff this is equals to obj
	 */
	public boolean equals(Object obj);

	/**
	 * The strong hashcode of an fg-node is computed as follows:<br/>
	 * 
	 * For every nt-molecule is serialized to a string, if the nt-molecules has
	 * an ifp as predicate its URIRef is bracketed with &lt; and &gt; and
	 * appended to the StringBuffer otherwise (i.e. when the predicate is an fp)
	 * the letter "i" is appended prior to the bracketed URIRef, after this a
	 * space-character is added to the buffer. If the grounding part of the
	 * nt-molecule is a literal or a named node it is appended to the buffer the
	 * same way as it would appear in an N-Triple document and followed by a
	 * space. If the grounding part is itself an fg-node the process is done
	 * recursilvely, but if an fg-node is encountered which was already
	 * processed as an- fg-node directly or indirectly containing it, it is not
	 * reprocesses instead the position at which it is already in the buffer is
	 * appended as a decimal number.<br/>
	 * 
	 * The strings for the individual fg-nodes are sorted using the natural
	 * order of StringS, joined and put between '[' and ']' to form a
	 * predictable serialization of an fg-node
	 * 
	 * The resulting String is converted to a byte array using the UTF-8
	 * encoding,the strong hashcode is the sha1-digest of this string.
	 * 
	 * @return the strong hashcode of the fg-node.
	 */
	public byte[] strongHashCode();

	/**
	 * @return true if this node is finalized and no molecule will be added to
	 *         it or to any of the fg-nodes directly or indirectly contained in
	 *         its molecules
	 */
	public boolean isFinalized();


}
