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

import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.event.AuctionClosedEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.EndOfDayEvent;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import uk.ac.liv.auction.stats.MarketDataLogger;
import uk.ac.liv.auction.stats.MarketStats;
import uk.ac.liv.auction.stats.CombiMarketStats;

import uk.ac.liv.auction.ui.AuctionConsoleFrame;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;
import ec.util.ParamClassLoadException;

import java.util.*;

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

 * <tr><td valign=top><i>base</i><tt>.agenttype.</tt><i>n</i><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of different agent types)</td></tr>
 *
 * <tr><td valign=top><i>base</i><tt>.agenttype.</tt><i>i</i><br>
 * <font size=-1>classname, inherits uk.ac.liv.auction.agent.RoundRobinTrader</font></td>
 * <td valign=top>(the class for agent type #<i>i</i>)</td></tr>
 * 
 * </table>
 *
 *
 * @see uk.ac.liv.auction.agent.TradingAgent
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
  
  protected int age = 0;

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
   * Optional graphical console
   */
  protected AuctionConsoleFrame guiConsole = null;

  /**
   * The statistics to use
   */
  protected MarketStats marketStats = null;

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
  

  public static final String P_MAXIMUM_ROUNDS = "maximumrounds";
  public static final String P_MAXIMUM_DAYS = "maximumdays";
  public static final String P_LOGGER = "logger";
  public static final String P_AUCTIONEER = "auctioneer";
  public static final String P_NAME = "name";
  public static final String P_STATS = "stats";
  public static final String P_LENGTH_OF_DAY = "lengthofday";
  public static final String P_NUM_AGENT_TYPES = "n";
  public static final String P_NUM_AGENTS = "numagents";
  public static final String P_AGENT_TYPE = "agenttype";
  public static final String P_CONSOLE = "console";
  public static final String P_EVENTHANDLER = "eventhandler";
  
  public static final String ERROR_SHOUTSVISIBLE =
    "Auctioneer does not permit shout inspection";

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

    if ( parameters.getBoolean(base.push(P_CONSOLE), null, false) ) {
      activateGUIConsole();
    }

    try {
      AuctionEventListener eventHandler =
          (AuctionEventListener) parameters.getInstanceForParameter(base.push(P_EVENTHANDLER),
                                                            null,
                                                            AuctionEventListener.class);
      addAuctionEventListener(eventHandler);
    } catch ( ParamClassLoadException e ) {
   
    }
    
    Parameter typeParam = base.push(P_AGENT_TYPE);

    int numAgentTypes = parameters.getInt(typeParam.push("n"), null, 1);

    for( int t=0; t<numAgentTypes; t++ ) {

      Parameter typeParamT = typeParam.push(""+t);
     
      int numAgents = parameters.getInt(typeParamT.push(P_NUM_AGENTS), null, 0);

      log4jLogger.info("Configuring agent population " + t + ":\n\t" + numAgents +
                   " agents of type " + parameters.getString(typeParamT, null));

      for( int i=0; i<numAgents; i++ ) {
      	TradingAgent agent =
          (TradingAgent)
            parameters.getInstanceForParameter(typeParamT, null,
                                                TradingAgent.class);
        ((Parameterizable) agent).setup(parameters, typeParamT);
        register(agent);
      }
      
      log4jLogger.info("done.\n");
    }

    initialise();
  }


  public void clear( Shout ask, Shout bid, double price ) {
    assert price >= ask.getPrice();
    assert price <= bid.getPrice();  
    fireEvent( new TransactionExecutedEvent(this, round, ask, bid, price, ask.getQuantity()) );
    //updateTransPriceLog(round, ask, bid, price, ask.getQuantity());
    TradingAgent buyer = (TradingAgent) bid.getAgent();
    TradingAgent seller = (TradingAgent) ask.getAgent();
    assert buyer.isBuyer();
    assert seller.isSeller();
    buyer.informOfSeller(this, ask, seller, price, ask.getQuantity());
    acceptedShouts.add(ask);
    acceptedShouts.add(bid);
  }

  /**
   * Determines whether or not the given shout was matched in
   * the current round of trading.
   */
  public boolean shoutAccepted( Shout shout ) throws ShoutsNotVisibleException {
    if ( auctioneer.shoutsVisible() ) {
      return acceptedShouts.contains(shout);
    } else {
      throw new ShoutsNotVisibleException(ERROR_SHOUTSVISIBLE);
    }
  }
  
  /**
   * Determines whether or not any transactions have occured in the
   * current round of trading.
   */
  public boolean transactionsOccured() throws ShoutsNotVisibleException {
    if ( auctioneer.shoutsVisible() ) {
      return !acceptedShouts.isEmpty();
    } else {
      throw new ShoutsNotVisibleException(ERROR_SHOUTSVISIBLE);
    }
  }

  /**
   * Register a new trader in the auction.
   */
  public void register( TradingAgent trader ) {
    registeredTraders.add(trader);
    activate(trader);
  }

  /**
   * Remove a trader from the auction.
   */
  public void remove( TradingAgent trader ) {
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
      TradingAgent trader = (TradingAgent) i.next();
      trader.requestShout(this);
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
   * Get the current round number
   */
  public int getRound() {
    return round;
  }
  
  public int getAge() {
    return age;
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
   * Runs the auction.
   */
  public void run() {

    if ( auctioneer == null ) {
      throw new AuctionError("No auctioneer has been assigned for auction " + name);
    }

    begin();

    try {
      while (!closed()) {
        step();
      }

    } catch ( AuctionClosedException e ) {
      e.printStackTrace();
      throw new AuctionError(e);
    }

    end();
  }
  
  
  public void begin() {
    informAuctionOpen();
  }
  
  
  public void end() {
    informAuctionClosed();
  }
  
  
  public void step() throws AuctionClosedException {
    runSingleRound();
    checkEndOfDay();
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
      round++;  age++;
      sweepDefunctTraders();
      auctioneer.endOfRoundProcessing();
      informRoundClosed();
      lastShout = null;
    }

    setChanged();
    notifyObservers();
  }


  public void informRoundClosed() {
    fireEvent( new RoundClosedEvent(this, round) );
  }


  public void newShout( Shout shout ) throws AuctionException {
    super.newShout(shout);
    setChanged();
    notifyObservers();
    fireEvent( new ShoutPlacedEvent(this, round, shout) );
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
   * Restore the auction to its original state, ready for another run.
   * This method can be used to rerun simulations efficienctly without the
   * overhead of (re)constructing new auction objects.
   */
  public void reset() {

    super.reset();

    acceptedShouts.clear();
    defunctTraders.clear();
    activeTraders.clear();

    activeTraders.addAll(registeredTraders);
    
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
      TradingAgent t = (TradingAgent) i.next();
      t.reset();
    }
  }

  /**
   * Generate a report.
   */
  public void generateReport() {
    if ( logger != null ) {
      logger.generateReport();
    }
    if ( marketStats != null ) {
      marketStats.calculate();
      marketStats.generateReport();
    }
  }
  
  public Map getResults() {
    HashMap results = new HashMap();
    results.putAll(super.getResults());
    marketStats.calculate();
    results.putAll(marketStats.getVariables());
    return results;
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

  public void setLengthOfDay( int lengthOfDay ) {
    this.lengthOfDay = lengthOfDay;
  }

  public void setMaximumDays( int maximumDays ) {
    this.maximumDays = maximumDays;
  }

  /**
   * Remove defunct traders.
   */
  protected void sweepDefunctTraders() {
    Iterator i = defunctTraders.iterator();
    while ( i.hasNext() ) {
      TradingAgent defunct = (TradingAgent) i.next();
      activeTraders.remove(defunct);
      removeAuctionEventListener(AuctionOpenEvent.class, defunct);
      removeAuctionEventListener(AuctionClosedEvent.class, defunct);
      removeAuctionEventListener(EndOfDayEvent.class, defunct);
      removeAuctionEventListener(RoundClosedEvent.class, defunct);
    }
  }

  protected void initialise() {

    super.initialise();

    numTraders = 0;
    round = 0;
    day = 0;
    age = 0;


    numTraders = activeTraders.size();
    shoutsProcessed = false;
  }

  protected void activate( TradingAgent agent ) {
    activeTraders.add(agent);
    addAuctionEventListener(AuctionOpenEvent.class, agent);
    addAuctionEventListener(AuctionClosedEvent.class, agent);
    addAuctionEventListener(EndOfDayEvent.class, agent);
    addAuctionEventListener(RoundClosedEvent.class, agent);
    numTraders++;
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
