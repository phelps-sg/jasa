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

import uk.ac.liv.auction.agent.MomentumStrategy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class MomentumStrategyTest extends TestCase {

  /**
   * @uml.property name="testTrader"
   * @uml.associationEnd
   */
  MockTrader testTrader;

  /**
   * @uml.property name="testStrategy"
   * @uml.associationEnd
   */
  MockMomentumStrategy testStrategy;

  /**
   * @uml.property name="pRIV_VALUE"
   */
  protected final double PRIV_VALUE = 100;

  public MomentumStrategyTest( String arg0 ) {
    super(arg0);
  }

  public void setUp() {
    testStrategy = new MockMomentumStrategy();
    testTrader = new MockTrader(this, 0, 0, PRIV_VALUE, true);
    testTrader.setStrategy(testStrategy);
    testStrategy.setAgent(testTrader);
  }

  public void testZeroTargetMargin() {
    double margin = testStrategy.targetMargin(PRIV_VALUE);
    assertTrue(margin == 0);
  }

  public void testBuyerMargin() {
    testTrader.setIsSeller(false);
    double margin = testStrategy.targetMargin(PRIV_VALUE + 10);
    assertTrue(margin == 10 / PRIV_VALUE);
  }

  public void testSellerMargin() {
    testTrader.setIsSeller(true);
    double margin = testStrategy.targetMargin(PRIV_VALUE - 10);
    assertTrue(margin == 10 / PRIV_VALUE);
  }

  public void testClipping() {

    double margin = -1;

    testTrader.setIsSeller(false);
    margin = testStrategy.targetMargin(PRIV_VALUE - 10);
    assertTrue(margin >= 0);

    testTrader.setIsSeller(true);
    margin = testStrategy.targetMargin(PRIV_VALUE + 10);
    assertTrue(margin >= 0);
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(MomentumStrategyTest.class);
  }
}

class MockMomentumStrategy extends MomentumStrategy {

  protected void adjustMargin() {
    // For a mock strategy do nothing
  }

  /**
   * A publically accessible tagetMargin() method.
   */
  public double targetMargin( double price ) {
    return super.targetMargin(price);
  }

}
