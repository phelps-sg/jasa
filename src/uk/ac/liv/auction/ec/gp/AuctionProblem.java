package uk.ac.liv.auction.ec.gp;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;

import uk.ac.liv.auction.ec.gp.func.*;


import uk.ac.liv.util.io.CSVReader;

import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

import java.io.*;
import java.util.*;


public class AuctionProblem extends GPProblem implements SimpleProblemForm {

  static final String P_TRADERS = "numtraders";
  static final String P_ROUNDS = "maxrounds";

  static Random rand = new Random();

  static final String BUYER_CONF  = "auction-buyers.csv";
  static final String SELLER_CONF = "auction-sellers.csv";

  public int numTraders;
  public int maxRounds;

  // We'll deep clone this anyway, even though we don't
  // need it by default!
  public GPAuctionData input;

  RoundRobinAuction auction;


  public Object protoClone() throws CloneNotSupportedException {

    AuctionProblem myobj = (AuctionProblem) super.protoClone();
    myobj.numTraders = numTraders;
    return myobj;
  }

  public void setup( EvolutionState state, Parameter base ) {

    super.setup(state,base);

    String buyerConfig = state.parameters.getString(base.push("buyerconf"), null);
    String sellerConfig = state.parameters.getString(base.push("sellerconf"), null);

    // numTraders = state.parameters.getInt(base.push(P_TRADERS),null,1);
    maxRounds = state.parameters.getInt(base.push(P_ROUNDS),null,1);

    auction = new RoundRobinAuction();
    auction.setMaximumRounds(maxRounds);

    try {
      registerTraders(auction, buyerConfig, false);
      registerTraders(auction, sellerConfig, true);
    } catch ( Exception e ) {
      e.printStackTrace();
    }

  }

  public void evaluate( EvolutionState state,
			 Individual individual,
			 int thread ) {

    if ( individual.evaluated ) {
      return;
    }

    GPAuctioneer auctioneer = (GPAuctioneer) individual;

    auctioneer.setGPContext(state, thread, stack, this);
    auction.setAuctioneer(auctioneer);
    auctioneer.setAuction(auction);
    auction.reset();

    // Reset the agents
    Iterator traders = auction.getTraderIterator();
    while ( traders.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) traders.next();
      agent.reset();
    }

    auction.run();

    ElectricityStats stats = new ElectricityStats(0, 200, auction);
    float avMarketPower = (float) (stats.mPB + stats.mPS) / 2.0f;
    float fitness = (Math.abs(avMarketPower) + 1-((float) stats.eA/100))/2;
    if ( Float.isNaN(fitness) ) {
      fitness = 100;
    }

    KozaFitness f = ((KozaFitness)individual.fitness);
    f.setStandardizedFitness(state, fitness);

    individual.evaluated = true;


    System.out.println("\nFitness = " + fitness);
    System.out.println("eA = " + stats.eA);
    System.out.println("mPB = " + stats.mPB);
    System.out.println("mPS = " + stats.mPS);
    System.out.println("pBA = " + stats.pBA);
    System.out.println("pSA = " + stats.pSA);
  }

  public static void registerTraders( RoundRobinAuction auction, String traderConfigFile,
                                      boolean areSellers )
                    throws IOException, FileNotFoundException, ClassNotFoundException {

    // Set up the parser for the trader config file

    ArrayList traderFormat = new ArrayList(2);  // Each record has 2 fields
    traderFormat.add(0, Double.class);
    traderFormat.add(1, Integer.class);
    CSVReader traderConfig =
      new CSVReader(new FileInputStream(traderConfigFile), traderFormat);

    // Iterate over all the traders in the trader config file
    List traderRecord;
    while ( (traderRecord = traderConfig.nextRecord()) != null ) {

      // Get the fields from this trader's record
      Double privateValue = (Double) traderRecord.get(0);
      Integer capacity = (Integer) traderRecord.get(1);

      // Construct a trader for this record
      MREElectricityTrader trader =
        new MREElectricityTrader(capacity.intValue(), privateValue.doubleValue(), 0, areSellers);

      // Register it in the auction
      auction.register(trader);
    }
  }

  public static void main( String[] args ) {
    ec.Evolve.main( new String[] { "-file", "ecj.params/gpauctioneer.params" } );
  }

}