/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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
 * <p>
 * Faster version of <code>java.lang.Long</code>.
 * </p>
 *
 * @author Steve Phelps
 *
 */

public class FastLong extends FastNumber {

  long primitiveValue;

  public FastLong() {
    this(0L);
  }

  public static FastLong newFastLong( long value ) {
    return FastLongPool.fetch(value);
  }

  public void release() {
    FastLongPool.release(this);
  }


  protected FastLong( Long value ) {
    this(value.longValue());
  }

  protected FastLong( long value ) {
    primitiveValue = value;
  }

  protected void setValue( long value ) {
    primitiveValue = value;
  }

  public FastNumber add( FastNumber other ) {
    if ( other instanceof FastLong ) {
      return newFastLong( primitiveValue + other.longValue() );
    } else if ( other instanceof FastDouble ) {
      return FastDoublePool.fetch(doubleValue() + other.doubleValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public FastNumber multiply( FastNumber other ) {
    if ( other instanceof FastLong ) {
      return newFastLong( primitiveValue * other.longValue() );
    } else if ( other instanceof FastDouble ) {
      return FastDoublePool.fetch(doubleValue() * other.doubleValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public FastNumber subtract( FastNumber other ) {
  if ( other instanceof FastLong ) {
      return newFastLong( primitiveValue - other.longValue() );
    } else if ( other instanceof FastDouble ) {
      return FastDoublePool.fetch(doubleValue() - other.doubleValue());
    } else {
      throw new IllegalArgumentException();
    }
  }

  public FastNumber divide( FastNumber other ) {
    return opResult( doubleValue() / other.doubleValue() );
  }

  protected FastNumber opResult( double tempResult ) {
    long intResult = Math.round(tempResult);
    if ( intResult == tempResult ) {
      return newFastLong(intResult);
    } else {
      return FastDoublePool.fetch(tempResult);
    }
  }


  public int compareTo( Object other ) {
    if ( other instanceof FastLong ) {
      long l0 = primitiveValue;
      long l1 = ((FastLong) other).longValue();
      if ( l0 < l1 ) {
        return -1;
      } else if ( l0 > l1 ) {
        return +1;
      } else {
        return 0;
      }
    } else if ( other instanceof FastDouble ) {
      double d0 = doubleValue();
      double d1 = ((FastDouble) other).doubleValue();
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
    return (int) primitiveValue;
  }

  public float floatValue() {
    return (float) primitiveValue;
  }

  public double doubleValue() {
    return (double) primitiveValue;
  }

  public long longValue() {
    return primitiveValue;
  }


  public String toString() {
    return primitiveValue+"";
  }

  public boolean equals( Object other ) {
    if ( other instanceof FastLong ) {
      return primitiveValue == ((FastLong) other).longValue();
    } else if ( other instanceof FastNumber ) {
      return doubleValue() == ((FastNumber) other).doubleValue();
    } else {
      return super.equals(other);
    }
  }

}
