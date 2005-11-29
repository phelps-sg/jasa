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
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class ParameterBasedCaseImpl implements ParameterBasedCase, Parameterizable {
	
  public static final String P_NAME = "name";

	protected String name;
	
	protected String value;
	
	public void setup(ParameterDatabase pdb, Parameter base) {
		name = pdb.getString(base.push(P_NAME));		
	}

	public void setValue(String value) {
		this.value = value;		
	}

	public void apply(ParameterDatabase pdb, Parameter base) {
		pdb.set(new Parameter(name), value);
	}
	
	public String toString() {
		return value;
	}
	
}
