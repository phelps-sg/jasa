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
 * This encapsulation of Long can be used in combination with the
 * GenericNumber and GenericDouble classes, for performing weakly-typed
 * arithmetic operations.
 * @author Steve Phelps
 *
 */

public class GenericLong extends GenericNumber {

  Long value;

  long primitiveValue;

  protected static Pooler pool;

  static final int DEFAULT_POOL_SIZE = 10000;


  public GenericLong() {
    this(0L);
  }

  public static GenericLong newGenericLong( long value ) {
    GenericLong result = null;
    try {
      initialisePool();
      result = (GenericLong) pool.fetch();
      result.setValue(value);
    } catch ( FetchException e ) {
      System.err.println("WARNING: " + e.getMessage());
      e.printStackTrace();
      result = new GenericLong(value);
    }
    return result;
  }

  public void release() {
    pool.release(this);
  }


  protected GenericLong( Long value ) {
    this(value.longValue());
  }

  protected GenericLong( long value ) {
    primitiveValue = value;
    synchFromPrimitive();
  }

  protected void setValue( long value ) {
    primitiveValue = value;
    synchFromPrimitive();
  }

  protected void synchFromPrimitive() {
    if ( value == null || value.longValue() != primitiveValue ) {
      value = new Long(primitiveValue);
    }
  }

  public GenericNumber add( GenericNumber other ) {
    if ( other instanceof GenericLong ) {
      return newGenericLong( primitiveValue + other.longValue() );
    } else if ( other instanceof GenericDouble ) {
      return GenericDouble.newGenericDouble(value.doubleValue() + other.doubleValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public GenericNumber multiply( GenericNumber other ) {
    if ( other instanceof GenericLong ) {
      return newGenericLong( primitiveValue * other.longValue() );
    } else if ( other instanceof GenericDouble ) {
      return GenericDouble.newGenericDouble(value.doubleValue() * other.doubleValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public GenericNumber subtract( GenericNumber other ) {
  if ( other instanceof GenericLong ) {
      return newGenericLong( primitiveValue - other.longValue() );
    } else if ( other instanceof GenericDouble ) {
      return GenericDouble.newGenericDouble(value.doubleValue() - other.doubleValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public GenericNumber divide( GenericNumber other ) {
    return opResult( value.doubleValue() / other.doubleValue() );
  }

  protected GenericNumber opResult( double tempResult ) {
    long intResult = Math.round(tempResult);
    if ( intResult == tempResult ) {
      return newGenericLong(intResult);
    } else {
      return GenericDouble.newGenericDouble(tempResult);
    }
  }


  public int compareTo( Object other ) {
    if ( other instanceof GenericLong ) {
      return value.compareTo( ((GenericLong) other).getValue() );
    } else if ( other instanceof GenericDouble ) {
      double d0 = doubleValue();
      double d1 = ((GenericDouble) other).doubleValue();
      if ( d0 < d1 ) {
        return -1;
      } else if ( d0 > d1 ) {
        return +1;
      } else {
        return 0;
      }
    } else {
      throw new ClassCastException("");
    }
  }

  public int intValue() {
    return value.intValue();
  }

  public float floatValue() {
    return value.floatValue();
  }

  public double doubleValue() {
    return value.doubleValue();
  }

  public long longValue() {
    return value.longValue();
  }

  public Long getValue() {
    return value;
  }

  public String toString() {
    return value.toString();
  }

  public boolean equals( Object other ) {
    if ( other instanceof GenericLong ) {
      return value.longValue() == ((GenericLong) other).longValue();
    } else if ( other instanceof GenericNumber ) {
      return value.longValue() == ((GenericNumber) other).doubleValue();
    } else {
      return super.equals(other);
    }
  }

  protected static synchronized void initialisePool() {
    try {
      if ( pool == null ) {
        pool = new FixedPooler(GenericLong.class, DEFAULT_POOL_SIZE);
      }
    } catch ( CreateException e ) {
      e.printStackTrace();
      throw new Error(e);
    }
  }
}
