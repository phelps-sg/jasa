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

package uk.ac.liv.auction.core;

import java.util.Comparator;

import java.io.Serializable;

/**
 * A comparator that can be used for arranging shouts in descending
 * order; that is, highest price first.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class DescendingShoutComparator implements Comparator, Serializable {

  public DescendingShoutComparator() {
  }

  public int compare(Object parm1, Object parm2) {
    Shout shout1 = (Shout) parm1;
    Shout shout2 = (Shout) parm2;
    return shout2.compareTo(shout1);
  }
}
