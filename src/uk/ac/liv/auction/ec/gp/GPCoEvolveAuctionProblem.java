package uk.ac.liv.auction.ec.gp;


import java.util.*;

import java.io.*;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.util.*;

import uk.ac.liv.ec.coevolve.*;

import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.auction.ec.gp.func.*;



/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class GPCoEvolveAuctionProblem extends GPProblem implements CoEvolutionaryProblem {

  static int NS = 3;
  static int NB = 3;

  static int CS = 10;
  static int CB = 10;

  static int MAX_ROUNDS = 1000;

  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";
  static final String P_ITERATIONS = "iterations";

  static final int buyerValues[] = { 36, 17, 12 };
  //static final int buyerValues[] = { 100, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  protected RoundRobinAuction auction;

  protected List buyers, sellers;

  protected CSVWriter statsOut;

  protected ArrayList allTraders;

  protected StatsMarketDataLogger logger;

  protected ElectricityStats stats;


  public Object protoClone() throws CloneNotSupportedException {

    GPCoEvolveAuctionProblem myobj = (GPCoEvolveAuctionProblem) super.protoClone();
    //TODO?
    return myobj;
  }


  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    NS = state.parameters.getIntWithDefault(base.push("ns"), null, 3);
    NB = state.parameters.getIntWithDefault(base.push("nb"), null, 3);

    CS = state.parameters.getIntWithDefault(base.push("cs"), null, 10);
    CB = state.parameters.getIntWithDefault(base.push("cb"), null, 10);

    MAX_ROUNDS = state.parameters.getIntWithDefault(base.push("maxrounds"), null, 1000);

    System.out.println("NS = " + NS);
    System.out.println("NB = " + NB);
    System.out.println("CS = " + CS);
    System.out.println("CB = " + CB);
    System.out.println("MAX_ROUNDS = " + MAX_ROUNDS);

    String statsFileName = state.parameters.getStringWithDefault(base.push("marketstatsfile"), "coevolve-electricity-stats.csv");

    auction = new RandomRobinAuction();
    auction.setMaximumRounds(MAX_ROUNDS);

    allTraders = new ArrayList(NS+NB);
    sellers = registerTraders(allTraders, auction, true, NS, CS, sellerValues);
    buyers = registerTraders(allTraders, auction, false, NB, CB, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    try {
      statsOut = new CSVWriter(new FileOutputStream(statsFileName), 10 + NS + NB);//13);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    // Print headings in CSV output file

    statsOut.newData("generation");

    for( int i=1; i<=NS; i++ ) {
      statsOut.newData("s" + i);
    }

    for( int i=1; i<=NB; i++ ) {
      statsOut.newData("b" + i);
    }

    statsOut.newData( new String[] {  "eA", "mPB", "mPS", "transPrice", "bidPrice",
                                      "askPrice", "quoteBidPrice", "quoteAskPrice",
                                      "fitness" });
  }

  public void evaluate( EvolutionState state, Vector[] group, int thread ) {

    // Log the generation number to the CSV file
    statsOut.newData(state.generation);

    // Reset the auction
    auction.reset();

    // Assign the appropriate GP-evolved strategy to each trading agent
    Iterator traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      ElectricityTrader trader = (ElectricityTrader) traders.next();
      GPTradingStrategy strategy = (GPTradingStrategy) group[i+1].get(0);
      strategy.reset();
      strategy.setGPContext(state, thread, stack, this);
      trader.setStrategy(strategy);
      strategy.setAgent(trader);
      strategy.setQuantity(trader.getCapacity());
      trader.reset();
    }

    // Initialise the GP-evolved auctioneer
    GPAuctioneer auctioneer = (GPAuctioneer) group[0].get(0);
    auctioneer.setGPContext(state, thread, stack, this);
    auctioneer.setAuction(auction);
    auction.setAuctioneer(auctioneer);

    // Run the auction
    auction.run();

    // Set the fitness for the strategy population according to profits made
    traders = allTraders.iterator();
    for( int i=0; traders.hasNext(); i++ ) {
      GPElectricityTrader trader = (GPElectricityTrader) traders.next();
      double profits = trader.getProfits();
      float fitness = Float.MAX_VALUE;
      if ( (!Double.isNaN(profits)) ) {
        fitness = 10000f - (float) profits;
      }
      if ( fitness < 0 ) {
        System.err.println("WARNING: trader " + trader + " had negative fitness!");
        fitness = 0;
      }
      GPTradingStrategy individual = (GPTradingStrategy) group[i+1].get(0);
      ((KozaFitness) individual.fitness).setStandardizedFitness(state, fitness);
      individual.evaluated = true;
      statsOut.newData(profits);
    }

    // Calculate market statistics for this run
    if ( stats == null ) {
      stats = new ElectricityStats(0, 200, auction);
    } else {
      stats.recalculate();
    }
    auctioneer.setStats(stats.newCopy());

    // Calculate auctioneer fitness based on market stats
    float relMarketPower = (float) (Math.abs(stats.mPB) + Math.abs(stats.mPS)) / 2.0f;
    if ( stats.eA > 100 ) {
      System.err.println("eA > 100% !!");
      System.err.println(stats);
    }
    float fitness = Float.MAX_VALUE;
    if ( !Float.isNaN(relMarketPower) && !Float.isInfinite(relMarketPower)
           && !Double.isNaN(stats.eA) ) {
      fitness = 1-((float) stats.eA/100) + relMarketPower;
    }
    GPIndividual individual = (GPIndividual) group[0].get(0);
    ((KozaFitness) individual.fitness).setStandardizedFitness(state, fitness);
    individual.evaluated = true;

    // Log market stats to CSV file
    statsOut.newData(stats.eA);
    statsOut.newData(stats.mPB);
    statsOut.newData(stats.mPS);
    statsOut.newData(logger.getTransPriceStats().getMean());
    statsOut.newData(logger.getAskPriceStats().getMean());
    statsOut.newData(logger.getBidPriceStats().getMean());
    statsOut.newData(logger.getAskQuoteStats().getMean());
    statsOut.newData(logger.getBidQuoteStats().getMean());
    statsOut.newData(fitness);
  }


  public static List registerTraders( ArrayList allTraders,
                                      RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    ArrayList result = new ArrayList();
    for( int i=0; i<num; i++ ) {

      // Construct a trader for this record
      GPElectricityTrader trader =
        new GPElectricityTrader(capacity, values[i % values.length], 0,
                                areSellers);

      result.add(trader);
      allTraders.add(trader);
      auction.register(trader);
      System.out.println("Registered " + trader);
    }

    return result;
  }

  public static void main( String[] args ) {
    ec.Evolve.main(new String[] { "-file", "ecj.params/coevolve-gpauctioneer.params"} );
  }

}

/**
 * GPElectricityTrader only makes deals that result in +ve profits.
 */
class GPElectricityTrader extends ElectricityTrader {

  public GPElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller ) {
    super(capacity, privateValue, fixedCosts, isSeller);
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {

    if ( price < privateValue ) {

      GPElectricityTrader trader = (GPElectricityTrader) seller;
      trader.informOfBuyer(this, price, quantity);
    }
  }

  public void trade( double price, int quantity ) {
    double profit = quantity * (privateValue - price);
    profits += profit;
  }

  public void informOfBuyer( GPElectricityTrader buyer, double price, int quantity ) {

    if ( price > privateValue ) {

      buyer.trade(price, quantity);

      double profit = quantity * (price - privateValue);
      profits += profit;
    }
  }



}