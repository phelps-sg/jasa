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


package uk.ac.liv.auction.agent;

import java.io.Serializable;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.CummulativeStatCounter;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.*;

import org.apache.log4j.Logger;

/**
 * An implementation of Kaplan's sniping strategy.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class KaplanStrategy extends FixedQuantityStrategyImpl
    implements Serializable, Parameterizable {

  protected double t = 4;

  protected double s = 0.5;

  protected MarketQuote quote;

  public static final String P_T = "t";
  public static final String P_S = "s";

  static Logger logger = Logger.getLogger(KaplanStrategy.class);

  public KaplanStrategy() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    t = parameters.getDoubleWithDefault(base.push(P_T), null, t);
    s = parameters.getDoubleWithDefault(base.push(P_S), null, s);
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    super.modifyShout(shout);
    quote = auction.getAuctioneer().getQuote();
    if ( timeRunningOut() || juicyOffer() || smallSpread() ) {
      logger.debug("quote = " + quote);
      logger.debug("my priv value = " + agent.getPrivateValue(auction));
      logger.debug("seller = " + agent.isSeller());
      if ( agent.isBuyer() ) {
        if ( quote.getAsk() > agent.getPrivateValue(auction) ) {
          shout.setPrice(quote.getAsk());
          logger.debug(this + " bidding at " + quote.getAsk());
          return true;
        }
      } else {
        if ( quote.getBid() < agent.getPrivateValue(auction) ) {
          shout.setPrice(quote.getBid());
          logger.debug(this + " asking at " + quote.getAsk());
          return true;
        }
      }
    }
    return false;
  }

  public void endOfRound( Auction auction ) {
  }

  public String toString() {
    return "(" + getClass() + " s:" + s + " t:" + t + ")";
  }


  protected boolean juicyOffer() {

    boolean juicyOffer = false;

    CummulativeStatCounter transPrice = null;

    try {
      transPrice = auction.getPreviousDayTransPriceStats();
    } catch ( DataUnavailableException e ) {
      error(e);
    }

    if ( transPrice == null ) {
      return false;
    }

    if (agent.isBuyer()) {
      juicyOffer =
          quote.getAsk() < transPrice.getMin();
    } else {
      juicyOffer =
          quote.getBid() > transPrice.getMax();
    }

    if ( juicyOffer ) {
      logger.debug(this + ": juicy offer detected");
    }

    return juicyOffer;
  }


  protected boolean smallSpread() {

    boolean smallSpread = false;

    CummulativeStatCounter transPrice = null;

    try {

      transPrice = auction.getPreviousDayTransPriceStats();

      if (transPrice == null) {
        return false;
      }

      if (agent.isBuyer()) {
        smallSpread =
            quote.getAsk() < transPrice.getMax() &&
            ( (quote.getBid() - quote.getAsk()) / quote.getAsk()) < s;
      } else {
        smallSpread =
            quote.getBid() > transPrice.getMin() &&
            ( (quote.getBid() - quote.getAsk()) / quote.getBid()) < s;
      }

    } catch ( DataUnavailableException e ) {
      error(e);
    }

    if ( smallSpread ) {
      logger.debug(this + ": small spread detected");
    }

    return smallSpread;
  }


  protected boolean timeRunningOut() {
    boolean timeOut = auction.getRemainingTime() < t;
    if ( timeOut ) {
      logger.debug(this + ": time running out");
    }
    return timeOut;
  }

  protected void error( DataUnavailableException e ) {
    logger.error("Auction is not configured with loggers appropriate for this strategy");
    logger.error(e.getMessage());
    throw new AuctionError(e);
  }

}