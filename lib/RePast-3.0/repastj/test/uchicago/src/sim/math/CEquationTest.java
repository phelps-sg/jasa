package uchicago.src.sim.math;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.TickCounter;

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
