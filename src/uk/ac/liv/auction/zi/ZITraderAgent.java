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

package uk.ac.liv.auction.zi;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * <p>
 * Class for "Zero Intelligence" (ZI) trader agents.
 * Agents of this type become inactive once their intitial trade
 * entitlement is used up, and their trade entitlement is restored
 * at the end of each day.
 * </p>
 * See:
 * </p>
 * <p>
 * "Minimal Intelligence Agents for Bargaining Behaviours in
 * Market-based Environments" Dave Cliff 1997.
 * </p>
 * <p>
 * and "An experimental study of competitive market behaviour",
 * Smith, V.L. 1962 in The Journal of Political Economy, vol 70.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ZITraderAgent extends AbstractTraderAgent implements Serializable {

  /**
   * The number of units this agent is entitlted to trade in this trading period.
   */
  protected int tradeEntitlement;

  /**
   * The initial value of tradeEntitlement
   */
  protected int initialTradeEntitlement;

  /**
   * Flag indicating whether the last shout resulted in a transaction.
   */
  protected boolean lastShoutSuccessful;

  /**
   * The number of units traded to date
   */
  protected int quantityTraded = 0;

  protected Shout dummyShout;

  static final String P_INITIAL_TRADE_ENTITLEMENT = "initialtradeentitlement";

  static final double DEFAULT_MAX_MARKUP = 100;

  static Logger logger = Logger.getLogger(ZITraderAgent.class);

  public ZITraderAgent() {
    super();
  }

  public ZITraderAgent( int stock, double funds, double privateValue,
                          int tradeEntitlement, boolean isSeller ) {
    super(stock, funds, privateValue, isSeller, null);
    setStrategy(new RandomConstrainedStrategy(this, DEFAULT_MAX_MARKUP));
    this.initialTradeEntitlement = tradeEntitlement;
    initialise();
  }

  public ZITraderAgent( double privateValue, int tradeEntitlement, boolean isSeller ) {
    this(0, 0, privateValue, tradeEntitlement, isSeller);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    initialTradeEntitlement =
        parameters.getInt(base.push(P_INITIAL_TRADE_ENTITLEMENT));
    super.setup(parameters, base);
  }

  protected void initialise() {
    super.initialise();
    lastShoutSuccessful = false;
    tradeEntitlement = initialTradeEntitlement;
    quantityTraded = 0;
    dummyShout = new Shout(this);
    logger.debug(this + ": initialised.");
  }

  public void endOfDay( Auction auction ) {
    logger.debug("Performing end-of-day processing..");
    tradeEntitlement = initialTradeEntitlement;
    quantityTraded = 0;
    lastShoutSuccessful = false;
    logger.debug("done.");
  }

  public boolean active() {
    return tradeEntitlement > 0;
  }

  /**
   * Default behaviour for winning ZI bidders is to purchase unconditionally.
   */
  public void informOfSeller( Auction auction, Shout winningShout,
                                  RoundRobinTrader seller,
                                  double price, int quantity) {
    super.informOfSeller(auction, winningShout, seller, price, quantity);
    AbstractTraderAgent agent = (AbstractTraderAgent) seller;
    purchaseFrom(auction, agent, quantity, price);
  }

  public void purchaseFrom( Auction auction, AbstractTraderAgent seller,
                             int quantity, double price ) {
    tradeEntitlement--;
    quantityTraded += quantity;
    super.purchaseFrom(auction, seller, quantity, price);
  }

  public int deliver( Auction auction, int quantity, double price ) {
    lastShoutSuccessful = true;
    tradeEntitlement--;
    quantityTraded += quantity;
    return super.deliver(auction, quantity, price);
  }

  public void sellUnits( Auction auction, int numUnits ) {
    stock -= numUnits;
    funds += numUnits * valuer.determineValue(auction);
  }

  public int getQuantityTraded() {
    return quantityTraded;
  }

  public int determineQuantity( Auction auction ) {
    return strategy.determineQuantity(auction);
  }

  public String toString() {
    return "(" + getClass() + " id:" + id + " isSeller:" + isSeller + " valuer:" + valuer + " strategy:" + strategy + " tradeEntitlement:" + tradeEntitlement + " quantityTraded:" + quantityTraded + ")";
  }


}