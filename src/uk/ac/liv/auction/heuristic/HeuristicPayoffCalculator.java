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

import uk.ac.liv.auction.core.AuctionEventListener;
import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;

import uk.ac.liv.auction.agent.Strategy;
import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.RandomValuer;
import uk.ac.liv.auction.agent.AgentGroup;

import uk.ac.liv.auction.stats.GroupPayoffLogger;
import uk.ac.liv.auction.stats.PayoffLogger;
import uk.ac.liv.auction.stats.EquilibriaStats;

import uk.ac.liv.util.Distribution;
import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Prototypeable;

import uk.ac.liv.util.io.CSVWriter;

import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.prng.PRNGFactory;

import uk.ac.liv.ec.EvolutionStateSingleton;

import java.util.Iterator;

import java.io.*;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import org.apache.log4j.Logger;


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

public class HeuristicPayoffCalculator  
    implements  Runnable, Serializable, AuctionEventListener {

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

  protected AbstractTradingAgent[] agents;

  protected int numStrategies;

  protected Strategy[] strategies;
  
  protected AgentGroup[] groups;

  protected ParameterDatabase parameters;

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
  public static final String P_ECJPARAMS = "ecjparams";
  public static final String P_INDIVIDUALFACTORY = "gpindividualfactory";

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

    logger.debug("Setup... ");
    
    this.parameters = parameters;

    GlobalPRNG.setup(parameters, base);

    String ecjParamFileName =
        parameters.getString(base.push(P_ECJPARAMS), null);
    if ( ecjParamFileName != null ) {
      EvolutionStateSingleton.initialise(ecjParamFileName);      
    }
    
    auction =
        (RoundRobinAuction)
        parameters.getInstanceForParameterEq(base.push(P_AUCTION),
                                             null,
                                             RoundRobinAuction.class);

    auction.setup(parameters, base.push(P_AUCTION));
    
    payoffLogger = new GroupPayoffLogger();
    payoffLogger.setAuction(auction);
    auction.addMarketDataLogger(payoffLogger);

    numAgents = parameters.getInt(base.push(P_NUMAGENTS));
    numSamples = parameters.getInt(base.push(P_NUMSAMPLES));

    numStrategies = parameters.getInt(base.push(P_STRATEGY).push(P_N));
    strategies = new Strategy[numStrategies];
    groups = new AgentGroup[numStrategies];

    for( int s=0; s<numStrategies; s++ ) {

      Parameter strategyParam = base.push(P_STRATEGY).push(s+"");
      Strategy strategy = (Strategy)
          parameters.getInstanceForParameter(strategyParam, null,
                                              Strategy.class);
      if ( strategy instanceof Parameterizable ) {
        ((Parameterizable) strategy).setup(parameters, strategyParam);
      }
      strategies[s] = strategy;
      groups[s] = new AgentGroup("strategy " + s);

      logger.debug("Strategy " + s + " = " + strategy);
    }

    minValueMin = parameters.getDoubleWithDefault(base.push(P_MINVALUEMIN), null, minValueMin);
    minValueMax = parameters.getDoubleWithDefault(base.push(P_MINVALUEMAX), null, minValueMax);
    rangeMin = parameters.getDoubleWithDefault(base.push(P_VALUERANGEMIN), null, rangeMin);
    rangeMax = parameters.getDoubleWithDefault(base.push(P_VALUERANGEMAX), null, rangeMax);

    numBuyers = numAgents / 2;
    numSellers = numBuyers;

    agents = new AbstractTradingAgent[numAgents];

    for( int i=0; i<numAgents; i++ ) {
      agents[i] =  (AbstractTradingAgent)
            parameters.getInstanceForParameter(base.push(P_AGENT), null,
                                                AbstractTradingAgent.class);
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
    logger.info("seed = " + GlobalPRNG.getSeed() + "\n");

    logger.debug("Setup complete.");
  }


  public void run() {
    computePayoffMatrix();
  }
  
  public void computePayoffMatrix() {
    payoffMatrix = new CompressedPayoffMatrix(numAgents, numStrategies);
    Iterator i = payoffMatrix.compressedEntryIterator();
    while ( i.hasNext() ) {
      CompressedPayoffMatrix.Entry entry = (CompressedPayoffMatrix.Entry) i.next();
      calculateExpectedPayoffs(entry);
    }
  }


  public void calculateExpectedPayoffs( CompressedPayoffMatrix.Entry entry  ) {

    logger.info("");
    logger.info("Calculating expected payoffs for ");
    for( int s=0; s<numStrategies; s++ ) {
      logger.info("\t" + entry.getNumAgents(s) + "/" + groups[s] + " ");    
    }
    logger.info("");

    Distribution[] payoffs = new Distribution[numStrategies];
    for( int i=0; i<numStrategies; i++ ) {
      payoffs[i] =
          new CummulativeDistribution("Payoff for group " + groups[i]);
    }

    assignStrategies(entry);

    for( int sample=0; sample<numSamples; sample++ ) {

      logger.debug("Taking Sample " + sample + ".....\n");

      randomlyAssignRoles();
      randomlyAssignValuers();
      auction.reset();
      auction.addAuctionEventListener(EndOfDayEvent.class, this);
      
      ensureEquilibriaExists();

      auction.run();

      payoffLogger.calculate();
//      payoffLogger.finalReport();      
      
      for( int i=0; i<numStrategies; i++ ) {
        double payoff = payoffLogger.getPayoff(groups[i]);          
        payoffs[i].newData(payoff);        
      }

    }

    double[] outcome = payoffMatrix.getCompressedPayoffs(entry);
    for( int i=0; i<numStrategies; i++ ) {
      logger.info("");
      payoffs[i].log();
      outcome[i] = payoffs[i].getMean();     
    }
    
  }
  
  public CompressedPayoffMatrix getCompressedPayoffMatrix() {
    return payoffMatrix;
  }

  public void eventOccurred( AuctionEvent event ) {
    if ( event instanceof EndOfDayEvent ) {
    //ensureEquilibriaExists();
    }
  }

  public void setStrategy( int i, Strategy s ) {
    strategies[i] = s;
  }
  
  protected void ensureEquilibriaExists() {
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
      AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
      agent.getValuationPolicy().reset();
    }
  }


  protected void randomlyAssignRoles() {
    for( int i = 0; i < numAgents; i++ ) {
      agents[i].setIsSeller(true);
    }
    GlobalPRNG.randomPermutation(agents);    
    for( int i=0; i<numBuyers; i++ ) {
      agents[i].setIsSeller(false);
    }
    for( int i=0; i<numAgents; i++ ) {
      logger.debug("Agent " + agents[i] + " isBuyer = " + agents[i].isBuyer());
    }
  }

  protected void randomlyAssignValuers() {
  	RandomElement prng = GlobalPRNG.getInstance();
    double minValue = prng.uniform(minValueMin, minValueMax);    
    double maxValue = prng.uniform(minValue+rangeMin, minValue + rangeMax);
    for( int i=0; i<numAgents; i++ ) {
      RandomValuer valuer = (RandomValuer) agents[i].getValuationPolicy();
      valuer.setMaxValue(maxValue);
      valuer.setMinValue(minValue);
    }
  }

  protected void assignStrategies( CompressedPayoffMatrix.Entry entry ) {
    int agentIndex = 0;
    for( int i=0; i<numStrategies; i++ ) {
      for( int s=0; s<entry.getNumAgents(i); s++ ) {
        Prototypeable prototypeStrategy = (Prototypeable) strategies[i];
        Strategy clonedStrategy = (Strategy) prototypeStrategy.protoClone();
        AbstractTradingAgent agent = agents[agentIndex++];
        agent.setStrategy(clonedStrategy);
        agent.setGroup(groups[i]);
        agent.reset();
      }
    }
  }


  protected static void fatalError( String message ) {
    System.err.println(message);
    System.exit(1);
  }

  public void reset() {
    payoffMatrix = new CompressedPayoffMatrix(numAgents, numStrategies);
  }
}
