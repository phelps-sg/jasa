/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.auction.config;

import uk.ac.liv.util.Parameterizable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

/**
 * A resettable enumeration. That's all the cases can be repeatedly enumerated.
 * Once the enumeration is done, it can be restarted again.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public abstract class CaseEnum implements Parameterizable {

	/**
	 * @uml.property name="name"
	 */
	private String name;

	public CaseEnum() {
	}

	public void setup(ParameterDatabase pdb, Parameter base) {
	}

	/**
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *          The name to set.
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	public abstract void reset();

	public abstract boolean moreCases();

	public abstract Case nextCase();

}