/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

package test.uk.ac.liv.auction.stats;

import java.util.*;

import junit.framework.*;

import test.uk.ac.liv.auction.agent.MockTrader;

import uk.ac.liv.auction.core.*;


public class EquilibriaStatsTest extends TestCase {

  RandomRobinAuction auction;

  MockTrader[] traders;

  Random randGenerator = new Random();

  static final int N = 10;
  static final double MAX_PV = 100;

  public EquilibriaStatsTest( String name ) {
    super(name);
  }

  public void setUp() {
    auction = new RandomRobinAuction();
    traders = new MockTrader[N];
    for( int i=0; i<N; i++ ) {
      traders[i] = new MockTrader(this, 0, 0, 0, randGenerator.nextBoolean());
      auction.register(traders[i]);
    }
  }

  protected void randomizePrivateValues() {
    for( int i=0; i<N; i++ ) {
      traders[i].setPrivateValue(randGenerator.nextDouble() * MAX_PV);
    }
  }


  public static void main( String[] args ) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite() {
    return new TestSuite(EquilibriaStatsTest.class);
  }

}

