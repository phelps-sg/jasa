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

import uk.ac.liv.auction.stats.*;

import uk.ac.liv.util.IdAllocator;
import uk.ac.liv.util.Resetable;

import java.io.Serializable;

import java.util.Map;
import java.util.Observable;
import java.util.LinkedList;
import java.util.Iterator;


/**
 * An abstract implementation of Auction that provides basic
 * logging facilities.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AuctionImpl extends Observable
                                    implements Auction, 
                                    			Serializable,
                                    			Resetable {

  /**
   * The name of this auction.
   */
  protected String name;

  /**
   * Used to assign unique ids to each instance.
   */
  static IdAllocator idAllocator = new IdAllocator();

  /**
   * A unique id for this auction.  It's main use is in debugging.
   */
  protected long id;

  /**
   * The last shout placed in the auction.
   */
  protected Shout lastShout;

  /**
   * The last bid placed in the auction.
   */
  protected Shout lastBid;

  /**
   * The last ask placed in the auction.
   */
  protected Shout lastAsk;

  /**
   * Flag indicating whether the auction is currently closed.
   */
  protected boolean closed;


  /**
   * The plugable auction rules to use for this auction,
   * e.g. AscendingAuctioneer.
   */
  protected Auctioneer auctioneer = null;

  /**
   * The optional MarketDataLogger to log data to.
   */
  protected MarketDataLogger logger = null;

  protected LinkedList roundClosedListeners = new LinkedList();

  protected LinkedList endOfDayListeners = new LinkedList();

  protected LinkedList auctionClosedListeners = new LinkedList();

  protected LinkedList[] auctionEventListeners = {
      roundClosedListeners, endOfDayListeners, auctionClosedListeners };

  protected HistoryStatsMarketDataLogger historyStats;
  
  protected DailyStatsMarketDataLogger dailyStats;
  
  
  public AuctionImpl( String name ) {
    id = idAllocator.nextId();
    if ( name != null ) {
      this.name = name;
    } else {
      this.name = "Auction " + id;
    }    
    //initialise();
  }

  public AuctionImpl( String name, MarketDataLogger logger ) {
    this(name);
    this.logger = logger;
  }

  public AuctionImpl() {
    this(null);
  }

  protected void initialise() {
    lastShout = null;
    lastBid = null;
    lastAsk = null;
    closed = false;
  }

  public void reset() {
    initialise();
  }

  public void setAuctioneer( Auctioneer auctioneer ) {
    this.auctioneer = auctioneer;
    auctioneer.setAuction(this);
  }

  public Auctioneer getAuctioneer() {
    return auctioneer;
  }


  public boolean closed() {
    return closed;
  }

  /**
   * Close the auction.
   */
  public void close() {
    closed = true;
  }


  public Shout getLastShout() throws ShoutsNotVisibleException {
    if ( !auctioneer.shoutsVisible() ) {
      throw new ShoutsNotVisibleException();
    }
    return lastShout;
  }

  /**
   * Assign a data logger
   */
  public void setMarketDataLogger( MarketDataLogger logger ) {
    this.logger = logger;
    removeAuctionEventListener(logger);
    addAuctionEventListener(logger);
  }

  /**
   * Get the current data logger
   */
  public MarketDataLogger getMarketDataLogger() {
    return logger;
  }

  /**
   * Change the name of this auction.
   *
   * @param name The new name of the auction.
   */
  public void setName( String name ) {
    this.name = name;
  }

  public MarketQuote getQuote() {
    return auctioneer.getQuote();
  }

  public String getName() {
    return name;
  }

  public void removeShout( Shout shout ) {
    // Remove this shout and all of its children.
    for( Shout s = shout; s != null; s = s.getChild() ) {
      auctioneer.removeShout(s);
//      if ( s != shout ) {
//        ShoutPool.release(s);
//      }
    }
    shout.makeChildless();
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
    if ( shout == null ) {
      throw new IllegalShoutException("null shout");
    }
    auctioneer.newShout(shout);
    recordShout(shout);

    notifyObservers();
  }

  protected void recordShout( Shout shout ) {
    lastShout = shout;
    if ( shout.isAsk() ) {
      lastAsk = shout;
    } else {
      lastBid = shout;
    }
  }


  public void printState() {
    auctioneer.printState();
  }

  /**
   * Add a new market data logger.
   *
   * @param newLogger  The new logger to add.
   */
  public void addMarketDataLogger( MarketDataLogger newLogger ) {
    MarketDataLogger oldLogger = logger;
    setMarketDataLogger(new CombiMarketDataLogger());
    if ( oldLogger != null ) {
      ( (CombiMarketDataLogger) logger).addLogger(oldLogger);
    }
    ((CombiMarketDataLogger) logger).addLogger(newLogger);
  }

  public void addListener( LinkedList listeners, AuctionEventListener listener ) {
    assert listener != null;
    if ( !listeners.contains(listener) ) {
      listeners.add(listener);
    }
  }

  public void addEndOfDayListener( EndOfDayListener listener ) {
    addListener(endOfDayListeners, listener);
  }

  public void addRoundClosedListener( RoundClosedListener listener ) {
    addListener(roundClosedListeners, listener);
  }

  public void addAuctionClosedListener( AuctionClosedListener listener ) {
    addListener(auctionClosedListeners, listener);
  }

  public void addAuctionEventListener( AuctionEventListener listener ) {
    for( int i=0; i<auctionEventListeners.length; i++ ) {
      addListener(auctionEventListeners[i], listener);
    }
  }

  public void removeAuctionEventListener( AuctionEventListener listener ) {
    for( int i=0; i<auctionEventListeners.length; i++ ) {
     auctionEventListeners[i].remove(listener);
    }
  }

  public void informAuctionClosed() {
    Iterator i = auctionClosedListeners.iterator();
    while (i.hasNext()) {
      AuctionClosedListener listener = (AuctionClosedListener) i.next();
      listener.auctionClosed(this);
    }
  }

  public void informEndOfDay() {
    Iterator i = endOfDayListeners.iterator();
    while (i.hasNext()) {
      EndOfDayListener listener = (EndOfDayListener) i.next();
      listener.endOfDay(this);
    }
  }

  public void informRoundClosed() {
    Iterator i = roundClosedListeners.iterator();
    while (i.hasNext()) {
      RoundClosedListener listener = (RoundClosedListener) i.next();
      listener.roundClosed(this);
    }
  }

  /**
   * Return a Map of all of the variables in all of the reports configured
   * for this auction.   The Map maps report variables, represented as
   * objects of type ReportVariable onto their values.
   * 
   * @see uk.ac.liv.auction.stats.ReportVariable
   */
  public Map getResults() {
    return logger.getVariables();
  }

  public DailyStatsMarketDataLogger getDailyStats() {
    return dailyStats;
  }
  
  public void setDailyStats( DailyStatsMarketDataLogger dailyStats ) {
    this.dailyStats = dailyStats;
  }
  
  public HistoryStatsMarketDataLogger getHistoryStats() {
    return historyStats;
  }
  
  public void setHistoryStats( HistoryStatsMarketDataLogger historyStats ) {
    this.historyStats = historyStats;
  }
  
  public String toString() {
    return "(Auction id:" + id + ")";
  }


  protected void updateShoutLog( int time, Shout shout ) {
    if ( logger != null ) {
      logger.updateShoutLog(time, shout);
    }
  }

  protected void updateQuoteLog( int time, MarketQuote quote ) {
    if ( logger != null ) {
      logger.updateQuoteLog(time, getQuote());
    }
  }

  protected void updateTransPriceLog( int time, Shout ask, Shout bid,
                                       double price, int quantity ) {
    if ( logger != null ) {
      logger.updateTransPriceLog(time, ask, bid, price, quantity);
    }
  }
  

}
