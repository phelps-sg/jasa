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

import org.apache.log4j.Logger;

/**
 *   
 * @author Steve Phelps
 * @version $Revision$
 * 
 */

public class ShoutStepAuction extends RoundRobinAuction implements
    Serializable {

  static Logger logger = Logger.getLogger(ShoutStepAuction.class);
  
  /**
   * Construct a new auction in the stopped state, with no traders, no shouts,
   * and no auctioneer.
   * 
   * @param name
   *          The name of this auction.
   */
  public ShoutStepAuction( String name ) {
    super(name);
    initialise();
  }

  public ShoutStepAuction() {
    this(null);
  }
  
  public void step() throws AuctionClosedException {
    if ( endOfRound ) {
      beginRound();
    }
    requestNextShout();
    if ( endOfRound ) {
      endRound();
    }
  }

}
