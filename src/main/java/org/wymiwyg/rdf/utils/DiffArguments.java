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
public interface DiffArguments extends GenericArguments {
	@CommandLine (
		longName ="model1",
		shortName = {"M1","B"},
		required = true,
		description = "the first model of the comparison"
	)
	public File getModel1Path();
	
	@CommandLine (
		longName ="model2",
		shortName = "M2", 
		required = true,
		description = "the second model of the comparison"
	)
	public File getModel2Path();
	
	@CommandLine (
			longName ="serializedDiff",
			shortName = "S", 
			required = false,
			description = "the location of the serialized diff to be created"
	)
	public File getDiffPath();

	@CommandLine (
			longName ="outputDiff",
			shortName = "OUT", 
			isSwitch = false,
			defaultBooleanValue = true,
			description = "output the diff in human readable form to standard output"
	)
	public boolean getOutputDiff();
	
	/*@CommandLine (
		longName ="common",
		shortName = "C", 
		isSwitch = true,
		description = "show the molecules contained in both models as well as the differences"
	)
	public boolean getShowCommon();*/

	

	

}
