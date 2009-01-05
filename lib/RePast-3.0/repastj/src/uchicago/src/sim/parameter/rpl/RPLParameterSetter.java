package uchicago.src.sim.parameter.rpl;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.parameter.AbstractParameterSetter;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;

/**
 * A ParameterSetter for reading and working with rpl format parameter
 * files.
 *
 * @version $Revision$ $Date$
 */

public class RPLParameterSetter extends AbstractParameterSetter {

  private RPLCompiler compiler;
  private Set paramSet, constSet;
  private boolean compiled = false;
  private RPLParameter main;
  private boolean finished = false;
  private Map getMethodMap = new HashMap();
  private boolean isRngDynamic = false;


  /**
   * Initialize the setter using the specified fileName. ParameterSetters
   * will most often be created using reflection via a no-arg constructor.
   * This init method functions like a proper constructor.
   *
   * @param fileName the name of the parameter file
   * @throws IOException
   */
  public void init(String fileName) throws IOException {
    compiler = new RPLCompiler(fileName);
    compiler.preProcess();
    paramSet = new HashSet();
    for (Iterator iter = compiler.getParamNames().iterator(); iter.hasNext();) {
      String name = (String) iter.next();
      paramSet.add(new ParamName(name));
    }

    constSet = new HashSet();
    for (Iterator iter = compiler.getConstNames().iterator(); iter.hasNext();) {
      String name = (String) iter.next();
      constSet.add(new ParamName(name));
    }

    ParamName rng = new ParamName("RngSeed");
    isRngDynamic = (!constSet.contains(rng));

    compiler.clearNames();
  }

