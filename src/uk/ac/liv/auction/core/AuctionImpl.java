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

import uk.ac.liv.auction.event.*;

import uk.ac.liv.auction.stats.DailyStatsMarketDataLogger;
import uk.ac.liv.auction.stats.HistoryStatsMarketDataLogger;
import uk.ac.liv.auction.stats.MarketDataLogger;
import uk.ac.liv.auction.stats.CombiMarketDataLogger;

import uk.ac.liv.util.IdAllocator;
import uk.ac.liv.util.Resetable;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
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
  protected MarketDataLogger marketDataLogger = null;

  protected HashMap eventListeners = new HashMap();
  
  protected HistoryStatsMarketDataLogger historyStats;
  
  protected DailyStatsMarketDataLogger dailyStats;
  
  private static final Class[] allEvents = 
  	{ RoundClosedEvent.class, AuctionOpenEvent.class, AuctionClosedEvent.class,
        EndOfDayEvent.class, TransactionExecutedEvent.class, 
        ShoutPlacedEvent.class, AgentPolledEvent.class }; 
  
  
  public AuctionImpl( String name ) {
    id = idAllocator.nextId();
    if ( name != null ) {
      this.name = name;
    } else {
      this.name = "Auction " + id;
    }    
    //initialise();
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
    this.marketDataLogger = logger;
    removeAuctionEventListener(logger);
    addAuctionEventListener(logger);
  }

  /**
   * Get the current data logger
   */
  public MarketDataLogger getMarketDataLogger() {
    return marketDataLogger;
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
    recordShout(shout);
    auctioneer.newShout(shout);
   
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
    MarketDataLogger oldLogger = marketDataLogger;
    setMarketDataLogger(new CombiMarketDataLogger());
    if ( oldLogger != null ) {
      ( (CombiMarketDataLogger) marketDataLogger).addLogger(oldLogger);
    }
    ((CombiMarketDataLogger) marketDataLogger).addLogger(newLogger);
  }

  public void addListener( LinkedList listeners, AuctionEventListener listener ) {
    assert listener != null;
    if ( !listeners.contains(listener) ) {
      listeners.add(listener);
    }
  }

  public void addAuctionEventListener( AuctionEventListener listener ) {
    for( int i=0; i<allEvents.length; i++ ) {
      addAuctionEventListener(allEvents[i], listener);
    }
  }
  
  public void removeAuctionEventListener( AuctionEventListener listener ) {
    for( int i=0; i<allEvents.length; i++ ) {
      removeAuctionEventListener(allEvents[i], listener);
    }
  }

  public void addAuctionEventListener( Class eventClass,
      								AuctionEventListener listener ) {
    LinkedList listenerList = (LinkedList) eventListeners.get(eventClass);
    if ( listenerList == null ) {
      listenerList = new LinkedList();
      eventListeners.put(eventClass, listenerList);
    }
    listenerList.add(listener);
  }

  
  public void removeAuctionEventListener( Class eventClass,
      								AuctionEventListener listener ) {
    LinkedList listenerList = (LinkedList) eventListeners.get(eventClass);
    if ( listenerList != null ) {
      listenerList.remove(listener);
    }
  }
  
  
  protected void fireEvent( AuctionEvent event ) {
    List listeners = (List) eventListeners.get(event.getClass());
    if ( listeners != null ) {
      Iterator i = listeners.iterator();
      while ( i.hasNext() ) {
        AuctionEventListener listener = (AuctionEventListener) i.next();
        listener.eventOccurred(event);
      }
    }
  }

  public void informAuctionClosed() {
    fireEvent( new AuctionClosedEvent(this) );
  }

  public void informEndOfDay() {
    fireEvent( new EndOfDayEvent(this) );
  }


  public void informAuctionOpen() {
    fireEvent( new AuctionOpenEvent(this) );
  }

  /**
   * Return a Map of all of the variables in all of the reports configured
   * for this auction.   The Map maps report variables, represented as
   * objects of type ReportVariable onto their values.
   * 
   * @see uk.ac.liv.auction.stats.ReportVariable
   */
  public Map getResults() {
    return marketDataLogger.getVariables();
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


  

}
