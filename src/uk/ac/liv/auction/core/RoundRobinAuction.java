/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.auction.stats.MarketDataLogger;
import uk.ac.liv.auction.stats.MarketStats;
import uk.ac.liv.auction.stats.DailyStatsMarketDataLogger;
import uk.ac.liv.auction.stats.HistoryStatsMarketDataLogger;
import uk.ac.liv.auction.stats.CombiMarketStats;

import uk.ac.liv.auction.ui.AuctionConsoleFrame;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.CummulativeStatCounter;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import ec.util.ParamClassLoadException;

import java.util.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

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
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.maximumrounds</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the number of auction rounds)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.maximumdays</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the number of days in the auction)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.lengthofday</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the maximum number of rounds in a trading day)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.auctioneer</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.core.Auctioneer</font></td>
 * <td valign=top>(the auction protocol to use)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.logger</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.stats.MarketDataLogger</font></td>
 * <td valign=top>(the MarketDataLogger to use)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.stats</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.stats.MarketStats</font></td>
 * <td valign=top>(the MarketStats to use)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.name</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top>(the name of this auction)</td><tr>

 * </table>
 *
 *
 * @see uk.ac.liv.auction.agent.RoundRobinTrader
 *
 * @author Steve Phelps
 * @version $Revision$
 *
 */

