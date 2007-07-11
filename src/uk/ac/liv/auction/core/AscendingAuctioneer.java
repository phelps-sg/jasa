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

import uk.ac.liv.auction.agent.TradingAgent;

import uk.ac.liv.util.Parameterizable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Auctioneer for standard multi-unit english ascending auction.
 */

public class AscendingAuctioneer extends TransparentAuctioneer implements
    Serializable {

  /**
   * The reservation price.
   * 
   * @uml.property name="reservePrice"
   */
  protected double reservePrice;

  /**
   * The seller.
   * 
   * @uml.property name="seller"
   * @uml.associationEnd
   */
  protected TradingAgent seller;
  
  protected Account account;

  /**
   * The number of items for sale.
   * 
   * @uml.property name="quantity"
   */
  int quantity;

  public static final String P_RESERVEPRICE = "reserveprice";

  public static final String P_QUANTITY = "quantity";

  public static final String P_SELLER = "seller";

  static Logger logger = Logger.getLogger(AscendingAuctioneer.class);

  public AscendingAuctioneer( Auction auction, TradingAgent seller,
      int quantity, double reservePrice ) {
    super(auction);

    this.reservePrice = reservePrice;
    this.quantity = quantity;
    this.seller = seller;

    setPricingPolicy(new UniformPricingPolicy(0));
    account = new Account(this, 0);
    
    initialise();
  }

  public AscendingAuctioneer() {
    super();
  }

  public void initialise() {
    super.initialise();
    try {
      newShout(new Shout(seller, quantity, 0, false));
    } catch ( DuplicateShoutException e ) {
      throw new AuctionRuntimeException(
          "Fatal error: invalid auction state on initialisation!");
    } catch (IllegalShoutException e) {
      throw new AuctionRuntimeException(
      		"Fatal error: invalid auction state on initialisation!");
		}
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    quantity = parameters.getInt(base.push(P_QUANTITY), null, 1);

    reservePrice = parameters.getDouble(base.push(P_RESERVEPRICE), null, 0);

    seller = (TradingAgent) parameters.getInstanceForParameterEq(base
        .push(P_SELLER), null, TradingAgent.class);

    if ( seller instanceof Parameterizable ) {
      ((Parameterizable) seller).setup(parameters, base.push(P_SELLER));
    }

    initialise();
  }

  public void endOfRoundProcessing() {
    super.endOfRoundProcessing();
    generateQuote();
  }

  public void endOfAuctionProcessing() {
    super.endOfAuctionProcessing();
    logger.debug("Clearing at end of auction..");
    shoutEngine.printState();
    clear();
    logger.debug("clearing done.");
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(null, shoutEngine.getLowestMatchedBid());
  }

  protected void checkShoutValidity( Shout shout ) throws IllegalShoutException {
  	super.checkShoutValidity(shout);
    if ( shout.isAsk() ) {
      throw new IllegalShoutException(
          "asks are not allowed in an ascending auction");
    }
    // TODO: Additional logic to enforce bid amounts at round nos and/or
    // beat existing bids by certain amount?  	
  }

  public boolean shoutsVisible() {
    return true;
  }
  
  public Account getAccount() {
    return account;
  }

}