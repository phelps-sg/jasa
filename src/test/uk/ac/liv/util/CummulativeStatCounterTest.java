/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */

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