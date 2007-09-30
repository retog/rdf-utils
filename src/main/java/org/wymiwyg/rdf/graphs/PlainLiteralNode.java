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

import java.util.Locale;

/**
 * @author reto
 *
 */
public interface PlainLiteralNode extends LiteralNode {
	public Locale getLocale();
	
	/** 
	 * 
	 * @return if lexical form and locale are equals
	 */
	public boolean equals(Object obj);
	
	/**
	 * 
	 * @return the hashCode of the lexical form plus the hashCode of the locale
	 */
	public int hashCode();
	
}
