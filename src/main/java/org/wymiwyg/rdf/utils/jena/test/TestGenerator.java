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
package org.wymiwyg.rdf.utils.jena.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.utils.jena.LeanDiffPatch;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author reto
 * 
 */
public class TestGenerator {

	/**
	 * 
	 */
	public TestGenerator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		int maxAttempts = 1000;
		for (int i = 0; i < maxAttempts; i++) {
			int size1 = (int) 1000;//(Math.random() * (1 * i + 2000));
			int size2 = (int) 1000;//(Math.random() * (1 * i + 2000));
			System.out.println("Random-Model test, round " + i
					+ " with models of size " + size1 + " and " + size2);
			Model m1 = ModelCreationUtil.createRandomModel(size1);
			Model m2 = ModelCreationUtil.createRandomModel(size2);
			m1.write(new FileOutputStream(File.createTempFile(
					"random-model-non-lean-1", ".nt")), "N-TRIPLE");
			m2.write(new FileOutputStream(File.createTempFile(
					"random-model-non-lean-1", ".nt")), "N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			m1.write(new FileOutputStream(File.createTempFile("random-model-1",
					".nt")), "N-TRIPLE");
			m2.write(new FileOutputStream(File.createTempFile("random-model-1",
					".nt")), "N-TRIPLE");
			try {
				MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);
				File zipFile = File.createTempFile("random-model", ".zip");
				OutputStream diffOut = new FileOutputStream(zipFile);
				diff.serialize(diffOut);
				diffOut.close();
				MoleculeDiff diffRec;// =
				// LeanDiffPatch.deserializeDiff(zipFile);
				{
					InputStream in1 = new FileInputStream(zipFile);
					File tempFile = File.createTempFile(Util
							.createRandomString(5), "dzip");
					FileOutputStream tempOut = new FileOutputStream(tempFile);
					byte[] bytes = new byte[1024];
					while (true) {
						int count = in1.read(bytes);
						if (count == -1) {
							break;
						}
						tempOut.write(bytes, 0, count);
					}
					tempOut.close();
					diffRec = LeanDiffPatch.deserializeDiff(tempFile);
				}
				Model m2reconstructed = LeanDiffPatch.patch(m1, diffRec);
				/*
				 * m2reconstructed.write(new FileOutputStream(
				 * "random-model-2-reconstructed.nt"), "N-TRIPLE");
				 */
				if (!m2.isIsomorphicWith(m2reconstructed)) {
					throw new Exception("not isomorphic");
				}
			} catch (Exception e) {
				File f1 = new File("problematic-model1.nt");
				File f2 = new File("problematic-model2.nt");
				System.out.println("Found failure");
				e.printStackTrace(System.out);
				System.out.println("writing " + f1 + " and " + f2);
				m1.write(new FileOutputStream(f1), "N-TRIPLE");
				m2.write(new FileOutputStream(f2), "N-TRIPLE");
				MinimumFailingGraphDetector minimumFailingGraphDetector = new MinimumFailingGraphDetector(
						JenaUtil.getGraphFromModel(m1, true), JenaUtil
								.getGraphFromModel(m2, true));
				File f1min = new File("problematic-model1-minimized.nt");
				File f2min = new File("problematic-model2-minimized.nt");
				System.out.println("writing " + f1min + " and " + f2min);
				JenaUtil.getModelFromGraph(
						minimumFailingGraphDetector.getMinimumFailingG1())
						.write(new FileOutputStream(f1min), "N-TRIPLE");
				JenaUtil.getModelFromGraph(
						minimumFailingGraphDetector.getMinimumFailingG2())
						.write(new FileOutputStream(f2min), "N-TRIPLE");
				return;
			}
			/*
			 * new File("random-model-1.nt").delete(); new
			 * File("random-model-2.nt").delete(); new
			 * File("random-model-diff.zip").delete(); new
			 * File("random-model-2-reconstructed.nt").delete();
			 */
		}
		System.out.println("No failing graph found in " + maxAttempts
				+ " attempts");
	}

}
