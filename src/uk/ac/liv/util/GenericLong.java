package uk.ac.liv.util;

/**
 * This encapsulation of Long can be used in combination with the
 * GenericNumber and GenericDouble classes, for performing weakly-typed
 * arithmetic operations.
 * @author Steve Phelps
 *
 */

public class GenericLong extends GenericNumber {

  Long value;

  public GenericLong( Long value ) {
    this.value = value;
  }

  public GenericNumber add( GenericNumber other ) {
    if ( other instanceof GenericLong ) {
      return new GenericLong( new Long(value.longValue() + other.longValue()) );
    } else if ( other instanceof GenericDouble ) {
      return new GenericDouble( new Double(value.doubleValue() + other.doubleValue()) );
    } else {
      throw new IllegalArgumentException();
    }
  }

  public GenericNumber multiply( GenericNumber other ) {
    if ( other instanceof GenericLong ) {
      return new GenericLong( new Long(value.longValue() * other.longValue()) );
    } else if ( other instanceof GenericDouble ) {
      return new GenericDouble( new Double(value.doubleValue() * other.doubleValue()) );
    } else {
      throw new IllegalArgumentException();
    }
  }

  public GenericNumber subtract( GenericNumber other ) {
    if ( other instanceof GenericLong ) {
      return new GenericLong( new Long(value.longValue() - other.longValue()) );
    } else if ( other instanceof GenericDouble ) {
      return new GenericDouble( new Double(value.doubleValue() - other.doubleValue()) );
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
      return new GenericLong( new Long(intResult) );
    } else {
      return new GenericDouble( new Double(tempResult) );
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

}
