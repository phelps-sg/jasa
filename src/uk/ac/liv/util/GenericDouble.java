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
 * GenericNumber and GenericInteger classes, for performing weakly-typed
 * arithmetic operations.
 * @author Steve Phelps
 *
 */

public class GenericDouble extends GenericNumber {

  double primitiveValue;

  static Pooler pool;

  static final int DEFAULT_POOL_SIZE = 1000000;

  static int poolSize = DEFAULT_POOL_SIZE;

  public GenericDouble() {
    this(Double.NaN);
  }

  public static GenericDouble newGenericDouble( double value ) {
    GenericDouble result = null;
    try {
      initialisePool();
      result = (GenericDouble) pool.fetch();
      result.setValue(value);
    } catch ( FetchException e ) {
      System.err.println("WARNING: " + e.getMessage());
      e.printStackTrace();
      result = new GenericDouble(value);
    }
    return result;
  }

  public static void setPoolSize( int poolSize ) {
    GenericDouble.poolSize = poolSize;
  }

  public void release() {
    pool.release(this);
  }

  public GenericNumber add( GenericNumber other ) {
    return newGenericDouble(primitiveValue + other.doubleValue());
  }

  public GenericNumber multiply( GenericNumber other ) {
    return newGenericDouble(primitiveValue * other.doubleValue());
  }

  public GenericNumber subtract( GenericNumber other ) {
    return newGenericDouble(primitiveValue - other.doubleValue());
  }

  public GenericNumber divide( GenericNumber other ) {
    return newGenericDouble(primitiveValue / other.doubleValue());
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
    if ( other instanceof GenericNumber ) {
      return doubleValue() == ((GenericNumber) other).doubleValue();
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

  protected GenericDouble( Double value ) {
    this(value.doubleValue());
  }

  protected GenericDouble( double value ) {
    primitiveValue = value;
  }

  protected static void initialisePool() {
    try {
      if ( pool == null ) {
        pool = new FixedPooler(GenericDouble.class, poolSize);
      }
    } catch ( CreateException e ) {
      e.printStackTrace();
      throw new Error(e);
    }
  }


}