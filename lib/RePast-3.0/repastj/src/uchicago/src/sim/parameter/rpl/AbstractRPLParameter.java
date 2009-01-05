package uchicago.src.sim.parameter.rpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 * Default but not complete implementation of the RPLParameter interface.
 *
 * @version $Revision$ $Date$
 */

public abstract class AbstractRPLParameter implements RPLParameter {

  protected String name;
  protected List childParams = new ArrayList();
  protected List childConsts = new ArrayList();
  protected Method setMethod = null;
  protected Method getMethod = null;

  protected Class type;
  protected int curIndex = 0;
  private String[] modelParamNames = {};
  protected NumberConvertor convertor = null;

  // all this convertor stuff is necessary as numeric parameters are
  // stored as double values. And we need convertor objects to
  // convert them back to the appropriate type if necessary.
  private static Map convertors = new HashMap();

  static interface NumberConvertor {
    public Object convert(double d);

    public Object convert(Double d);
  }

  static class DoubleToInt implements NumberConvertor {
    public Object convert(double d) {
      return new Integer((int) d);
    }

    public Object convert(Double d) {
      return new Integer(d.intValue());
    }
  }

  static class DoubleToFloat implements NumberConvertor {
    public Object convert(double d) {
      return new Float((float) d);
    }

    public Object convert(Double d) {
      return new Float(d.floatValue());
    }
  }

  static class DoubleToLong implements NumberConvertor {
    public Object convert(double d) {
      return new Long((long) d);
    }

    public Object convert(Double d) {
      return new Long(d.longValue());
    }
  }

  static class DoubleToDouble implements NumberConvertor {
    public Object convert(double d) {
      return new Double(d);
    }

    public Object convert(Double d) {
      return d;
    }
  }

  static {
    convertors.put(double.class, new DoubleToDouble());
    convertors.put(int.class, new DoubleToInt());
    convertors.put(long.class, new DoubleToLong());
    convertors.put(float.class, new DoubleToFloat());
  }

  /**
   * Creates an AbstractRPLParameter with the specified name.
   *
   * @param name the name of the parameter.
   */
  public AbstractRPLParameter(String name) {
    this.name = name;
  }

  /**
   * Gets the name of this RPLParameter.
   */
  public String getName() {
    return name;
  }

  /**
   * Adds the specified RPLParameter as a child parameter of this RPLParameter.
   *
   * @param child the child RPLParameter
   */
  public void addChildParameter(RPLParameter child) {
    childParams.add(child);
  }

  /**
   * Adds the specified RPLParameter as a child constant parameter of this
   * RPLParameter.
   *
   * @param child the child RPLParameter to add
   */
  public void addChildConstant(RPLParameter child) {
    childConsts.add(child);
  }

  /**
   * Sets the model parameter to the current value of this RPLParameter.
   *
   * @param model the model whose parameter we want to set
   * @throws RepastException if there is an error setting the parameter
   */
  public void setModelParameter(SimModel model) throws RepastException {
    if (setMethod == null) {
      initWithModel(model);
    }

    invokeSet(model);

    for (int i = 0, n = childConsts.size(); i < n; i++) {
      RPLParameter parameter = (RPLParameter) childConsts.get(i);
      parameter.setModelParameter(model);
    }

    if (childParams.size() > 0) {
      RPLParameter parameter = (RPLParameter) childParams.get(curIndex);
      parameter.setModelParameter(model);
    }
  }

  /**
   * Invokes the model's set method for this parameter's value. The actual invocation
   * is deferred to a subclass, so that subclass do any necessary conversion
   * of method args.
   *
   * @param model the model to invoke the set method on
   * @throws RepastException if the method fails
   */
  protected abstract void invokeSet(SimModel model) throws RepastException;



  /**
   * Gets an Iterator over the child constant parameters of this RPLParameter.
   */
  public Iterator constantIterator() {
    return childConsts.iterator();
  }

  /**
   * Gets an Iterator over the child constant parameters of this RPLParameter.
   */
  public Iterator parameterIterator() {
    return childParams.iterator();
  }

  private void initWithModel(SimModel model) throws RepastException {
    setupParamArray(model);
    createMethods(model);
    Class pType = setMethod.getParameterTypes()[0];
    // do the type check, throw exception if types don't match or
    // coercion is not possible
    String message = "Type expected by model's parameter set method (" +
            pType.getName() + ") and type of '" + name +
            "' parameter (" + type + ") are not compatible";
    if (type.equals(double.class)) {
      // numeric parameter
      convertor = (NumberConvertor) convertors.get(pType);
      if (convertor == null) throw new RepastException(message);


    } else if (!type.equals(pType)) {
      throw new RepastException(message);
    }
  }

  private void setupParamArray(SimModel model) {
    String[] paramArray = model.getInitParam();
    modelParamNames = new String[paramArray.length + 1];
    System.arraycopy(paramArray, 0, modelParamNames, 0, paramArray.length);
    modelParamNames[paramArray.length] = "rngSeed";
  }

  private void createMethods(SimModel obj) throws RepastException {
    boolean found = false;
    for (int i = 0; i < modelParamNames.length; i++) {
      if (name.equalsIgnoreCase(modelParamNames[i])) {
        found = true;
        break;
      }
    }

    if (!found)
      throw new RepastException("Parameter '" + name +
                                "' is not named as a parameter in model's getInitParams");

    Method[] methods = obj.getClass().getMethods();
    for (int i = 0, n = methods.length; i < n; i++) {
      Method m = methods[i];
      String mName = m.getName();
      if (mName.startsWith("set") && m.getParameterTypes().length == 1) {
        String propName = mName.substring(3);
        if (propName.equalsIgnoreCase(name)) setMethod = m;

      } else if (mName.startsWith("get") && m.getParameterTypes().length == 0) {
        String propName = mName.substring(3);
        if (propName.equalsIgnoreCase(name)) getMethod = m;
      } else if (mName.startsWith("is") && m.getParameterTypes().length == 0 &&
              m.getReturnType().equals(boolean.class)) {
        String propName = mName.substring(2);
        if (propName.equalsIgnoreCase(name)) getMethod = m;
      }
    }

    if (setMethod == null)
      throw new RepastException("Set method for parameter '" + name + "' not found in model");
  }

  /**
   * Gets the current value of this RPLParameter.
   *
   * @param model the model associated with this RPLParameter
   * @throws RepastException if the value cannot be returned
   */
  public Object getValue(SimModel model) throws RepastException {
    if (setMethod == null) {
      // set up the convertor
      initWithModel(model);
    }

    return getValue();
  }

  /**
   * Gets the current value of this AbstractRPLParameter. The actual implementation
   * is deferred to a subclass so that the subclass can do any conversion
   * if necessary.
   *
   * @return the value of this parameter.
   */
  protected abstract Object getValue();
}
