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

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.core.RoundRobinAuction;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.MutableDoubleWrapper;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.*;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A MarketDataLogger that keeps track of the ratio of actual
 * verses theoretical profits for each strategy being played in the
 * auction.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class PayoffLogger extends EquilibriumSurplusLogger {

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
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      double profits = agent.getProfits();
      assert profits >= 0;
      Class strategyClass = agent.getStrategy().getClass();
      StrategyStats stats = (StrategyStats) table.get(strategyClass);
      if ( stats == null ) {
        stats = new StrategyStats(1, profits);
        table.put(strategyClass, stats);
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
      Class strategyClass = (Class) i.next();
      StrategyStats stats = (StrategyStats) table.get(strategyClass);
      if ( averageSurplus != 0 ) {        
        stats.payoff = ((stats.profits / stats.numAgents)  ) / averageSurplus;
      } else {        
        stats.payoff = 0;
      }
    }
  }


  public double getTotalProfits() {
    return totalProfits;
  }

  public double getProfits( Class strategyClass ) {
    StrategyStats stats =
        (StrategyStats) table.get(strategyClass);
    if ( stats != null ) {
      return stats.profits;
    } else {
      return 0;
    }
  }

  public double getPayoff( Class strategyClass ) {
    StrategyStats stats =
       (StrategyStats) table.get(strategyClass);
    if (stats != null) {
      return stats.payoff;
    } else {
      return 0;
    }
  }

  public int getNumberOfAgents( Class strategyClass ) {
    StrategyStats stats =
        (StrategyStats) table.get(strategyClass);
    if ( stats != null ) {
      return stats.numAgents;
    } else {
      return 0;
    }

  }


  public void finalReport() {
    calculate();
    logger.info("\nProfits per strategy");
    logger.info("-----------------------");
    logger.info("");
    Iterator i = table.keySet().iterator();
    while ( i.hasNext() ) {
      Class strategy = (Class) i.next();
      StrategyStats stats = (StrategyStats) table.get(strategy);
      logger.info(stats.numAgents + " agents playing strategy " +
                   strategy.getName() + "\n\ttotal profits: " + stats.profits +
                                        "\n\tpayoff: " + stats.payoff +
                   "\n");
    }
    super.finalReport();
  }


  public void initialise() {
    super.initialise();
    totalProfits = 0;
    table.clear();
  }

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
