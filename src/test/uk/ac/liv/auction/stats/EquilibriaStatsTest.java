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

package test.uk.ac.liv.auction.stats;

import java.util.*;

import junit.framework.*;

import test.uk.ac.liv.auction.agent.MockTrader;

import uk.ac.liv.auction.agent.FixedValuer;
import uk.ac.liv.auction.agent.TruthTellingStrategy;
import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.EquilibriumReport;
import uk.ac.liv.util.MathUtil;

public class EquilibriaStatsTest extends TestCase {

  /**
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  RandomRobinAuction auction;

  /**
   * @uml.property name="traders"
   * @uml.associationEnd multiplicity="(0 -1)"
   */
  MockTrader[] traders;

  /**
   * @uml.property name="randGenerator"
   */
  Random randGenerator = new Random();

  static double[] NO_EP = { 100, 90, 80, 10, 20, 30 };

  static double[] SINGLE_CROSS = { 100, 90, 40, 10, 20, 50 };

  static double[] EXACT_OVERLAP = { 100, 90, 40, 10, 20, 40 };

  static double[] NPT = { 37, 17, 12, 11, 16, 37 };

  static final int N = 6;

  static final int NS = 3;

  static final double MAX_PV = 100;

  public EquilibriaStatsTest( String name ) {
    super(name);
  }

  public void setUp() {
    auction = new RandomRobinAuction();
    traders = new MockTrader[N];
    for ( int i = 0; i < N; i++ ) {
      traders[i] = new MockTrader(this, 0, 0, 0, i < NS);
      traders[i].setStrategy(new TruthTellingStrategy(traders[i]));
      auction.register(traders[i]);
    }
  }

  /**
   * Check that EP is zero when valuations are zero.
   * 
   */
  public void testZeroEP() {
    EquilibriumReport ep = new EquilibriumReport(auction);
    ep.calculate();
    assertTrue(ep.calculateMidEquilibriumPrice() == 0);
  }

  public void testSingleCross() {
    checkEP(SINGLE_CROSS, 45);
  }

  public void testExactOverlap() {
    checkEP(EXACT_OVERLAP, 40);
  }

  public void testNPT() {
    checkEP(NPT, 16.5);
  }

  /**
   * Check that no equilibria exists when supp/demand do not cross.
   */
  public void testNoEP() {
    setValuations(NO_EP);
    EquilibriumReport ep = new EquilibriumReport(auction);
    ep.calculate();
    assertTrue(!ep.equilibriaExists());
  }

  /**
   * Check that no equilibria exists when there are no traders.
   */
  public void testNoTraders() {
    auction = new RandomRobinAuction();
    EquilibriumReport ep = new EquilibriumReport(auction);
    ep.calculate();
    assertTrue(!ep.equilibriaExists());
  }

  protected void checkEP( double[] valuations, double correctEP ) {
    setValuations(valuations);
    EquilibriumReport ep = new EquilibriumReport(auction);
    ep.calculate();
    double mep = ep.calculateMidEquilibriumPrice();
    System.out.println("Mid EP = " + mep);
    assertTrue(MathUtil.approxEqual(mep, correctEP));
    assertTrue(ep.equilibriaExists());
  }

  protected void setValuations( double[] valuations ) {
    for ( int i = 0; i < N; i++ ) {
      traders[i].setValuationPolicy(new FixedValuer(valuations[i]));
    }
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(EquilibriaStatsTest.class);
  }

}
