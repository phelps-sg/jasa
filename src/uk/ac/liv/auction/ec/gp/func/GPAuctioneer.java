package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.electricity.ElectricityStats;

import uk.ac.liv.util.Debug;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.*;

import uk.ac.liv.ec.gp.func.GPGenericData;
import uk.ac.liv.ec.gp.*;

import java.util.List;
import java.util.Iterator;

/**
 * An Auctioneer whose bidding logic is evolved using genetic programming.
 *
 */

public class GPAuctioneer extends GPIndividualCtx implements Auctioneer {

  final ShoutEngine shoutEngine = new FourHeapShoutEngine();

  Shout currentShout;

  Auction auction;

  MarketQuote currentQuote;

  protected Shout clearBid, clearAsk;

  public ElectricityStats stats;

  public GPAuctioneer() {
    super();
  }

  public void setAuction( Auction auction ) {
    this.auction = auction;
  }

  public void reset() {
  }

  public void endOfRoundProcessing() {
    clear();
  }

  public MarketQuote getQuote() {
    if ( currentQuote == null ) {
      generateQuote();
    }
    return currentQuote;
  }

  public synchronized void clear() {
    List shouts = shoutEngine.getMatchedShouts();
    Iterator i = shouts.iterator();
    while ( i.hasNext() ) {
      Shout bid = (Shout) i.next();  Debug.assert( bid.isBid() );
      Shout ask = (Shout) i.next();  Debug.assert( ask.isAsk() );
      double price = determineClearingPrice(bid, ask);
      auction.clear(ask, bid.getAgent(), ask.getAgent(), price, ask.getQuantity());
    }
  }

  public double determineClearingPrice( Shout bid, Shout ask ) {
    clearBid = bid;
    clearAsk = ask;
    GPGenericData input = new GPGenericData();
    try {
      evaluateTree(0, input);
    } catch ( ArithmeticException e ) {
      System.out.println("Caught: " + e);
      //e.printStackTrace();
      return 0;
    }
    return ((GenericNumber) input.data).doubleValue();
  }

  public void generateQuote() {
    double bid = Shout.maxPrice(shoutEngine.getHighestMatchedAsk(), shoutEngine.getHighestUnmatchedBid());
    double ask = Shout.minPrice(shoutEngine.getLowestUnmatchedAsk(), shoutEngine.getLowestMatchedBid());
    currentQuote = new MarketQuote(ask, bid);
  }

  public synchronized void newShout( Shout shout ) throws IllegalShoutException {
    if ( ! shout.isValid() ) {
      throw new IllegalShoutException("Malformed shout");
    }
    if ( shout.isBid() ) {
      shoutEngine.newBid(shout);
    } else {
      shoutEngine.newAsk(shout);
    }
  }

  public void endOfAuctionProcessing() {
  }

  public Shout getCurrentShout() {
    return currentShout;
  }

  public synchronized void removeShout( Shout shout ) {
    shoutEngine.removeShout(shout);
  }

  public synchronized void printState() {
    shoutEngine.printState();
  }



}
