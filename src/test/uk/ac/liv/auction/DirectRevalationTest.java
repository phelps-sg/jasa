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

package test.uk.ac.liv.auction;

import test.uk.ac.liv.ai.learning.RothErevLearnerTest;

import junit.framework.*;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.ai.learning.NPTRothErevLearner;

import java.util.*;

import org.apache.log4j.BasicConfigurator;

/**
 *
 * Attempt an approximate replication of some of the experiments
 * described in
 *
 * "Market Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * <br>
 * Nicolaisen, Petrov, and Tesfatsion
 * <i>IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001</I>
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class DirectRevalationTest extends ElectricityTest {

  public static final double VALUE_MIN = 50;
  public static final double VALUE_MAX = 100;
    
  public DirectRevalationTest( String name ) {
    super(name);
    generatePRNGseeds();
  }


  public void testTruthTelling() {
    experimentSetup(3, 3, 10, 10);
    runExperiment();
    assertTrue(eA.getMean() >= 99.99);    
  }

  public void experimentSetup( int ns, int nb, int cs, int cb ) {
    super.experimentSetup(ns, nb, cs, cb);
    auction.setMaximumRounds(1);
  }
  
  public void assignStrategy( ElectricityTrader agent ) {
    PureSimpleStrategy truth = new PureSimpleStrategy();
    truth.setMargin(0.0);
    agent.setStrategy(truth);
    agent.reset();
  }
  
  public void assignValuer( ElectricityTrader agent ) {
    agent.setValuer( new RandomValuer(VALUE_MIN, VALUE_MAX) );
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(DirectRevalationTest.class);
  }

}