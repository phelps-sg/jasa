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



public class PerStrategyStats implements MarketStats, Serializable, Resetable {

  protected HashMap table  = new HashMap();

  protected RoundRobinAuction auction;

  static Logger logger = Logger.getLogger(PerStrategyStats.class);


  public void setup( ParameterDatabase parameters, Parameter base ) {
  }

  public void setAuction( RoundRobinAuction auction ) {
    logger.debug("Setting auction to " + auction);
    this.auction = auction;
  }

  public void calculate() {
    reset();
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      double profits = agent.getProfits();
      Class strategyClass = agent.getStrategy().getClass();
      StrategyStats stats = (StrategyStats) table.get(strategyClass);
      if ( stats == null ) {
        stats = new StrategyStats();
      }
      stats.profits += profits;
      stats.numAgents++;
      table.put(strategyClass, stats);
    }
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

  public int getNumberOfAgents( Class strategyClass ) {
    StrategyStats stats =
        (StrategyStats) table.get(strategyClass);
    if ( stats != null ) {
      return stats.numAgents;
    } else {
      return 0;
    }

  }

  public void generateReport() {
    logger.info("\nProfits per strategy");
    logger.info("-----------------------");
    logger.info("");
    Iterator i = table.keySet().iterator();
    while ( i.hasNext() ) {
      Class strategy = (Class) i.next();
      StrategyStats stats = (StrategyStats) table.get(strategy);
      logger.info(stats.numAgents + " agents playing strategy " +
                   strategy.getName() + "\n\ttotal profits: " + stats.profits +
                   "\n");
    }
  }

  public void reset() {
    table.clear();
  }

}


class StrategyStats {

  public double profits = 0;
  public int numAgents = 0;

}
