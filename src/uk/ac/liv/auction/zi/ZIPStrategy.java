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

import uk.ac.liv.auction.core.*;
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

  public static final String ERROR_SHOUTVISIBILITY = 
    "ZIPStrategy can only be used with auctioneers who permit shout visibility";

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
    try {
      Shout lastShout = auction.getLastShout();      
      if ( agent.isSeller() ) {
        sellerStrategy(lastShout);
      } else {
        buyerStrategy(lastShout);
      }      
    } catch ( ShoutsNotVisibleException e ) {
      throw new AuctionError(ERROR_SHOUTVISIBILITY);
    }
  }
  
  
  protected void sellerStrategy( Shout lastShout ) 
      throws ShoutsNotVisibleException {   
        
    if ( lastShout == null ) {
      return;
    }
    
    double lastPrice = lastShout.getPrice();
    adjustMargin(targetMargin(lastPrice - perterb(lastPrice)));
  }

  
  protected void buyerStrategy( Shout lastShout ) 
      throws ShoutsNotVisibleException {    
        
    if ( lastShout == null ) {
      return;
    }
    
    double lastPrice = lastShout.getPrice();
    adjustMargin(targetMargin(lastPrice + perterb(lastPrice)));
    
    
    
  }


}