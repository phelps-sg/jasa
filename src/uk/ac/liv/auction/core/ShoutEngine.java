package uk.ac.liv.auction.core;

import java.util.List;

/**
 * Interface for classes providing a shout management service for auctioneers.
 * It is envisaged that there could be many different classes of shout management
 * service, e.g. 4heap memory resident, 4heap with persistence and crash recovery,
 * etc.
 *
 * @author Steve Phelps
 */

public interface ShoutEngine {

  public void newBid( Shout bid ) throws DuplicateShoutException;

  public void newAsk( Shout ask ) throws DuplicateShoutException;

  void removeShout( Shout shout );

  /**
   * Log the current state of the auction.
   */
  public void printState();

  /**
   * Insert an unmatched ask into the approriate heap.
   */
  void insertUnmatchedAsk( Shout ask ) throws DuplicateShoutException;

  /**
   * Insert an unmatched bid into the approriate heap.
   */
  void insertUnmatchedBid( Shout bid ) throws DuplicateShoutException;

  /**
   * <p>
   * Return a list of matched bids and asks.  The list is of the form
   * </p><br>
   *
   *   ( b0, a0, b1, a1 .. bn, an )<br>
   *
   * <p>
   * where bi is the ith bid and a0 is the ith ask.  A typical auctioneer would
   * clear by matching bi with ai for all i at some price.</p>
   */
  public List getMatchedShouts();

  /**
   * Get the highest unmatched bid in the auction.
   */
  Shout getHighestUnmatchedBid();

  /**
   * Get the lowest matched bid in the auction.
   */
  Shout getLowestMatchedBid();

  /**
   * Get the lowest unmatched ask.
   */
  Shout getLowestUnmatchedAsk();

  /**
   * Get the highest matched ask.
   */
  Shout getHighestMatchedAsk();

  /**
   * Reset state.
   */
  public void reset();

}
