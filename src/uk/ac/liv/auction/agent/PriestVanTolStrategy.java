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

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Seeder;
import uk.ac.liv.util.Seedable;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Prototypeable;

import uk.ac.liv.prng.PRNGFactory;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class PriestVanTolStrategy extends MomentumStrategy
    implements Serializable  {

  static Logger logger = Logger.getLogger(PriestVanTolStrategy.class);

  protected void adjustMargin() {
    try {
      double highestBid = auction.getHighestBidPrice();
      double lowestAsk = auction.getLowestAskPrice();
      if ( agent.isBuyer() ) {
        if ( lowestAsk > highestBid && highestBid > 0 ) {
          adjustMargin(targetMargin(highestBid+perterb(highestBid)));
        } else if ( agent.active() && lowestAsk < Double.POSITIVE_INFINITY ) {
          adjustMargin(targetMargin(lowestAsk-perterb(lowestAsk)));
        }
      } else {
        if ( lowestAsk > highestBid && lowestAsk < Double.POSITIVE_INFINITY ) {
          adjustMargin(targetMargin(lowestAsk-perterb(lowestAsk)));
        } else if ( agent.active() && highestBid > 0 ) {
          adjustMargin(targetMargin(highestBid+perterb(highestBid)));
        }
      }
    } catch ( DataUnavailableException e ) {
      throw new AuctionError(e);
    }
  }
  
}