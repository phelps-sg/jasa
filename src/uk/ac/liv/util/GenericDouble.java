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

}