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

package uk.ac.liv.auction;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.PerStrategyStats;
import uk.ac.liv.auction.stats.DailyStatsMarketDataLogger;

import uk.ac.liv.ai.learning.Learner;
import uk.ac.liv.ai.learning.StochasticLearner;

import uk.ac.liv.util.*;
import uk.ac.liv.util.io.CSVWriter;

import uk.ac.liv.prng.PRNGFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.io.*;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class HeuristicPayoffCalculator extends AbstractSeeder
    implements  Runnable, Serializable {

  protected String resultsFileName = "/tmp/payoffs.csv";

  protected CSVWriter results;

  protected RoundRobinAuction auction;

  protected PerStrategyStats strategyStats;

  protected int numAgents;

  protected int numBuyers;

  protected int numSellers;

  protected int numSamples = 10;

  protected AbstractTraderAgent[] agents;

  protected int numStrategies;

  protected Strategy[] strategies;

  protected ParameterDatabase parameters;

  protected RandomElement orderingPrng = PRNGFactory.getFactory().create();

  public static final String P_NUMAGENTS = "numagents";
  public static final String P_STRATEGY = "strategy";
  public static final String P_HEURISTIC = "heuristic";
  public static final String P_AUCTION = "auction";
  public static final String P_BUYER = "buyer";
  public static final String P_SELLER = "seller";
  public static final String P_N = "n";
  public static final String P_RESULTS = "results";
  public static final String P_NUMSAMPLES = "numsamples";

  static Logger logger = Logger.getLogger(HeuristicPayoffCalculator.class);



  public static void main( String[] args ) {

    try {

      if ( args.length < 1 ) {
        fatalError("Must specify a parameter file");
      }

      String fileName = args[0];
      File file = new File(fileName);
      if ( ! file.canRead() ) {
        fatalError("Cannot read parameter file " + fileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      MarketSimulation.gnuMessage();

      ParameterDatabase parameters = new ParameterDatabase(file);
      HeuristicPayoffCalculator calculator = new HeuristicPayoffCalculator();
      calculator.setup(parameters, new Parameter(P_HEURISTIC));
      calculator.run();

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    this.parameters = parameters;

    logger.debug("Setup... ");

    super.setup(parameters, base);

    auction =
        (RoundRobinAuction)
        parameters.getInstanceForParameterEq(base.push(P_AUCTION),
                                             null,
                                             RoundRobinAuction.class);

    auction.setup(parameters, base.push(P_AUCTION));

    strategyStats = new PerStrategyStats();
    strategyStats.setAuction(auction);
    auction.addMarketStats(strategyStats);


    numAgents = parameters.getInt(base.push(P_NUMAGENTS));
    numSamples = parameters.getInt(base.push(P_NUMSAMPLES));

    numStrategies = parameters.getInt(base.push(P_STRATEGY).push(P_N));
    strategies = new Strategy[numStrategies];

    for( int s=0; s<numStrategies; s++ ) {

      Parameter strategyParam = base.push(P_STRATEGY).push(s+"");
      Strategy strategy = (Strategy)
          parameters.getInstanceForParameter(strategyParam, null,
                                              Strategy.class);
      if ( strategy instanceof Parameterizable ) {
        ((Parameterizable) strategy).setup(parameters, strategyParam);
      }
      strategies[s] = strategy;

      logger.debug("Strategy " + s + " = " + strategy);
    }

    numBuyers = numAgents / 2;
    numSellers = numBuyers;

    agents = new AbstractTraderAgent[numAgents];

    for( int i=0; i<numBuyers; i++ ) {
      agents[i] =  (AbstractTraderAgent)
            parameters.getInstanceForParameter(base.push(P_BUYER), null,
                                                AbstractTraderAgent.class);
      agents[i].setup(parameters, base.push(P_BUYER));
      agents[i].setIsSeller(false);
      logger.debug("Configured seller " + agents[i]);
    }

    for( int i=numBuyers; i<numAgents; i++ ) {
      agents[i] = (AbstractTraderAgent)
         parameters.getInstanceForParameter(base.push(P_SELLER), null,
                                             AbstractTraderAgent.class);
      agents[i].setup(parameters, base.push(P_SELLER));
      agents[i].setIsSeller(true);
      logger.debug("Configured buyer " + agents[i]);
    }

    for( int i=0; i<numAgents; i++ ) {
      auction.register(agents[i]);
    }

    resultsFileName =
        parameters.getStringWithDefault(base.push(P_RESULTS), null,
                                         resultsFileName);

    logger.info("prng = " + PRNGFactory.getFactory().getDescription());
    logger.info("seed = " + prngSeed + "\n");

    seedObjects();

    logger.debug("Setup complete.");
  }


  public void run() {
    try {
      results = new CSVWriter(new FileOutputStream(resultsFileName),
                              numStrategies * 2);
    } catch ( FileNotFoundException e ) {
      logger.error(e);
      throw new Error(e);
    }
    Partitioner partitioner = new Partitioner(numAgents, numStrategies);
    while ( partitioner.hasNext() ) {
      int[] partition = (int[]) partitioner.next();
      calculateExpectedPayoffs(partition);
    }
  }


  public void calculateExpectedPayoffs( int[] entry ) {

    logger.info("");
    logger.info("Calculating expected payoffs for ");
    for( int i=0; i<numStrategies; i++ ) {
      logger.info("\t" + entry[i] + "/" + strategies[i].getClass().getName() + " ");
      results.newData(entry[i]);
    }
    logger.info("");

    CummulativeStatCounter[] payoffs = new CummulativeStatCounter[numStrategies];
    for( int i=0; i<numStrategies; i++ ) {
      payoffs[i] = new CummulativeStatCounter("Payoff for strategy " + strategies[i].getClass().getName());
    }

    for( int sample=0; sample<numSamples; sample++ ) {

      logger.debug("Taking Sample " + sample + ".....\n");

      randomlySortAgents();
      assignStrategies(entry);

      do {
        auction.reset();
        strategyStats.calculate();
      } while ( !strategyStats.equilibriaExists() );

      auction.run();
      strategyStats.calculate();

      for( int i=0; i<numStrategies; i++ ) {
        payoffs[i].newData(strategyStats.getPayoff(strategies[i].getClass()));
      }

    }

    for( int i=0; i<numStrategies; i++ ) {
      logger.info("");
      payoffs[i].log();
      results.newData(payoffs[i].getMean());
    }

    results.flush();
  }

  protected void randomlySortAgents() {
    int numCandidates = numAgents;
    for (int i = 0; i < numAgents; i++) {
      int choice = orderingPrng.choose(numCandidates - 1);
      AbstractTraderAgent agent = agents[choice];
      agents[choice] = agents[numCandidates - 1];
      agents[numCandidates - 1] = agent;
      numCandidates--;
    }

  }

  protected void assignStrategies( int[] entry ) {
    int agentIndex = 0;
    for( int i=0; i<numStrategies; i++ ) {
      for( int s=0; s<entry[i]; s++ ) {
        Prototypeable prototypeStrategy = (Prototypeable) strategies[i];
        AbstractTraderAgent agent = agents[agentIndex++];
        agent.setStrategy((Strategy) prototypeStrategy.protoClone());
        agent.reset();
      }
    }
  }


  protected static void fatalError( String message ) {
    System.err.println(message);
    System.exit(1);
  }


  protected void seedAgents() {
    for( int i=0; i<numAgents; i++ ) {
      agents[i].seed(this);
    }
  }


  protected void seedAuction() {
    if ( auction instanceof Seedable ) {
      ((Seedable) auction).seed(this);
    }
  }

  public void seed( Seeder seeder ) {
    super.seed(seeder);
    orderingPrng = PRNGFactory.getFactory().create(seeder.nextSeed());
  }


  protected void seedObjects() {
    logger.info("Seeding objects...");
    seedAgents();
    seedAuction();
    logger.info("Seeding done.\n");
    seed(this);
  }



}
