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

package uk.ac.liv.auction.core;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import uk.ac.liv.util.Parameterizable;

/**
 * The interface for expressing the condition of closing an auction.
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 * 
 */

public class MaxRoundsDayEndingCondition extends TimingCondition implements
    Parameterizable, DayEndingCondition {

  public static final String P_LENGTH_OF_DAY = "lengthofday";

  /**
   * The maximum length in rounds of a trading day
   * 
   * @uml.property name="lengthOfDay"
   */
  protected int lengthOfDay = -1;

  public MaxRoundsDayEndingCondition() {
    this(null);
  }

  public MaxRoundsDayEndingCondition( Auction auction ) {
    super(auction);
  }

  /*
   * @see uk.ac.liv.util.Parameterizable#setup(ec.util.ParameterDatabase,
   *      ec.util.Parameter)
   */
  public void setup( ParameterDatabase parameters, Parameter base ) {

    lengthOfDay = parameters.getIntWithDefault(base.push(P_LENGTH_OF_DAY),
        null, -1);
  }

  /**
   * @uml.property name="lengthOfDay"
   */
  public int getLengthOfDay() {
    return lengthOfDay;
  }

  /**
   * @uml.property name="lengthOfDay"
   */
  public void setLengthOfDay( int lengthOfDay ) {
    this.lengthOfDay = lengthOfDay;
  }

  /*
   * @see uk.ac.liv.auction.core.TimingCondition#eval()
   */
  public boolean eval() {
    return getRemainingRounds() <= 0;
  }

  public int getRemainingRounds() {
    if ( lengthOfDay > getAuction().getRound() )
      return lengthOfDay - getAuction().getRound();
    else
      return 0;
  }
}