package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.core.MarketQuote;
import uk.ac.liv.auction.core.Shout;

import uk.ac.liv.util.CummulativeStatCounter;

/**
 * <p>
 * A market data logger that keeps cummulative statistics on a number of variables
 * </p>
 *
 * @author Steve Phelps
 */

public class StatsMarketDataLogger implements MarketDataLogger {

  /**
   * Cummulative statistics on transaction prices.
   */
  CummulativeStatCounter transPriceStats;

  /**
   * Cummulative statistics on bid prices.
   */
  CummulativeStatCounter bidPriceStats;

  /**
   * Cummulative statistics on ask prices.
   */
  CummulativeStatCounter askPriceStats;

  /**
   * Cummulative statistics on the bid part of market quotes.
   */
  CummulativeStatCounter bidQuoteStats;

  /**
   * Cumulative statistics on the ask part of market quotes.
   */
  CummulativeStatCounter askQuoteStats;


  public StatsMarketDataLogger() {
    initialise();
  }

  public void updateQuoteLog( int time, MarketQuote quote ) {
    bidQuoteStats.newData((double) quote.getBid());
    askQuoteStats.newData((double) quote.getAsk());
  }

  public void updateTransPriceLog( int time, Shout ask, double price ) {
    transPriceStats.newData(price);
  }

  public void updateShoutLog( int time, Shout shout ) {
    if ( shout.isBid() ) {
      bidPriceStats.newData(shout.getPrice());
    } else {
      askPriceStats.newData(shout.getPrice());
    }
  }

  public CummulativeStatCounter getTransPriceStats() {
    return transPriceStats;
  }

  public CummulativeStatCounter getBidPriceStats() {
    return bidPriceStats;
  }

  public CummulativeStatCounter getAskPriceStats() {
    return askPriceStats;
  }

  public CummulativeStatCounter getBidQuoteStats() {
    return bidQuoteStats;
  }

  public CummulativeStatCounter getAskQuoteStats() {
    return askQuoteStats;
  }

  public void initialise() {

    transPriceStats =
      new CummulativeStatCounter("Transaction Price");

    bidPriceStats =
      new CummulativeStatCounter("Bid Price");

    askPriceStats =
      new CummulativeStatCounter("Ask Price");

    bidQuoteStats =
      new CummulativeStatCounter("Bid Quote");

    askQuoteStats =
      new CummulativeStatCounter("Ask Quote");

  }

  public void reset() {
    initialise();
  }

}