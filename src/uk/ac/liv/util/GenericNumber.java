/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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
 * An abstract class encapsulating numbers for weakly-typed
 * arithmetic operations.  This is an extension of Java's
 * Number class that actually provides methods for performing
 * arithemetic.
 * </p>
 *
 * @author Steve Phelps
 *
 */

public abstract class GenericNumber extends Number implements Comparable {

  public abstract GenericNumber multiply( GenericNumber other );

  public abstract GenericNumber add( GenericNumber other );

  public abstract GenericNumber subtract( GenericNumber other );

  public abstract GenericNumber divide( GenericNumber other );

  public abstract void release();

}