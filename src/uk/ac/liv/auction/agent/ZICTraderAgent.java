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

import uk.ac.liv.auction.core.*;

/**
 * A constrained ZI trader agent (ZI-C).
 * Agents of this type always trade at or above the margin.
 *
 * @author Steve Phelps
 */

public class ZICTraderAgent extends ZITraderAgent {

  public ZICTraderAgent( int stock, double funds, double privateValue, int tradeEntitlement, boolean isSeller ) {
    super(stock, funds, privateValue, tradeEntitlement, isSeller);
  }

  public ZICTraderAgent( double privateValue, int tradeEntitlement, boolean isSeller ) {
    super(privateValue, tradeEntitlement, isSeller);
  }

  public double determinePrice( Auction auction ) {
    if ( isSeller ) {
      return privateValue + randomPrice( (int) (MAX_PRICE-privateValue));
    } else {
      return randomPrice( (int) (privateValue-1))+1;
    }
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                                long price, int quantity ) {
    if ( price <= privateValue ) {
      //purchaseFrom(seller, winningShout.getQuantity(), price);
      AbstractTraderAgent sellerAgent = (AbstractTraderAgent) seller;
      purchaseFrom(sellerAgent, quantity, price);
      // System.out.println("Accepting offer from " + seller + " at price " + price + " my priv value = " + limitPrice);
    } else {
      //System.out.println("Rejecting offer from " + seller + " at price " + price);
    }
  }


}