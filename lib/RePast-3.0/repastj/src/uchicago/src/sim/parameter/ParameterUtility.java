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

import java.beans.IntrospectionException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import uchicago.src.reflector.Introspector;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;

/**
 * Some utility methods for working with parameters. All parameter queries
 * should now go through this class. This class is a singleton. The single
 * instance can be retrieved with ParameterUtility.getInstance(). Before using
 * this single instance, it must be created. All the controllers that come
 * with repast do this in their constructors.<p>
 *
 * Various methods of ParameterUtility refer to default parameters. These
 * are the parameters that are loaded via an external parameter file.
 *
 * @version $Revision$ $Date$
 */

public class ParameterUtility {
  
  private static Set floatingTypes = new HashSet();
  static {
    floatingTypes.add(double.class);
    floatingTypes.add(float.class);
    floatingTypes.add(Double.class);
    floatingTypes.add(float.class);
  }

  //private HashMap defaultParams = new HashMap();
  private ParameterSetter params;
  private static ParameterUtility instance;

  /**
   * Creates an instance of a ParameterUtility.
   *
   * @param params a list of Parameter objects. These are the default
   * parameters for this ParameterUtility instance.
   */
  public static void createInstance(ParameterSetter params) {
    instance = new ParameterUtility(params);
  }

  /**
   * Creates an instance of a ParameterUtility.
   */
  // @todo why is this necessary?
  public static void createInstance() {
    instance = new ParameterUtility(null);
  }

  /**
   * Returns the single instance of this ParameterUtility.
   * @return
   */
  public static ParameterUtility getInstance() {
    return instance;
  }

  private ParameterUtility(ParameterSetter params) {
    if (params != null) this.params = params;

  }

  /**
   * Returns true if the named parameter is a default parameter. A default
   * parameter is a parameter names in a parameter file. Otherwise
   * returns false.
   *
   * @param name the name of the parameter to check
   */
  public boolean isDefaultParam(String name) {
    return params.isParameter(name);
  }

  /**
   * Returns true if the named parameter is a default parameter and is
   * a constant parameter. Otherwise returns false.
   *
   * @param name the name of the parameter to check.
   */
  public boolean isConstantDefaultParam(String name) {
    return params.isConstant(name);
  }

  /**
   * Returns a list of the names of the dynamic default parameters associated
   * with this ParameterUtility. The names are lower cased before they are
   * returned.
   */
  public ArrayList getDynamicParameterNames() {
    ArrayList list = new ArrayList();
    for (Iterator iter = params.getDynamicParameterNames().iterator(); iter.hasNext();) {
      String name = (String)iter.next();
      list.add(name.toLowerCase());
    }

    return list;
  }

