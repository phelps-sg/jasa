/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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



public interface Auctioneer extends QuoteProvider {

  /**
   * Perform any end-of-round auction functions.
   */
  public void endOfRoundProcessing();

  /**
   * Perform any end-of-auction functions.
   */
  public void endOfAuctionProcessing();

  /**
   * Perform the clearing operation for the auction;
   * match buyers with sellers and inform the auction
   * of any deals.
   */
  public void clear();

  /**
   * Get a quote for the auction.
   */
  //public MarketQuote getQuote();


  /**
   * Code for handling a new shout in the auction.
   * Subclasses should override this method if they wish
   * to provide different handling for different auction rules.
   *
   *  @param shout  The new shout to be processed
   *
   *  @exception IllegalShoutException  Thrown if the shout is invalid in some way.
   */
  public void newShout( Shout shout ) throws IllegalShoutException;

  /**
   * Handle a request to retract a shout.
   */
  public void removeShout( Shout shout );

  /**
   * Log the current state of the auction.
   */
  public void printState();

  /**
   * Reset state
   */
  public void reset();

  public void setAuction( Auction auction );

}