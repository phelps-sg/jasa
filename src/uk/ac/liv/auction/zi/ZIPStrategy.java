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

import uk.ac.liv.ai.learning.MimicryLearner;

import uk.ac.liv.util.Prototypeable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of the Zero-Intelligence-Plus (ZIP) strategy.
 * See:
 * </p>
 *
 * <p>
 * "Minimal Intelligence Agents for Bargaining Behaviours in
 * Market-based Environments" Dave Cliff 1997.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ZIPStrategy extends MomentumStrategy implements Prototypeable {

  static Logger logger = Logger.getLogger(ZIPStrategy.class);


  public ZIPStrategy( AbstractTradingAgent agent ) {
    super(agent);
  }

  public ZIPStrategy() {
    this(null);
  }


  public Object protoClone() {
    ZIPStrategy clone = new ZIPStrategy();
    clone.scaling = this.scaling;
    clone.learner = (MimicryLearner) ((Prototypeable) this.learner).protoClone();
    clone.reset();
    return clone;
  }

  
  protected void adjustMargin() {
    if ( agent.isSeller() ) {
      sellerStrategy();
    } else {
      buyerStrategy();
    }
  }
  
  
  protected void sellerStrategy() {

    if ( lastShout == null ) {
      return;
    }

    if ( lastShoutAccepted ) {
      if ( trPrice > agent.getValuation(auction) ) {
        adjustMargin(targetMargin(trAskPrice + perterb(trAskPrice)));
      } else if (  agent.active() ) {
        //adjustMargin(0);
        adjustMargin(targetMargin(trAskPrice - perterb(trAskPrice)));
      }
    } else {
      if (  agent.active()  ) {
        adjustMargin(targetMargin(lastShout.getPrice() - perterb(lastShout.getPrice())));
        //adjustMargin(0);
      }
    }
  }

  
  protected void buyerStrategy() {

    if ( lastShout == null ) {
      return;
    }

    if ( lastShoutAccepted ) {
      if ( trPrice < agent.getValuation(auction) ) {
        adjustMargin(targetMargin(trBidPrice - perterb(trBidPrice)));
      } else if ( agent.active() ) {
        adjustMargin(targetMargin(trBidPrice + perterb(trBidPrice)));
        //adjustMargin(0);
      }
    } else {
      if (  agent.active()  ) {
        adjustMargin(targetMargin(lastShout.getPrice() + perterb(lastShout.getPrice())));
        //adjustMargin(0);
      }
    }
    
    
  }


}