package uk.ac.liv.auction.ec.gp;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
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

    // numTraders = state.parameters.getInt(base.push(P_TRADERS),null,1);
    maxRounds = state.parameters.getInt(base.push(P_ROUNDS),null,1);

    auction = new RoundRobinAuction();
    auction.setMaximumRounds(maxRounds);

    try {
      registerTraders(auction,BUYER_CONF,false);
      registerTraders(auction,SELLER_CONF,true);
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
      ZITraderAgent agent = (ZITraderAgent) traders.next();
      agent.reset();
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
      surplus += agent.getQuantityTraded() * agent.getPrivateValue() - fundsSpent;
    }

    long optSurplus = 1000000;

    KozaFitness f = ((KozaFitness)individual.fitness);
    f.setFitness(state,(float)(optSurplus-surplus));
    //? f.hits = sum;
    individual.evaluated = true;
  }

  public static void registerTraders( RoundRobinAuction auction, String traderConfigFile,
                                      boolean areSellers )
                    throws IOException, FileNotFoundException, ClassNotFoundException {

    // Set up the parser for the trader config file

    ArrayList traderFormat = new ArrayList(2);  // Each record has 2 fields
    traderFormat.add(0, Class.forName("java.lang.Long"));
    traderFormat.add(1, Class.forName("java.lang.Integer"));
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

}