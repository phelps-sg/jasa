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

package uk.ac.liv.auction.heuristic;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.MarketSimulation;
import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.PayoffLogger;
import uk.ac.liv.auction.stats.EquilibriaStats;
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
 * <p>
 * An application to generate a heuristic payoff matrix according to the
 * Walsh-et-al approximation technique.  This is described in the
 * following paper.  Note that this class is highly experimental and
 * is not guaranteed to generate accurate results.
 * </p>
 *
 * <p>
 * "Analysing Complex Strategic Interactions in Multi-Agent Systems"
 * W. Walsh, R. Das, G. Tesauro, J.O. Kephart
 * </p>
 *
 * <p>
 * This code was written independently from the authors of the above paper,
 * and any errors in the implementation are my own.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class HeuristicPayoffCalculator extends AbstractSeeder
    implements  Runnable, Serializable, EndOfDayListener {

  protected String resultsFileName = "/tmp/payoffs.csv";
  
  protected String gambitFileName;  
  
  protected String rdPlotFileName;
  
  protected RoundRobinAuction auction;

  protected PayoffLogger payoffLogger;

  protected int numAgents;

  protected int numBuyers;

  protected int numSellers;

  protected int numSamples = 10;

  protected double minValueMin = 61;

  protected double minValueMax = 160;

  protected double rangeMin = 60;

  protected double rangeMax = 209;

  protected AbstractTraderAgent[] agents;

  protected int numStrategies;

  protected Strategy[] strategies;

  protected ParameterDatabase parameters;

  protected RandomElement prng = PRNGFactory.getFactory().create();
  
  protected CompressedPayoffMatrix payoffMatrix;

  public static final String P_NUMAGENTS = "numagents";
  public static final String P_STRATEGY = "strategy";
  public static final String P_HEURISTIC = "heuristic";
  public static final String P_AUCTION = "auction";
  public static final String P_AGENT = "agent";
  public static final String P_MINVALUEMIN = "minvaluemin";
  public static final String P_MINVALUEMAX = "minvaluemax";
  public static final String P_VALUERANGEMIN = "valuerangemin";
  public static final String P_VALUERANGEMAX = "valuerangemax";
  public static final String P_N = "n";
  public static final String P_RESULTS = "results";
  public static final String P_GAMBITEXPORT = "gambitexport";
  public static final String P_RDPLOT = "rdplots";
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
      calculator.computePayoffMatrix();
      calculator.exportPayoffMatrixToCSV();
      calculator.exportPayoffMatrixToGambit(); 
      calculator.plotRDFlows();           

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }
  
  public void plotRDFlows() throws FileNotFoundException {
    if ( rdPlotFileName != null ) {    
      CSVWriter rdPlot = new CSVWriter( new FileOutputStream(rdPlotFileName), numStrategies);
      double p = 1.0/3.0;
      payoffMatrix.plotRDflow(rdPlot, new double[] {0.2, 0.6, 0.2}, 0.0000001, 100000);
    }
  }
  
  public void exportPayoffMatrixToCSV() throws FileNotFoundException {
    CSVWriter results = new CSVWriter( new FileOutputStream(resultsFileName), numStrategies*2);
    payoffMatrix.export(results);
  }
  
  public void exportPayoffMatrixToGambit() throws FileNotFoundException {
    if ( gambitFileName != null ) {    
      PrintWriter gambitNfg = new PrintWriter( new FileOutputStream(gambitFileName));
      payoffMatrix.exportToGambit(gambitNfg);
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
    
    payoffLogger = new PayoffLogger();
    payoffLogger.setAuction(auction);
    auction.addMarketDataLogger(payoffLogger);

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

    minValueMin = parameters.getDoubleWithDefault(base.push(P_MINVALUEMIN), null, minValueMin);
    minValueMax = parameters.getDoubleWithDefault(base.push(P_MINVALUEMAX), null, minValueMax);
    rangeMin = parameters.getDoubleWithDefault(base.push(P_VALUERANGEMIN), null, rangeMin);
    rangeMax = parameters.getDoubleWithDefault(base.push(P_VALUERANGEMAX), null, rangeMax);

    numBuyers = numAgents / 2;
    numSellers = numBuyers;

    agents = new AbstractTraderAgent[numAgents];

    for( int i=0; i<numAgents; i++ ) {
      agents[i] =  (AbstractTraderAgent)
            parameters.getInstanceForParameter(base.push(P_AGENT), null,
                                                AbstractTraderAgent.class);
      agents[i].setup(parameters, base.push(P_AGENT));
      agents[i].setIsSeller(false);
      auction.register(agents[i]);
      logger.debug("Configured agent " + agents[i]);
    }

    resultsFileName =
        parameters.getStringWithDefault(base.push(P_RESULTS), null,
                                         resultsFileName);
    
    gambitFileName = 
        parameters.getStringWithDefault(base.push(P_GAMBITEXPORT), null, null);
        
    rdPlotFileName =
        parameters.getStringWithDefault(base.push(P_RDPLOT), null, null);        
      
    logger.info("numAgents = " + numAgents);
    logger.info("numStrategies = " + numStrategies);
    logger.info("prng = " + PRNGFactory.getFactory().getDescription());
    logger.info("seed = " + prngSeed + "\n");

    seedObjects();

    logger.debug("Setup complete.");
  }


  public void run() {
    computePayoffMatrix();
  }
  
  public void computePayoffMatrix() {
    payoffMatrix = new CompressedPayoffMatrix(numAgents, numStrategies);
    Iterator i = payoffMatrix.compressedEntryIterator();
    while ( i.hasNext() ) {
      int[] payoffMatrixEntry = (int[]) i.next();
      calculateExpectedPayoffs(payoffMatrixEntry);
    }
  }


  public void calculateExpectedPayoffs( int[] entry ) {

    logger.info("");
    logger.info("Calculating expected payoffs for ");
    for( int i=0; i<numStrategies; i++ ) {
      logger.info("\t" + entry[i] + "/" + strategies[i].getClass().getName() + " ");    
    }
    logger.info("");

    CummulativeStatCounter[] payoffs =
        new CummulativeStatCounter[numStrategies];
    for( int i=0; i<numStrategies; i++ ) {
      payoffs[i] =
          new CummulativeStatCounter("Payoff for strategy " +
                                       strategies[i].getClass().getName());
    }

    assignStrategies(entry);

    for( int sample=0; sample<numSamples; sample++ ) {

      logger.debug("Taking Sample " + sample + ".....\n");

      randomlyAssignRoles();
      randomlyAssignValuers();
      auction.reset();
      auction.addEndOfDayListener(this);
      
      ensureEquilibriaExists();

      auction.run();

      payoffLogger.calculate();
//      payoffLogger.finalReport();
      
      EquilibriaStats stats = new EquilibriaStats(auction);
      stats.calculate();
      assert stats.equilibriaExists();
      
      for( int i=0; i<numStrategies; i++ ) {
        double payoff = payoffLogger.getPayoff(strategies[i].getClass());        
        payoffs[i].newData(payoff);        
      }

    }

    double[] outcome = payoffMatrix.getCompressedOutcome(entry);
    for( int i=0; i<numStrategies; i++ ) {
      logger.info("");
      payoffs[i].log();
      outcome[i] = payoffs[i].getMean();     
    }
    
  }
  
  public CompressedPayoffMatrix getCompressedPayoffMatrix() {
    return payoffMatrix;
  }

  public void endOfDay( Auction a ) {
    //ensureEquilibriaExists();
  }

  public void ensureEquilibriaExists() {
    EquilibriaStats stats = new EquilibriaStats(auction);
    stats.calculate();
    while ( ! (stats.equilibriaExists() && stats.getQuantity() >= ((numAgents) / 2)-2) ) {
      resetValuations();
      stats.reset();
      stats.calculate();
    }
  }


  protected void resetValuations() {
    Iterator i = auction.getTraderIterator();
    while (i.hasNext()) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      agent.getValuer().reset();
    }
  }


  protected void randomlyAssignRoles() {
    for( int i = 0; i < numAgents; i++ ) {
      agents[i].setIsSeller(true);
    }
    int numCandidates = numAgents;
    for( int i=0; i<numBuyers; i++ ) {
      int choice = prng.choose(numCandidates)-1;
      agents[choice].setIsSeller(false);
      AbstractTraderAgent lastAgent = agents[numCandidates-1];
      agents[numCandidates-1] = agents[choice];
      agents[choice] = lastAgent;
      numCandidates--;
    }
    for( int i=0; i<numAgents; i++ ) {
      logger.debug("Agent " + agents[i] + " isBuyer = " + agents[i].isBuyer());
    }
  }

  protected void randomlyAssignValuers() {
    double minValue = prng.uniform(minValueMin, minValueMax);    
    double maxValue = prng.uniform(minValue+rangeMin, minValue + rangeMax);
    for( int i=0; i<numAgents; i++ ) {
      RandomValuer valuer = (RandomValuer) agents[i].getValuer();
      valuer.setMaxValue(maxValue);
      valuer.setMinValue(minValue);
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
    prng = PRNGFactory.getFactory().create(seeder.nextSeed());
  }


  protected void seedObjects() {
    logger.info("Seeding objects...");
    seedAgents();
    seedAuction();
    seed(this);
    logger.info("Seeding done.\n");
  }



}
