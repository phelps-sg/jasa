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

import uk.ac.liv.auction.core.MarketQuote;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A strategy based on the Priest-VanTol strategy described in the following
 * paper.
 * 
 * "Adaptive agents in a persistent shout double auction" by C. Priest and M.
 * Van Tol. in "Proceedings of the first international conference on Information
 * and computation economies" 1998.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class PriestVanTolStrategy extends MomentumStrategy
    implements Serializable  {

  static Logger logger = Logger.getLogger(PriestVanTolStrategy.class);

  protected void adjustMargin() {
    
    MarketQuote quote = auction.getQuote();
    
    double bidQuote = quote.getBid();
    double askQuote = quote.getAsk();
    
    if ( agent.isBuyer() ) {
      if ( askQuote < agent.getValuation(auction) ) {
        adjustMargin(targetMargin(askQuote - perterb(askQuote)));
      } else if ( agent.active() ) {
        adjustMargin(targetMargin(askQuote));
      }
    } else {
      if ( bidQuote > agent.getValuation(auction) ) {
        adjustMargin(targetMargin(bidQuote + perterb(bidQuote)));       
      } else if ( agent.active()  ) {
        adjustMargin(targetMargin(bidQuote));
      }
    }
   
  }
  
}