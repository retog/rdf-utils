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
package org.wymiwyg.rdf.graphs.impl;

import java.net.URI;

import org.wymiwyg.rdf.graphs.AbstractTypedLiteralNode;
import org.wymiwyg.rdf.graphs.TypedLiteralNode;

/**
 * @author reto
 *
 */
public class TypedLiteralNodeImpl extends AbstractTypedLiteralNode implements 
		TypedLiteralNode {

	private String lexicalForm;
	private URI dataType;
	private int hashCode;

	/**
	 * @param lexicalForm 
	 * @param dataType 
	 */
	public TypedLiteralNodeImpl(String lexicalForm, URI dataType) {
		this.lexicalForm = lexicalForm;
		this.dataType = dataType;
		this.hashCode = super.hashCode();
	}
	
	public URI getDataType() {
		return dataType;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.LiteralNode#getLexicalForm()
	 */
	public String getLexicalForm() {
		return lexicalForm;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
