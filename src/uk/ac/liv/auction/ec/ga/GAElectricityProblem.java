package uk.ac.liv.auction.ec.ga;

import ec.EvolutionState;
import ec.Individual;
import ec.coevolve.*;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;
import ec.simple.*;
import ec.Problem;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.ec.CoEvolutionaryProblem;
import uk.ac.liv.auction.agent.PureSimpleStrategy;
import uk.ac.liv.auction.stats.*;
import uk.ac.liv.util.io.*;
import uk.ac.liv.ai.learning.*;

import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.*;


/**
 * @author Steve Phelps
 */

public class GAElectricityProblem extends Problem implements SimpleProblemForm {

  static final int NS = 3;
  static final int NB = 3;

  static final int CS = 10;
  static final int CB = 10;

  static final int MAX_ROUNDS = 1;

  static final int buyerValues[] = { 37, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  RandomRobinAuction auction;
  Auctioneer auctioneer;

  List buyers, sellers;

  CSVWriter statsOut;


  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(MAX_ROUNDS);

    sellers = registerTraders(auction, true, NS, CS, sellerValues);
    buyers = registerTraders(auction, false, NB, CB, buyerValues);

    auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);
    auction.setAuctioneer(auctioneer);

    try {
      statsOut = new CSVWriter(new FileOutputStream("electricity-stats.csv"), 6);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

  }


  public void evaluate( EvolutionState state, Individual individual,
                         int threadNum ) {

    auction.reset();

    setGenomes(individual);

    StatsMarketDataLogger logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);
    auction.run();
/*
    System.out.println("\n******** MARKET DATA *********\n");
    System.out.println(logger.getTransPriceStats()); */
    ElectricityStats marketStats = new ElectricityStats(0, 200, auction);
//    System.out.println(marketStats);

  //  setFitness(individual);
    SimpleFitness fitness = (SimpleFitness) individual.fitness;
    // Try to maximise market efficiency EA
    fitness.setFitness(state, (float) (marketStats.eA), false);
    individual.evaluated = true;

    statsOut.newData(state.generation);
    statsOut.newData(marketStats.pBA);
    statsOut.newData(marketStats.pSA);
    statsOut.newData(marketStats.mPB);
    statsOut.newData(marketStats.mPS);
    statsOut.newData(marketStats.eA);

  }

  public void setGenomes( Individual ind ) {
    DoubleVectorIndividual vectorInd = (DoubleVectorIndividual) ind;
    double[] vector = (double[]) vectorInd.getGenome();

    Iterator it = auction.getTraderIterator();
    for( int i=0; i<NS+NB; i++ ) {
      setGenome( (ElectricityTrader) it.next(), vector[i]);
    }
  }
/*
  public void setFitness( EvolutionState state, Vector group[] ) {

    setFitness(state, buyers.get(0), group[0].get(0));
    setFitness(state, buyers.get(1), group[1].get(0));
    setFitness(state, buyers.get(2), group[2].get(0));
    setFitness(state, buyers.get(3), group[0].get(1));
    setFitness(state, buyers.get(4), group[1].get(1));
    setFitness(state, buyers.get(5), group[2].get(1));
    setFitness(state, sellers.get(0), group[3].get(0));
    setFitness(state, sellers.get(1), group[4].get(0));
    setFitness(state, sellers.get(2), group[5].get(0));
    setFitness(state, sellers.get(3), group[3].get(1));
    setFitness(state, sellers.get(4), group[4].get(1));
    setFitness(state, sellers.get(5), group[5].get(1));
  }
*/

  public void setGenome( ElectricityTrader trader, double value ) {
    trader.setStrategy( new PureSimpleStrategy(trader, value, trader.getCapacity()) );
  }

  public void setFitness( EvolutionState state, Object t, Object i ) {
    ElectricityTrader trader = (ElectricityTrader) t;
    Individual individual = (Individual) i;
    ((SimpleFitness) individual.fitness).setFitness(state, (float) trader.getProfits(), false);
    individual.evaluated = true;
  }

  public static List registerTraders( RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    ArrayList result = new ArrayList();
    for( int i=0; i<num; i++ ) {

      // Construct a trader for this record
      ElectricityTrader trader =
        new ElectricityTrader(capacity, values[i % values.length], 0,
                            areSellers);

      result.add(trader);
      auction.register(trader);
      System.out.println("Registered " + trader);
    }

    return result;
  }


  public void describe( Individual individual, EvolutionState state, int threadnum, int log, int verbosity ) {
    DoubleVectorIndividual vectorInd = (DoubleVectorIndividual) individual;
    double[] vector = (double[]) vectorInd.getGenome();

    Iterator it = auction.getTraderIterator();
    for( int i=0; i<NS+NB; i++ ) {
      ElectricityTrader trader = (ElectricityTrader) it.next();
      String margin = "";
      double price = 0;
      if ( trader.isBuyer() ) {
        margin = "below";
        price = trader.getPrivateValue() - vector[i];
      } else {
        margin = "above";
        price = trader.getPrivateValue() + vector[i];
      }
      state.output.println( trader + " bids at " + vector[i] + " " + margin + " " + trader.getPrivateValue() + " = " + price,verbosity,log);
    }

  }

  public static void main( String[] args ) {
    ec.Evolve.main(new String[] { "-file", "electricity-auction.params" } );
  }

}


class SimpleGrid extends TransmissionGrid {

  int globalCapacity;

  SimpleGrid( int globalCapacity ) {
    super(null);
    this.globalCapacity = globalCapacity;
  }

  public int getATC( ElectricityTrader x, ElectricityTrader y ) {
    return globalCapacity;
  }

}

