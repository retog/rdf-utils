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
package org.wymiwyg.rdf.graphs.matcher;

import org.wymiwyg.rdf.graphs.GroundedNode;
import org.wymiwyg.rdf.graphs.Node;
import org.wymiwyg.rdf.graphs.PropertyNode;
import org.wymiwyg.rdf.graphs.Triple;


/**
 * @author reto
 *
 */
public class TriplePattern {

	Node subject, object;
	PropertyNode predicate;
	final static Node CENTER = new GroundedNode() {};
	/**
	 * @param triple
	 */
	/*public TriplePattern(Triple triple) {
		subject = triple.getSubject();
		object = triple.getObject();
		predicate = triple.getPredicate();
	}*/

	/** This constructor is used when using the class as "PropertyPattern"
	 * @param triple
	 */
	public TriplePattern(Triple triple, Node center) {
		subject = triple.getSubject();
		if (!(subject instanceof GroundedNode)) {
			if (subject.equals(center)) {
				subject = CENTER;
			} else {
				//subject = null;
			}
		}
		object = triple.getObject();
		if (!(object instanceof GroundedNode)) {
			if (object.equals(center)) {
				object = CENTER;
			} else {
				//object = null;
			}
		}
		predicate = triple.getPredicate();
	}
	
	//public boolean equals(Object obj) {
	//	throw new RuntimeException("not supported");
		/*if (this == obj) {
			return true;
		}
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		TriplePattern other = (TriplePattern) obj;
		
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else {
			if (!subject.equals(other.subject)) {
				return false;
			}
		}
		if (object == null) {
			if (other.object != null) {
				return false;
			}
		} else {
			if (!object.equals(other.object)) {
				return false;
			}
		}
		return predicate.equals(other.predicate);/*
		/*if (predicate == null) {
			if (other.predicate != null) {
				return false;
			}
		} else {
			if (!predicate.equals(other.predicate)) {
				return false;
			}
		}*/
	//}

	public int contextHash(int level) {
		int hash = 0;
		hash ^= (((subject instanceof ContextNode) ? ((ContextNode)subject).contextHash(level)  : subject.hashCode()) * 0x101 );
		hash ^= (((object instanceof ContextNode) ? ((ContextNode)object).contextHash(level) : object.hashCode())  * 0x41 );
        hash ^= predicate.hashCode() * 0x3f;
        return hash;
	}

	public String toString() {
		return subject+" "+predicate+" "+object+".\n";
	}
	

}
