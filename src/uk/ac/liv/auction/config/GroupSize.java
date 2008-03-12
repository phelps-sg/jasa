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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Defines the unit size of agents participating an auction, together with
 * Ratio, determining the populations of each type of agents.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class GroupSize implements ParameterBasedCase {

	public static final String P_GROUPSIZE = "groupsize";

	/**
	 * @uml.property name="size"
	 */
	private int size;

	public GroupSize() {
	}

	public void setValue(String value) {
		this.size = Integer.parseInt(value);
	}

	public String toString() {
		return String.valueOf(size);
	}

	public void apply(ParameterDatabase pdb, Parameter base) {
		pdb.set(base.push(P_GROUPSIZE), String.valueOf(size));
	}
}