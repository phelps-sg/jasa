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

import huyd.poolit.*;

/**
 * This encapsulation of Double can be used in combination with the
 * FastNumber and FastLong classes, for performing weakly-typed
 * arithmetic operations.
 * @author Steve Phelps
 *
 */

public class FastDouble extends FastNumber {

  double primitiveValue;

  public FastDouble() {
    this(Double.NaN);
  }

  public static FastDouble newFastDouble( double value ) {
    return FastDoublePool.fetch(value);
  }

  public void release() {
    FastDoublePool.release(this);
  }

  public FastNumber add( FastNumber other ) {
    return newFastDouble(primitiveValue + other.doubleValue());
  }

  public FastNumber multiply( FastNumber other ) {
    return newFastDouble(primitiveValue * other.doubleValue());
  }

  public FastNumber subtract( FastNumber other ) {
    return newFastDouble(primitiveValue - other.doubleValue());
  }

  public FastNumber divide( FastNumber other ) {
    return newFastDouble(primitiveValue / other.doubleValue());
  }

  public int intValue() {
    return (int) primitiveValue;
  }

  public float floatValue() {
    return (float) primitiveValue;
  }

  public double doubleValue() {
    return primitiveValue;
  }

  public long longValue() {
    return (long) primitiveValue;
  }


  public int compareTo( Object other ) {
    if ( other instanceof Number ) {
      double d1 = ((Number) other).doubleValue();
      double d0 = doubleValue();
      if ( d0 > d1 ) {
        return +1;
      } else if ( d0 < d1 ) {
        return -1;
      } else {
        return 0;
      }
    } else {
      throw new ClassCastException();
    }
  }

  public boolean equals( Object other ) {
    if ( other instanceof FastNumber ) {
      return doubleValue() == ((FastNumber) other).doubleValue();
    } else {
      return super.equals(other);
    }
  }

  public String toString() {
    return primitiveValue + "";
  }

  protected void setValue( double value ) {
    primitiveValue = value;
  }

  protected FastDouble( Double value ) {
    this(value.doubleValue());
  }

  protected FastDouble( double value ) {
    primitiveValue = value;
  }



}
