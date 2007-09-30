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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.commons.util.arguments.AnnotatedInterfaceArguments;
import org.wymiwyg.commons.util.arguments.ArgumentHandler;
import org.wymiwyg.commons.util.arguments.InvalidArgumentsException;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiffImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class DiffMain {

	private static final Log log = LogFactory.getLog(DiffMain.class);


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
		DiffArguments arguments;
		try {
			arguments = AnnotatedInterfaceArguments.getInstance(
					DiffArguments.class, argumentHandler).getValueObject();
		} catch (InvalidArgumentsException e) {
			System.err.println(e.getMessage());
			showHelp(executionString);
			return;
		}
		if (arguments.getShowHelp()) {
			showHelp(executionString);
		}
		if ((arguments.getDiffPath() == null) && (!arguments.getOutputDiff())) {
			log.warn("You suppressed output of diff and did not specified a location to write the serialized diff. You wont be able to see or process the diff result!");
		}
		log.info("Model 1: " + arguments.getModel1Path());
		log.info("Model 2: " + arguments.getModel2Path());
		Model model1 = ModelFactory.createDefaultModel();
		Model model2 = ModelFactory.createDefaultModel();
		if (!arguments.getModel1Path().exists()) {
			System.err.println(arguments.getModel1Path().getAbsolutePath()
					+ " not found.");
			showHelp(executionString);
			return;
		}
		if (!arguments.getModel2Path().exists()) {
			System.err.println(arguments.getModel2Path().getAbsolutePath()
					+ " not found.");
			showHelp(executionString);
			return;
		}
		try {
			model1.read(arguments.getModel1Path().toURL().toString(), arguments
					.getFileFormat());
			model2.read(arguments.getModel2Path().toURL().toString(), arguments
					.getFileFormat());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		MoleculeDiff diff;
		File ontologyFile = arguments.getModelOntologyPath();
		if (ontologyFile != null) {
			Model ontology = ModelFactory.createDefaultModel();
			try {
				ontology.read(ontologyFile.toURL().toString(), arguments
						.getFileFormat());
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			diff = new MoleculeDiffImpl(model1, model2, ontology, arguments
					.getUseDefaultOntology());
		} else {

			diff = new MoleculeDiffImpl(model1, model2, arguments
					.getUseDefaultOntology());
		}
		if (arguments.getDiffPath() != null) {
			try {
				diff.serialize(arguments.getDiffPath());
			} catch (IOException e) {
				log.error("Failed serializing diff", e);
			}
		}
		if (arguments.getOutputDiff()) {
			diff.print(new PrintWriter(System.out, true));
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
				.getArgumentsSyntax(DiffArguments.class));
		PrintWriter out = new PrintWriter(System.out, true);
		AnnotatedInterfaceArguments.printArgumentDescriptions(
				DiffArguments.class, out);
		out.flush();
	}

}
