/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.agent.TraderAgent;

import uk.ac.liv.util.io.CSVWriter;
import uk.ac.liv.util.IdAllocator;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

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
  implements Runnable, Serializable, Parameterizable {

  /**
   * The collection of TraderAgents currently taking part in this auction.
   */
  LinkedList activeTraders = new LinkedList();

  /**
   * The collection of idle TraderAgents
   */
  LinkedList defunctTraders = new LinkedList();

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



  static final String P_MAXIMUM_ROUNDS = "maximumrounds";


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

  public void setup( ParameterDatabase parameters, Parameter base ) {
    maximumRounds = parameters.getIntWithDefault(base.push(P_MAXIMUM_ROUNDS), null, -1);
    initialise();
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
   * Get the last bid placed in the auction.
   */
  public Shout getLastBid() {
    return lastBid;
  }

  /**
   * Get the last ask placed in the auction.
   */
  public Shout getLastAsk() {
    return lastAsk;
  }

  /**
   * Runs the auction.
   */
  public void run() {

    if ( auctioneer == null ) {
      throw new AuctionError("No auctioneer has been assigned for auction " + name);
    }

    try {
      while (!closed()) {
        runSingleRound();
      }
    } catch ( AuctionClosedException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }

  }


  public void runSingleRound() throws AuctionClosedException {

    if ( closed() ) {
      throw new AuctionClosedException("Auction " + name + "is closed.");
    }

    if ( maximumRounds > 0 && round >= maximumRounds ) {
      close();
    } else {
      requestShouts();
      logger.updateQuoteLog(round++, getQuote());
      sweepDefunctTraders();
      auctioneer.endOfRoundProcessing();
    }

    setChanged();
    notifyObservers();
  }


  public void newShout( Shout shout ) throws AuctionException {
    super.newShout(shout);
    logger.updateShoutLog(round, shout);
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
      ((Resetable) auctioneer).reset();
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
    defunctTraders.clear();
    activeTraders.clear();
    activeTraders.addAll(registeredTraders);
    numTraders = activeTraders.size();
  }

  protected void activate( TraderAgent agent ) {
    activeTraders.add(agent);
    numTraders++;
  }


}
