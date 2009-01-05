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
package uchicago.src.sim.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.TickCounter;
import uchicago.src.sim.math.CEquation;
import uchicago.src.sim.math.CEquationFactory;

/**
 * Tests CEquation
 */
public class CEquationTest extends TestCase {
  
  private CEquation equation;
  private EquationTarget target;
  
  class Counter implements TickCounter {
    
    double curTime = 2.0;
    /**
     * @return the current time, the current tick count
     */
    public double getCurrentTime() {
      return curTime;
    }
    
    public void increment() {
      curTime++;
    }
  }
  
  /*
  x = 3;
  y = 4;
  z = 1;
  */

  public CEquationTest(String name) {
    super(name);
  }

  public void setUp() {
    target = new EquationTarget();
  }
  
  public void testSin() {
    equation = new CEquation(target, new Counter(), "sin(x * 10)", "y");
    equation.evaluate();
    equation.assign();
    assertEquals(Math.sin(30), target.getY(), 0);
  }
  
  public void testEq() {
    equation = new CEquation(target, new Counter(), "x + z * 5", "y");
    equation.evaluate();
    equation.assign();
    
    assertEquals(8, target.getY(), 0);
  }
  
  public void testEqSwitch() {
    Counter counter = new Counter();
    equation = new CEquation(target, counter, "x + z * 5", "y");
    equation.evaluate();
    equation.assign();
    counter.increment();
    
    assertEquals(8, target.getY(), 0);
    equation.evaluate();
    equation.assign();
    counter.increment();
    counter.increment();
    
    // current time = 5
    // last evaluated at 3 so dt should now = 2
    equation.setEquation("x / 2 * z * dt");
    equation.evaluateAndAssign();
    
    assertEquals(3.0, target.getY(), 0);
  }
  
  public void testT() {
    equation = new CEquation(target, new Counter(), "t * x + z * 5", "y");
    equation.evaluate();
    equation.assign();
    
    assertEquals(11, target.getY(), 0);
  }
  
  public void testDTInitialValue() {
    Counter counter = new Counter();
    // counter = 2.0
    equation = new CEquation(target, counter, "dt * x + z * 5.1", "y", 3);
    equation.evaluate();
    equation.assign();
    assertEquals(14.1, target.getY(), 0);
    
    counter.increment();
    counter.increment();
    
    equation.evaluate();
    equation.assign();
    assertEquals(11.1, target.getY(), 0);
  }
  
  public void testDT() {
    Counter counter = new Counter();
    // counter = 2.0
    equation = new CEquation(target, counter, "dt * x + z * 5.1", "y");
    equation.evaluate();
    equation.assign();
    assertEquals(5.1, target.getY(), 0);
    
    counter.increment();
    counter.increment();
    
    equation.evaluate();
    equation.assign();
    assertEquals(11.1, target.getY(), 0);
  }
  
  public void testSimpleFactory() {
    EquationTarget targetA = new EquationTarget();
    Schedule schedule = new Schedule();
    
    CEquationFactory factory = new CEquationFactory(schedule);
    factory.createEquation(targetA, "t * x + z * 5", "y", 1.0);
    schedule.execute();
    assertEquals(8, targetA.getY(), 0);
    
    schedule.execute();
    assertEquals(11, targetA.getY(), 0);
  }
  
  public void testComplexFactory() {
    Schedule schedule = new Schedule();
    CEquationFactory factory = new CEquationFactory(schedule);
    EquationTarget targetA = new EquationTarget();
    EquationTarget targetB = new EquationTarget();
    factory.createEquation(targetA, "t * x + z * 5", "y", 1.0);
    factory.createEquation(targetB, "t * 5 + x", "y", 1.5);
    
    //System.out.println("schedule.getCurrentTime() = " + schedule.getCurrentTime());
    schedule.execute();
    //System.out.println("schedule.getCurrentTime() = " + schedule.getCurrentTime());
    assertEquals(8, targetA.getY(), 0);
    // targetB should not be updated yet
    assertEquals(4, targetB.getY(), 0);
    
    schedule.execute();
    // targetB update, but not targetA
    assertEquals(8, targetA.getY(), 0);
    assertEquals(10.5, targetB.getY(), 0);
    
    schedule.execute();
    // targetA update, but not targetB
    assertEquals(11, targetA.getY(), 0);
    assertEquals(10.5, targetB.getY(), 0);
    
    schedule.execute();
    // both updated
    assertEquals(14, targetA.getY(), 0);
    assertEquals(18, targetB.getY(), 0);
  }
  
  public void testSameIntervalFactory() {
    Schedule schedule = new Schedule();
    CEquationFactory factory = new CEquationFactory(schedule);
    EquationTarget targetA = new EquationTarget();
    EquationTarget targetB = new EquationTarget();
    EquationTarget targetC = new EquationTarget();
    EquationTarget targetD = new EquationTarget();
    
    
    factory.createEquation(targetA, "t * x + z * 5", "y", 1.0);
    factory.createEquation(targetB, "t * 5 + x", "y", 1.5);
    factory.createEquation(targetC, "x * (z + t)", "y", 1.0);
    factory.createEquation(targetD, "x * (z + t)", "y", 1.0);
    
    schedule.execute();
    assertEquals(8, targetA.getY(), 0);
    assertEquals(6, targetC.getY(), 0);
    assertEquals(6, targetD.getY(), 0);
    // targetB should not be updated yet
    assertEquals(4, targetB.getY(), 0);
    
    schedule.execute();
    // targetB update, but not targetA
    assertEquals(8, targetA.getY(), 0);
    assertEquals(6, targetC.getY(), 0);
    assertEquals(6, targetD.getY(), 0);
    assertEquals(10.5, targetB.getY(), 0);
    
    schedule.execute();
    // targetA update, but not targetB
    assertEquals(11, targetA.getY(), 0);
    assertEquals(9, targetC.getY(), 0);
    assertEquals(9, targetD.getY(), 0);
    assertEquals(10.5, targetB.getY(), 0);
    
    schedule.execute();
    // both updated
    assertEquals(14, targetA.getY(), 0);
    assertEquals(12, targetC.getY(), 0);
    assertEquals(12, targetD.getY(), 0);
    assertEquals(18, targetB.getY(), 0);
    
  }

  public static Test suite() {
    return new TestSuite(CEquationTest.class);
  }
}
