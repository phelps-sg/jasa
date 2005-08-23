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

package uk.ac.liv.auction.electricity;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.util.*;

import uk.ac.liv.prng.PRNGFactory;

import java.util.*;
import java.io.Serializable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

// import edu.cornell.lassp.houle.RngPack.RandomElement;

import org.apache.log4j.Logger;

import cern.jet.random.engine.RandomEngine;
import cern.jet.random.engine.RandomSeedGenerator;

public class StandardRandomizer implements Parameterizable, Serializable {

  /**
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  protected RoundRobinAuction auction;

  /**
   * @uml.property name="minPrivateValue"
   */
  protected double minPrivateValue = 30;

  /**
   * @uml.property name="maxPrivateValue"
   */
  protected double maxPrivateValue = 1000;

  /**
   * @uml.property name="experiment"
   * @uml.associationEnd inverse="randomizer:uk.ac.liv.auction.electricity.ElectricityExperiment"
   */
  protected ElectricityExperiment experiment;

  /**
   * @uml.property name="privValuePRNG"
   * @uml.associationEnd
   */
  protected RandomEngine privValuePRNG;

  /**
   * @uml.property name="seeds" multiplicity="(0 -1)" dimension="1"
   */
  protected long[] seeds;

  static Logger logger = Logger.getLogger(StandardRandomizer.class);

  static final String P_MAXPRIVATEVALUE = "maxprivatevalue";

  static final String P_MINPRIVATEVALUE = "minprivatevalue";

  public StandardRandomizer( ElectricityExperiment simulation ) {
    this();
    setExperiment(experiment);
  }

  public StandardRandomizer() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    minPrivateValue = parameters.getDoubleWithDefault(base
        .push(P_MINPRIVATEVALUE), null, minPrivateValue);
    maxPrivateValue = parameters.getDoubleWithDefault(base
        .push(P_MAXPRIVATEVALUE), null, maxPrivateValue);

  }

  /**
   * @uml.property name="experiment"
   */
  public void setExperiment( ElectricityExperiment experiment ) {
    this.experiment = experiment;
    this.auction = experiment.auction;
  }

  public double randomValue( RandomEngine prng, double min, double max ) {
    return min + prng.raw() * (max - min);
  }

  public double randomPrivateValue( double min, double max ) {
    return randomValue(privValuePRNG, min, max);
  }

  public double randomPrivateValue() {
    return randomPrivateValue(minPrivateValue, maxPrivateValue);
  }

  public void randomizePrivateValues( double[][] values, int iteration ) {
    Iterator i = auction.getTraderIterator();
    int traderNumber = 0;
    while ( i.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) i.next();
      trader.setPrivateValue(values[iteration][traderNumber++]);
    }
  }

  protected double[][] generateRandomizedPrivateValues( int numTraders,
      int numIterations ) {
    double[][] values = new double[numIterations][numTraders];
    EquilibriumReport stats = new EquilibriumReport(auction);
    for ( int i = 0; i < numIterations; i++ ) {
      privValuePRNG = PRNGFactory.getFactory().create(seeds[i]);
      do {
        Iterator traders = auction.getTraderIterator();
        for ( int t = 0; t < numTraders; t++ ) {
          double value = randomPrivateValue();
          AbstractTradingAgent agent = (AbstractTradingAgent) traders.next();
          agent.setPrivateValue(value);
          values[i][t] = value;
        }
        stats.recalculate();
      } while ( !stats.equilibriaExists() );
    }
    return values;
  }

  protected void generatePRNGseeds( int numIterations ) {
    seeds = new long[numIterations];
    RandomSeedGenerator seedGenerator = new RandomSeedGenerator();
    for ( int i = 0; i < numIterations; i++ ) {
      seeds[i] = seedGenerator.nextSeed();
    }
  }

  public String toString() {
    return "(" + getClass() + " minPrivateValue:" + minPrivateValue
        + " maxPrivateValue:" + maxPrivateValue + ")";
  }

}