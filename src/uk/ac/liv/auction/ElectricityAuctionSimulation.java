package uk.ac.liv.auction;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.stats.StatsMarketDataLogger;
import uk.ac.liv.auction.stats.MetaMarketStats;
import uk.ac.liv.auction.electricity.*;
import uk.ac.liv.util.*;
import uk.ac.liv.util.io.*;
import uk.ac.liv.ai.learning.*;

import ec.util.MersenneTwisterFast;

import java.util.*;

import java.io.*;


public class ElectricityAuctionSimulation  {

  static final String OUTPUT_FILE   = "electricity-data.csv";
  static final String BUYER_CONFIG  = "electricity-buyers.csv";
  static final String SELLER_CONFIG = "electricity-sellers.csv";

  //static final int ATC = 1000;


  static int MAX_ROUNDS = 2000;

  static final int ITERATIONS = 100;

  static final int buyerValues[] = { 37, 17, 12 };

  static final int sellerValues[] = { 35, 16, 11 };

  static double R = 0.02;    // Recency
  static double E = 0.99;    // Experimentation
  static int K = 100;         // No. of possible different actions
  static double X = 15000;
  static double S1 = 1.0;

  static String dataFileName  = "electricity-data.csv";

  static CSVWriter dataFile;

  public static void main( String[] args ) {

    if ( args.length > 0 && "-set".equals(args[0]) ) {
      MAX_ROUNDS = Integer.valueOf(args[1]).intValue();
      R = Double.valueOf(args[2]).doubleValue();
      E = Double.valueOf(args[3]).doubleValue();
      K = Integer.valueOf(args[4]).intValue();
      X = Double.valueOf(args[5]).doubleValue();
      S1 = Double.valueOf(args[6]).doubleValue();
      dataFileName = args[7];
    }

    System.out.println("Using global parameters:");
    System.out.println("MAX_ROUNDS = " + MAX_ROUNDS);
    System.out.println("R = " + R);
    System.out.println("E = " + E);
    System.out.println("K = " + K);
    System.out.println("X = " + X);
    System.out.println("S1 = " + S1);
    System.out.println("Data File = " + dataFileName);

    try {
      dataFile = new CSVWriter(new FileOutputStream(dataFileName), 6);
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    //TODO remove this one
    //experiment( 24, 24, 10, 10);

    experiment( 6, 3, 10, 10 );
    experiment( 6, 3, 10, 20 );
    experiment( 6, 3, 10, 40 );
    experiment( 3, 3, 20, 10 );
    experiment( 3, 3, 10, 10 );
    experiment( 3, 3, 10, 20 );
    experiment( 3, 6, 40, 10 );
    experiment( 3, 6, 20, 10 );
    experiment( 3, 6, 10, 10 );
  }

  public static void experiment( int ns, int nb, int cs, int cb ) {
    System.out.println("\n*** Experiment with parameters");
    System.out.println("ns = " + ns);
    System.out.println("nb = " + nb);
    System.out.println("cs = " + cs);
    System.out.println("cb = " + cb);
    CummulativeStatCounter efficiency = new CummulativeStatCounter("efficiency");
    CummulativeStatCounter mPB = new CummulativeStatCounter("mPB");
    CummulativeStatCounter mPS = new CummulativeStatCounter("mPS");
    CummulativeStatCounter pSA = new CummulativeStatCounter("pSA");
    CummulativeStatCounter pBA = new CummulativeStatCounter("pBA");

    for( int i=0; i<ITERATIONS; i++ ) {
      ElectricityStats results = runSimulation(ns, nb, cs, cb);
      efficiency.newData(results.eA);
      mPB.newData(results.mPB);
      mPS.newData(results.mPS);
      pBA.newData(results.pBA);
      pSA.newData(results.pSA);
      //System.out.println("\nResults for iteration " + i + "\n" + results);
    }
    System.out.println("\n*** Summary results for ns = " + ns + " nb = " + nb + " cs = " + cs + " cb = " + cb + "\n");
    System.out.println(efficiency);
    System.out.println(mPB);
    System.out.println(mPS);
    System.out.println(pSA);
    System.out.println(pBA);
    dataFile.newData(efficiency.getMean());
    dataFile.newData(efficiency.getStdDev());
    dataFile.newData(mPB.getMean());
    dataFile.newData(mPB.getStdDev());
    dataFile.newData(mPS.getMean());
    dataFile.newData(mPS.getStdDev());
    dataFile.flush();
  }

  public static ElectricityStats runSimulation( int ns, int nb, int cs, int cb ) {

    RandomRobinAuction auction;
    HashMap gridGraph;
    StatsMarketDataLogger logger;
    ContinuousDoubleAuctioneer auctioneer;

    auction = new RandomRobinAuction("Electricity Auction ns:" + ns + " nb:" + nb + " cs:" + cs + " cb:" + cb);
    //auctioneer = new ElectricityAuctioneer(new SimpleGrid(ATC), auction);
    //auctioneer = new ContinuousDoubleAuctioneer(auction, 0.5);
    //auctioneer.setK(0);
    auctioneer = new DiscrimPriceCDAAuctioneer(auction, 0.5);
    //auctioneer = new AdaptiveElectricityAuctioneer(auction);
    auction.setAuctioneer(auctioneer);

    registerTraders(auction, true, ns, cs, sellerValues);
    registerTraders(auction, false, nb, cb, buyerValues);

    logger = new StatsMarketDataLogger();
    auction.setMarketDataLogger(logger);

    auction.setMaximumRounds(MAX_ROUNDS);

    auction.run();

    ElectricityStats stats = new ElectricityStats(0, 200, auction);

    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      MREElectricityTrader trader = (MREElectricityTrader) i.next();
//      int numPeaks = ((RothErevLearner) trader.getLearner()).countPeaks();
//      if ( numPeaks > 1 ) {
//        System.out.println("WARNING: No. of peaks for trader " + trader + "\n=" + numPeaks);
//      }
    }


    return stats;
  }


  public static void registerTraders( RoundRobinAuction auction,
                                      boolean areSellers, int num, int capacity,
                                      int[] values ) {
    for( int i=0; i<num; i++ ) {

      // Construct a trader for this record
      MREElectricityTrader trader =
        new MREElectricityTrader(capacity, values[i % values.length], 0, areSellers);

      // Configure the MRE algorithm
      int seed = Math.abs((int) System.currentTimeMillis());
      trader.setLearner( new NPTRothErevLearner(K, R, E, S1*X,
                                                  seed) );
      //trader.setLearner( new RandomLearner(K, seed) );


      //if ( areSellers ) {
        //trader.setStrategy(new PureSimpleStrategy(trader, 35-values[i], capacity));
      //}
      //trader.setStrategy(new PureSimpleStrategy(trader, 0, capacity));


      // Register it in the auction
      auction.register(trader);
    }
  }

}

class RandomLearner implements StimuliResponseLearner {

  MersenneTwisterFast randGenerator;

  int k;


  public RandomLearner( int k, int seed ) {
    this.k = k;
    randGenerator = new MersenneTwisterFast(seed);
  }


  public int act() {
    return randGenerator.nextInt(k);
  }

  public void reward( double r ) {
  }


}