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

package uk.ac.liv.util;

import java.io.Serializable;

/**
 * <p>
 * A utility class for cummulative tracking of stats for a series
 * of doubles.  Moments are incremented dynamically, rather than keeping
 * the actual cases in memory.
 * </p>
 * <p>
 * Example usage:
 * </p>
 * <p>
 * <code>
 * CummulativeStatCounter series1 = new CummulativeStatCounter("series1");<br>
 * series1.newData(4.5);<br>
 * series1.newData(5.6);<br>
 * series1.newData(9.0);<br>
 * System.out.println("Standard deviation of series1 = " + series1.getStdDev());<br>
 * series1.newData(5.56);<br>
 * series1.newData(12);<br>
 * System.out.println("And now the standard deviation = " + series1.getStdDev());<br>
 * </code>
 * </p>
 *
 * @author Steve Phelps
 */

public class CummulativeStatCounter implements Serializable, Cloneable, Resetable {

  /**
   * The number of data in the series so far.
   */
  int n;
  /**
   * The cummulative total of all numbers in the series so far.
   */
  double total;
  /**
   * The square of the total.
   */
  double totalSq;
  /**
   * The minimum so far.
   */
  double min;
  /**
   * The maximum so far.
   */
  double max;
  /**
   * The name of this series.
   */
  String varName;

  public CummulativeStatCounter( String varName ) {
    this.varName = varName;
    initialise();
  }

  public CummulativeStatCounter() {
    this("");
  }

  public void initialise() {
    n = 0;
    total = 0;
    min = Double.POSITIVE_INFINITY;
    max = Double.NEGATIVE_INFINITY;
    totalSq = 0;
  }

  /**
   * Add a new datum to the series.
   */
  public void newData( double i ) {
    n++;
    total += i;
    totalSq += i*i;
    if ( i > max ) {
      max = i;
    }
    if ( i < min ) {
      min = i;
    }
  }

  /**
   * Get the number of items in the series.
   */
  public int getN() {
    return n;
  }

  /**
   * Get the mean of the data.
   */
  public double getMean() {
    return total / n;
  }

  /**
   * Get the variance of the data about origin.
   */
  public double getVariance( double origin ) {
    return Math.abs(totalSq/n - origin*origin);
  }

  /**
   * Get the standard deviation of the data about origin.
   */
  public double getStdDev( double origin ) {
    return Math.sqrt(getVariance(origin));
  }

  /**
   * Get the variance about the mean.
   */
  public double getVariance() {
    return getVariance(getMean());
  }

  /**
   * Get the standard deviation from the mean.
   */
  public double getStdDev() {
    return getStdDev(getMean());
  }

  /**
   * Get the coefficient of variable about origin.
   */
  public double getVarCoef( double origin ) {
    return 100 * getStdDev(origin) / origin;
  }

  /**
   * Get the minimum datum.
   */
  public double getMin() {
    return min;
  }

  /**
   * Get the maximum datum.
   */
  public double getMax() {
    return max;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public void reset() {
    initialise();
  }

  public String toString() {
    return "(" + getClass() + " varName:\"" + varName + "\" n:" + n + " mean:" + getMean() + " stdev:" + getStdDev() + " min:" + min + " max:" + max + ")";
  }

}