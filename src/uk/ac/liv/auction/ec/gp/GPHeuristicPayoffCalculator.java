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

import uk.ac.liv.auction.heuristic.*;
import uk.ac.liv.auction.core.AuctionException;
import uk.ac.liv.auction.ec.gp.func.GPTradingStrategy;

import uk.ac.liv.util.CummulativeDistribution;

import org.apache.log4j.Logger;

import java.util.Iterator;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPHeuristicPayoffCalculator extends HeuristicPayoffCalculator {
  
  protected int gpStrategyIndex;
  
  protected boolean gpStrategyMisbehaved; 
  
  static Logger logger = Logger.getLogger(GPHeuristicPayoffCalculator.class);
  
  public static final String P_GPSTRATEGY = "gpstrategy";

  
  public void computePayoffMatrix() {

    gpStrategyMisbehaved = false;

    try {
      payoffMatrix = new CompressedPayoffMatrix(numAgents, numStrategies);
      Iterator i = payoffMatrix.compressedEntryIterator();
      while ( i.hasNext() ) {
        int[] payoffMatrixEntry = (int[]) i.next();
        if ( payoffMatrixEntry[gpStrategyIndex] > 0 ) {
          calculateExpectedGPPayoffs(payoffMatrixEntry);
        }
      }
      
    } catch ( NegativePayoffException e ) {
      gpStrategyMisbehaved = true;
    }
  }

  
  public void calculateExpectedGPPayoffs( int[] entry ) throws NegativePayoffException {

    logger.info("");
    logger.info("Calculating expected payoffs for ");
    for( int i=0; i<numStrategies; i++ ) {
      logger.info("\t" + entry[i] + "/" + groups[i] + " ");    
    }
    logger.info("");
    
    CummulativeDistribution[] payoffs =
        new CummulativeDistribution[numStrategies];
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
      
      ensureEquilibriaExists();

      auction.run();

      payoffLogger.calculate();      
      
      if ( payoffLogger.getPayoff(groups[gpStrategyIndex]) < 0 ) {
        negativePayoffException();
      }
      
      for( int i=0; i<numStrategies; i++ ) {
        double payoff = payoffLogger.getPayoff(groups[i]);        
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
  
  public boolean gpStrategyMisbehaved() {
    return gpStrategyMisbehaved;
  }
  
  public int getGPStrategyIndex() {
    return gpStrategyIndex;
  }
  
  public void setGPStrategy( GPTradingStrategy gpStrategy ) {
    setStrategy(gpStrategyIndex, gpStrategy);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {   
    super.setup(parameters, base);
    gpStrategyIndex = parameters.getInt(base.push(P_GPSTRATEGY)); 
  }
  
  private void negativePayoffException() throws NegativePayoffException {
    if ( negativePayoffException == null ) {
      negativePayoffException = new NegativePayoffException();
    }
    throw negativePayoffException;
  }
  
  private NegativePayoffException negativePayoffException;
}


class NegativePayoffException extends AuctionException {
 
  
}
