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

package uk.ac.liv.auction.core;

import uk.ac.liv.auction.core.DiscriminatoryPricingPolicy;
import uk.ac.liv.auction.core.UniformPricingPolicy;
import uk.ac.liv.auction.core.ClearingHouseAuctioneer;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.stats.EquilibriumReport;

import uk.ac.liv.auction.agent.MockTrader;
import uk.ac.liv.auction.agent.TruthTellingStrategy;
import uk.ac.liv.util.MathUtil;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class KPricingPolicyTest extends TestCase {

  /**
   * @uml.property name="auctioneer"
   * @uml.associationEnd
   */
  ClearingHouseAuctioneer auctioneer;

  /**
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  RoundRobinAuction auction;

  /**
   * @uml.property name="agents"
   * @uml.associationEnd multiplicity="(0 -1)"
   */
  MockTrader[] agents;

  public KPricingPolicyTest( String name ) {
    super(name);
  }

  public void setUp() {
    auction = new RoundRobinAuction();
    auctioneer = new ClearingHouseAuctioneer(auction);
    auction.setAuctioneer(auctioneer);

    agents = new MockTrader[4];

    agents[0] = new MockTrader(this, 0, 0, 200, false);
    agents[1] = new MockTrader(this, 0, 0, 150, false);

    agents[2] = new MockTrader(this, 0, 0, 100, true);
    agents[3] = new MockTrader(this, 0, 0, 50, true);

    for ( int i = 0; i < agents.length; i++ ) {
      agents[i].setStrategy(new TruthTellingStrategy(agents[i]));
      auction.register(agents[i]);
    }

  }

  /**
   * Test that truthful agents transact at mid equilibrium price in a k=0.5 CH
   * with uniform clearing.
   */
  public void testUniformPolicyEquilibriumPrice() {

    EquilibriumReport eqStats = new EquilibriumReport(auction);
    auctioneer.setPricingPolicy(new UniformPricingPolicy(0.5));
    auction.setMaximumRounds(1);
    auction.addReport(eqStats);
    auction.run();

    eqStats.calculate();
    double ep = eqStats.calculateMidEquilibriumPrice();

    for ( int i = 0; i < agents.length; i++ ) {
      assertTrue(MathUtil.approxEqual(ep, agents[i].lastWinningPrice));
    }
  }

  public void testPayAsBid() {

    auctioneer.setPricingPolicy(new DiscriminatoryPricingPolicy(1));
    auction.setMaximumRounds(1);
    auction.run();

    for ( int i = 0; i < agents.length; i++ ) {
      if ( agents[i].isBuyer(auction) ) {
        assertTrue(MathUtil.approxEqual(agents[i].lastWinningPrice, agents[i]
            .getValuation(auction)));
      }
    }
  }

  public void testPayAsAsk() {

    auctioneer.setPricingPolicy(new DiscriminatoryPricingPolicy(0));
    auction.setMaximumRounds(1);
    auction.run();

    for ( int i = 0; i < agents.length; i++ ) {
      if ( agents[i].isSeller(auction) ) {
        assertTrue(MathUtil.approxEqual(agents[i].lastWinningPrice, agents[i]
            .getValuation(auction)));
      }
    }
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(KPricingPolicyTest.class);
  }
}
