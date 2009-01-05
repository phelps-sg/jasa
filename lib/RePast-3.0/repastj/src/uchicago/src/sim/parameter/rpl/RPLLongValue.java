package uchicago.src.sim.parameter.rpl;

/**
 * Intermediate representation of a long value used by the compiler.
 *
 * @version $Revision$ $Date$
 */

public class RPLLongValue extends RPLValue {

  private long value;

  /**
   * Creates a RPLLongValue with the specified value.
   *
   * @param value the long value.
   */
  public RPLLongValue(long value) {
    this.value = value;
  }

   /**
   * Gets the value of this RPLLongValue as an Long.
   */
  public Object getValue() {
    return new Long(value);
  }

  /**
   * Returns long.class.
   */
  public Class getType() {
    return long.class;
  }
}
