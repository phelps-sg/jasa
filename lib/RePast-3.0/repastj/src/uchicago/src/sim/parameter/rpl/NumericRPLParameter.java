package uchicago.src.sim.parameter.rpl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 * Defines a dynamic numeric parameter. A NumericRPLParameter has a
 * starting value, an ending value and an amount to increment. Every call
 * to "next" will increment the current value by the amount to increment and
 * set the current value to that new value. Once the current value is more than
 * the ending value this will not increment.
 *
 * @version $Revision$ $Date$
 */
public class NumericRPLParameter extends AbstractDynamicParameter {

  private BigDecimal start, end, curValue;
  private BigDecimal incr = new BigDecimal("1");

  /**
   * Creates a NumericRPLParameter with the specified name, starting
   * value and ending value. The amount to increment defaults to 1.
   *
   * @param name the name of the parameter
   * @param start the starting value
   * @param end the ending value
   */
  public NumericRPLParameter(String name, double start, double end) {
    this(name, start, end, 1);
  }

  /**
   * Creates a NumericRPLParameter with the specified name, starting
   * value, ending value and amount to increment.
   *
   * @param name the name of the parameter
   * @param start the starting value
   * @param end the ending value
   * @param incr the amount to increment
   */
  public NumericRPLParameter(String name, double start, double end, double incr) {
    super(name);
    this.start = new BigDecimal(String.valueOf(start));
    this.end = new BigDecimal(String.valueOf(end));
    this.incr = new BigDecimal(String.valueOf(incr));
    curValue = new BigDecimal(String.valueOf(start));
    type = double.class;
  }

  /**
   * Resets this NumericRPLParameter to its starting value.
   */
  public void reset() {
    super.reset();
    curValue = new BigDecimal(start.toString());
  }

  /**
   * Increments the current value of this parameter by the amount to
   * increment. The curent value is then set to this new value.
   *
   * @return returns true if the new current value is less than or equal
   * the ending value, otherwise false.
   */
  protected boolean incrementSelf() {
    curValue = curValue.add(incr);
    return (curValue.compareTo(end) < 1);
    //return curValue <= end;
  }

  /**
   * Invokes the model's appropriate set method with this parameters's current
   * value as an argument.
   *
   * @param model the model to invoke the set method on
   * @throws RepastException if the method fails
   */
  protected void invokeSet(SimModel model) throws RepastException {
    Object arg = convertor.convert(curValue.doubleValue());
    try {
      setMethod.invoke(model, new Object[]{arg});
    } catch (IllegalAccessException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (IllegalArgumentException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (InvocationTargetException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    }
  }

  /**
   * Returns the current value of this NumericRPLParameter as a Double.
   * @return
   */
  protected Object getValue() {
    return convertor.convert(curValue.doubleValue());
  }

  public String toString() {
    StringBuffer b = new StringBuffer(name);
    b.append("(");
    b.append(start);
    b.append(", ");
    b.append(end);
    b.append(", ");
    b.append(incr);
    b.append(")");
    return b.toString();
  }
}
