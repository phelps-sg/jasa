package uk.ac.liv.util;

/**
 * Title:
 * Description:
 * Copyright:
 * Company:
 * @author Steve Phelps
 * @version
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