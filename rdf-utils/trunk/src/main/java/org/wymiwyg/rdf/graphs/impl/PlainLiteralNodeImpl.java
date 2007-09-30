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

import java.util.Locale;

import org.wymiwyg.rdf.graphs.AbstractPlainLiteralNode;

/**
 * @author reto
 *
 */
public class PlainLiteralNodeImpl extends AbstractPlainLiteralNode {

	private String lexicalForm;
	private Locale locale;

	/**
	 * @param literal
	 */
	public PlainLiteralNodeImpl(String lexicalForm) {
		this.lexicalForm = lexicalForm;
		
	}
	
	public PlainLiteralNodeImpl(String lexicalForm, Locale locale) {
		this.lexicalForm = lexicalForm;
		this.locale = locale;
		
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.PlainLiteralNode#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.rdf.graphs.LiteralNode#getLexicalForm()
	 */
	public String getLexicalForm() {
		return lexicalForm;
	}

	

}
