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

package test.uk.ac.liv.auction.agent;

import junit.framework.Test;
import junit.framework.TestSuite;

import uk.ac.liv.ai.learning.WidrowHoffLearner;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.PriestVanTolStrategy;
import uk.ac.liv.auction.stats.HistoricalDataReport;

/**
 * @author Steve Phelps
 * @version $Revision$
 */
public class PriestVanTolEfficiencyTest extends EfficiencyTest {

  public static final double BENCHMARK_EFFICIENCY = 85.0;
  
  public PriestVanTolEfficiencyTest( String name ) {
    super(name);
  }

  protected void initialiseAuction() {
    super.initialiseAuction();
    HistoricalDataReport report = new HistoricalDataReport();
    report.setAuction(auction);
    auction.setReport(report);    
  }
  
  protected void assignStrategy( AbstractTradingAgent agent ) {
    PriestVanTolStrategy strategy = new PriestVanTolStrategy();
    strategy.setLearner( new WidrowHoffLearner() );
    agent.setStrategy(strategy);
    strategy.setAgent(agent);
  }

  protected double getMinMeanEfficiency() {
    return BENCHMARK_EFFICIENCY;
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(PriestVanTolEfficiencyTest.class);
  }
}