  /**
   * Returns a Hashtable whose keys are the names of the default parameters
   * and whose values are the values of the parameters. The value of the
   * parameter is not necessarily the current value of the model parameter.
   *
   * @param model the model whose parameters we want to get
   * @throws java.beans.IntrospectionException
   * @throws java.lang.reflect.InvocationTargetException
   * @throws java.lang.IllegalAccessException
   */
  public Hashtable getDefaultParameters(SimModel model)
          throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    return params.getDefaultModelParameters(model);
  }

  /**
   * Creates a Vector of Parameters based on the current property values
   * of Object o.
   *
   * @param o the Object whose properties should become the parameters
   * @param properties the array of properties to create parameters from
   */
  public ParameterSetter createParameters(Object o, String[] properties)
          throws IntrospectionException, IllegalAccessException,
          InvocationTargetException {
    return new DefaultParameterSetter(o, properties);
  }

  /**
   * Returns a Hashtable of all the current property name and value pairs for
   * the specified model. The properties are those specified by the models
   * getInitParam() method and the value is the current value of that property
   * in the model.
   *
   * @param model the model whose properties we want to get.
   * @return
   * @throws java.beans.IntrospectionException
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.reflect.InvocationTargetException
   */
  public Hashtable getModelProperties(SimModel model) throws
          IntrospectionException, IllegalAccessException,
          InvocationTargetException {
    Hashtable props = new Hashtable(23);


    model.clearPropertyListeners();

    String[] pNames = model.getInitParam();
    Introspector intro = new Introspector();

    intro.introspect(model, pNames);
    props = intro.getPropValues();


    return props;
  }

  /**
   * Creates and writes a parameter file for the specified properties
   * of the specified object. The file write property name value pairs in
   * repast's parameter file format.
   *
   * @param o the object whose properties we want to write
   * @param props the names of the properties to write
   * @param fileName the name of the file to write the properties to
   * @throws java.beans.IntrospectionException
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.reflect.InvocationTargetException
   * @throws uchicago.src.sim.util.RepastException
   * @throws java.io.IOException
   */
  public void makeParameterFileFromCurVals(Object o, String[] props,
                                           String fileName) throws IntrospectionException, IllegalAccessException,
          InvocationTargetException, RepastException, IOException {
    ParameterSetter setter = createParameters(o, props);
    
    StringBuffer b = new StringBuffer("runs: 1" + SimUtilities.newLine);
    for (Iterator iter = setter.parameterNames(); iter.hasNext(); ) {
      String name = (String)iter.next();
      // we use null here as the DefaultParameterSetter doesn't need a reference
      // to the model to get parameter values.
      String setVal = setter.getParameterValue(name, null).toString();
      if (setVal == null) {
        throw new RepastException(null, "Illegal Parameter type");
      } else {
        Class clazz = ((DefaultParameterSetter)setter).getParameterType(name);
        /*
        if (floatingTypes.contains(clazz)) {
          NumberFormat format = NumberFormat.getInstance();
          format.setMaximumFractionDigits(340);
          System.out.println("new Double(setVal) = " + new Double(setVal));
          setVal = NumberFormat.getInstance().format(new Double(setVal));
        }
        */
        String set = "set: ";
        if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) set = "set_boolean: ";
        else if (clazz.equals(String.class)) set = "set_string: ";
        setVal = set + setVal;
        b.append(name);
        b.append(" ");
        b.append("{");
        b.append(SimUtilities.newLine);
        b.append("  ");
        b.append(setVal);
        b.append(SimUtilities.newLine);
        b.append("}");
        b.append(SimUtilities.newLine);
        b.append(SimUtilities.newLine);
      }
    }

    PrintWriter out = null;
    try {
      out = new PrintWriter(new FileOutputStream(fileName));
      out.println(b.toString());
      out.flush();
    } catch (IOException ex) {
      if (out != null) out.close();
      throw ex;
    }
  }
  
  public String getPropertyNamesHeader(SimModel model) {
    Hashtable propsVals = null;
         try {
          propsVals = getModelProperties(model);
        } catch (IntrospectionException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);

        } catch (IllegalAccessException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);
        } catch (InvocationTargetException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);
        }

    StringBuffer b = new StringBuffer();

    ArrayList list = new ArrayList(propsVals.keySet());
    Collections.sort(list);

    for (int i = 0; i < list.size(); i++) {
      Object key = list.get(i);
      b.append(key).append("\t");
      b.append(propsVals.get(key));
      b.append(SimUtilities.newLine);
    }

    return b.toString();

  }

  
  public String getPropertyValues(SimModel model) {
    Hashtable propsVals = null;
         try {
          propsVals = getModelProperties(model);
        } catch (IntrospectionException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);

        } catch (IllegalAccessException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);
        } catch (InvocationTargetException ex) {
          SimUtilities.showError("Error getting model parameters", ex);
          System.exit(0);
        }

    StringBuffer b = new StringBuffer();

    ArrayList list = new ArrayList(propsVals.keySet());
    Collections.sort(list);

    for (int i = 0; i < list.size(); i++) {
      Object key = list.get(i);
      b.append(propsVals.get(key)).append("\t");
    }

    return b.toString();

  }
}
