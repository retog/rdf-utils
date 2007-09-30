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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.wymiwyg.commons.util.Util;
import org.wymiwyg.rdf.graphs.Graph;
import org.wymiwyg.rdf.graphs.jenaimpl.JenaUtil;
import org.wymiwyg.rdf.molecules.diff.MoleculeDiff;
import org.wymiwyg.rdf.molecules.functref.ReferenceGroundedDecomposition;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedDecompositionImpl;
import org.wymiwyg.rdf.molecules.functref.impl.ReferenceGroundedUtil;
import org.wymiwyg.rdf.molecules.model.modelref.implgraph.ModelReferencingDecompositionImpl;
import org.wymiwyg.rdf.utils.jena.LeanDiffPatch;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;


/**
 * @author reto
 * 
 */
public class DiffPatchTest extends TestCase {

	public void testWithRandomModels() throws IOException {
		for (int i = 0; i < 5; i++) {
			int size1 = (int) (Math.random() * (10 * i + 200));
			int size2 = (int) (Math.random() * (10 * i + 200));
			System.out.println("Random-Model test, round " + i
					+ " with models of size " + size1 + " and " + size2);
			Model m1 = ModelCreationUtil.createRandomModel(size1);
			Model m2 = ModelCreationUtil.createRandomModel(size2);
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			m1.write(new FileOutputStream(File.createTempFile("random-model-1",
					".nt")), "N-TRIPLE");
			m2.write(new FileOutputStream(File.createTempFile("random-model-1",
					".nt")), "N-TRIPLE");
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);
			File zipFile = File.createTempFile("random-model", ".zip");
			OutputStream diffOut = new FileOutputStream(zipFile);
			diff.serialize(diffOut);
			diffOut.close();
			MoleculeDiff diffRec;// = LeanDiffPatch.deserializeDiff(zipFile);
			{
				InputStream in1 = new FileInputStream(zipFile);
				File tempFile = File.createTempFile(Util.createRandomString(5),
						"dzip");
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
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
			/*
			 * new File("random-model-1.nt").delete(); new
			 * File("random-model-2.nt").delete(); new
			 * File("random-model-diff.zip").delete(); new
			 * File("random-model-2-reconstructed.nt").delete();
			 */
		}
	}

	public void test2() throws IOException {
		for (int i = 0; i < 2; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1
					.read(getClass().getResource("test2-1.nt").toString(),
							"N-TRIPLE");
			m2
					.read(getClass().getResource("test2-2.nt").toString(),
							"N-TRIPLE");
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			File file = File.createTempFile("test2-serial", ".zip");
			OutputStream diffOut = new FileOutputStream(file);
			diff.serialize(baos);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			baos.close();
			diffOut.write(baos.toByteArray());
			diffOut.close();

			MoleculeDiff diffRec;
			{
				// with my jvm (Java(TM) 2 Runtime Environment, Standard Edition
				// (build 1.5.0_05-b05)) it
				// regularly fails when reading file directly!!!
				InputStream in1 = new FileInputStream(file);
				File tempFile = File.createTempFile(Util.createRandomString(5),
						"dzip");
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
				// diffRec = LeanDiffPatch.deserializeDiff(file);
			}
			Model m2reconstructed = LeanDiffPatch.patch(m2, diffRec);
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}

	public void test4() throws IOException {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("test4-1.nt").toString(), "N-TRIPLE");
		m2.read(getClass().getResource("test4-2.nt").toString(), "N-TRIPLE");
		m1 = LeanDiffPatch.leanify(m1);
		m2 = LeanDiffPatch.leanify(m2);
		MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);
		System.out.println(diff);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		File file = File.createTempFile("test4-serial", ".zip");
		OutputStream diffOut = new FileOutputStream(file);
		diff.serialize(baos);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		baos.close();
		diffOut.write(baos.toByteArray());
		diffOut.close();

		MoleculeDiff diffRec;

