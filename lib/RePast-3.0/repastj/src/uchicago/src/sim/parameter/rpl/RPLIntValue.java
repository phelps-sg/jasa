package uchicago.src.sim.parameter.rpl;

/**
 * Intermediate representation of an int value used by the compiler.
 *
 * @version $Revision$ $Date$
 */

public class RPLIntValue extends RPLValue {

  private int value;

  /**
   * Creates a RPLIntValue with the specified value.
   *
   * @param value the int value.
   */
  public RPLIntValue(int value) {
    this.value = value;
  }


  /**
   * Gets the value of this RPLIntValue as an Int.
   */
  public Object getValue() {
    return new Integer(value);
  }

  /**
   * Returns int.class.
   */
  public Class getType() {
    return int.class;
  }
}
