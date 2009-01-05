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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uchicago.src.sim.engine.ActionQueue;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;

/**
 * Factory for creating CEquations and automatically scheduling their evaluate and assignment at
 * some specified interval. All equations scheduled for the same tick will be evaluated and then
 * they will all be assigned. The order of evaluation between is undetermined and so it is expected
 * that the equations can be evaluated in any order without producing side-effects.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class CEquationFactory {
  
  private Set scheduledIntervals = new HashSet();
  private Map eqActionsByInterval = new HashMap();
  private ActionQueue queue = new ActionQueue();
  private List execList = new ArrayList();
  private Schedule schedule;

  /**
   * Creates a CEquationFactory.
   * 
   * @param schedule the schedule against which to schedule created CEquations evaluation and
   * assignment
   */ 
  public CEquationFactory(Schedule schedule) {
    this.schedule = schedule;
  }
  
  class MainAction extends BasicAction {
   
    public void execute() {
      update();
    }
  }
  
  static class EqAction extends BasicAction {
    
    private List equations = new ArrayList();
    
    public EqAction(CEquation equation) {
      equations.add(equation);
    }
    
    public void addEquation(CEquation equation) {
      equations.add(equation);
    }
    
    // we don't use this method to execute, but rather the
    // evaluate and assign methods
    public void execute() {
      throw new UnsupportedOperationException();
    }
    
    public void evaluate() {
      for (Iterator iter = equations.iterator(); iter.hasNext(); ) {
        CEquation equation = (CEquation) iter.next();
        equation.evaluate();
      }
    }
    
    public void postEvaluate(ActionQueue queue) {
      for (Iterator iter = equations.iterator(); iter.hasNext(); ) {
        CEquation equation = (CEquation) iter.next();
        equation.assign();
      }
      
      this.reSchedule(queue);
    }
  }
  
 
  /**
   * Creates and schedule a CEquation.
   * 
   * @param target    the target from which the equations variable values are read and to which the
   *                  result is assigned
   * @param equation  the equation to evaluate.
   * @param resultVar the name of the variable to assign the result to
   * @param updateInterval the interval at which to evaluate and assign the created CEquation
   * @return the created CEquation.
   * @see uchicago.src.sim.math.CEquation
   */ 
  public  CEquation createEquation(Object target, String equation, String resultVar, 
                                  double updateInterval)
  {
    return createEquation(target, equation, resultVar, 0, updateInterval);
  }
  
  /**
   * Creates and schedule a CEquation.
   * 
   * @param target    the target from which the equations variable values are read and to which the
   *                  result is assigned
   * @param equation  the equation to evaluate.
   * @param resultVar the name of the variable to assign the result to
   * @param initialDTValue the initial value of dt to use on the first evaluation when
   *                       it would otherwise be set to 0
   * @param updateInterval the interval at which to evaluate and assign the created CEquation
   * @return the created CEquation.
   * @see uchicago.src.sim.math.CEquation
   */ 
  public  CEquation createEquation(Object target, String equation, String resultVar, double initialDTValue,
                                  double updateInterval)
  {
    
    CEquation anEquation = new CEquation(target, this.schedule, equation, resultVar, initialDTValue);
    scheduleEquation(anEquation, updateInterval);
    return anEquation;
  }

  private void scheduleEquation(CEquation anEquation, double updateInterval) {
    Double interval = new Double(updateInterval);
    if (!scheduledIntervals.contains(interval)) {
      // schedule an update for this interval
      schedule.scheduleActionAtInterval(updateInterval, new MainAction());
      scheduledIntervals.add(interval);
    }
    
    EqAction action = (EqAction) eqActionsByInterval.get(interval);
    if (action == null) {
      action = new EqAction(anEquation);
      action.setNextTime(this.schedule.getCurrentTime() + updateInterval);
      action.setIntervalTime(updateInterval);
      eqActionsByInterval.put(interval, action);
      queue.toss(action);
    } else {
      action.addEquation(anEquation);
    }
  }
  
  private void update() {
    execList.clear();
    double currentTime = schedule.getCurrentTime();
    while (queue.size() > 0) {
      BasicAction action = queue.peekMin();
      if (action.getNextTime() <= currentTime) {
        execList.add(queue.popMin());
      } else {
        break;
      }
    }
    
    for (int i = 0, n = execList.size(); i < n; i++) {
      EqAction action = (EqAction) execList.get(i);
      action.evaluate();
    }
    
    for (int i = 0, n = execList.size(); i < n; i++) {
      EqAction action = (EqAction) execList.get(i);
      action.postEvaluate(queue);
    }
  }
}
