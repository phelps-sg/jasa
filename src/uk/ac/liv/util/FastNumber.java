/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

package uk.ac.liv.util;

/**
 * <p>
 * This is an extension of Java's Number class that provides fast
 * methods for performing arithmetic.  These methods use object-pooling
 * for their results, which makes them more efficient than the standard
 * Java arithmetic operators.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 *
 */

public abstract class FastNumber extends Number
    implements Comparable, Pooled {

  public abstract FastNumber multiply( FastNumber other );

  public abstract FastNumber add( FastNumber other );

  public abstract FastNumber subtract( FastNumber other );

  public abstract FastNumber divide( FastNumber other );

  public Object newCopy() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

}