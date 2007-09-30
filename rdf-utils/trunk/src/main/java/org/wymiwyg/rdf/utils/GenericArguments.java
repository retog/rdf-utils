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
package org.wymiwyg.rdf.utils;

import java.io.File;

import org.wymiwyg.commons.util.arguments.CommandLine;

/**
 * @author reto
 *
 */
public interface GenericArguments {
	
	@CommandLine (
			longName ="useDefaultOntology",
			shortName = "D",
			isSwitch = false,
			defaultBooleanValue = true,
			description = "use the default ontology to find functional and inverse functional properties"
	)
	public boolean getUseDefaultOntology();
	
	
	@CommandLine (
			longName ="help",
			shortName = "H",
			isSwitch = true
	)
	public boolean getShowHelp();
	
	@CommandLine (
			longName ="ontology",
			shortName = "O",
			description = "use the ontology to find functional and inverse functional properties"
	)
	public File getModelOntologyPath();
	
	@CommandLine (
			longName ="format",
			shortName = "F",
			defaultValue ="RDF/XML",
			description = "the format used for (de)serializing RDF, either \"RDF/XML\" (default), \"N3\" or \"N-TRIPLE\""
	)
	public String getFileFormat();
}

