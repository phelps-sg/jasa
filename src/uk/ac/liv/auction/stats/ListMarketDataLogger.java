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

package uk.ac.liv.auction.stats;

import java.util.List;
import java.util.LinkedList;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Debug;

/**
 * A utility class for helping auctions to record market data in List
 * collections.
 *
 * @author Steve Phelps
 */

public class ListMarketDataLogger implements MarketDataLogger {

  List quoteLog = null;
  List transPriceLog = null;
  List shoutLog = null;

  public ListMarketDataLogger() {
    initialise();
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

  public void updateTransPriceLog( int time, Shout ask, double price,
                                    int quantity ) {
    try {
      if ( transPriceLog != null ) {
        transPriceLog.add(new Integer(time));
        transPriceLog.add(ask.clone());
        transPriceLog.add(new Double(price));
        transPriceLog.add(new Integer(quantity));
      }
    } catch ( CloneNotSupportedException e ) {
      Debug.assertTrue("Shouts should be cloneable!", false);
    }
  }

  public void updateShoutLog( int time, Shout shout ) {
    try {
      if ( shoutLog != null ) {
        shoutLog.add(new Integer(time));
        shoutLog.add(shout.clone());
      }
    } catch ( CloneNotSupportedException e ) {
      Debug.assertTrue("Shouts should be cloneable!", false);
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

  public void finalReport() {
  }

}