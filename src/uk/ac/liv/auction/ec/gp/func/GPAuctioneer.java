package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Debug;

import ec.gp.*;
import ec.*;

import uk.ac.liv.util.*;

import uk.ac.liv.ec.gp.func.GPNumberData;

import java.util.List;
import java.util.Iterator;

/**
 * An Auctioneer whose bidding logic is evolved using genetic programming.
 *
 */

public class GPAuctioneer extends GPIndividual implements Auctioneer {

  final ShoutEngine shoutEngine = new FourHeapShoutEngine();

  Shout currentShout;

  Auction auction;

  MarketQuote currentQuote;

  // Why doesn't ECJ wrap this up in a context object?!
  EvolutionState contextState;
  int contextThread;
  ADFStack contextStack;
  Problem contextProblem;

  public GPAuctioneer() {
    super();
  }

  public void setAuction( Auction auction ) {
    this.auction = auction;
  }

  public void reset() {
  }

  public void setGPContext( EvolutionState state, int thread, ADFStack stack,
                        Problem problem ) {
    contextState = state;
    contextThread = thread;
    contextStack = stack;
    contextProblem = problem;
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
    GPNumberData input = new GPNumberData();
    trees[0].child.eval(contextState, contextThread, input, contextStack, this, contextProblem);
    return input.data.doubleValue();
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

class GPDoubleData extends GPData {

  public double data;

  public GPData copyTo(GPData parm1) {
    ((GPDoubleData) parm1).data = this.data;
    return this;
  }
}