		{
			// with my jvm (Java(TM) 2 Runtime Environment, Standard Edition
			// (build 1.5.0_05-b05)) it
			// regularly fails when reading file directly!!!
			InputStream in1 = new FileInputStream(file);
			File tempFile = File.createTempFile(Util.createRandomString(5),
					"dzip");
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
			// diffRec = LeanDiffPatch.deserializeDiff(file);
		}
		Model m2reconstructed = LeanDiffPatch.patch(m2, diffRec);
		assertTrue("reconstructed is isomorphic", m2
				.isIsomorphicWith(m2reconstructed));
	}

	public void testDecomposition() {
		Model firstModel = null;
		Graph firstGraph = null;
		for (int i = 0; i < 3; i++) {
			Model m = ModelFactory.createDefaultModel();
			/*
			 * m.read(getClass().getResource("test4-2.nt").toString(),
			 * "N-TRIPLE");
			 */
			m.read(getClass().getResource("test-decomposition.nt").toString(),
					"N-TRIPLE");
			m = LeanDiffPatch.leanify(m);
			if (firstModel == null) {
				firstModel = m;
			} else {
				if (m.isIsomorphicWith(firstModel)) {
					System.out.println("same again");
				} else {
					System.out.println("different");
				}
			}
			Graph g = JenaUtil.getGraphFromModel(m, true);
			if (firstGraph == null) {
				firstGraph = g;
			} else {
				if (g.equals(firstGraph)) {
					System.out.println("same again");
				} else {
					System.out.println("different graph");
				}
			}
			ReferenceGroundedDecomposition dec = new ReferenceGroundedDecompositionImpl(
					new ModelReferencingDecompositionImpl(g));
			Graph gRec = ReferenceGroundedUtil.reconstructGraph(dec);
			assertTrue("same opinion", g.equals(gRec) == JenaUtil
					.getModelFromGraph(g).isIsomorphicWith(
							JenaUtil.getModelFromGraph(gRec)));
			/*
			 * if (!g.equals(gRec)) { ReferenceGroundedDecomposition dec2 = new
			 * ReferenceGroundedDecompositionImpl( new
			 * ModelReferencingDecompositionImpl(g)); new MoleculeDiffImpl(
			 * dec2, dec).print(new PrintWriter(System.out, true)); gRec =
			 * ReferenceGroundedUtil.reconstructGraph(dec2); if
			 * (!g.equals(gRec)) { System.out.println("still not the same"); m =
			 * ModelFactory.createDefaultModel();
			 * m.read(getClass().getResource("test4-2.nt").toString(),
			 * "N-TRIPLE"); Graph g2 = JenaUtil.getGraphFromModel(m, true);
			 * ReferenceGroundedDecomposition dec3 = new
			 * ReferenceGroundedDecompositionImpl( new
			 * ModelReferencingDecompositionImpl(g2)); //new MoleculeDiffImpl(
			 * dec3, dec).print(new PrintWriter(System.out, true)); gRec =
			 * ReferenceGroundedUtil.reconstructGraph(dec3); if
			 * (g2.equals(gRec)) { System.out.println("goes with new graph"); }
			 * else { System.out.println(g2.equals(g)); } } }
			 */
			JenaUtil.getModelFromGraph(gRec).write(System.out);
			JenaUtil.getModelFromGraph(g).write(System.out);
			System.out.println("graph equal, round " + i + ": "
					+ g.equals(gRec));
			assertTrue("graph equal, round " + i, g.equals(gRec));
			assertTrue("model isomorphic, round " + i, JenaUtil
					.getModelFromGraph(g).isIsomorphicWith(
							JenaUtil.getModelFromGraph(gRec)));
		}
	}

	public void test1() throws IOException {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("test1-1.nt").toString(), "N-TRIPLE");
		m2.read(getClass().getResource("test1-2.nt").toString(), "N-TRIPLE");
		MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);
		File file = File.createTempFile("test1-serial", ".zip");
		diff.serialize(new FileOutputStream(file));
		System.out.println(diff);
		MoleculeDiff diffRec = LeanDiffPatch.deserializeDiff(file);
		System.out.println(diffRec);
		Model m2reconstructed = LeanDiffPatch.patch(m1, diffRec);
		m2reconstructed.write(System.out);
		assertTrue("reconstructed is isomorphic", m2
				.isIsomorphicWith(m2reconstructed));

	}

	public void test6() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1
					.read(getClass().getResource("test6-1.nt").toString(),
							"N-TRIPLE");
			m2
					.read(getClass().getResource("test6-2.nt").toString(),
							"N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			//System.out.println(diff);
			File file = File.createTempFile("test6-serial", ".zip");
			diff.serialize(new FileOutputStream(file));
			MoleculeDiff diffRec = LeanDiffPatch.deserializeDiff(file);
			Model m2reconstructed = LeanDiffPatch.patch(m1, diffRec);
			m2reconstructed = LeanDiffPatch.leanify(m2reconstructed);
			//m2.write(System.out);
			//m2reconstructed.write(System.out);
			//System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}

	}

	public void test7() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1
					.read(getClass().getResource("test7-1.nt").toString(),
							"N-TRIPLE");
			m2
					.read(getClass().getResource("test7-2.nt").toString(),
							"N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			System.out.println(diff);
			File file = File.createTempFile("test7-serial", ".zip");
			diff.serialize(new FileOutputStream(file));
			MoleculeDiff diffRec = diff;// LeanDiffPatch.deserializeDiff(file);
			System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
			System.out
					.println(diffRec.getCommonFgNodesInDiffMolecules().size());
			Model m2reconstructed = LeanDiffPatch.patch(m1, diff);// diffRec);
			// m2reconstructed.write(System.out);
			System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}

	public void test8() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1
					.read(getClass().getResource("test8-1.nt").toString(),
							"N-TRIPLE");
			m2
					.read(getClass().getResource("test8-2.nt").toString(),
							"N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			System.out.println(diff);
			File file = File.createTempFile("test8-serial", ".zip");
			diff.serialize(new FileOutputStream(file));
			MoleculeDiff diffRec = diff;// LeanDiffPatch.deserializeDiff(new
			// File("test7-serial.zip"));
			System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
			System.out
					.println(diffRec.getCommonFgNodesInDiffMolecules().size());
			Model m2reconstructed = LeanDiffPatch.patch(m1, diff);// diffRec);
			// m2reconstructed.write(System.out);
			System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}

	public void test9() throws IOException {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(getClass().getResource("test9.nt").toString(), "N-TRIPLE");
		m1 = LeanDiffPatch.leanify(m1);
		MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

		System.out.println(diff);
		File file = File.createTempFile("test9-serial", ".zip");
		diff.serialize(new FileOutputStream(file));
		MoleculeDiff diffRec = diff;// LeanDiffPatch.deserializeDiff(new
		// File("test7-serial.zip"));
		System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
		System.out.println(diffRec.getCommonFgNodesInDiffMolecules().size());
		Model m2reconstructed = LeanDiffPatch.patch(m1, diff);// diffRec);
		// m2reconstructed.write(System.out);
		System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
		assertTrue("reconstructed is isomorphic", m2
				.isIsomorphicWith(m2reconstructed));
	}

	public void test10() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1.read(getClass().getResource("test10-1.nt").toString(),
					"N-TRIPLE");
			m2.read(getClass().getResource("test10-2.nt").toString(),
					"N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			System.out.println(diff);
			File file = File.createTempFile("test10-serial", ".zip");
			diff.serialize(file);
			MoleculeDiff diffRec = diff;// LeanDiffPatch.deserializeDiff(file);
			System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
			System.out
					.println(diffRec.getCommonFgNodesInDiffMolecules().size());
			Model m2reconstructed = LeanDiffPatch.patch(m1, diff);// diffRec);
			// m2reconstructed.write(System.out);
			System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}

	// this model was once see to cause a NoMapping found exception
	public void test11() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1.read(getClass().getResource("test11-1.nt").toString(),
					"N-TRIPLE");
			m2.read(getClass().getResource("test11-2.nt").toString(),
					"N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			System.out.println(diff);
			File file = File.createTempFile("test11-serial", ".zip");
			diff.serialize(new FileOutputStream(file));
			MoleculeDiff diffRec = diff;// LeanDiffPatch.deserializeDiff(file);
			System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
			System.out
					.println(diffRec.getCommonFgNodesInDiffMolecules().size());
			Model m2reconstructed = LeanDiffPatch.patch(m1, diff);// diffRec);
			// m2reconstructed.write(System.out);
			System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}

	// this model was once see to cause a NoMapping found exception
	public void test12() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1.read(getClass().getResource("test12-1.nt").toString(),
					"N-TRIPLE");
			m2.read(getClass().getResource("test12-2.nt").toString(),
					"N-TRIPLE");
			m1 = LeanDiffPatch.leanify(m1);
			m2 = LeanDiffPatch.leanify(m2);
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			File file = File.createTempFile("test12-serial", ".zip");
			diff.serialize(new FileOutputStream(file));
			MoleculeDiff diffRec = LeanDiffPatch.deserializeDiff(file);
			System.out.println("orig diff");
			System.out.println(diff);
			System.out.println("reconstructed diff");
			System.out.println(diffRec);
			System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
			System.out
					.println(diffRec.getCommonFgNodesInDiffMolecules().size());
			Model m2reconstructed = LeanDiffPatch.patch(m1, diffRec);
			// m2reconstructed.write(System.out);
			System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}
	public void test13() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test13-1.nt").getFile()),
				new File(getClass().getResource("test13-2.nt").getFile()));
	}

	public void test14() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test14-1.nt").getFile()),
				new File(getClass().getResource("test14-2.nt").getFile()));
	}
	
	public void test15() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test15-1.nt").getFile()),
				new File(getClass().getResource("test15-2.nt").getFile()));
	}

	public void test16() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test16-1.nt").getFile()),
				new File(getClass().getResource("test16-2.nt").getFile()));
	}
	
	public void test17() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test17-1.nt").getFile()),
				new File(getClass().getResource("test17-2.nt").getFile()));
	}
	
	public void test18() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test18-1.nt").getFile()),
				new File(getClass().getResource("test18-2.nt").getFile()));
	}
	
	
	/**self referencing FGNS
	 * 
	 * @throws IOException
	 */
	public void test19() throws IOException {
		testDiffPatch(
				new File(getClass().getResource("test19-1.nt").getFile()),
				new File(getClass().getResource("test19-2.nt").getFile()));
	}

	public void testSingleNatural() throws IOException {
		for (int i = 0; i < 1; i++) {
			Model m1 = ModelFactory.createDefaultModel();
			Model m2 = ModelFactory.createDefaultModel();
			m1.read(getClass().getResource("single-natural.nt").toString(),
					"N-TRIPLE");
			MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

			// diff.serialize(new FileOutputStream("single-natural.zip"));
			MoleculeDiff diffRec = diff;// LeanDiffPatch.deserializeDiff(new
			// File("test7-serial.zip"));
			System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
			System.out
					.println(diffRec.getCommonFgNodesInDiffMolecules().size());
			Model m2reconstructed = LeanDiffPatch.patch(m1, diff);// diffRec);
			System.out.println("m1");
			m1.write(System.out, "N-TRIPLE");
			System.out.println("---");
			System.out.println("diff:");
			System.out.println(diff);
			System.out.println("---");
			System.out.println("m2reconstructed:");
			m2reconstructed.write(System.out, "N-TRIPLE");
			System.out.println("---");
			// System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
			assertTrue("reconstructed is isomorphic", m2
					.isIsomorphicWith(m2reconstructed));
		}
	}

	public void testWithFiles() throws IOException {
		URL dirURL = getClass().getResource(
				getClass().getSimpleName() + ".class");
		File dir;
		try {
			dir = new File(new URI(dirURL.toString())).getParentFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		try {
			File[] files = dir.listFiles(new FileFilter() {
				RE re = new RE("test[0-9]+-1.nt");

				public boolean accept(File pathname) {
					return re.match(pathname.getName());
				}
			});
			for (File file : files) {
				System.out.println(file);
				String fileName = file.getAbsolutePath();
				File second = new File(fileName.substring(0,
						fileName.length() - 4)
						+ "2.nt");
				System.out.println(second);
				// testDiffPatch(file, second);
			}

		} catch (RESyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void testDiffPatch(File f1, File f2) throws IOException {
		Model m1 = ModelFactory.createDefaultModel();
		Model m2 = ModelFactory.createDefaultModel();
		m1.read(f1.toURL().toString(), "N-TRIPLE");
		m2.read(f2.toURL().toString(), "N-TRIPLE");
		m1 = LeanDiffPatch.leanify(m1);
//		m1.removeAll(null, FOAF.homepage, null);
//		m1.removeAll(null, FOAF.mbox_sha1sum, null);
//		m1.remove(m1.listStatements(new SimpleSelector() {
//
//			@Override
//			public boolean isSimple() {
//				return false;
//			}
//
//			@Override
//			public boolean test(Statement s) {
//				return !s.getPredicate().equals(FOAF.mbox);
//			}
//			
//		}));
		m1.write(System.out, "N-TRIPLE");
		m2 = LeanDiffPatch.leanify(m2);
		MoleculeDiff diff = LeanDiffPatch.getDiff(m1, m2);

		File diffFile = File.createTempFile(f1.getName() + "-" + f2.getName(),
				".zdiff");
		diff.serialize(new FileOutputStream(diffFile));
		MoleculeDiff diffRec = LeanDiffPatch.deserializeDiff(diffFile);
		// System.out.println("orig diff");
		// System.out.println(diff);
		// System.out.println("reconstructed diff");
		// System.out.println(diffRec);
		// System.out.println(diff.getCommonFgNodesInDiffMolecules().size());
		// System.out.println(diffRec.getCommonFgNodesInDiffMolecules().size());
		Model m2reconstructed = LeanDiffPatch.patch(m1, diffRec);
		//TODO investigate why we get more unlean results (e.g. with test6 and 13)
		m2reconstructed = LeanDiffPatch.leanify(m2reconstructed);
		// m2reconstructed.write(System.out);
		System.out.println(LeanDiffPatch.getDiff(m2, m2reconstructed));
		assertEquals("reconstructed lean graphs are equal", JenaUtil.getGraphFromModel(m2, true),  JenaUtil.getGraphFromModel(LeanDiffPatch.leanify(m2reconstructed), true));
		assertEquals("reconstructed graphs are equal", JenaUtil.getGraphFromModel(m2, true),  JenaUtil.getGraphFromModel(m2reconstructed, true));
		assertTrue("reconstructed is isomorphic", m2
				.isIsomorphicWith(m2reconstructed));
	}
}
