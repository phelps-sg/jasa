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

package uk.ac.liv.auction.ec.gp;


import java.util.*;

import ec.*;
import ec.gp.*;
import ec.util.*;

import uk.ac.liv.util.*;

import uk.ac.liv.prng.GlobalPRNG;

import uk.ac.liv.ec.gp.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;

import edu.cornell.lassp.houle.RngPack.RandomElement;

/**
 * Abstract super-class for electricity-trading scenarios.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class GPTradingProblem extends GPProblem {

  protected EquilibriumSurplusLogger surplusLogger;

  protected GPContext context = new GPContext();

  protected RoundRobinAuction auction;
  
  protected Auctioneer auctioneer;

  protected StrategyMixer strategyMixer;
  
  protected int numAgents;
  
  protected int numBuyers;
  
  protected int numSellers;  
  
  protected AbstractTraderAgent[] agents;

  protected double minValueMin = 61;

  protected double minValueMax = 160;

  protected double rangeMin = 60;

  protected double rangeMax = 209;
  
  public static final String P_NUMAGENTS = "numagents";
  public static final String P_AUCTION = "auction";
  public static final String P_MINVALUEMIN = "minvaluemin";
  public static final String P_MINVALUEMAX = "minvaluemax";
  public static final String P_VALUERANGEMIN = "valuerangemin";
  public static final String P_VALUERANGEMAX = "valuerangemax"; 
  public static final String P_AGENT = "agent";
  
  static final String DEFAULT_PARAM_FILE
                    = "ecj.params/test.params";


  public void setup( EvolutionState state, Parameter base ) {
  	
    super.setup(state, base);

    ParameterDatabase parameters = state.parameters;

    GlobalPRNG.setup(parameters, base);
    
    auction =
        (RoundRobinAuction)
        parameters.getInstanceForParameterEq(base.push(P_AUCTION),
                                             null,
                                             RoundRobinAuction.class);

    auction.setup(parameters, base.push(P_AUCTION));
    
    surplusLogger = new StrategyPayoffLogger();
    surplusLogger.setAuction(auction);
    auction.addMarketDataLogger(surplusLogger);

    numAgents = parameters.getInt(base.push(P_NUMAGENTS));
  
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
    }

  }  



  public void evaluate( EvolutionState state, Vector[] group, int thread ) {

    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(this);

    auctioneer = assignAuctioneer(group);
    auctioneer.setAuction(auction);
    auction.setAuctioneer(auctioneer);

    assignStrategies(group);

    resetStatCounters();    
	
	  preAuctionProcessing();
	  auction.run();
	  postAuctionProcessing();
	  computeFitnesses(group);

    postEvaluationStats();
  }


  public int getNumBuyers() {
    return numBuyers;
  }


  public int getNumSellers() {
    return numSellers;
  }


  public GPContext getGPContext() {
    return context;
  }


  protected void postEvaluationStats() {
    //TODO
  	surplusLogger.generateReport();
  }

  protected void resetStatCounters() {
  	//TODO
  }


  protected void randomlyAssignRoles() {
    for( int i = 0; i < numAgents; i++ ) {
      agents[i].setIsSeller(true);
    }
    int numCandidates = numAgents;
    for( int i=0; i<numBuyers; i++ ) {
      int choice = GlobalPRNG.getInstance().choose(numCandidates)-1;
      agents[choice].setIsSeller(false);
      AbstractTraderAgent lastAgent = agents[numCandidates-1];
      agents[numCandidates-1] = agents[choice];
      agents[choice] = lastAgent;
      numCandidates--;
    }  
  }

  public void ensureEquilibriaExists() {
    EquilibriaStats stats = new EquilibriaStats(auction);
    stats.calculate();
    while ( ! (stats.equilibriaExists() && stats.getQuantity() >= ((numAgents) / 2)-2) ) {
      resetTraders();
      stats.reset();
      stats.calculate();
    }
  }
  
  protected void randomizePrivateValues() {
  	RandomElement prng = GlobalPRNG.getInstance();
    double minValue = prng.uniform(minValueMin, minValueMax);    
    double maxValue = prng.uniform(minValue+rangeMin, minValue + rangeMax);
    for( int i=0; i<numAgents; i++ ) {
      RandomValuer valuer = (RandomValuer) agents[i].getValuer();
      valuer.setMaxValue(maxValue);
      valuer.setMinValue(minValue);
    }
  }


  protected void preAuctionProcessing() {  
  	randomizePrivateValues();   
  	randomlyAssignRoles();
  	auction.reset();
  	ensureEquilibriaExists();    
  }


  protected void resetTraders() {
    for( int i=0; i<agents.length; i++ ) {
    	agents[i].reset();      
    }
  }


  protected void postAuctionProcessing() {
  	surplusLogger.calculateTotalEquilibriumSurplus();
  }


  protected Auctioneer assignAuctioneer( Vector[] group ) {
    if ( auctioneer == null ) {
      auctioneer = new KDoubleAuctioneer(auction, 0.5);
    }
    return auctioneer;
  }

  
  protected LinkedList assignStrategies( Vector[] group ) {
    Auctioneer auctioneer = auction.getAuctioneer();
    LinkedList strategies = new LinkedList();    
    for( int i=0; i<agents.length; i++ ) {
      AbstractTraderAgent trader = agents[i];
      Strategy strategy = getStrategy(i, group);
      ((Resetable) strategy).reset();
      trader.setStrategy(strategy);
      strategy.setAgent(trader);      
      trader.reset();
      strategies.add(strategy);
    }
    return strategies;
  }

  protected Strategy getStrategy( int i, Vector[] group ) {
  	GPGenericIndividual individual = (GPGenericIndividual) group[0].get(i);
  	individual.setGPContext(context);
  	//individual.printIndividualForHumans(context.getState(), 0, ec.util.Output.V_NO_GENERAL);
  	return (Strategy) individual.getGPObject();
    //return strategyMixer.getStrategy(i, group);
  }

  public static void main( String[] args ) {
    ec.Evolve.main(new String[] {"-file", DEFAULT_PARAM_FILE});
  }


  public static EvolutionState make() {
    return make(DEFAULT_PARAM_FILE);
  }


  public static EvolutionState make( String parameterFile ) {
    return ec.Evolve.make( new String[] { "-file", parameterFile } );
  }


  protected abstract void computeFitnesses( Vector[] group );

  public abstract int getFirstStrategySubpop();

}

