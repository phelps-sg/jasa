/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa.market;

import java.util.Comparator;

import java.io.Serializable;

/**
 * This class can be used to as a Comparator to rank shouts in ascending order.
 * 
 * @author Steve Phelps
 * @version $Revision$
 * 
 */

public class AscendingOrderComparator implements Comparator, Serializable {

	public AscendingOrderComparator() {
	}

	public int compare(Object parm1, Object parm2) {
		Order shout1 = (Order) parm1;
		Order shout2 = (Order) parm2;
		return shout1.compareTo(shout2);
	}
}