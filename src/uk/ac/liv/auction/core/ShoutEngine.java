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

import java.util.List;

/**
 * Interface for classes providing a shout management service for auctioneers.
 * It is envisaged that there could be many different classes of shout management
 * service, e.g. 4heap memory resident, 4heap with persistence and crash recovery,
 * etc.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public interface ShoutEngine extends uk.ac.liv.util.Resetable {

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

}
