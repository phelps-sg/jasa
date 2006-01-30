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

package uk.ac.liv.auction.agent;

/**
 * <p>
 * Sellers configured with this valuation policy will receive a unique private
 * value from a common set of values starting at <code>minValue</code> and
 * incrementing by <code>step</code> as each agent is assigned a valuation.
 * </p>
 * 
 * @version $Revision$
 * @author Steve Phelps
 */

public class SellerIntervalValuer extends IntervalValuer {

  /**
   * The minimum valuation that any buyer will receive.
   */
  protected static double minValue;

  /**
   * The increment in valuation to use
   */
  protected static double step;

  protected static double nextValue;

  protected static boolean firstValue = true;
  
  
  public SellerIntervalValuer() {
  }
  
  public SellerIntervalValuer(double minValue, double step) {
  	super(minValue, step);
  }

  protected boolean firstValue() {
    return firstValue;
  }

  protected double getMinValue() {
    return minValue;
  }

  protected double getNextValue() {
    return nextValue;
  }

  protected double getStep() {
    return step;
  }

  protected void setFirstValue( boolean firstValue ) {
    this.firstValue = firstValue;
  }

  protected void setMinValue( double value ) {
    this.minValue = value;
  }

  protected void setNextValue( double value ) {
    this.nextValue = value;
  }

  protected void setStep( double step ) {
    this.step = step;
  }

}
