/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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
 * An unconstrained ZI trader agent (ZI-U).
 */

import uk.ac.liv.auction.core.Auction;

public class ZIUTraderAgent extends ZITraderAgent {

  public ZIUTraderAgent( long privateValue, int tradeEntitlement, boolean isSeller ) {
    super(privateValue, tradeEntitlement, isSeller);
  }

  public double determinePrice( Auction auction ) {
    return randomPrice( (int) (MAX_PRICE-1))+1;
  }



}