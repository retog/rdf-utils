package org.wymiwyg.rdf.graphs;

import java.util.Collection;



/** A graph, modelled a Set ot triples.
 * This interface does not extend java.util.Set because of the different identity constraints, i.e. two graph may be 
 * 
 * @author reto
 *
 */
public interface Graph extends Collection<Triple> {

	/** return true is two graphs entail each other
	 * 
	 * @return
	 */
	public boolean equals(Object obj);
	
	/** return the sum of the blank-nodes independent hashs of the triples. Moremprecisely the hash of the triple
	 * is calculated as following
	 * (hash(subject) >> 1) ^  hash(hashCode) ^ (hash(hashCode) << 1)
	 * Where the hash-fucntion return the hashCode of the argument for grounded arguments and 0 otherwise. 
	 * 
	 * 
	 * @return
	 */
	public int hashCode();

}