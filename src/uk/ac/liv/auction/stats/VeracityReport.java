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
package uk.ac.liv.auction.stats;

import java.util.HashMap;
import java.util.Map;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import uk.ac.liv.auction.core.Shout;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;

import uk.ac.liv.util.CummulativeDistribution;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

public class VeracityReport extends AbstractAuctionReport {

  protected CummulativeDistribution veracity;
  
  public static final ReportVariable VAR_VERACITY_MEAN = 
      new ReportVariable("veracity.mean", "mean value of veracity");
  
  public static final ReportVariable VAR_VERACITY_STDEV =
      new ReportVariable("veracity.stdev", "stdev of veracity");
  
  public VeracityReport() {
    super();  
    veracity = new CummulativeDistribution("veracity");
  }

  public void produceUserOutput() {
    logger.info("");
    logger.info("Veracity report");
    logger.info("---------------------------");
    logger.info("");
    veracity.log();
    logger.info("");
  }

  public Map getVariables() {
    HashMap vars = new HashMap();
    vars.put(VAR_VERACITY_MEAN, new Double(veracity.getMean()));
    vars.put(VAR_VERACITY_STDEV, new Double(veracity.getStdDev()));
    return vars;
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof ShoutPlacedEvent ) {
      recordVeracity(((ShoutPlacedEvent) event).getShout());
    }
  }
  
  public void recordVeracity( Shout shout ) {
    double shoutPrice = shout.getPrice();
    AbstractTradingAgent agent = (AbstractTradingAgent) shout.getAgent();
    double value = agent.getValuation(auction);
    veracity.newData( Math.abs(value-shoutPrice) );
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
   
  }
  
  public void reset() {
    veracity.reset();
  }

}
