/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

package uk.ac.liv.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import uk.ac.liv.util.HeavyweightDistribution;

import uk.ac.liv.util.MathUtil;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class HeavyweightDistributionTest extends TestCase {

  /**
   * @uml.property name="distribution"
   * @uml.associationEnd
   */
  HeavyweightDistribution distribution;

  /**
   * @uml.property name="testData1" multiplicity="(0 -1)" dimension="1"
   */
  double[] testData1 = { 2, 5, 5, 5, 6, 7, 100 };

  /**
   * @uml.property name="mean1"
   */
  double mean1 = (5.0 + 5.0 + 5.0 + 6.0 + 7.0) / 5.0;

  public HeavyweightDistributionTest( String name ) {
    super(name);
  }

  public void setUp() {
    distribution = new HeavyweightDistribution("test");
  }

  public void test1() {
    System.out.println("test1()");
    loadData(testData1);
    checkTrimmedMean(mean1, 2.0 / 7.0);
  }

  public void testZeroTrim() {
    System.out.println("testZeroTrim()");
    loadData(testData1);
    checkTrimmedMean(distribution.getMean(), 0);
  }

  public void loadData( double[] testData ) {
    for ( int i = 0; i < testData.length; i++ ) {
      distribution.newData(testData[i]);
    }
  }

  public void checkTrimmedMean( double correctMean, double trim ) {
    double trimmedMean = distribution.getTrimmedMean(trim);
    System.out.println("Trimmed mean = " + trimmedMean + " expecting "
        + correctMean);
    assertTrue(MathUtil.approxEqual(distribution.getTrimmedMean(trim),
        correctMean));
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(HeavyweightDistributionTest.class);
  }
}
