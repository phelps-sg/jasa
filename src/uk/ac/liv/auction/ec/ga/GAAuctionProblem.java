package uk.ac.liv.auction.ec.ga;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.*;

import uk.ac.liv.util.io.CSVReader;

import uk.ac.liv.util.CummulativeStatCounter;

import ec.util.*;
import ec.*;
import ec.simple.*;

import java.io.*;
import java.util.*;


/**
 * @author Steve Phelps
 */


public class GAAuctionProblem extends Problem implements SimpleProblemForm {

  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";

  static Random rand = new Random();

  static final String BUYER_CONF  = "auction-buyers.csv";
  static final String SELLER_CONF = "auction-sellers.csv";

  static final double EQUILIBRIUM_PRICE = 70.0;

  public int numTraders;
  public int maxRounds;


  RoundRobinAuction auction;

  public Object protoClone() throws CloneNotSupportedException {

    GAAuctionProblem myobj = (GAAuctionProblem) super.protoClone();
    myobj.numTraders = numTraders;
    myobj.maxRounds = maxRounds;
    return myobj;
  }

  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    // numTraders = state.parameters.getInt(base.push(P_TRADERS),null,1);
    maxRounds = state.parameters.getInt(base.push(P_ROUNDS),null,1);

    auction = new RoundRobinAuction();
    auction.setMaximumRounds(maxRounds);

    try {
      registerTraders(auction,BUYER_CONF,false);
      registerTraders(auction,SELLER_CONF,true);
    } catch ( Exception e ) {
      e.printStackTrace();
    }  /*
    registerRandomTraders(auction, 10, true);
    registerRandomTraders(auction, 10, false); */

  }

  public void evaluate( EvolutionState state,
			 Individual individual,
			 int thread ) {

    if ( individual.evaluated ) {
      return;
    }

    GAAuctioneerContainer container = (GAAuctioneerContainer) individual;

    Auctioneer auctioneer = container.getAuctioneer();
    auction.setAuctioneer(auctioneer);
    auction.reset();
    StatsMarketDataLogger logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger( logger );

    // Reset the agents
    Iterator traders = auction.getTraderIterator();
    while ( traders.hasNext() ) {
      ZITraderAgent agent = (ZITraderAgent) traders.next();
      agent.reset();
      System.out.println("resetting " + agent);
    }

    auction.run();

    // calculate global surplus
    traders = auction.getTraderIterator();
    long surplus = 0L;
    while ( traders.hasNext() ) {
      ZITraderAgent agent = (ZITraderAgent) traders.next();
      double fundsSpent;
      if ( agent.isSeller() ) {
        fundsSpent = -agent.getFunds();
      } else {
        fundsSpent = agent.getFunds();
      }
      System.out.println(agent + " qty:" + agent.getQuantityTraded() + " utility:" + agent.getPrivateValue() + " fundsSp:" + fundsSpent);
      surplus += agent.getQuantityTraded() * agent.getPrivateValue() - fundsSpent;
    }

    System.out.println("surplus = " + surplus);

    // Calculate 'efficiency' as a measure of deviation from theoretical
    // equilibrium price.
    CummulativeStatCounter transPriceStats = logger.getTransPriceStats();
    double efficiency = 100 - transPriceStats.getVarCoef(EQUILIBRIUM_PRICE);
    ((SimpleFitness) individual.fitness).setFitness(state, (long) efficiency, false);
    individual.evaluated = true;
  }

  public static void registerRandomTraders( RoundRobinAuction auction,
                                            int numTraders, boolean areSellers ) {
    for( int i=0; i<numTraders; i++ ) {
      long limitPrice = rand.nextInt(200);
      ZISTraderAgent trader = new ZISTraderAgent(limitPrice, 100, areSellers);
      auction.register(trader);
    }
  }

  public static void registerTraders( RoundRobinAuction auction, String traderConfigFile,
                                      boolean areSellers )
                    throws IOException, FileNotFoundException, ClassNotFoundException {

    // Set up the parser for the trader config file

    ArrayList traderFormat = new ArrayList(2);  // Each record has 2 fields
    traderFormat.add(0, Long.class);
    traderFormat.add(1, Integer.class);
    CSVReader traderConfig =
      new CSVReader(new FileInputStream(traderConfigFile), traderFormat);

    // Iterate over all the traders in the trader config file
    List traderRecord;
    while ( (traderRecord = traderConfig.nextRecord()) != null ) {

      // Get the fields from this trader's record
      Long limitPrice = (Long) traderRecord.get(0);
      Integer tradeEntitlement = (Integer) traderRecord.get(1);

      // Construct a ZI-C trader for this record
      ZISTraderAgent trader =
        new ZISTraderAgent(limitPrice.longValue(), tradeEntitlement.intValue(),
                            areSellers);

      // Register it in the auction
      auction.register(trader);
    }
  }

  public void describe(final Individual ind,
			 final EvolutionState state,
			 final int threadnum,
			 final int log,
			 final int verbosity)
	{
        state.output.println("k = " +
            ((GAAuctioneerContainer) ind).getAuctioneer().getK() +
            " fitness = " + ind.fitness.fitness()
            ,verbosity,log);
	return;
	}

}