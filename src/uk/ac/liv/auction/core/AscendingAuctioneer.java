/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.auction.agent.TradingAgent;

import uk.ac.liv.util.Parameterizable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 *  Auctioneer for standard multi-unit english ascending auction.
 */

public class AscendingAuctioneer extends KAuctioneer implements Serializable {

  /**
   * The reservation price.
   */
  protected double reservePrice;

  /**
   * The seller.
   */
  protected TradingAgent seller;

  /**
   * The number of items for sale.
   */
  int quantity;

  public static final String P_RESERVEPRICE = "reserveprice";
  public static final String P_QUANTITY = "quantity";
  public static final String P_SELLER = "seller";

  static Logger logger = Logger.getLogger(AscendingAuctioneer.class);


  public AscendingAuctioneer( Auction auction, TradingAgent seller,
                                  int quantity, double reservePrice ) {
    super(auction, new UniformPricingPolicy(0));

    this.reservePrice = reservePrice;
    this.quantity = quantity;
    this.seller = seller;

    initialise();
  }

  public AscendingAuctioneer() {
    super();
  }

  public void initialise() {
    super.initialise();
    try {
     newAsk( new Shout(seller, quantity, 0, false) );
   } catch ( DuplicateShoutException e ) {
     throw new AuctionError("Fatal error: invalid auction state on initialisation!");
   }
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    quantity = parameters.getInt(base.push(P_QUANTITY), null, 1);

    reservePrice = parameters.getDouble(base.push(P_RESERVEPRICE), null, 0);

    seller = (TradingAgent)
        parameters.getInstanceForParameterEq(base.push(P_SELLER), null,
                                             TradingAgent.class);

    if ( seller instanceof Parameterizable ) {
      ((Parameterizable) seller).setup(parameters, base.push(P_SELLER));
    }

    initialise();
  }


  public void endOfRoundProcessing() {
    generateQuote();
  }


  public void endOfAuctionProcessing() {
    logger.debug("Clearing at end of auction..");
    shoutEngine.printState();
    clear();
    logger.debug("clearing done.");
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(null, shoutEngine.getLowestMatchedBid());
  }

  public void newShout( Shout shout) throws IllegalShoutException {
    if ( shout.isAsk() ) {
      throw new IllegalShoutException("asks are not allowed in an ascending auction");
    }
    // TODO: Additional logic to enforce bid amounts at round nos and/or
    // beat existing bids by certain amount?
    super.newShout(shout);
  }

  public boolean shoutsVisible() {
    return true;
  }

}