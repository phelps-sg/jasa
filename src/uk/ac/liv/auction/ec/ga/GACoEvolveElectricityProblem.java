/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

package uk.ac.liv.auction.ec.ga;

import java.util.*;

import java.io.*;

import ec.*;
import ec.vector.*;
import ec.simple.*;
import ec.util.*;

import uk.ac.liv.ec.coevolve.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.util.io.*;

/**
 * @author Steve Phelps
 */

public class GACoEvolveElectricityProblem extends Problem implements CoEvolutionaryProblem {

  static int NS = 2;
  static int NB = 3;

  static int CS = 10;
  static int CB = 10;

  static final int MAX_ROUNDS = 1;

  static final int buyerValues[] = { 36, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  RandomRobinAuction auction;
  Auctioneer auctioneer;
  StatsMarketDataLogger logger;

  List buyers, sellers;

  CSVWriter statsOut, spOut;

  ArrayList allTraders;


  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    boolean uniPrice = state.parameters.getBoolean(base.push("uniprice"),null,false);

    NS = state.parameters.getIntWithDefault(base.push("ns"), null, 3);
    NB = state.parameters.getIntWithDefault(base.push("nb"), null, 3);

    CS = state.parameters.getIntWithDefault(base.push("cs"), null, 10);
    CB = state.parameters.getIntWithDefault(base.push("cb"), null, 10);

    String statsFileName = state.parameters.getStringWithDefault(base.push("statsfile"), "coevolve-electricity-stats.csv");
    String spFileName = state.parameters.getStringWithDefault(base.push("spfile"), "coevolve-electricity-sp.csv");

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(MAX_ROUNDS);

    allTraders = new ArrayList(10);
    sellers = registerTraders(allTraders, auction, true, NS, CS, sellerValues);
    buyers = registerTraders(allTraders, auction, false, NB, CB, buyerValues);

    if ( uniPrice ) {
      auctioneer = new ContinuousDoubleAuctioneer(auction, 0.5);
    } else {
      auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);
    }

    auction.setAuctioneer(auctioneer);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    try {
      statsOut = new CSVWriter(new FileOutputStream(statsFileName), 7 + NS + NB);//13);
      spOut = new CSVWriter(new FileOutputStream(spFileName), (NS+NB)*2);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

  }

  public static List registerTraders( ArrayList allTraders,
                                      RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    System.out.println("num = " + num);
    ArrayList result = new ArrayList();
    for( int i=0; i<num; i++ ) {

      // Construct a trader for this record
      ElectricityTrader trader =
        new ElectricityTrader(capacity, values[i % values.length], 0,
                            areSellers);

      result.add(trader);
      allTraders.add(trader);
      auction.register(trader);
      System.out.println("Registered " + trader);
    }

    return result;
  }

  public void evaluate( EvolutionState state,
				  Vector[] group,  // the individuals to evaluate together
				  int threadnum) {

    Iterator traders = allTraders.iterator();

    auction.reset();

    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      setStrategy(trader, (Individual) group[i].get(0));
    }

    auction.run();

    traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      makeSidePayments(trader, (BitVectorIndividual) group[i].get(0));
    }

    traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      BitVectorIndividual individual
        = (BitVectorIndividual) group[i].get(0);
      double profits = trader.getProfits();
      float fitness = (float) profits;
      ((SimpleFitness) individual.fitness).setFitness(state, fitness, false);
      individual.evaluated = true;
    }

    ElectricityStats marketStats = new ElectricityStats(0, 400, auction);

    statsOut.newData(state.generation);
    double meanTransPrice = logger.getTransPriceStats().getMean();
    if ( !Double.isNaN(meanTransPrice) ) {
      statsOut.newData(meanTransPrice);
    } else {
      statsOut.newData(0);
    }
    traders = auction.getTraderIterator();
    while ( traders.hasNext() ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      statsOut.newData(trader.getProfits());
    }
    statsOut.newData(marketStats.getPBA());
    statsOut.newData(marketStats.getPSA());
    statsOut.newData(marketStats.getMPB());
    statsOut.newData(marketStats.getMPS());
    statsOut.newData(marketStats.getEA());
  }

  public void makeSidePayments( ElectricityTrader trader, Individual individual ) {
    double profits = trader.getProfits();
    double sidePayment = 0;
    ElectricityTrader partner = null;
    if ( profits > 0 ) {
      BitVectorIndividual i = (BitVectorIndividual) individual;
      boolean[] bits = (boolean []) i.getGenome();
      int binBits = bitVectorToInt(bits);
      //int agent = (binBits & 0x060) >> 5;
      double sidePaymentRatio = ((double) ((binBits & 0xf80) >> 7)) / 32.0;
      sidePayment = profits * sidePaymentRatio / 2;
      if ( sidePayment > 0 ) {
        for( int agent=0; agent<NS-1; agent++ ) {
          if ( trader.isBuyer() ) {
            partner = (ElectricityTrader) buyers.get(agent);
          } else {
            partner = (ElectricityTrader) sellers.get(agent);
          }
          if ( partner != null ) {
            trader.transferFunds(sidePayment, partner);
          }
        }
      }
    }

    if ( partner != null ) {
      spOut.newData(sidePayment);
      spOut.newData("all");
    } else {
      spOut.newData("no sp");
      spOut.newData(0);
    }
  }

  public void setStrategy( ElectricityTrader trader, Individual individual ) {
    BitVectorIndividual i = (BitVectorIndividual) individual;
    boolean[] bits = (boolean []) i.getGenome();
    int binBits = bitVectorToInt(bits);
    double value = binBits & 0x01f;
    Strategy s =
      new PureSimpleStrategy(trader, value,
                                trader.getCapacity());
    trader.setStrategy(s);
  }

  public static int bitVectorToInt( boolean[] vector ) {
    int value = 0;
    int base = 1;
    for( int i=0; i<vector.length; i++ ) {
      if ( vector[i] == true ) {
        value += base;
      }
      base *= 2;
    }
    return value;
  }

  public static void main( String[] args ) {
    ec.Evolve.main(new String[] { "-file", "ecj.params/coevolve-electricity-auction.params" } );
  }


}