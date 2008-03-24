/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.rdf.molecules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.wymiwyg.rdf.molecules.NonTerminalMolecule;

/**
 * An implementation of FunctionallyGroundedNode.
 * 
 * Instances must be finalized before being shared, as they otherwise don't
 * agree to the equals/hashcode contract of FunctionallyGroundedNode
 * 
 * @author reto
 * 
 */
public class FunctionallyGroundedNodeImpl extends FunctionallyGroundedNodeBase
		implements Finalizable {

	private Collection<NonTerminalMolecule> groundingMolecules = new ArrayList<NonTerminalMolecule>();
	private Set<NonTerminalMolecule> groundingMoleculeSet = null;
	private Object tempIdentity = new Object();

	public FunctionallyGroundedNodeImpl() {

	}

	public FunctionallyGroundedNodeImpl(Set<NonTerminalMolecule> ntMolecules) {
		groundingMolecules.addAll(ntMolecules);
	}

	public Set<NonTerminalMolecule> getGroundingMolecules() {
		if (!isFinalized()) {
			return new GoodFaithSet<NonTerminalMolecule>(groundingMolecules);
		}
		if (groundingMoleculeSet == null) {
			groundingMoleculeSet = new GoodFaithSet<NonTerminalMolecule>(
					groundingMolecules);
			groundingMolecules = null;
		}
		return groundingMoleculeSet;
	}

	/**
	 * It's the responsibility of the caller not to add a molecule twice
	 */
	public void addGroundingMolecule(NonTerminalMolecule molecule) {
		groundingMolecules.add(molecule);
	}

	public void setMolecules(Collection<NonTerminalMolecule> molecules) {
		groundingMolecules.clear();
		groundingMolecules.addAll(molecules);
	}

	@Override
	public void markFinalized() {
		tempIdentity = null;
		super.markFinalized();
	}

	@Override
	public boolean equals(Object object) {
		if (tempIdentity == null) {
			return super.equals(object);
		} else {
			return this == object;
		}
	}

	@Override
	public int hashCode() {
		if (tempIdentity == null) {
			return super.hashCode();
		} else {
			return tempIdentity.hashCode();
		}
	}

}
