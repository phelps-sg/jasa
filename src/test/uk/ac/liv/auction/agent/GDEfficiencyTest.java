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

package test.uk.ac.liv.auction.agent;

import junit.framework.Test;
import junit.framework.TestSuite;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.GDStrategy;
import uk.ac.liv.auction.stats.HistoricalDataReport;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class GDEfficiencyTest extends EfficiencyTest {

  public GDEfficiencyTest( String name ) {
    super(name);
  }
  
  protected void initialiseAuction() {
    super.initialiseAuction();
    HistoricalDataReport report = new HistoricalDataReport();
    report.setAuction(auction);
    auction.setReport(report);    
  }
  
  protected void assignStrategy( AbstractTradingAgent agent ) {
    GDStrategy strategy = new GDStrategy();
    agent.setStrategy(strategy);
    strategy.setAgent(agent);
  }

  protected double getMinMeanEfficiency() {
    return 90.0;
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(GDEfficiencyTest.class);
  }
}
