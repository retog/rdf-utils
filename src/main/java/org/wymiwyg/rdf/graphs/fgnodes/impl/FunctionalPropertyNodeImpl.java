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
package org.wymiwyg.rdf.graphs.fgnodes.impl;

import org.wymiwyg.rdf.graphs.fgnodes.FunctionalPropertyNode;
import org.wymiwyg.rdf.graphs.impl.PropertyNodeImpl;

/**
 * @author reto
 *
 */
public class FunctionalPropertyNodeImpl extends PropertyNodeImpl implements
		FunctionalPropertyNode {

	/**
	 * @param uriRef
	 */
	public FunctionalPropertyNodeImpl(String uriRef) {
		super(uriRef);
	}

}
