package uk.ac.liv.auction.stats;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 */

public class CombiDataLogger implements MarketDataLogger {

  List loggers = null;

  public CombiDataLogger( List loggers ) {
    this.loggers = loggers;
  }

  public CombiDataLogger() {
    this.loggers = new LinkedList();
  }

  public void addLogger( MarketDataLogger logger ) {
    loggers.add(logger);
  }

  public void updateQuoteLog(int time, MarketQuote quote) {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.updateQuoteLog(time, quote);
    }
  }

  public void updateTransPriceLog(int time, Shout ask, double price) {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.updateTransPriceLog(time, ask, price);
    }
  }

  public void updateShoutLog(int time, Shout shout) {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.updateShoutLog(time, shout);
    }
  }

  public void reset() {
    Iterator i = loggers.iterator();
    while ( i.hasNext() ) {
      MarketDataLogger logger = (MarketDataLogger) i.next();
      logger.reset();
    }
  }

}