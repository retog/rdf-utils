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

import java.util.List;

import org.wymiwyg.commons.util.arguments.ArgumentHandler;
import org.wymiwyg.commons.util.arguments.ArgumentProcessor;
import org.wymiwyg.commons.util.arguments.InvalidArgumentsException;

/**
 * @author reto
 * 
 */
public class Main {

	private static String command;


	public static void main(String[] args) throws InvalidArgumentsException {
		if (args.length == 0) {
			showHelp();
			return;
		}
		String[] firstArg = new String[1];
		firstArg[0] = args[0];
		ArgumentHandler argumentHandler = new ArgumentHandler(firstArg);
		HelpArguments helpArguments;
		try {
			helpArguments = argumentHandler.getInstance(HelpArguments.class);
		} catch (InvalidArgumentsException e) {
			System.err.println(e.getMessage());
			showHelp();
			return;
		}
		if (helpArguments.getShowHelp()) {
			showHelp();
			return;
		}
		argumentHandler.processArguments(new ArgumentProcessor() {
			/**
			 * @see org.wymiwyg.commons.util.arguments.ArgumentProcessor#process(java.util.List)
			 */
			public void process(List<String> argumentList) {
				if (argumentList.size() > 0) {
					command = (String) argumentList.get(0);
					if (command.equals("diff") || command.equals("leanify") || command.equals("patch")) {
						argumentList.remove(0);
					}
				} else {
					showHelp();
					return;
				}
			}
		});
		if (command == null) {
			System.err.println("must specify a command");
			showHelp();
			return;
		}
		String[] restArgs = new String[args.length -1];
		System.arraycopy(args, 1,restArgs, 0, restArgs.length);
		if ("diff".equals(command)) {
			DiffMain.main("java -jar rdf-utils.jar diff", new ArgumentHandler(restArgs));
			return;
		}
		if ("leanify".equals(command)) {
			LeanifyMain.main("java -jar rdf-utils.jar leanify", new ArgumentHandler(restArgs));
			return;
		}
		if ("patch".equals(command)) {
			PatchMain.main("java -jar rdf-utils.jar patch", new ArgumentHandler(restArgs));
			return;
		}
		showHelp();

	}

	/**
	 * 
	 */
	private static void showHelp() {
		System.out.println("Usage:");
		System.out
				.println("java -jar rdf-utils.jar leanify|diff|patch [-H|--help] options...");
	}
}
