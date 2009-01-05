/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
 * Neither the name of the ROAD nor the names of its
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
package uchicago.src.sim.math;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;

import uchicago.src.sim.engine.TickCounter;
import uchicago.src.sim.util.RepastException;
import uchicago.src.sim.util.SimUtilities;

/**
 * A user specifiable evaluatable equation. This class will evaluate a specified equation
 * replacing the variables in the equation with values from a specified object. The result
 * of the equation evaluation will then be written to the object.<p>
 * <p/>
 * <p/>
 * When evaluated a CEquation will
 * replace the variables in the specified equation with those read from a specified
 * object. For example, if the equation is "x + 1 * z", then evaluation will replace x and z
 * in the equation with the results of calling getX() and getZ() on the object. The
 * result of this evaluation will be assigned to the a resultVar in a similar manner.
 * For example, if the resultVar is "y", then on assignment the result of the equation evaluation
 * will be assigned to the object via the object's setY(double) method.<p>
 * <p/>
 * The variable "t" can also be used. "t" is replaced by the value of the current
 * tick count. Similarly, the variable "dt" can be used. It will be set to the delta of the current
 * tick count and the tick count at which the last evaluation occured.
 * <p/>
 * Evaluation is performed using the <code>evaluate</code> method and assignment is
 * performed using the <code>assign</code> method.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class CEquation {

  private static final Object[] EMPTY_OBJ_ARRAY = new Object[0];
  private static Set validReturnTypes = new HashSet();
  private static Set validAssignArgTypes = new HashSet();

  private String equation = "";
  private Object target;
  private Double result = new Double(0);
  private ResultVariable resultVar;
  private TickCounter counter;
  private JEP eqParser = new JEP();
  private List vars = new ArrayList();
  private Double initialDTVal;
  private double lastTimeEvaluated = -1;

  static {
    validReturnTypes.add(double.class);
    validReturnTypes.add(int.class);
    validReturnTypes.add(float.class);
    validReturnTypes.add(long.class);
    validReturnTypes.add(Double.class);
    validReturnTypes.add(Float.class);
    validReturnTypes.add(Integer.class);
    validReturnTypes.add(Long.class);

    validAssignArgTypes.add(double.class);
    validAssignArgTypes.add(Double.class);
  }

  static interface Variable {
    public void setEquationValue(JEP parser, Object target) throws IllegalAccessException, InvocationTargetException;
  }

  static class ResultVariable {

    private String var;
    private Method write;

    public ResultVariable(String var) {
      this.var = var;
    }

    public void setValue(Object target, Double val) throws IllegalAccessException, InvocationTargetException {
      write.invoke(target, new Object[]{val});
    }
  }

  static class TimeVariable implements Variable {

    protected TickCounter counter;

    public TimeVariable(TickCounter counter) {
      this.counter = counter;
    }

    public void setEquationValue(JEP parser, Object target) throws IllegalAccessException, InvocationTargetException {
      parser.addVariable("t", counter.getCurrentTime());
    }
  }

  static class DTVariable extends TimeVariable {

    double lastTime = 0;
    Double initialVal;

    public DTVariable(TickCounter counter, Double initialDTVal) {
      super(counter);
      this.initialVal = initialDTVal;
    }

    public void setEquationValue(JEP parser, Object target) throws IllegalAccessException, InvocationTargetException {
      double currentTime = counter.getCurrentTime();
      if (lastTime == 0) {
        if (initialVal == null)
          parser.addVariable("dt", 0);
        else
          parser.addVariable("dt", initialVal.doubleValue());

      } else {
        parser.addVariable("dt", currentTime - lastTime);
      }
      lastTime = currentTime;
    }
  }

  static class EqVariable implements Variable {

    private String var;
    private Method read;

    public EqVariable(Method read, String var) {
      this.read = read;
      this.var = var;
    }

    public void setEquationValue(JEP parser, Object target) throws IllegalAccessException, InvocationTargetException {
      Object value = read.invoke(target, CEquation.EMPTY_OBJ_ARRAY);
      parser.addVariableAsObject(var, value);
    }
  }

  /**
   * Creates a CEquation from the specified parameters. When evaluated the CEquation will
   * replace the variables in the specified equation with those read from the specified
   * target. For example, if the equation is "x + 1 * z", then evaluation will replace x and z
   * in the equation with the results of calling getX() and getZ() on the target. The
   * result of this evaluation will be assigned to the specified resultVar in a similar manner.
   * For example, if the resultVar is "y", then on assignment the result of the equation evaluation
   * will be assigned to the target via a setY(double) method.<p>
   * <p/>
   * The variable "t" can also be used. "t" is replaced by the value of the current
   * tick count. Similarly, the variable "dt" can be used. It will be set to the delta of the current
   * tick count and the tick count at which the last evaluation occured. dt defaults to a value of
   * 0 on the first evaluation. Use the alternate constructor to specify a different initial value.
   * <p/>
   * Evaluation is performed using the <code>evaluate</code> method and assignment is
   * performed using the <code>assign</code> method.
   *
   * @param target    the target from which the equations variable values are read and to which the
   *                  result is assigned
   * @param counter   a TickCounter from which the value of the "t" variable can be set
   * @param equation  the equation to evaluate.
   * @param resultVar the name of the variable to assign the result to
   */
  public CEquation(Object target, TickCounter counter, String equation, String resultVar) {
    this(target, counter, equation, resultVar, 0);
  }

  /**
   * Creates a CEquation from the specified parameters. When evaluated the CEquation will
   * replace the variables in the specified equation with those read from the specified
   * target. For example, if the equation is "x + 1 * z", then evaluation will replace x and z
   * in the equation with the results of calling getX() and getZ() on the target. The
   * result of this evaluation will be assigned to the specified resultVar in a similar manner.
   * For example, if the resultVar is "y", then on assignment the result of the equation evaluation
   * will be assigned to the target via a setY(double) method.<p>
   * <p/>
   * The variable "t" can also be used. "t" is replaced by the value of the current
   * tick count. Similarly, the variable "dt" can be used. It will be set to the delta of the current
   * tick count and the tick count at which the last evaluation occured.
   * <p/>
   * Evaluation is performed using the <code>evaluate</code> method and assignment is
   * performed using the <code>assign</code> method.
   *
   * @param target         the target from which the equations variable values are read and to which the
   *                       result is assigned
   * @param counter        a TickCounter from which the value of the "t" variable can be set
   * @param equation       the equation to evaluate.
   * @param resultVar      the name of the variable to assign the result to
   * @param initialDTValue the initial value of dt to use on the first evaluation when
   *                       it would otherwise be set to 0
   */
  public CEquation(Object target, TickCounter counter, String equation, String resultVar, double initialDTValue) {
    this.equation = equation;
    this.counter = counter;
    this.target = target;
    this.resultVar = new ResultVariable(resultVar);
    this.initialDTVal = new Double(initialDTValue);
    
    eqParser.addStandardConstants();
    eqParser.addStandardFunctions();
    
    // setup the evaluator
    eqParser.setAllowUndeclared(true);
    reset();
  }

  private void reset() {
    vars.clear();
    result = new Double(0);
    eqParser.parseExpression(this.equation);
    checkForErrors();

    try {
      initTarget();
      initResultVariable();
    } catch (IntrospectionException e) {

    }
  }

  private void initResultVariable() throws IntrospectionException {
    Class clazz = target.getClass();
    BeanInfo info = Introspector.getBeanInfo(clazz);
    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
    for (int i = 0; i < descriptors.length; i++) {
      PropertyDescriptor descriptor = descriptors[i];
      if (descriptor.getName().equalsIgnoreCase(resultVar.var)) {
        Method writeMethod = descriptor.getWriteMethod();
        if (writeMethod == null) {
          String msg = "Equation Error - target is missing set method for resultVar '" + resultVar.var + "'";
          SimUtilities.showError(msg,
                  new IllegalArgumentException(msg));
        } else {
          Class[] args = writeMethod.getParameterTypes();
          if (args.length > 1) {
            String msg = "Equation Error - target has invalid set method for resultVar '" + resultVar.var + "'";
            SimUtilities.showError(msg,
                    new IllegalArgumentException(msg));
          }

          if (!validAssignArgTypes.contains(args[0])) {
            String msg = "Equation Error - target set method for resultVar '" + resultVar.var + "' must take a numeric parameter";
            SimUtilities.showError(msg,
                    new IllegalArgumentException(msg));
          }

          resultVar.write = writeMethod;
          return;
        }
      }
    }

    String msg = "Equation Error - unable to find set method for resultVar: '" + resultVar.var + "'";
    SimUtilities.showError(msg, new IllegalArgumentException(msg));
  }

  private void initTarget() throws IntrospectionException {
    SymbolTable table = eqParser.getSymbolTable();
    // creates Variables for each var
    Class clazz = target.getClass();
    BeanInfo info = Introspector.getBeanInfo(clazz);
    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
    for (Enumeration keys = table.keys(); keys.hasMoreElements();) {
      String var = (String) keys.nextElement();
      if (var.equals("e") || var.equals("pi")) continue;
      if (var.equals("t")) {
        this.vars.add(new TimeVariable(counter));
      } else if (var.equals("dt")) {
        DTVariable dtVar = new DTVariable(counter, initialDTVal);
        if (lastTimeEvaluated != -1) dtVar.lastTime = lastTimeEvaluated;
        this.vars.add(dtVar);
      } else {
        boolean found = false;
        for (int i = 0; i < descriptors.length; i++) {
          PropertyDescriptor descriptor = descriptors[i];
          if (descriptor.getName().equalsIgnoreCase(var)) {
            Method readMethod = descriptor.getReadMethod();
            if (!validReturnTypes.contains(readMethod.getReturnType())) {
              String msg = "Equation Error - target get method for variable '" + var + "' does not return a number";
              SimUtilities.showError(msg, new IllegalArgumentException(msg));
            }
            Variable eqVar = new EqVariable(readMethod, var);
            found = true;
            this.vars.add(eqVar);
            break;
          }
        }
        if (!found) {
          String msg = "Equation Error - target get method for variable '" + var + "' not found.";
          SimUtilities.showError(msg, new IllegalArgumentException(msg));
        }
      }
    }
  }

  private void checkForErrors() {
    if (eqParser.hasError()) {
      SimUtilities.showError("CEquation Error, error evaluating equation '" + equation + "'", new RepastException(eqParser.getErrorInfo()));
    }
  }

  /**
   * Evaluates the equation encapsulated by this CEquation. This does <b>NOT</b> assign
   * the result to the variable specified in the constructor. To assign the result to the
   * result variable, follow the <code>evaluate()</code> call  with
   * a call to <code>assign()</code> or alternatively call <code>evaluateAndAssign()</code>.
   *
   * @see #assign()
   * @see #evaluateAndAssign()
   */
  public void evaluate() {
    // iterate through vars list calling setValue
    for (Iterator iter = vars.iterator(); iter.hasNext();) {
      Variable var = (Variable) iter.next();
      try {
        var.setEquationValue(eqParser, target);
      } catch (IllegalAccessException e) {
        String msg = "Equation Error - unable to evaluate equation";
        SimUtilities.showError(msg, new IllegalArgumentException(msg));
      } catch (InvocationTargetException e) {
        String msg = "Equation Error - unable to evaluate equation";
        SimUtilities.showError(msg, new IllegalArgumentException(msg));
      }
    }

    result = new Double(eqParser.getValue());
    checkForErrors();
    lastTimeEvaluated = counter.getCurrentTime();
  }

  /**
   * Assigns the result of the last call to <code>evaluate</code> to the result variable of the
   * target specified in the constructor.
   */
  public void assign() {
    try {
      resultVar.setValue(target, result);
    } catch (IllegalAccessException e) {
      String msg = "Equation Error - unable to assign result to resultVar '" + resultVar.var + "'";
      SimUtilities.showError(msg, new IllegalArgumentException(msg));
    } catch (InvocationTargetException e) {
      String msg = "Equation Error - unable to assign result to resultVar '" + resultVar.var + "'";
      SimUtilities.showError(msg, new IllegalArgumentException(msg));
    }
  }

  /**
   * Evaluates the currently encapsulated equation and assigns the result to the result
   * variable.
   */
  public void evaluateAndAssign() {
    evaluate();
    assign();
  }

  /**
   * @return the equation currently encapsulated and evaluated by this CEquation.
   */
  public String getEquation() {
    return equation;
  }

  /**
   * Sets the equation to evalute. 
   * 
   * @param equation
   */ 
  public void setEquation(String equation) {
    this.equation = equation;
    eqParser = new JEP();
    
    eqParser.addStandardConstants();
    eqParser.addStandardFunctions();
    eqParser.setAllowUndeclared(true);
    reset();
  }
}
