package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.MarketQuote;

public interface MarketDataLogger {

  public void updateQuoteLog( int time, MarketQuote quote );

  public void updateTransPriceLog( int time, Shout ask, double price );

  public void updateShoutLog( int time, Shout shout );

  public void reset();

}