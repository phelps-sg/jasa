/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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
import uk.ac.liv.auction.event.AgentPolledEvent;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionEventListener;
import uk.ac.liv.auction.event.RoundClosedEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import uk.ac.liv.auction.stats.AuctionReport;

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
 * TraderAgents are notified that it is their turn to bid by invokation of the
 * requestShout() method on each agent.
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
 * <b>Parameters </b> <br>
 * </p>
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maximumrounds</tt><br>
 * <font size=-1>int >= 0 </font></td>
 * <td valign=top>(the number of auction rounds)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maximumdays</tt><br>
 * <font size=-1>int >= 0 </font></td>
 * <td valign=top>(the number of days in the auction)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.lengthofday</tt><br>
 * <font size=-1>int >= 0 </font></td>
 * <td valign=top>(the maximum number of rounds in a trading day)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.auctioneer</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.core.Auctioneer </font></td>
 * <td valign=top>(the auction protocol to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.logger</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.stats.MarketDataLogger
 * </font></td>
 * <td valign=top>(the MarketDataLogger to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.stats</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.stats.MarketStats </font>
 * </td>
 * <td valign=top>(the MarketStats to use)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.name</tt><br>
 * <font size=-1>string </font></td>
 * <td valign=top>(the name of this auction)</td>
 * <tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.agenttype.</tt> <i>n </i> <br>
 * <font size=-1>int </font></td>
 * <td valign=top>(the number of different agent types)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.agenttype.</tt> <i>i </i> <br>
 * <font size=-1>classname, inherits uk.ac.liv.auction.agent.RoundRobinTrader
 * </font></td>
 * <td valign=top>(the class for agent type # <i>i </i>)</td>
 * </tr>
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

public class RoundRobinAuction extends AuctionImpl implements Runnable,
    Serializable, Parameterizable {

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
   * Were any shouts processed (received & accepted) in the last round of
   * trading?
   */
  protected boolean shoutsProcessed;

  /**
   * Optional graphical console
   */
  protected AuctionConsoleFrame guiConsole = null;

  /**
   * The current trading day (period)
   */
  protected int day = 0;

  /**
   * The set of shouts that have been matched in the current round.
   */
  protected HashSet acceptedShouts = new HashSet();

  public static final String P_REPORT = "report";

  public static final String P_AUCTIONEER = "auctioneer";

  public static final String P_NAME = "name";

  public static final String P_STATS = "stats";

  public static final String P_NUM_AGENT_TYPES = "n";

  public static final String P_NUM_AGENTS = "numagents";

  public static final String P_AGENT_TYPE = "agenttype";

  public static final String P_CONSOLE = "console";

  public static final String P_EVENTHANDLER = "eventhandler";

  public static final String P_AUCTION_CLOSING = "closing";

  public static final String P_DAY_ENDING = "dayending";

  public static final String ERROR_SHOUTSVISIBLE = "Auctioneer does not permit shout inspection";

  static Logger logger = Logger.getLogger(RoundRobinAuction.class);

  private TimingCondition closingCondition;

  private TimingCondition dayEndingCondition;

  /**
   * Construct a new auction in the stopped state, with no traders, no shouts,
   * and no auctioneer.
   * 
   * @param name
   *          The name of this auction.
   */
  public RoundRobinAuction(String name) {
    super(name);
    initialise();
  }

  public RoundRobinAuction() {
    this(null);
  }

  public void setup(ParameterDatabase parameters, Parameter base) {

    name = parameters.getStringWithDefault(base.push(P_NAME), null, "Auction "
        + id);

    try {
      closingCondition = (TimingCondition) parameters.getInstanceForParameter(
          base.push(P_AUCTION_CLOSING), null, AuctionClosingCondition.class);
      closingCondition.setAuction(this);
    } catch (ParamClassLoadException e) {
      closingCondition = null;
    }

    if (closingCondition != null && closingCondition instanceof Parameterizable) {
      ((Parameterizable) closingCondition).setup(parameters, base
          .push(P_AUCTION_CLOSING));
    }
    
    try {
      dayEndingCondition = (TimingCondition) parameters
          .getInstanceForParameter(base.push(P_DAY_ENDING), null,
              DayEndingCondition.class);
      dayEndingCondition.setAuction(this);
    } catch (ParamClassLoadException e) {
      dayEndingCondition = null;
    }
    
    if (dayEndingCondition != null
        && dayEndingCondition instanceof Parameterizable) {
      ((Parameterizable) dayEndingCondition).setup(parameters, base
          .push(P_DAY_ENDING));
    }
    
    try {
      report = (AuctionReport) parameters.getInstanceForParameter(base
          .push(P_REPORT), null, AuctionReport.class);
      report.setAuction(this);
      addAuctionEventListener(report);
    } catch (ParamClassLoadException e) {
      report = null;
    }

    if (report != null && report instanceof Parameterizable) {
      ((Parameterizable) report).setup(parameters, base.push(P_REPORT));
    }

    Auctioneer auctioneer = (Auctioneer) parameters.getInstanceForParameter(
        base.push(P_AUCTIONEER), null, Auctioneer.class);
    ((Parameterizable) auctioneer).setup(parameters, base.push(P_AUCTIONEER));
    setAuctioneer(auctioneer);
    addAuctionEventListener(auctioneer);

    if (parameters.getBoolean(base.push(P_CONSOLE), null, false)) {
      activateGUIConsole();
    }

    try {
      AuctionEventListener eventHandler = (AuctionEventListener) parameters
          .getInstanceForParameter(base.push(P_EVENTHANDLER), null,
              AuctionEventListener.class);
      addAuctionEventListener(eventHandler);
    } catch (ParamClassLoadException e) {
    }

    Parameter typeParam = base.push(P_AGENT_TYPE);

    int numAgentTypes = parameters.getInt(typeParam.push("n"), null, 1);

    for (int t = 0; t < numAgentTypes; t++) {

      Parameter typeParamT = typeParam.push("" + t);

      int numAgents = parameters.getInt(typeParamT.push(P_NUM_AGENTS), null, 0);

      logger.info("Configuring agent population " + t + ":\n\t" + numAgents
          + " agents of type " + parameters.getString(typeParamT, null));

      for (int i = 0; i < numAgents; i++) {
        TradingAgent agent = (TradingAgent) parameters.getInstanceForParameter(
            typeParamT, null, TradingAgent.class);
        ((Parameterizable) agent).setup(parameters, typeParamT);
        register(agent);
      }

      logger.info("done.\n");
    }

    initialise();
  }

  public void clear(Shout ask, Shout bid, double trPrice) {

    TradingAgent buyer = (TradingAgent) bid.getAgent();
    TradingAgent seller = (TradingAgent) ask.getAgent();

    assert buyer.isBuyer();
    assert seller.isSeller();
    assert trPrice >= ask.getPrice();
    assert trPrice <= bid.getPrice();

    TransactionExecutedEvent transactionEvent = new TransactionExecutedEvent(
        this, round, ask, bid, trPrice, ask.getQuantity());
    fireEvent(transactionEvent);

    buyer.informOfSeller(this, ask, seller, trPrice, ask.getQuantity());

    acceptedShouts.add(ask);
    acceptedShouts.add(bid);
  }

  /**
   * Determines whether or not the given shout was matched in the current round
   * of trading.
   */
  public boolean shoutAccepted(Shout shout) throws ShoutsNotVisibleException {
    if (auctioneer.shoutsVisible()) {
      return acceptedShouts.contains(shout);
    } else {
      throw new ShoutsNotVisibleException(ERROR_SHOUTSVISIBLE);
    }
  }

  /**
   * Determines whether or not any transactions have occured in the current
   * round of trading.
   */
  public boolean transactionsOccured() throws ShoutsNotVisibleException {
    if (auctioneer.shoutsVisible()) {
      return !acceptedShouts.isEmpty();
    } else {
      throw new ShoutsNotVisibleException(ERROR_SHOUTSVISIBLE);
    }
  }

  /**
   * Register a new trader in the auction.
   */
  public void register(TradingAgent trader) {
    registeredTraders.add(trader);
    activate(trader);
  }

  /**
   * Remove a trader from the auction.
   */
  public void remove(TradingAgent trader) {
    if (!defunctTraders.contains(trader)) {
      defunctTraders.add(trader);
    }
  }

  /**
   * Invokes the requestShout() method on each trader in the auction, giving
   * each trader the opportunity to bid in the auction.
   */
  public void requestShouts() {
    Iterator i = activeTraders.iterator();
    while (i.hasNext()) {
      TradingAgent trader = (TradingAgent) i.next();
      requestShout(trader);
    }
  }

  public void requestShout(TradingAgent trader) {
    trader.requestShout(this);
    fireEvent(new AgentPolledEvent(this, getRound(), trader));
  }

  /**
   * Return the number of traders currently active in the auction.
   */
  public int getNumberOfTraders() {
    return activeTraders.size();
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

    if (auctioneer == null) {
      throw new AuctionError("No auctioneer has been assigned for auction "
          + name);
    }

    begin();

    try {
      while (!closed) {
        step();
      }

    } catch (AuctionClosedException e) {
      throw new AuctionError(e);
    }

    end();
  }

  public void begin() {
    informAuctionOpen();
  }

  public void end() {
    informAuctionClosed();
    sweepDefunctTraders();
  }

  public void step() throws AuctionClosedException {
    runSingleRound();
    checkEndOfDay();
  }

  public void runSingleRound() throws AuctionClosedException {

    if (closed()) {
      throw new AuctionClosedException("Auction " + name + " is closed.");
    }

    if (closingCondition != null && closingCondition.eval()) {
      close();
    } else {
      shoutsProcessed = false;
      acceptedShouts.clear();
      requestShouts();
      round++;
      age++;
      sweepDefunctTraders();
      auctioneer.endOfRoundProcessing();
      informRoundClosed();
      lastShout = null;
    }

    setChanged();
    notifyObservers();
  }

  public void informRoundClosed() {
    fireEvent(new RoundClosedEvent(this, round));
  }

  public void newShout(Shout shout) throws AuctionException {
    fireEvent(new ShoutPlacedEvent(this, round, shout));
    super.newShout(shout);
    setChanged();
    notifyObservers();
    shoutsProcessed = true;
  }

  public void changeShout(Shout shout) throws AuctionException {
    removeShout(shout);
    newShout(shout);
  }

  /**
   * Return an iterator iterating over all traders registered (as opposed to
   * actively trading) in the auction.
   */
  public Iterator getTraderIterator() {
    return registeredTraders.iterator();
  }

  public Iterator getActiveTraderIterator() {
    return activeTraders.iterator();
  }

  /**
   * Restore the auction to its original state, ready for another run. This
   * method can be used to rerun simulations efficienctly without the overhead
   * of (re)constructing new auction objects.
   */
  public void reset() {

    super.reset();

    acceptedShouts.clear();
    defunctTraders.clear();
    activeTraders.clear();

    activeTraders.addAll(registeredTraders);

    if (auctioneer != null) {
      ((Resetable) auctioneer).reset();
      addAuctionEventListener(auctioneer);
    }

    if (report != null) {
      if (report instanceof Resetable) {
        ((Resetable) report).reset();
      }
      addAuctionEventListener(report);
    }

    if (guiConsole != null) {
      guiConsole.reset();
    }

    Iterator i = getTraderIterator();
    while (i.hasNext()) {
      TradingAgent t = (TradingAgent) i.next();
      addAuctionEventListener(t);
      t.reset();
    }
  }

  /**
   * Generate a report.
   */
  public void generateReport() {
    if (report != null) {
      report.produceUserOutput();
    }
  }

  /**
   * Activate a graphical console for monitoring and controlling the progress of
   * the auction. Activation of the console may significantly impact the time
   * performance of the auction.
   */
  public void activateGUIConsole() {
    if (guiConsole == null) {
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

  public void setConsole(AuctionConsoleFrame console) {
    this.guiConsole = console;
  }

  public AuctionConsoleFrame getConsole() {
    return guiConsole;
  }

  /**
   * Remove defunct traders.
   */
  protected void sweepDefunctTraders() {
    Iterator i = defunctTraders.iterator();
    while (i.hasNext()) {
      TradingAgent defunct = (TradingAgent) i.next();
      activeTraders.remove(defunct);
      //removeAuctionEventListener(defunct);
    }
  }

  protected void initialise() {

    super.initialise();

    round = 0;
    day = 0;
    age = 0;

    shoutsProcessed = false;
  }

  protected void activate(TradingAgent agent) {
    activeTraders.add(agent);
    addAuctionEventListener(agent);
  }

  protected void checkEndOfDay() {
    if (dayEndingCondition != null && dayEndingCondition.eval())
      endDay();
  }

  /**
   * Terminate the current trading period (day)
   */
  protected void endDay() {
    logger.debug("endDay()");
    day++;
    //logger.debug("day = " + day + " of " + getMaximumDays());
    round = 0;
    informEndOfDay();
    auctioneer.endOfDayProcessing();
  }

  // TODO: temporary
  public boolean shoutsProcessed() {
    return shoutsProcessed;
  }

  public int getRemainingTime() {
    TimingCondition cond = getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

    if (cond != null) {
      return ((MaxRoundsAuctionClosingCondition) cond).getRemainingRounds();
    } else {
      cond = getDayEndingCondition(MaxRoundsDayEndingCondition.class);

      if (cond != null) {
        return ((MaxRoundsDayEndingCondition) cond).getRemainingRounds();
      } else {
        throw new AuctionError(
            getClass()
                + " requires a TimingCondition knowing remaining time in the auction to be configured");
      }
    }
  }

  public int getLengthOfDay() {
    TimingCondition cond = getDayEndingCondition(MaxRoundsDayEndingCondition.class);

    if (cond != null) {
      return ((MaxRoundsDayEndingCondition) cond).getLengthOfDay();
    } else {
      return -1;
    }
  }

  public void setLengthOfDay(int lengthOfDay) {
    MaxRoundsDayEndingCondition cond = new MaxRoundsDayEndingCondition(this);
    cond.setLengthOfDay(lengthOfDay);
    setDayEndingCondition(cond);
  }

  public int getMaximumDays() {
    TimingCondition cond = getAuctionClosingCondition(MaxDaysAuctionClosingCondition.class);

    if (cond != null) {
      return ((MaxDaysAuctionClosingCondition) cond).getMaximumDays();
    } else {
      return -1;
    }
  }

  public void setMaximumRounds(int maximumRounds) {
    MaxRoundsAuctionClosingCondition cond = new MaxRoundsAuctionClosingCondition(
        this);
    cond.setMaximumRounds(maximumRounds);
    setAuctionClosingCondition(cond);
  }

  public int getMaximumRounds() {
    TimingCondition cond = getAuctionClosingCondition(MaxRoundsAuctionClosingCondition.class);

    if (cond != null) {
      return ((MaxRoundsAuctionClosingCondition) cond).getMaximumRounds();
    } else {
      return -1;
    }
  }

  public void setMaximumDays(int maximumDays) {
    MaxDaysAuctionClosingCondition cond = new MaxDaysAuctionClosingCondition(
        this);
    cond.setMaximumDays(maximumDays);
    setAuctionClosingCondition(cond);
  }

  private TimingCondition getTimingCondition(TimingCondition cond,
      Class conditionClass) {
    if (cond != null) {
      if (cond.getClass().equals(conditionClass)) {
        return cond;
      } else if (cond instanceof CombiTimingCondition) {
        Iterator i = ((CombiTimingCondition) cond).conditionIterator();
        while (i.hasNext()) {
          TimingCondition c = (TimingCondition) i.next();
          if (c.getClass().equals(conditionClass)) {
            return c;
          }
        }
      }
    }

    return null;
  }

  public TimingCondition getAuctionClosingCondition(Class conditionClass) {
    return getTimingCondition(closingCondition, conditionClass);
  }

  public TimingCondition getDayEndingCondition(Class conditionClass) {
    return getTimingCondition(dayEndingCondition, conditionClass);
  }

  public void setAuctionClosingCondition(TimingCondition cond) {
    assert (cond instanceof AuctionClosingCondition);
    closingCondition = cond;
  }

  public void setDayEndingCondition(TimingCondition cond) {
    assert (cond instanceof DayEndingCondition);
    dayEndingCondition = cond;
  }

  protected void fireEvent( AuctionEvent event ) {
    super.fireEvent(event);
    getAuctioneer().eventOccurred(event);
  }
}