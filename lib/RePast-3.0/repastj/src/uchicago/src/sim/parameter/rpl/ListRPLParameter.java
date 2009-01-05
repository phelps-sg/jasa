package uchicago.src.sim.parameter.rpl;

import java.lang.reflect.InvocationTargetException;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 * Defines a parameter whose values are drawn from a list. Calling "next"
 * on this parameter will increment the list index to the next item in
 * the list.
 *
 * @version $Revision$ $Date$
 */

public class ListRPLParameter extends AbstractDynamicParameter {

  private RPLList list;
  private int index = 0;


  /**
   * Creates a ListRPLParameter with the specified name and from the specified
   * RPLList.
   *
   * @param name the name of the parameter
   * @param list the list of values contained by this parameter
   */
  public ListRPLParameter(String name, RPLList list) {
    super(name);
    this.list = list;
    this.name = name;
    type = list.getType();
  }

  /**
   * Invokes the model's appropriate set method with the value of the
   * current item of this list as an argument.
   *
   * @param model the model to invoke the set method on
   * @throws RepastException if the invocation fails
   */
  protected void invokeSet(SimModel model) throws RepastException {
    Object o = list.get(index);
    if (convertor != null) o = convertor.convert((Double)o);

    try {
      setMethod.invoke(model, new Object[]{o});
    } catch (IllegalAccessException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (IllegalArgumentException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    } catch (InvocationTargetException e) {
      throw new RepastException(e, "Unable to set model parameter '" + name + "'.");
    }
  }

  /**
   * Sets the curernt value of this ListRPLParameter to the next item in
   * its list. Returns true if there is a next item otherwise false if
   * we are at the end of the list.
   *
   * @return Returns true if there is a next item otherwise false if
   * we are at the end of the list.
   */
  protected boolean incrementSelf() {
    index++;
    return index < list.size();
  }

  /**
   * Resets the current value of this ListRPLParameter to the first item
   * in its list of items.
   */
  public void reset() {
    super.reset();
    index = 0;
  }

  /**
   * Gets the current value of this ListRPLParameter.
   */
  protected Object getValue() {
    if (convertor != null) {
      // if convertor is not null, then we can assume that this
      // list stores numeric types.
      return convertor.convert((Double)list.get(index));
    }
    return list.get(index);
  }

  public String toString() {
    StringBuffer b = new StringBuffer(name);
    b.append("(");
    b.append(list.toString());
    b.append(")");
    return b.toString();
  }
}
