package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.TraderAgent;

/**
 * The interface used by agents to interact with an auction.
 *
 * @see AuctionImpl
 *
 * @author Steve Phelps
 *
 */

public interface Auction {

  /**
   * Request a market quote from the auction.
   */
  public MarketQuote getQuote();

  /**
   * Returns true if the auciton is closed.
   */
  public boolean closed();

  /**
   * Close the auction.
   */
  public void close();

  /**
   * Place a new shout in the auction.
   */
  public void newShout( Shout shout ) throws AuctionException;

  /**
   * Remove a shout from the auction.
   */
  public void removeShout( Shout shout );

  /**
   * Get the last shout to date placed in the auction.
   * @return  A Shout object representing the last shout in the auction.
   */
  public Shout getLastShout();

  /**
   * Report the state of the auction.
   */
  public void printState();

  /**
   * Handle a single clearing operation between two traders
   */
  public void clear( Shout ask, TraderAgent buyer, TraderAgent seller,
                       double price, int quantity );

  /**
   * Get the age of the auction in unspecified units
   */
  public int getAge();

  /**
   * Get the number of traders known to be trading in the auction.
   */
  public int getNumberOfTraders();
}