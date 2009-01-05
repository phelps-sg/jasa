package uchicago.src.sim.parameter.rpl;

import java.lang.reflect.InvocationTargetException;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 * Represents a constant boolean parameter.
 *
 * @version $Revision$ $Date$
 */

public class BooleanRPLConstant extends AbstractConstantParameter {

  private boolean val;

  /**
   * Creates a BooleanRPLConstat with the specified name and value.
   *
   * @param name the name of the parameter
   * @param val the value of this BooleanRPLConstant
   */
  public BooleanRPLConstant(String name, boolean val) {
    super(name);
    this.val = val;
    type = boolean.class;
  }

  /**
   * Invokes the model's set method with this constants's value as an
   * argument.
   *
   * @param model the model to invoke the set method on
   * @throws RepastException if the method fails
   */
  protected void invokeSet(SimModel model) throws RepastException {
    try {
      setMethod.invoke(model, new Object[]{Boolean.valueOf(val)});
    } catch (IllegalAccessException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (IllegalArgumentException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (InvocationTargetException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    }
  }

  /**
   * Gets the current value of this parameter.
   */
  protected Object getValue() {
    return Boolean.valueOf(val);
  }

  public String toString() {
    StringBuffer b = new StringBuffer(name);
    b.append("(");
    b.append(val);
    b.append(")");
    return b.toString();
  }
}
