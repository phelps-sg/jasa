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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.agent.AbstractTradingAgent;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * A report that keeps track of the ratio of actual
 * verses theoretical profits for each strategy being played in the
 * auction.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class PayoffLogger extends EquilibriumSurplusLogger 
								implements Serializable {

  private HashMap table = new HashMap();
  
  protected double totalProfits;

  static Logger logger = Logger.getLogger(PayoffLogger.class);


  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
  }


  public void calculate() {
    table.clear();
    totalProfits = 0;
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
      double profits = agent.getProfits();
      //assert profits >= 0;
      Object key = getKey(agent);
      StrategyStats stats = (StrategyStats) table.get(key);
      if ( stats == null ) {
        stats = new StrategyStats(1, profits);
        table.put(key, stats);
      } else {
        stats.profits += profits;
        stats.numAgents++;
      }
      totalProfits += profits;
    }
    calculatePayoffs();
  }


  protected void calculatePayoffs() {
    int totalAgents = auction.getNumberOfRegisteredTraders();
    double averageSurplus = calculateTotalEquilibriumSurplus() / totalAgents;    
    double totalPayoff = 0;
    Iterator i = table.keySet().iterator();
    while ( i.hasNext() ) {
      Object key = i.next();
      StrategyStats stats = (StrategyStats) table.get(key);
      if ( averageSurplus != 0 ) {
        stats.payoff = ((stats.profits / stats.numAgents)  ) / averageSurplus;      
      } else {
        stats.payoff = 1;        
      }
    }
  }


  public double getTotalProfits() {
    return totalProfits;
  }

  public double getProfits( Object key ) {
    StrategyStats stats =
        (StrategyStats) table.get(key);
    if ( stats != null ) {
      return stats.profits;
    } else {
      return 0;
    }
  }

  public double getPayoff( Object key ) {
    StrategyStats stats =
       (StrategyStats) table.get(key);
    if ( stats != null ) {
      return stats.payoff;
    } else {
      return 0;
    }
  }

  public int getNumberOfAgents( Object key ) {
    StrategyStats stats =
        (StrategyStats) table.get(key);
    if ( stats != null ) {
      return stats.numAgents;
    } else {
      return 0;
    }

  }


  public void generateReport() {
    calculate();
    logger.info("\nProfits per " + getKeyName());
    logger.info("-----------------------");
    logger.info("");
    Iterator i = table.keySet().iterator();
    while ( i.hasNext() ) {
      Object key = (Object) i.next();
      StrategyStats stats = (StrategyStats) table.get(key);
      logger.info(stats.numAgents + " " + getReportText() + " " +
                   key + "\n\ttotal profits: " + stats.profits +
                                        "\n\tpayoff: " + stats.payoff +
                   "\n");
    }
    super.generateReport();
  }
  


  public void initialise() {
    super.initialise();
    totalProfits = 0;
    table.clear();
  }
  
  public abstract Object getKey( AbstractTradingAgent agent );

  public abstract String getKeyName();
  
  public abstract String getReportText();
}


class StrategyStats {

  public double profits = 0;
  public int numAgents = 0;
  public double payoff = 0;

  public StrategyStats( int numAgents, double profits ) {
    this.numAgents = numAgents;
    this.profits = profits;
  }

}
