package uk.ac.liv.auction;

import uk.ac.liv.auction.agent.ZPTraderAgent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.ContinuousDoubleAuctioneer;

import java.util.Random;


public class SupplyRingSimulation {

  static final int NUM_TRADERS = 2000;

  public static void main( String[] args ) {

    Random rand = new Random();

    RoundRobinAuction auction = new RoundRobinAuction("SupplyRing");
    auction.setAuctioneer(new ContinuousDoubleAuctioneer(auction));

    for( int i=0; i<NUM_TRADERS; i++ ) {
      int stock = rand.nextInt(100);
      auction.register( new ZPTraderAgent(stock) );
    }

    auction.activateGUIConsole();
    auction.run();
  }
}