public class RoundRobinAuction extends AuctionImpl
  implements Runnable, Serializable, Parameterizable {

  /**
   * The collection of TraderAgents currently taking part in this auction.
   */
  protected LinkedList activeTraders = new LinkedList();

  /**
   * The collection of idle TraderAgents
   */
  protected LinkedList defunctTraders = new LinkedList();

  /**
   * The collection of all TraderAgents registered in the auction.
   */
  protected LinkedList registeredTraders = new LinkedList();

  /**
   * The current round.
   */
  protected int round;

  /**
   * The maximum number of rounds in the auction.
   * Ignored if negative.
   */
  protected int maximumRounds = -1;

  /**
   * The current number of traders in the auction.
   */
  protected int numTraders;

  /**
   * Were any shouts processed (received & accepted) in the last round of
   * trading?
   */
  protected boolean shoutsProcessed;

  /**
   * Flag indicating whether the auction is currently running.
   */
  protected boolean isRunning;

  /**
   * Optional graphical console
   */
  protected AuctionConsoleFrame guiConsole = null;

  /**
   * The statistics to use
   */
  protected MarketStats marketStats = null;

  /**
   * True if the auction is currently paused
   */
  protected boolean paused;

  /**
   * True if the auction about to be paused
   */
  protected boolean pausePending;

  /**
   * The maximum length in rounds of a trading day
   */
  protected int lengthOfDay = -1;

  /**
   * The current trading day (period)
   */
  protected int day = 0;

  /**
   * The maximum number of trading days before the auction closes
   */
  protected int maximumDays = -1;

  /**
   * The set of shouts that have been matched in the current round.
   */
  protected HashSet acceptedShouts = new HashSet();

  /**
   * The optional logger used to calculate statistics for the previous
   * day's trading.
   *
   * @see RoundRobinAuction#getPreviousDayTransPriceStats()
   */
  protected DailyStatsMarketDataLogger dailyStats = null;

  /**
   * The optional logger used to calculate the historical statistics
   * returned by the getNumberOfBids() and getNumberOfAsks() methods.
   *
   * @see RoundRobinAuction#getNumberOfBids(double, boolean)
   * @see RoundRobinAuction#getNumberOfAsks(double, boolean)
   */
  protected HistoryStatsMarketDataLogger historyStats = null;


  public static final String P_MAXIMUM_ROUNDS = "maximumrounds";
  public static final String P_MAXIMUM_DAYS = "maximumdays";
  public static final String P_LOGGER = "logger";
  public static final String P_AUCTIONEER = "auctioneer";
  public static final String P_NAME = "name";
  public static final String P_STATS = "stats";
  public static final String P_LENGTH_OF_DAY = "lengthofday";

  static Logger log4jLogger = Logger.getLogger(RoundRobinAuction.class);

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

    name =
        parameters.getStringWithDefault(base.push(P_NAME), null, "Auction " + id);

    maximumRounds = parameters.getIntWithDefault(base.push(P_MAXIMUM_ROUNDS),
                                                  null, -1);

    maximumDays = parameters.getIntWithDefault(base.push(P_MAXIMUM_DAYS),
                                                null, -1);

    lengthOfDay = parameters.getIntWithDefault(base.push(P_LENGTH_OF_DAY),
                                                null, -1);

    try {
      logger =
          (MarketDataLogger) parameters.getInstanceForParameter(base.push(P_LOGGER),
                                                                 null,
                                                                 MarketDataLogger.class);
      logger.setAuction(this);
      addAuctionEventListener(logger);

    } catch ( ParamClassLoadException e ) {
      logger = null;
    }

    if ( logger != null && logger instanceof Parameterizable ) {
      ((Parameterizable) logger).setup(parameters, base.push(P_LOGGER));
    }

    try {
      marketStats =
          (MarketStats) parameters.getInstanceForParameter(base.push(P_STATS),
                                                            null,
                                                            MarketStats.class);
      marketStats.setAuction(this);
    } catch ( ParamClassLoadException e ) {
      marketStats = null;
    }

    if ( marketStats != null && marketStats instanceof Parameterizable ) {
      ((Parameterizable) marketStats).setup(parameters, base.push(P_STATS));
    }

    Auctioneer auctioneer =
      (Auctioneer) parameters.getInstanceForParameter(base.push(P_AUCTIONEER),
                                                      null, Auctioneer.class);
    ((Parameterizable) auctioneer).setup(parameters, base.push(P_AUCTIONEER));
    setAuctioneer(auctioneer);

    initialise();
  }


  public void clear( Shout ask, Shout bid, double price ) {
    updateTransPriceLog(round, ask, bid, price, ask.getQuantity());
    RoundRobinTrader buyer = (RoundRobinTrader) bid.getAgent();
    RoundRobinTrader seller = (RoundRobinTrader) ask.getAgent();
    buyer.informOfSeller(this, ask, seller, price, ask.getQuantity());
    acceptedShouts.add(ask);
    acceptedShouts.add(bid);
  }

  /**
   * Determines whether or not the given shout was matched in
   * the current round of trading.
   */
  public boolean shoutAccepted( Shout shout ) {
    return acceptedShouts.contains(shout);
  }

  /**
   * Register a new trader in the auction.
   */
  public void register( TraderAgent trader ) {
    registeredTraders.add(trader);
    activate((RoundRobinTrader) trader);
  }

  /**
   * Remove a trader from the auction.
   */
  public void remove( RoundRobinTrader trader ) {
    if ( !defunctTraders.contains(trader) ) {
      defunctTraders.add(trader);
      if ( --numTraders == 0 ) {
        close();
      }
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


  public void informAuctionOpen() {
     Iterator i = activeTraders.iterator();
     while ( i.hasNext() ) {
       RoundRobinTrader trader = (RoundRobinTrader) i.next();
       trader.auctionOpen(this);
     }
   }


  /**
   * Set the maximum number of rounds for this auction.
   * The auction will automatically close after this number
   * of rounds has been dealt.
   *
   * @param maximumRounds The maximum number of roudns for this auction.
   */
  public void setMaximumRounds( int maximumRounds ) {
    this.maximumRounds = maximumRounds;
  }

  /**
   * Return the maximum number of rounds for this auction.
   */
  public int getMaximumRounds() {
    return maximumRounds;
  }

  public int getLengthOfDay() {
    return lengthOfDay;
  }

  public int getMaximumDays() {
    return maximumDays;
  }

  /**
   * Return the number of traders currently active in the auction.
   */
  public int getNumberOfTraders() {
    return numTraders;
  }

  /**
   * Return the total number of traders registered in the auction.
   */
  public int getNumberOfRegisteredTraders() {
    return registeredTraders.size();
  }

  /**
   * Get the age of the auction in rounds
   */
  public int getAge() {
    return round;
  }

  public int getDay() {
    return day;
  }


  public int getRemainingTime() {
    if ( lengthOfDay > 0 ) {
      return lengthOfDay-round;
    } else if ( maximumRounds > 0 ) {
      return maximumRounds-round;
    } else {
      return -1;
    }
  }

  /**
   * Get the last bid placed in the auction.
   */
  public Shout getLastBid() throws ShoutsNotVisibleException {
    return lastBid;
  }

  /**
   * Get the last ask placed in the auction.
   */
  public Shout getLastAsk() throws ShoutsNotVisibleException {
    return lastAsk;
  }

  /**
   * Fetch statistics on the previous day's transaction price.
   * The auction must be configured with a DailyStatsMarketDataLogger
   * in order for this method to succeed.
   *
   * @throws DataUnavailableException  Thrown if the auction does not have
   *                                   a DailyStatsMarketDataLogger configured.
   */
  public CummulativeStatCounter getPreviousDayTransPriceStats()
      throws DataUnavailableException {

    if ( dailyStats == null ) {
      throw new DataUnavailableException("The auction must be configured " +
                               "with a DailyStatsMarketDataLogger in order " +
                               "to retrieve previous day's statistics");
    }

    if ( day == 0 ) {
      return null;
    } else {
      return dailyStats.getTransPriceStats(day - 1);
    }
  }

  protected void checkHistoryStats() throws DataUnavailableException {
    if (historyStats == null) {
      throw new DataUnavailableException("The auction must be configured " +
                                         "with a HistoryStatsMarketDataLogger " +
          "in order to retrieve historical stats");
    }
  }

  /**
   * Calculate the number of unaccepted asks >= price.
   * If accepted is true then count the number of accepted asks.
   * If price is negative then count the number of asks < price.
   *
   * @throws DataUnavailableException If a HistoryStatsMarketDataLogger
   * is not configured.
   */
  public int getNumberOfAsks( double price, boolean accepted )
      throws DataUnavailableException {
    checkHistoryStats();
    return historyStats.getNumberOfAsks(price, accepted);
  }

  /**
    * Calculate the number of unaccepted bids >= price.
    * If accepted is true then count the number of accepted bids.
    * If price is negative then count the number of bids < price.
    *
    * @throws DataUnavailableException If a HistoryStatsMarketDataLogger
    * is not configured.
    */

  public int getNumberOfBids( double price, boolean accepted )
      throws DataUnavailableException {
    checkHistoryStats();
    return historyStats.getNumberOfBids(price, accepted);
  }

  /**
   * Runs the auction.
   */
  public void run() {

    if ( auctioneer == null ) {
      throw new AuctionError("No auctioneer has been assigned for auction " + name);
    }

    isRunning = true;

    informAuctionOpen();

    try {
      while (!closed()) {
        runSingleRound();
        checkEndOfDay();
        checkPaused();
      }

    } catch ( AuctionClosedException e ) {
      e.printStackTrace();
      throw new AuctionError(e);
    }

    informAuctionClosed();

    isRunning = false;
  }


  public void runSingleRound() throws AuctionClosedException {

    if ( closed() ) {
      throw new AuctionClosedException("Auction " + name + " is closed.");
    }

    if ( maximumRounds > 0 && round >= maximumRounds ) {
      close();
    } else {
      shoutsProcessed = false;
      acceptedShouts.clear();
      requestShouts();
      updateQuoteLog(round, getQuote());
      round++;
      sweepDefunctTraders();
      auctioneer.endOfRoundProcessing();
      informRoundClosed();
    }

    setChanged();
    notifyObservers();
  }


  public void close() {
    super.close();
  }


  public void newShout( Shout shout ) throws AuctionException {
    super.newShout(shout);
    setChanged();
    notifyObservers();
    updateShoutLog(round, shout);
    shoutsProcessed = true;
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

  /**
   * Returns true if no bidding activity occured in the latest auction round.
   */
  public boolean isQuiescent() {
    return !shoutsProcessed;
  }

  /**
   * Returns true if the auction is still running
   */
  public boolean isRunning() {
    return isRunning;
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

    if ( logger != null && logger instanceof Resetable ) {
      ((Resetable) logger).reset();
    }

    if ( marketStats != null && marketStats instanceof Resetable ) {
      ((Resetable) marketStats).reset();
    }

    if ( guiConsole != null ) {
      guiConsole.reset();
    }

    Iterator i = getTraderIterator();
    while ( i.hasNext() ) {
      RoundRobinTrader t = (RoundRobinTrader) i.next();
      t.reset();
    }
  }

  /**
   * Generate a report.
   */
  public void generateReport() {
    if ( logger != null ) {
      logger.finalReport();
    }
    if ( marketStats != null ) {
      marketStats.calculate();
      marketStats.generateReport();
    }
  }

  public void addMarketStats( MarketStats newStats ) {
    MarketStats oldStats = marketStats;
    marketStats = new CombiMarketStats();
    if ( oldStats != null ) {
      ( (CombiMarketStats) marketStats).addStats(oldStats);
    }
    ((CombiMarketStats) marketStats).addStats(newStats);
  }

  /**
   * Activate a graphical console for monitoring and controlling
   * the progress of the auction.  Activation of the console
   * may significantly impact the time performance of the auction.
   */
  public void activateGUIConsole() {
    if ( guiConsole == null ) {
      guiConsole = new AuctionConsoleFrame(this, name);
    }
    guiConsole.activate();
    // Add the console as an observer so that it will be informed
    // of state changes when we call notifyObservers().
    addObserver(guiConsole);
  }

  /**
   * Deactivate the graphical console.
   */
  public void deactivateGUIConsole() {
    guiConsole.deactivate();
    deleteObserver(guiConsole);
    guiConsole = null;
  }

  public void setConsole( AuctionConsoleFrame console ) {
    this.guiConsole = console;
  }

  public AuctionConsoleFrame getConsole() {
    return guiConsole;
  }

  /**
   * Pause the running of the auction at the end of the current round.
   *
   * @throws AuctionPauseException
   */
  public void pause() throws AuctionClosedException {
    if ( closed ) {
      throw new AuctionClosedException("Cannot pause closed auction");
    }
    pausePending = true;
  }

  /**
   * Resume running of the auction after it has been paused.
   */
  public void resume() {
    pausePending = false;
  }

  /**
   * Returns true if the auction is currently paused.
   */
  public boolean isPaused() {
    return paused;
  }

  public void setLengthOfDay( int lengthOfDay ) {
    this.lengthOfDay = lengthOfDay;
  }

  public void setMaximumDays( int maximumDays ) {
    this.maximumDays = maximumDays;
  }

  public void setDailyStats( DailyStatsMarketDataLogger dailyStats ) {
    this.dailyStats = dailyStats;
  }

  public void setHistoryStats( HistoryStatsMarketDataLogger historyStats ) {
    this.historyStats = historyStats;
  }

  /**
   * Remove defunct traders.
   */
  protected void sweepDefunctTraders() {
    Iterator i = defunctTraders.iterator();
    while ( i.hasNext() ) {
      TraderAgent defunct = (TraderAgent) i.next();
      activeTraders.remove(defunct);
      removeAuctionEventListener((RoundRobinTrader) defunct);
    }
  }

  protected void initialise() {

    super.initialise();

    numTraders = 0;
    round = 0;
    day = 0;

    acceptedShouts.clear();
    defunctTraders.clear();
    activeTraders.clear();
    for( int i=0; i<auctionEventListeners.length; i++ ) {
      auctionEventListeners[i].clear();
    }

    activeTraders.addAll(registeredTraders);
    for( int i=0; i<auctionEventListeners.length; i++ ) {
      auctionEventListeners[i].addAll(registeredTraders);
    }

    if ( logger != null ) {
      addAuctionEventListener(logger);
    }

    numTraders = activeTraders.size();
    shoutsProcessed = false;
    paused = false;
    pausePending = false;
    isRunning = false;
  }

  protected void activate( RoundRobinTrader agent ) {
    activeTraders.add(agent);
    addAuctionEventListener(agent);
    numTraders++;
  }

  /**
   * If a pause request is pending then loop until a resume
   * request is received.
   */
  protected void checkPaused() {
    while ( pausePending ) {
      paused = true;
    }
    paused = false;
  }

  /**
   * Terminate the current trading day (period) if the auction is quiescent
   * or the maximum time allowed for a period has expired.
   */
  protected void checkEndOfDay() {
    if ( isQuiescent() ) {
      log4jLogger.debug("Auction quiescent - ending day");
      endDay();
    } else {
      if (lengthOfDay > 0) {
        if ( round >= lengthOfDay ) {
          endDay();
        }
      }
    }
  }

  /**
   * Terminate the current trading period (day)
   */
  protected void endDay() {
    log4jLogger.debug("endDay()");
    informEndOfDay();
    auctioneer.endOfDayProcessing();
    day++;
    log4jLogger.debug("new day = " + day + " of " + maximumDays);
    round = 0;
    if ( day >= maximumDays ) {
      log4jLogger.debug("exceeded maximum days- closing auction");
      close();
    }
  }




}
