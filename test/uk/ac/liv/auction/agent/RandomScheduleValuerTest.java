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

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RandomScheduleValuerTest extends TestCase {

  protected MockTrader agent;
  
  protected MockRandomScheduleValuer valuer;  
  
  public static final double MIN = 10;
  public static final double MAX = 100;
  
  public RandomScheduleValuerTest( String name ) {
    super(name); 
  }
  
  public void setUp() {
    agent = new MockTrader(this, 2, 0);  
    valuer = new MockRandomScheduleValuer(MIN, MAX);
    agent.setValuationPolicy(valuer);
  }
  
  public void testValueChanges() {
    RandomRobinAuction auction = new RandomRobinAuction();
    agent.shoutAccepted(auction, new Shout(agent, 1, 100, true), 100, 1);
    assertTrue(valuer.consumed);
  }
  
  

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(RandomScheduleValuerTest.class);
  }

}

class MockRandomScheduleValuer extends RandomScheduleValuer {
  
  public boolean consumed = false;
  
  public MockRandomScheduleValuer( double min, double max ) {
    super(min, max);
  }
  
  public void consumeUnit( Auction auction ) {
    super.consumeUnit(auction);
    consumed = true;
  }
}