  /**
   * Sets the parameters for the specified model. Sets the model's
   * parameters to the current values of the parameters contained by this
   * ParameterSetter.
   *
   * @param model the model whose parameters are set
   */
  public void setModelParameters(SimModel model) {
    try {
      if (!compiled) {
        compiler.compile(model);
        compiled = true;
        main = compiler.getMain();
      }

      main.setModelParameter(model);
    } catch (IOException ex) {
      SimUtilities.showError("Error setting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    } catch (RepastException ex) {
      SimUtilities.showError("Error setting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    }
  }

  /**
   * Increments the parameters in this ParameterSetter and then sets
   * the model's parameters.
   *
   * @param model the model whose parameters are set
   */
  public void setNextModelParameters(SimModel model) {
    try {
      if (!compiled) {
        // we don't increment if the model hasn't been set to the initial
        // parameters yet.
        compiler.compile(model);
        compiled = true;
        main = compiler.getMain();
      } else {
        incrementParameters();
      }

      if (!finished) main.setModelParameter(model);


    } catch (IOException ex) {
      SimUtilities.showError("Error setting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    } catch (RepastException ex) {
      SimUtilities.showError("Error setting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    }
  }

  private void incrementParameters() {
    finished = !main.next();
  }

  /**
   * @return true if the parameter space described by this ParameterSetter
   * has more parameter combinations to be explored; false if not.
   */
  public boolean hasNext() {
    return !finished;
  }

  /**
   * @return a list of the names dynamic (non-constant) parameters defined in
   * this ParameterSetter.
   */
  public ArrayList getDynamicParameterNames() {
    ArrayList list = new ArrayList();
    for (Iterator iter = paramSet.iterator(); iter.hasNext();) {
      list.add(capitalize(((ParamName) iter.next()).name));
    }

    /*
     * TODO do we want this in here. Note that if this is included
     * the rng seed will appear in the dynamic list but its recorded value
     * does not reflect its new value
     */
    ParamName rng = new ParamName("RngSeed");
    if (!paramSet.contains(rng) && !constSet.contains(rng)) {
      // if neither contain it that means that it is probably dynamic
      // unless the user sets it him or herself.
      list.add(rng.getCapName());
    }
    return list;
  }

  /**
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a parameter in this
   * ParameterSetter; otherwise false.
   */
  public boolean isParameter(String name) {
    ParamName pn = new ParamName(name);
    if (pn.equals(new ParamName("RngSeed"))) return true;
    return paramSet.contains(pn) || constSet.contains(pn);
  }

  /**
   * Tests if the named parameter is a constant.
   *
   * @param name the name of the parameter to test
   * @return true if the specified name is the name of a constant parameter in this
   * ParameterSetter. false if the named parameter is not constant <b>or</b>
   * if the named parameter is not found.
   */
  public boolean isConstant(String name) {
    ParamName pn = new ParamName(name);
    if (pn.equals(new ParamName("RngSeed"))) return !isRngDynamic;
    return constSet.contains(pn) &&
            !(paramSet.contains(pn));
  }

  /**
   * @param name the name of the parameter
   * @return the current value of the named parameter. Returns null if the
   * parameter is not found.
   */
  public Object getParameterValue(String name, SimModel model) {
    Object retval = null;
    try {
      if (!compiled) {
        compiler.compile(model);
        compiled = true;
        main = compiler.getMain();
      }

      retval = getParameterValue(main, name, model);

    } catch (IOException ex) {
      SimUtilities.showError("Error getting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    } catch (RepastException ex) {
      SimUtilities.showError("Error getting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    }

    return retval;
  }

  // if the specified RPLParameter matches the name, then return the value.
  // otherwise iterate through the child parameters and constants of the
  // specified RPLParameter.
  private Object getParameterValue(RPLParameter p, String name, SimModel model)
          throws RepastException {
    Object retval = null;
    if (p.getName().equalsIgnoreCase(name))
      retval = p.getValue(model);
    else {
      for (Iterator iter = p.parameterIterator(); iter.hasNext();) {
        RPLParameter child = (RPLParameter) iter.next();
        Object val = getParameterValue(child, name, model);
        if (val != null) {
          return val;
        }
      }

      for (Iterator iter = p.constantIterator(); iter.hasNext();) {
        RPLParameter child = (RPLParameter) iter.next();
        Object val = getParameterValue(child, name, model);
        if (val != null) {
          return val;
        }
      }
    }

    return retval;
  }

  /**
   * @return An iterator returning the names of the parameters contained in
   * this ParameterSetter.
   */
  public Iterator parameterNames() {
    List allParams = new ArrayList();
    allParams.addAll(paramSet);
    allParams.addAll(constSet);

    return new ParamNameIterator(allParams.iterator());
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
    Hashtable h = new Hashtable();
    try {
      if (!compiled) {
        compiler.compile(model);
        compiled = true;
        main = compiler.getMain();
      }

      String[] params = model.getInitParam();
      for (int i = 0; i < params.length; i++) {
        String pName = params[i];
        ParamName pn = new ParamName(pName);
        if (paramSet.contains(pn) || constSet.contains(pn)) {
          Object val = getParameterValue(pName, model);
          if (val != null) {
            // val will == null if parameter (not const) is defined but not
            // used in a block
            h.put(pn.getCapName(), val);
          } else {
            Method m = (Method) getMethodMap.get(pName.toLowerCase());
            if (m == null) fillGetMethodMap(model, params);
            m = (Method) getMethodMap.get(pName.toLowerCase());
            if (m == null) {
              SimUtilities.showError("Error getting model parameter '" + pName + "'",
                                     new NullPointerException());
              if (model.getController().getExitOnExit()) System.exit(0);
              return null;
            }
            m = (Method) getMethodMap.get(pName.toLowerCase());
            Object ret = m.invoke(model, new Object[]{});
            h.put(pn.getCapName(), ret);
          }
        } else {
          Method m = (Method) getMethodMap.get(pName.toLowerCase());
          if (m == null) fillGetMethodMap(model, params);
          m = (Method) getMethodMap.get(pName.toLowerCase());
          if (m == null) {
            SimUtilities.showError("Error getting model parameter '" + pName + "'",
                                   new NullPointerException());
            if (model.getController().getExitOnExit()) System.exit(0);
            return null;
          }
          Object ret = m.invoke(model, new Object[]{});
          h.put(pn.getCapName(), ret);
        }
      }

      ParamName pn = new ParamName("RngSeed");
      if (paramSet.contains(pn) || constSet.contains(pn)) {
        h.put(pn.getCapName(), getParameterValue("RngSeed", model));
      } else {
        h.put("RngSeed", new Long(Random.getSeed()));
      }


    } catch (IOException ex) {
      SimUtilities.showError("Error getting model parameter", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    } catch (IllegalAccessException ex) {
      SimUtilities.showError("Error getting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    } catch (InvocationTargetException ex) {
      SimUtilities.showError("Error getting model parameters", ex);
      if (model.getController().getExitOnExit()) System.exit(0);
    }

    return h;
  }

  private void fillGetMethodMap(SimModel obj, String[] params) {
    getMethodMap.clear();
    Method[] methods = obj.getClass().getMethods();
    for (int i = 0, n = methods.length; i < n; i++) {
      Method m = methods[i];
      String name = m.getName();
      if (name.startsWith("get") && m.getParameterTypes().length == 0) {
        String propName = name.substring(3).toLowerCase();
        for (int j = 0; j < params.length; j++) {
          if (propName.equalsIgnoreCase(params[j])) {
            getMethodMap.put(propName, m);
            break;
          }
        }
      } else if (name.startsWith("is") && m.getParameterTypes().length == 0 &&
              m.getReturnType().equals(boolean.class)) {
        String propName = name.substring(2).toLowerCase();
        for (int j = 0; j < params.length; j++) {
          if (propName.equalsIgnoreCase(params[j])) {
            getMethodMap.put(propName, m);
            break;
          }
        }
      }
    }
  }
}
