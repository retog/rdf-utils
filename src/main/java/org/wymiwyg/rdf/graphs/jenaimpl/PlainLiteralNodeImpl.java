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
package org.wymiwyg.rdf.graphs.jenaimpl;

import java.util.Locale;

import org.wymiwyg.rdf.graphs.AbstractPlainLiteralNode;
import org.wymiwyg.rdf.graphs.PlainLiteralNode;

import com.hp.hpl.jena.rdf.model.Literal;

/**
 * @author reto
 *
 */
public class PlainLiteralNodeImpl  extends AbstractPlainLiteralNode implements PlainLiteralNode {

	private Literal literal;



	/**
	 * @param literal
	 */
	public PlainLiteralNodeImpl(Literal literal) {
		this.literal = literal;
	}

	public String getLexicalForm() {
		return literal.getLexicalForm();
	}


	public Locale getLocale() {
		String langString = literal.getLanguage();
		if (!langString.equals("")) {
			return new Locale(langString);
		} else {
			return null;
		}
	}

	

	public String toString() {
		return '\"'+literal.toString()+'\"';
	}

}
