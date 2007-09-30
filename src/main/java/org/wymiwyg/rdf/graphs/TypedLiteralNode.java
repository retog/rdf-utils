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
package org.wymiwyg.rdf.graphs;

import java.net.URI;

/**
 * @author reto
 *
 */
public interface TypedLiteralNode extends LiteralNode {

	public URI getDataType();
	
	/** two TypedLiteralNodes are equals iff the have the same lexicalform and
	 * the same dataType
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj);

	/** The hascode is equals to the hascode of the lexical form plus the hashcode of the dataType
	 * 
	 * @return
	 */
	public int hashCode();
}
