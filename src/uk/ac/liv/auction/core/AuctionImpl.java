/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import java.io.PrintStream;

import java.util.Observable;


/**
 * An abstract implementation of Auction that provides basic
 * logging facilities.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AuctionImpl extends Observable
                                    implements Auction, Resetable {

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
   * PrintStream for log output.
   */
  protected PrintStream logOut = System.out;

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


  public AuctionImpl( String name, MarketDataLogger logger ) {
    id = idAllocator.nextId();
    if ( name != null ) {
      this.name = name;
    } else {
      this.name = "Auction " + id;
    }
    this.logger = logger;
    //initialise();
  }

  public AuctionImpl( String name ) {
    this(name, new CSVMarketDataLogger());
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
    auctioneer.endOfAuctionProcessing();
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
  }

  /**
   * Get the current data logger
   */
  public MarketDataLogger getMarketDataLogger() {
    return logger;
  }

  /**
   * Assign a PrintStream for generic logging
   */
  public void setLogOutput( PrintStream logOut ) {
    this.logOut = logOut;
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
    if ( lastShout == null ) {
      lastShout = new Shout();
    }
    lastShout.copyFrom(shout);
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
    logger = new CombiMarketDataLogger();
    ((CombiMarketDataLogger) logger).addLogger(oldLogger);
    ((CombiMarketDataLogger) logger).addLogger(newLogger);
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

  protected void updateTransPriceLog( int time, Shout winningShout,
                                       double price, int quantity ) {
    if ( logger != null ) {
      logger.updateTransPriceLog(time, winningShout, price, quantity);
    }
  }

}
