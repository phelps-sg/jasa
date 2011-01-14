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
 * A comparator that can be used for arranging shouts in descending order; that
 * is, highest price first.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class DescendingOrderComparator implements 
		Comparator<Order>, Serializable {

	public DescendingOrderComparator() {
	}

	public int compare(Order shout1, Order shout2) {
		return shout2.compareTo(shout1);
	}
}
