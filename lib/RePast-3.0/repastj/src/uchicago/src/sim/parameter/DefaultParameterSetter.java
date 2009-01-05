/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.parameter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import uchicago.src.reflector.Invoker;
import uchicago.src.reflector.InvokerException;
import uchicago.src.reflector.InvokerFactory;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;


/**
 * Implementation of ParameterSetter that works with the original "{" style
 * repast parameter format.
 *
 * @version $Revision$ $Date$
 */

public class DefaultParameterSetter extends AbstractParameterSetter {

  //private Vector parameters;
  private boolean first = true;
  // used to introspect the model for the appropriate setX methods.
  private String[] modelParamNames;
  private HashMap setMethodsTable = new HashMap();
  private HashMap getMethodsTable = new HashMap();
  private ArrayList dynamicParamNames = new ArrayList();
  private HashMap parameterMap = new HashMap();
  private boolean finished = false;
  private HashSet stringables = new HashSet();
  private boolean doSetup = true;
  private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];

  /**
   * Creates a DefaultParameterSetter.
   */
  public DefaultParameterSetter() {
    stringables.add(Integer.class);
    stringables.add(int.class);
    stringables.add(Long.class);
    stringables.add(long.class);
    stringables.add(String.class);
    stringables.add(Float.class);
    stringables.add(float.class);
    stringables.add(Byte.class);
    stringables.add(byte.class);
    stringables.add(Double.class);
    stringables.add(double.class);
    stringables.add(Boolean.class);
    stringables.add(boolean.class);
    stringables.add(Character.class);
    stringables.add(char.class);
  }

  /**
   * Creates a DefaultParameterSetter to set parameter on the specified object.
   * The names of the parameters to set are given the properties array.
   *
   * @param o the Object whose properties or parameters we want to set
   * @param properties the name of those parameters
   *
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public DefaultParameterSetter(Object o, String[] properties) throws
          IllegalAccessException, InvocationTargetException {
    this();
    List list = new ArrayList();
    modelParamNames = new String[properties.length];
    System.arraycopy(properties, 0, modelParamNames, 0, properties.length);
    fillMethodTable(o);
    for (int i = 0; i < properties.length; i++) {
      String name = properties[i];
      Method m = (Method) getMethodsTable.get(capitalize(name));
      if (m != null) {
        if (stringables.contains(m.getReturnType())) {
          NumericParameter p = new NumericParameter();
          p.setName(capitalize(name));
          p.setConstVal(m.invoke(o, EMPTY_OBJ_ARRAY));
          list.add(p);
        }
      }
    }
    doSetup = false;
    fillListSet(list);
  }

  /**
   * Initialize the setter using the specified file. ParameterSetters
   * will most often be created using reflection via a no-arg constructor.
   * This init method functions like a proper constructor.
   *
   * @param file the name of the parameter file
   * @throws IOException
   */
  public void init(String file) throws IOException {
    ParameterReader reader = new ParameterReader(file);
    Vector parameters = reader.getParameters();
    fillListSet(parameters);
  }

  private void setupParamArray(SimModel model) {
    String[] paramArray = model.getInitParam();
    //if (paramArray == null) paramArray = new String[0];
    modelParamNames = new String[paramArray.length + 1];
    System.arraycopy(paramArray, 0, modelParamNames, 0, paramArray.length);
    modelParamNames[paramArray.length] = "rngSeed";
  }

  private void fillListSet(List list) {
    for (int i = 0, n = list.size(); i < n; i++) {
      Parameter p = (Parameter) list.get(i);
      parameterMap.put(new ParamName(p.getName()), p);
      if (!p.isConstant()) dynamicParamNames.add(capitalize(p.getName()));
      if (p.hasChildren()) fillListSet(p.getChildren());
    }
  }

  private void fillMethodTable(Object obj) {
    getMethodsTable.clear();
    setMethodsTable.clear();
    Method[] methods = obj.getClass().getMethods();
    for (int i = 0, n = methods.length; i < n; i++) {
      Method m = methods[i];
      String name = m.getName();
      if (name.startsWith("set") && m.getParameterTypes().length == 1) {
        String propName = name.substring(3);
        for (int j = 0; j < modelParamNames.length; j++) {
          if (propName.equalsIgnoreCase(modelParamNames[j])) {
            setMethodsTable.put(propName.toLowerCase(), m);
            break;
          }
        }
      } else if (name.startsWith("get") && m.getParameterTypes().length == 0) {
        String propName = name.substring(3);
        for (int j = 0; j < modelParamNames.length; j++) {
          if (propName.equalsIgnoreCase(modelParamNames[j])) {
            propName = capitalize(propName);
            getMethodsTable.put(propName, m);
            break;
          }
        }
      } else if (name.startsWith("is") && m.getParameterTypes().length == 0 &&
              m.getReturnType().equals(boolean.class)) {
        String propName = name.substring(2);
        for (int j = 0; j < modelParamNames.length; j++) {
          if (propName.equalsIgnoreCase(modelParamNames[j])) {
            propName = capitalize(propName);
            getMethodsTable.put(propName, m);
            break;
          }
        }
      }
    }
  }

  private void setParameters(SimModel model) {
    for (Iterator iter = parameterMap.values().iterator(); iter.hasNext();) {
      Parameter p = (Parameter) iter.next();


      Method m = (Method) setMethodsTable.get(p.getName().toLowerCase());
      Invoker invoker = InvokerFactory.createInvoker(m.getParameterTypes()[0],
                                                     model, m, p.getStringValue());
      try {
        invoker.execute();
      } catch (InvokerException ex) {
        SimUtilities.showError("Unable to set model parameter "
                               + p.getName(), ex);
        ex.printStackTrace();
        System.exit(0);

      } catch (InvocationTargetException ex) {
        SimUtilities.showError("Unable to set model parameter "
                               + p.getName(), ex);
        ex.printStackTrace();
        System.exit(0);
      } catch (IllegalAccessException ex) {
        SimUtilities.showError("Unable to set model parameter "
                               + p.getName(), ex);
        ex.printStackTrace();
        System.exit(0);
      } catch (NullPointerException ex) {
        SimUtilities.showError("Unable to set model parameter "
                               + p.getName(), ex);
        ex.printStackTrace();
        System.exit(0);
      }
    }
  }

  private void incrementParameters() {
    for (Iterator iter = parameterMap.values().iterator(); iter.hasNext();) {
      Parameter p = (Parameter) iter.next();

      // we only want to increment top-level parameters -- those without
      // parents because parameter.increment is forwarded to a parents
      // children.
      if (p.getParent() == null) {

        // increment() will call increment on children of this parameter,
        // if any, and returns true if the increment was successful.
        if (!p.increment()) {
          if (p.isConstant()) {
            if (dynamicParamNames.size() == 0) {
              finished = true;
              return;
            }
          } else {
            finished = true;
            return;
          }

        }
      }
    }

  }

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a parameter in this
   * ParameterSetter; otherwise false.
   */
  public boolean isParameter(String name) {
    if (name.equalsIgnoreCase("RngSeed")) return true;
    return parameterMap.containsKey(new ParamName(name));
  }

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a constant parameter in this
   * ParameterSetter. false if the named parameter is not constant <b>or</b>
   * if the named parameter is not found.
   */
  public boolean isConstant(String name) {
    Parameter p = (Parameter) parameterMap.get(new ParamName(name));
    return p == null ? false : p.isConstant();
  }


  /**
   * @return a list of the names dynamic (non-constant) parameters defined in
   * this ParameterSetter.
   */
  public ArrayList getDynamicParameterNames() {
    ArrayList l = new ArrayList();
    l.addAll(dynamicParamNames);
    if (!parameterMap.containsKey(new ParamName("RngSeed")) && (!l.contains("RngSeed"))) l.add("RngSeed");
    return l;
  }

  /**
   * Returns a Hashtable whose keys are parameter names and values are parameter
   * values. The parameters themselves are those specified by the model in
   * <code>getInitParam</code>  with the addition of the random seed
   * and the value is the current parameter file
   * value if the parameter exists in the parameter file. If not, then the
   * value is that of the model.
   *
   * @param model the model whose parameters we want to get
   */
  public Hashtable getDefaultModelParameters(SimModel model) {
    Hashtable props = new Hashtable(23);

    if (model != null) {
      if (first) setup(model);


      props.put("RngSeed", new Long(Random.getSeed()));
      if (model != null) {
        Object[] args = new Object[]{};

        for (Iterator iter = getMethodsTable.keySet().iterator(); iter.hasNext();) {
          String propName = (String) iter.next();
          Method m = (Method) getMethodsTable.get(propName);
          Object val = null;
          try {
            val = m.invoke(model, args);
          } catch (IllegalAccessException e) {
            SimUtilities.showError("Error getting model parameter '" + propName + "'", e);
            if (model.getController().getExitOnExit()) System.exit(0);
            return null;
          } catch (IllegalArgumentException e) {
            SimUtilities.showError("Error getting model parameter '" + propName + "'", e);
            if (model.getController().getExitOnExit()) System.exit(0);
            return null;
          } catch (InvocationTargetException e) {
            SimUtilities.showError("Error getting model parameter '" + propName + "'", e);
            if (model.getController().getExitOnExit()) System.exit(0);
            return null;
          }
          props.put(propName, val);
        }

        for (Iterator iter = parameterMap.values().iterator(); iter.hasNext();) {
          Parameter p = (Parameter) iter.next();
          StringBuffer key = new StringBuffer(p.getName());
          char c = Character.toUpperCase(key.charAt(0));
          key.setCharAt(0, c);
          String propName = key.toString();
          if (props.containsKey(propName)) {
            props.put(propName, p.getStringValue());
          }
        }
      }
    }

    return props;
  }

  /**
   * Sets the parameters for the specified model. Sets the model's
   * parameters to the current values of the parameters contained by this
   * ParameterSetter.
   *
   * @param model the model whose parameters are set
   */
  public void setModelParameters(SimModel model) {
    model.generateNewSeed();
    Random.createUniform();

    if (first) {
      setup(model);
      first = false;
    }
    setParameters(model);
  }

  private void setup(SimModel model) {
    if (doSetup) {
      setupParamArray(model);
      fillMethodTable(model);
    }
  }

  /**
   * @return true if the parameter space described by this ParameterSetter
   * has more parameter combinations to be explored; false if not.
   */
  public boolean hasNext() {
    return !finished;
  }

  /**
   * Increments the parameters in this ParameterSetter and then sets
   * the model's parameters.
   *
   * @param model the model whose parameters are set
   */
  public void setNextModelParameters(SimModel model) {

    if (first) {
      setup(model);
      setModelParameters(model);
      first = false;
      return;
    }

    incrementParameters();
    if (!finished) setModelParameters(model);
  }

  /**
   * Gets the current value of a named parameter. A reference to the model
   * may be necessary for those ParameterSetters whose formats allow for
   * some interaction with the model.
   *
   * @param name the name of the parameter
   * @param model a reference to the SimModel associated with these
   * parameters
   * @return the current value of the named parameter. Returns null if the
   * parameter is not found.
   */
  public Object getParameterValue(String name, SimModel model) {
    Parameter p = (Parameter) parameterMap.get(new ParamName(name));
    if (p != null) return p.getStringValue();
    return null;
  }

  /**
   * Gets the current value of a named parameter. A reference to the model
   * may be necessary for those ParameterSetters whose formats allow for
   * some interaction with the model.
   *
   * @param name the name of the parameter
   * @param model a reference to the SimModel associated with these
   * parameters
   * @return the named parameter. Returns null if the
   * parameter is not found.
   */
  public Object getParameter(String name) {
    Parameter p = (Parameter) parameterMap.get(new ParamName(name));
    return p;
  }

  /**
   * @return An iterator returning the names of the parameters contained in
   * this ParameterSetter.
   */
  public Iterator parameterNames() {
    return new ParamNameIterator(parameterMap.keySet().iterator());
  }
  
  public Class getParameterType(String name) {
    Method method = (Method) getMethodsTable.get(capitalize(name));
    if (method == null) return null;
    return method.getReturnType();
  }
  
}
