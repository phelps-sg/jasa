/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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
 * This encapsulation of Double can be used in combination with the
 * GenericNumber and GenericInteger classes, for performing weakly-typed
 * arithmetic operations.
 * @author Steve Phelps
 *
 */

public class GenericDouble extends GenericNumber {

  Double value;

  public GenericDouble( Double value ) {
    this.value = value;
  }

  public GenericNumber add( GenericNumber other ) {
    return new GenericDouble( new Double(value.doubleValue() + other.doubleValue()) );
  }

  public GenericNumber multiply( GenericNumber other ) {
    return new GenericDouble( new Double(value.doubleValue() * other.doubleValue()) );
  }

  public GenericNumber subtract( GenericNumber other ) {
    return new GenericDouble( new Double(value.doubleValue() - other.doubleValue()) );
  }

  public GenericNumber divide( GenericNumber other ) {
    return new GenericDouble( new Double(value.doubleValue() / other.doubleValue()) );
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

  public Double getValue() {
    return value;
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
    return value.toString();
  }

}