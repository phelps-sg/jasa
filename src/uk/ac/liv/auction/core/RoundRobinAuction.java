package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.agent.TraderAgent;

import uk.ac.liv.util.io.CSVWriter;
import uk.ac.liv.util.IdAllocator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

/**
 * <p>
 * A class representing an auction in which RoundRobinTraders can trade by
 * placing shouts in a synchronous round-robin shedule.
 * </p>
 *
 * <p>
 * TraderAgents are notified that it is their turn to bid by invokation
 * of the requestShout() method on each agent.
 * </p>
 *
 * <p>
 * This class implements Runnable so auctions can be run as threads, e.g.:
 * </p>
 *
 * <code>
 *  Thread t = new Thread(auction);
 *  t.start();
 * </code><br>
 *
 * <p>
 * However, this class is not necessarily itself thread-safe.
 * </p>
 *
 * <p>
 * This class is designed for high-performance lightweight simulation of
 * auctions.  Developers wishing to provide asynchronous auction functionality
 * through, e.g. a web servlet, should either extend AuctionImpl or implement
 * the Auction directly directly using the existing Auctioneer classes to provide
 * the "bidding logic".
 * </p>
 *
 * @see uk.ac.liv.auction.agent.RoundRobinTrader
 *
 * @author Steve Phelps
 *
 */

public class RoundRobinAuction extends AuctionImpl
                                implements Runnable, Serializable {

  /**
   * The collection of TraderAgents currently taking part in this auction.
   */
  LinkedList activeTraders = new LinkedList();

  /**
   * The collection of idle TraderAgents
   */
  LinkedList defunctTraders;

  /**
   * The collection of all TraderAgents registered in the auction.
   */
  LinkedList registeredTraders = new LinkedList();

  /**
   * The current round.
   */
  int round;

  /**
   * The maximum number of rounds in the auction.
   * Ignored if negative.
   */
  int maximumRounds = -1;

  /**
   * The current number of traders in the auction.
   */
  int numTraders;


  /**
   * Construct a new auction in the stopped state, with no traders, no shouts,
   * and no auctioneer.
   *
   * @param name  The name of this auction.
   */
  public RoundRobinAuction( String name ) {
    super(name);
    initialise();
  }

  public RoundRobinAuction() {
    this(null);
  }

  public void clear( Shout winningShout, TraderAgent buyer,
                      TraderAgent seller, double price, int quantity ) {
    RoundRobinTrader rrBuyer = (RoundRobinTrader) buyer;
    RoundRobinTrader rrSeller = (RoundRobinTrader) seller;
    rrBuyer.informOfSeller(winningShout, rrSeller, price, quantity);
    logger.updateTransPriceLog(round, winningShout, price);
  }

  /**
   * Register a new trader in the auction.
   */
  public void register( TraderAgent trader ) {
    registeredTraders.add(trader);
    activate(trader);
  }

  /**
   * Remove a trader from the auction.
   */
  public void remove( RoundRobinTrader trader ) {
    defunctTraders.add(trader);
    if ( --numTraders == 0 ) {
      close();
    }
  }

  /**
   * Invokes the requestShout() method on each trader in the auction, giving
   * each trader the opportunity to bid in the auction.
   */
  public void requestShouts() {
    Iterator i = activeTraders.iterator();
    while ( i.hasNext() ) {
      RoundRobinTrader trader = (RoundRobinTrader) i.next();
      trader.requestShout(this);
    }
  }


  /**
   * Set the maximum number of rounds for this auction.
   * The auction will automatically close after this number
   * of rounds has been dealt.
   */
  public void setMaximumRounds( int maximumRounds ) {
    this.maximumRounds = maximumRounds;
  }

  /**
   * Return the number of traders currently active in the auction.
   */

  public int getNumberOfTraders() {
    return numTraders;
  }

  /**
   * Get the age of the auction in rounds
   */
  public int getAge() {
    return round;
  }

  /**
   * Runs the auction.
   */
  public void run() {

    if ( auctioneer == null ) {
      throw new AuctionError("No auctioneer has been assigned for auction " + name);
    }

    for( round=0; !closed(); round++ ) {

      if ( maximumRounds > 0 && round >= maximumRounds ) {
        close();
        break;
      }

      requestShouts();

      logger.updateQuoteLog(round, getQuote());

      sweepDefunctTraders();

      auctioneer.endOfRoundProcessing();

      setChanged();
      notifyObservers();  // notify, e.g. the gui console, of a state change

    }

    auctioneer.endOfAuctionProcessing();
  }

  /**
   *  Handle a new shout in the auction.
   *
   *  @param shout  The new shout in the auction.
   */
  public void newShout( Shout shout ) throws AuctionException {
    if ( closed() ) {
      throw new AuctionClosedException("Auction " + name + " is closed.");
    }
    lastShout = shout;
    logger.updateShoutLog(round, shout);
    auctioneer.newShout(shout);
    notifyObservers();
  }

  public void changeShout( Shout shout ) throws AuctionException {
    removeShout(shout);
    newShout(shout);
  }

  /**
   * Return an iterator iterating over all traders registered
   * (as opposed to actively trading) in the auction.
   */
  public Iterator getTraderIterator() {
    return registeredTraders.iterator();
  }

  public List getTraderList() {
    return registeredTraders;
  }

  /**
   * Remove defunct traders.
   */
  private void sweepDefunctTraders() {
    Iterator i = defunctTraders.iterator();
    while ( i.hasNext() ) {
      TraderAgent defunct = (TraderAgent) i.next();
      activeTraders.remove(defunct);
    }
  }

  /**
   * Restore the auction to its original state, ready for another run.
   * This method can be used to rerun simulations efficienctly without the
   * overhead of (re)constructing new auction objects.
   */
  public void reset() {

    super.reset();

    if ( auctioneer != null ) {
      auctioneer.reset();
    }

    if ( logger != null ) {
      logger.reset();
    }

    Iterator i = getTraderIterator();
    while ( i.hasNext() ) {
      RoundRobinTrader t = (RoundRobinTrader) i.next();
      t.reset();
    }
  }

  protected void initialise() {
    super.initialise();
    numTraders = 0;
    round = 0;
    defunctTraders = new LinkedList();
    activeTraders = new LinkedList();
    Iterator i = registeredTraders.iterator();
    while ( i.hasNext() ) {
      TraderAgent agent = (TraderAgent) i.next();
      activate(agent);
    }
  }

  protected void activate( TraderAgent agent ) {
    activeTraders.add(agent);
    numTraders++;
  }


}