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
public interface LeanifyArguments extends GenericArguments {
	@CommandLine (
		longName ="model",
		shortName = {"M"},
		required = true,
		description = "The file containg the model to be leanified"
	)
	public File getModelPath();

	@CommandLine (
		longName ="pedantic",
		shortName = "P", 
		isSwitch = true,
		description = "Remove all redundancies but takes ages (with bigger models), without this options ungrounded nodes are not removed if they are made obsolete by grounded nodes"
	)
	public boolean isPedantic();

	

	

}
