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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.SimUtilities;

/**
 * Placeholder type parameter setter that only uses a model's
 * current parameters. This does no iteration through a parameter
 * space.
 *
 * @version $Revision$ $Date$
 */
class SingleSetParameterSetter extends AbstractParameterSetter {

  private ArrayList paramList = new ArrayList();
  private boolean requiresSetup = true;
  private Map getMethodsTable = new HashMap();
  private int runCount = 0;
  private int numRuns = 1;
  public SingleSetParameterSetter(int numRuns) {
    this.numRuns = numRuns;
    paramList.add("RngSeed");
  }

  private void setup(SimModel obj) {
    if (requiresSetup) {
      String[] modelParamNames = obj.getInitParam();
      getMethodsTable.clear();
      Method[] methods = obj.getClass().getMethods();
      for (int i = 0, n = methods.length; i < n; i++) {
        Method m = methods[i];
        String name = m.getName();
        if (name.startsWith("get") && m.getParameterTypes().length == 0) {
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


      requiresSetup = false;
    }
  }

  /**
   * Initialize the setter using the specified fileName. ParameterSetters
   * will most often be created using reflection via a no-arg constructor.
   * This init method functions like a proper constructor.
   *
   * Here, this throws a UnsupportedOperationException as SingleSetParameterSetter
   * should not be passed a file.
   *
   * @param fileName the name of the parameter file
   * @throws IOException
   * @throws UnsupportedOperationException
   */
  public void init(String fileName) throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * Sets the parameters for the specified model. Sets the model's
   * parameters to the current values of the parameters contained by this
   * ParameterSetter.
   *
   * In this case, this does nothing as a the parameters are all in
   * the model itself.
   *
   * @param model the model whose parameters are set
   */
  public void setModelParameters(SimModel model) {
  }

  /**
   * Increments the parameters in this ParameterSetter and then sets
   * the model's parameters.
   *
   * In this case, this does nothing as a the parameters are all in
   * the model itself.
   *
   * @param model the model whose parameters are set
   */
  public void setNextModelParameters(SimModel model) {
    runCount++;

  }

  /**
   *
   * @return false as the parameter space is just the current parameters
   * contained by the model.
   */
  public boolean hasNext() {
    return runCount < numRuns;
  }

  /**
   * @return a list of the names dynamic (non-constant) parameters defined in
   * this ParameterSetter.
   */
  public ArrayList getDynamicParameterNames() {
    return paramList;
  }

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a parameter in this
   * ParameterSetter; otherwise false.
   */
  public boolean isParameter(String name) {
    if (name.equalsIgnoreCase("RngSeed")) return true;
    return false;
  }

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a constant parameter in this
   * ParameterSetter. false if the named parameter is not constant <b>or</b>
   * if the named parameter is not found.
   */
  public boolean isConstant(String name) {
    return false;
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
    return null;
  }

  /**
   * @return An iterator returning the names of the parameters contained in
   * this ParameterSetter.
   */
  public Iterator parameterNames() {
    return paramList.iterator();
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
   * @return a Hashtable whose keys are parameter names and values are parameter
   * values.
   */
  public Hashtable getDefaultModelParameters(SimModel model) {
    setup(model);
    Hashtable h = new Hashtable();
    h.put("RngSeed", new Long(model.getRngSeed()));
    Object[] args = new Object[0];
    String propName = null;
    try {
      for (Iterator iter = getMethodsTable.keySet().iterator(); iter.hasNext();) {
        propName = (String) iter.next();
        Method m = (Method) getMethodsTable.get(propName);
        Object val = m.invoke(model, args);
        h.put(propName, val);
      }
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

    return h;
  }
}
