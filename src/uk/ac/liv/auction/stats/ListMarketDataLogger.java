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

package uk.ac.liv.auction.stats;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.auction.core.*;

/**
 * A utility class for helping auctions to record market data in List
 * collections.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ListMarketDataLogger extends AbstractMarketDataLogger {

  protected List quoteLog = null;
  protected List transPriceLog = null;
  protected List shoutLog = null;


  public ListMarketDataLogger() {
    super();
    initialise();
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

  }

  public void setQuoteLog( List quoteLog ) { this.quoteLog = quoteLog; }
  public List getQuoteLog() { return quoteLog; }

  public void setTransPriceLog( List transPriceLog ) { this.transPriceLog = transPriceLog; }
  public List getTransPriceLog() { return transPriceLog; }

  public void setShoutLog( List shoutLog ) { this.shoutLog = shoutLog; }
  public List getShoutLog() { return shoutLog; }

  public void updateQuoteLog( int time, MarketQuote quote ) {
    if ( quoteLog != null ) {
      quoteLog.add(new Integer(time));
      quoteLog.add(quote);
    }
  }

  public void updateTransPriceLog( int time, Shout ask, Shout bid, double price,
                                    int quantity ) {
    try {
      if ( transPriceLog != null ) {
        transPriceLog.add(new Integer(time));
        transPriceLog.add(ask.clone());
        transPriceLog.add(new Double(price));
        transPriceLog.add(new Integer(quantity));
      }
    } catch ( CloneNotSupportedException e ) {
      throw new AuctionError(e);
    }
  }

  public void updateShoutLog( int time, Shout shout ) {
    try {
      if ( shoutLog != null ) {
        shoutLog.add(new Integer(time));
        shoutLog.add(shout.clone());
      }
    } catch ( CloneNotSupportedException e ) {
      throw new AuctionError(e);
    }
  }

  public void initialise() {
    quoteLog = new LinkedList();
    transPriceLog = new LinkedList();
    shoutLog = new LinkedList();
  }

  public void reset() {
    initialise();
  }

  public void generateReport() {
  }
  
  public Map getVariables() {
    return new HashMap();
  }

  public void setAuction( RoundRobinAuction auction ) {
    this.auction = auction;
  }

  public void roundClosed( Auction auction ) {
    // Do nothing
  }

  public void auctionClosed( Auction auction ) {
    // Do nothing
  }

  public void endOfDay( Auction auction ) {
    // Do nothing
  }


}