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

/**
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class InOrderPricingPolicy extends DiscriminatoryPricingPolicy {

  public InOrderPricingPolicy() {
    this(0);
  }

	public InOrderPricingPolicy(double k) {
		super(k);
	}
	
  public double determineClearingPrice( Shout bid, Shout ask,
                                          MarketQuote clearingQuote ) {

    if ( bid.getId() > ask.getId() ) {
      // ask comes first
      return kInterval(ask.getPrice(), bid.getPrice());
    } else {
      // bid comes first
      return kInterval(bid.getPrice(), ask.getPrice());
    }
  } 
  
}
