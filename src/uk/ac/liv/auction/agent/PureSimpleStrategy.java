/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

/**
 * @author Steve Phelps
 * @version 1.0
 */

public class PureSimpleStrategy implements Strategy {

  AbstractTraderAgent agent;

  double delta;

  int quantity;

  public PureSimpleStrategy( AbstractTraderAgent agent, double margin, int quantity ) {
    this.agent = agent;
    if ( agent.isSeller() ) {
      delta = margin;
    } else {
      delta = -margin;
    }
    this.quantity = quantity;
  }

  public void modifyShout( Shout shout, Auction auction ) {
    shout.setIsBid(agent.isBuyer());
    shout.setPrice(agent.getPrivateValue() + delta);
    shout.setQuantity(quantity);
    if ( shout.getPrice() < 0 ) {
      shout.setPrice(0);
    }
  }

}