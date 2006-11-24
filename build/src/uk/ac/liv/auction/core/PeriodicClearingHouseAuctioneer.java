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

import java.io.Serializable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;

/**
 * An auctioneer for a periodic k-double-auction. The clearing operation is
 * performed periodically as well as at the end of every round. The length of
 * each period depends upon how many shouts have been made since last clearing.
 * 
 *  @deprecated
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class PeriodicClearingHouseAuctioneer extends ClearingHouseAuctioneer
    implements Serializable, Parameterizable {

  public static final String P_SHOUTNUMEACHPERIOD = "shoutnumeachperiod";

  public static final int DEF_SHOUTNUMEACHPERIOD = 6;

  public static final String P_DEF_BASE = "pch";

  /**
   * @uml.property name="shoutNumEachPeriod"
   */
  private int shoutNumEachPeriod;

  /**
   * @uml.property name="shoutNum"
   */
  private int shoutNum;

  public PeriodicClearingHouseAuctioneer() {
    this(null);
  }

  public PeriodicClearingHouseAuctioneer( Auction auction ) {
    super(auction);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);

    shoutNumEachPeriod = parameters.getInt(base.push(P_SHOUTNUMEACHPERIOD),
        new Parameter(P_DEF_BASE).push(P_SHOUTNUMEACHPERIOD), DEF_SHOUTNUMEACHPERIOD);

    if ( shoutNumEachPeriod <= 0 )
      shoutNumEachPeriod = DEF_SHOUTNUMEACHPERIOD;

  }

  public void clear() {
    super.clear();
    shoutNum = 0;
  }

  protected void newShoutInternal( Shout shout ) throws DuplicateShoutException {
    super.newShoutInternal(shout);
    shoutNum++;
    if ( shoutNum >= shoutNumEachPeriod ) {
      generateQuote();
      clear();
    }
  }
}