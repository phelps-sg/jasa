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

import uk.ac.liv.util.Prototypeable;
import uk.ac.liv.util.CummulativeDistribution;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.*;

import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of Todd Kaplan's sniping strategy.  Agents using
 * this strategy wait until the last minute before attempting to
 * "steal the bid".  See
 * </p>
 * <p>
 * "Behaviour of trading automata in a computerized double auction market"
 * J. Rust, J. Miller and R. Palmer in "The Double Auction Market:
 * Institutions, Theories and Evidence" 1992, Addison-Wesley
 * </p>
 *
 * <p>
 * Note that you must configure a logger of type DailyStatsMarketDataLogger
 * in order to use this strategy.
 * </p>
 *
 * </p><p><b>Parameters</b><br>
 * <table>
 * <tr><td valign=top><i>base</i><tt>.s</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(the spread factor)</td></tr>
 *
 * <tr><td valign=top><i>base</i><tt>.t</tt><br>
 * <font size=-1>double &gt;= 0</font></td>
 * <td valign=top>(the time factor)</td><tr>
 *
 * </table>
 *
 * @see uk.ac.liv.auction.stats.DailyStatsMarketDataLogger
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class KaplanStrategy extends FixedQuantityStrategyImpl
    implements Serializable, Prototypeable {

  /**
   * The time factor.  Kaplan will bid if the remaining time in
   * the current period is less than t.
   */
  protected double t = 4;

  /**
   * The spread factor.
   */
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

  public Object protoClone() {
  	Object clone;
  	try {
  		clone = clone();
  	} catch ( CloneNotSupportedException e ) {
  		throw new Error(e);
  	}
    return clone;
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    super.modifyShout(shout);
    quote = auction.getQuote();
    if ( timeRunningOut() || juicyOffer() || smallSpread() ) {
      logger.debug("quote = " + quote);
      logger.debug("my priv value = " + agent.getPrivateValue(auction));
      logger.debug("isSeller = " + agent.isSeller());
      shout.setPrice(agent.getPrivateValue(auction));
      if ( agent.isBuyer() ) {
        if ( quote.getAsk() <= agent.getPrivateValue(auction) ) {
          shout.setPrice(quote.getAsk());
        }
      } else {
        if ( quote.getBid() >= agent.getPrivateValue(auction) ) {
          shout.setPrice(quote.getBid());
        }
      }
      logger.debug(this + ": price = " + shout.getPrice());
      return true;
    } else {
      return false;
    }
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public boolean juicyOffer() {

    boolean juicyOffer = false;

    CummulativeDistribution transPrice = null;

    try {
      transPrice = auction.getPreviousDayTransPriceStats();
    } catch ( DataUnavailableException e ) {
      error(e);
    }

    if ( transPrice == null ) {
      return false;
    }

    if ( agent.isBuyer() ) {
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


  public boolean smallSpread() {

    boolean smallSpread = false;

    CummulativeDistribution transPrice = null;

    try {
      transPrice = auction.getPreviousDayTransPriceStats();
    } catch ( DataUnavailableException e ) {
      error(e);
    }

    if (agent.isBuyer()) {
      smallSpread =
//          quote.getAsk() < transPrice.getMax() &&
          ( (quote.getBid() - quote.getAsk()) / quote.getAsk()) < s;
    } else {
      smallSpread =
//          quote.getBid() > transPrice.getMin() &&
          ( (quote.getBid() - quote.getAsk()) / quote.getBid()) < s;
    }


    if ( smallSpread ) {
      logger.debug(this + ": small spread detected");
    }

    return smallSpread;
  }


  public boolean timeRunningOut() {
    boolean timeOut = auction.getRemainingTime() < t;
    if ( timeOut ) {
      logger.debug(this + ": time running out");
    }
    return timeOut;
  }


  public String toString() {
    return "(" + getClass() + " s:" + s + " t:" + t + ")";
  }


  protected void error( DataUnavailableException e ) {
    logger.error("Auction is not configured with loggers appropriate for this strategy");
    logger.error(e.getMessage());
    throw new AuctionError(e);
  }

}