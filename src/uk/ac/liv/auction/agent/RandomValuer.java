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

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.event.AuctionEvent;

import uk.ac.liv.prng.GlobalPRNG;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A valuation policy in which we randomly determine our valuation across all
 * auctions and all units at agent-initialisation time. Valuations are drawn
 * from a uniform distribution with the specified range.
 * 
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.minvalue</tt><br>
 * <font size=-1>double &gt;= 0 </font></td>
 * <td valign=top>(the minimum valuation)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maxvalue</tt><br>
 * <font size=-1>double &gt;=0 </font></td>
 * <td valign=top>(the maximum valuation)</td>
 * <tr></table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomValuer 
    implements ValuationPolicy, Serializable {

  /**
   * The current valuation.
   */
  protected double value;

  /**
   * The minimum valuation to use.
   */
  protected double minValue;

  /**
   * The maximum valuation to use.
   */
  protected double maxValue;
  
  /**
   * The probability distribution to use for drawing valuations.
   */
  protected AbstractContinousDistribution distribution;

  public static final String P_MINVALUE = "minvalue";
  public static final String P_MAXVALUE = "maxvalue";

  static Logger logger = Logger.getLogger(RandomValuer.class);

  public RandomValuer() {
  }

  public RandomValuer( double minValue, double maxValue ) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    initialise();
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {
    minValue = parameters.getDouble(base.push(P_MINVALUE), null, 0);
    maxValue = parameters.getDouble(base.push(P_MAXVALUE), null, minValue);
    initialise();
  }

  public double determineValue( Auction auction ) {   
    assert minValue >= 0 && maxValue >= minValue;
    return value;
  }

  public void consumeUnit( Auction auction ) {
    // Do nothing
  }
  
  public void eventOccurred( AuctionEvent event ) {
    // Do nothing
  }

  public void reset() {
    initialise();
  }

  public void initialise() {    
    distribution = new Uniform(minValue, maxValue, GlobalPRNG.getInstance());
    drawRandomValue();
  }

  public void setMaxValue( double maxValue ) {
    this.maxValue = maxValue;
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setMinValue( double minValue ) {
    this.minValue = minValue;
  }

  public double getMinValue() {
    return minValue;
  }
  
  public double getCurrentValuation() {
    return value;
  }

  public void drawRandomValue() {    
    value = distribution.nextDouble();
  }

  public String toString() {
    return "(" + getClass() + " minValue:" + minValue + " maxValue:" +
             maxValue + " value:" + value + ")";
  }

}
