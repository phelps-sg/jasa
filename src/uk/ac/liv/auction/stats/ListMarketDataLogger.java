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

  public void updateTransPriceLog( int time, Shout ask, double price ) {
    try {
      if ( transPriceLog != null ) {
        transPriceLog.add(new Integer(time));
        transPriceLog.add(ask.clone());
      }
    } catch ( CloneNotSupportedException e ) {
      Debug.assert("Shouts should be cloneable!", false);
    }
  }

  public void updateShoutLog( int time, Shout shout ) {
    try {
      if ( shoutLog != null ) {
        shoutLog.add(new Integer(time));
        shoutLog.add(shout.clone());
      }
    } catch ( CloneNotSupportedException e ) {
      Debug.assert("Shouts should be cloneable!", false);
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

}