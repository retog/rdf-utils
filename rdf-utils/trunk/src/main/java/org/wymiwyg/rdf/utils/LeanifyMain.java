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
import java.io.PrintWriter;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wymiwyg.commons.util.arguments.AnnotatedInterfaceArguments;
import org.wymiwyg.commons.util.arguments.ArgumentHandler;
import org.wymiwyg.commons.util.arguments.InvalidArgumentsException;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.leanifier.GraphLeanifier;
import org.wymiwyg.rdf.leanifier.MoleculeBasedLeanifier;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author reto
 * 
 */
public class LeanifyMain {

	private static final Log log = LogFactory.getLog(LeanifyMain.class);

	/**
	 * @param args
	 * @throws InvalidArgumentsException
	 */
	public static void main(String[] args) throws InvalidArgumentsException {
		ArgumentHandler argumentHandler = new ArgumentHandler(args);
		main("java -jar rdf-Leanify.jar", argumentHandler);
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
		LeanifyArguments arguments;
		try {
			arguments = AnnotatedInterfaceArguments.getInstance(
					LeanifyArguments.class, argumentHandler).getValueObject();
		} catch (InvalidArgumentsException e) {
			System.err.println(e.getMessage());
			showHelp(executionString);
			return;
		}
		if (arguments.getShowHelp()) {
			showHelp(executionString);
		}
		log.info("Model: " + arguments.getModelPath());
		Model model = ModelFactory.createDefaultModel();
		if (!arguments.getModelPath().exists()) {
			System.err.println(arguments.getModelPath().getAbsolutePath()
					+ " not found.");
			showHelp(executionString);
			return;
		}

		try {
			model.read(arguments.getModelPath().toURL().toString(), arguments
					.getFileFormat());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		if (arguments.isPedantic()) {
			Graph graph = JenaUtil.getGraphFromModel(model, false);
			GraphLeanifier.makeLean(graph);
			model = JenaUtil.getModelFromGraph(graph);
			model.write(System.out, arguments.getFileFormat());
		} else {
			File ontologyFile = arguments.getModelOntologyPath();
			Graph source;
			if (ontologyFile != null) {
				Model ontology = ModelFactory.createDefaultModel();
				try {
					ontology.read(ontologyFile.toURL().toString(), arguments
							.getFileFormat());
				} catch (MalformedURLException e) {
					throw new RuntimeException(e);
				}
				source = JenaUtil.getGraphFromModel(model, ontology, arguments.getUseDefaultOntology());		
			} else {
				source = JenaUtil.getGraphFromModel(model, arguments.getUseDefaultOntology());
			}
			Graph result = MoleculeBasedLeanifier.getLeanVersionOf(source);
			JenaUtil.getModelFromGraph(result).write(System.out, arguments.getFileFormat());
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
				.getArgumentsSyntax(LeanifyArguments.class));
		PrintWriter out = new PrintWriter(System.out, true);
		AnnotatedInterfaceArguments.printArgumentDescriptions(
				LeanifyArguments.class, out);
		out.flush();
	}

}
