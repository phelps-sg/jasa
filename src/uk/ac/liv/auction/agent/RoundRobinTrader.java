package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 */

public interface RoundRobinTrader {

 /**
   * Request a shout from this trader.  The trader will perform any bidding activity
   * in this method and return when it is done.  An auction invokes this method
   * on a trader when it is the traders "turn" to bid in that auction.
   *
   * @param auction The auction in which to trade
   */
  public void requestShout( RoundRobinAuction auction );

  /**
   * This method is used to notify a buyer that one of its bids has been successful.
   *
   * @param seller  The seller whose ask has been matched
   * @param price   The price of the goods as determined by the auction
   */
  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                                        double price, int quantity );

  public void reset();

}