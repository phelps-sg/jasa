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
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class GroupSize extends ParameterBasedCase {
	
	public static final String P_GROUPSIZE = "groupsize";
	
	private int size;
	
	public GroupSize() {
	}
	
	public void setParameter(String param) {
		this.size = Integer.parseInt(param);
	}
	
	public String toString() {
		return String.valueOf(size);
	}
	
	public void apply(ParameterDatabase pdb, Parameter base) {
		pdb.set(base.push(P_GROUPSIZE), String.valueOf(size));
	}
}
