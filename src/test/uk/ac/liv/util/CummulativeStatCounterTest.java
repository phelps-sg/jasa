package test.uk.ac.liv.util;

import uk.ac.liv.util.CummulativeStatCounter;

import java.util.Random;

import junit.framework.*;

import ec.util.MersenneTwisterFast;

/**
 * @author Steve Phelps
 */

public class CummulativeStatCounterTest extends TestCase {

  CummulativeStatCounter testSeries;

  public CummulativeStatCounterTest( String name ) {
    super(name);
  }

  public void setUp() {
    testSeries = new CummulativeStatCounter("test series");
  }

  public void test1() {
    Random randGenerator = new Random();

    for( int i=0; i<1000000; i++ ) {
      testSeries.newData( randGenerator.nextDouble() );
    }
    System.out.println(testSeries);
    assertTrue( Math.abs(testSeries.getMean() - 0.5) < 0.01 );
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(CummulativeStatCounterTest.class);
  }

}