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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.commons.util.arguments.AnnotatedInterfaceArguments;
import org.wymiwyg.commons.util.arguments.ArgumentHandler;
import org.wymiwyg.commons.util.arguments.InvalidArgumentsException;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.utils.jena.LeanDiffPatch;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class PatchMain {

	private static final Log log = LogFactory.getLog(PatchMain.class);


	/**
	 * @param args
	 * @throws InvalidArgumentsException
	 */
	public static void main(String[] args) throws InvalidArgumentsException {
		ArgumentHandler argumentHandler = new ArgumentHandler(args);
		main("java -jar rdf-diff.jar", argumentHandler);
	}

	/**
	 * @param executionString
	 * @param argumentHandler
	 * @throws InvalidArgumentsException
	 */
	public static void main(final String executionString,
			ArgumentHandler argumentHandler) throws InvalidArgumentsException {
		HelpArguments helpArguments;
		try {
			helpArguments = AnnotatedInterfaceArguments.getInstance(
					HelpArguments.class, argumentHandler).getValueObject();
		} catch (InvalidArgumentsException e) {
			System.err.println(e.getMessage());
			showHelp(executionString);
			return;
		}
		if (helpArguments.getShowHelp()) {
			showHelp(executionString);
			return;
		}
		PatchArguments arguments;
		try {
			arguments = AnnotatedInterfaceArguments.getInstance(
					PatchArguments.class, argumentHandler).getValueObject();
		} catch (InvalidArgumentsException e) {
			System.err.println(e.getMessage());
			showHelp(executionString);
			return;
		}
		if (arguments.getShowHelp()) {
			showHelp(executionString);
		}
		log.info("Input: " + arguments.getInputModel());
		log.info("Target: " + arguments.getTargetModel());
		Model inputModel = ModelFactory.createDefaultModel();
		Model targetModel = ModelFactory.createDefaultModel();
		if (!arguments.getInputModel().exists()) {
			System.err.println(arguments.getInputModel().getAbsolutePath()
					+ " not found.");
			showHelp(executionString);
			return;
		}
		if (!arguments.getDiffPath().exists()) {
			System.err.println(arguments.getDiffPath().getAbsolutePath()
					+ " not found.");
			showHelp(executionString);
			return;
		}
		try {
			inputModel.read(arguments.getInputModel().toURL().toString(), arguments
					.getFileFormat());

		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		MoleculeDiff diff;
		try {
			diff = LeanDiffPatch.deserializeDiff(arguments.getDiffPath());
		} catch (IOException e) {
			log.error("Failure reading diff", e);
			return;
		}
		File ontologyFile = arguments.getModelOntologyPath();
		
		
		if (ontologyFile != null) {
			Model ontology = ModelFactory.createDefaultModel();
			LeanDiffPatch.setOntology(ontology);
		}
		LeanDiffPatch.setUseDefaultOntology(arguments.getUseDefaultOntology());
		targetModel = LeanDiffPatch.patch(inputModel, diff);
		try {
		OutputStream out = new FileOutputStream(arguments.getTargetModel());
		targetModel.write(out, arguments.getFileFormat());
		out.close();
		} catch (Exception e) {
			log.error("Failed writing output", e);
		}

	}

	/**
	 * 
	 */
	private static void showHelp(String executionString) {
		System.out.println("Usage:");
		System.out.print(executionString);
		System.out.print(' ');
		System.out.println(AnnotatedInterfaceArguments
				.getArgumentsSyntax(PatchArguments.class));
		PrintWriter out = new PrintWriter(System.out, true);
		AnnotatedInterfaceArguments.printArgumentDescriptions(
				PatchArguments.class, out);
		out.flush();
	}

}
