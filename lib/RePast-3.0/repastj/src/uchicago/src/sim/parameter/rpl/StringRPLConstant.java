package uchicago.src.sim.parameter.rpl;

import java.lang.reflect.InvocationTargetException;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 * Defines a constant parameter whose value is a String.
 *
 * @version $Revision$ $Date$
 */

public class StringRPLConstant extends AbstractConstantParameter {

  private String val;

  /**
   * Creates a StringRPLConstant with specified name and value.
   *
   * @param name the name of the parameter
   * @param val the value of the parameter
   */
  public StringRPLConstant(String name, String val) {
    super(name);
    this.val = val;
    type = String.class;
  }

  /**
   * Invokes the model's appropriate set method with this constant's
   * value as an argument.
   *
   * @param model the model to invoke the set method on
   * @throws RepastException if the invocation fails
   */
  protected void invokeSet(SimModel model) throws RepastException {
    try {
      setMethod.invoke(model, new Object[]{val});
    } catch (IllegalAccessException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (IllegalArgumentException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (InvocationTargetException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    }
  }

  /**
   * Returns the value of this StringRPLConstant.
   */
  protected Object getValue() {
    return val;
  }

  public String toString() {
    StringBuffer b = new StringBuffer(name);
    b.append("(");
    b.append(val);
    b.append(")");
    return b.toString();
  }

}
