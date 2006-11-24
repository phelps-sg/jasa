/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import java.util.Iterator;

import uk.ac.liv.auction.event.AuctionEventListener;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public interface Auctioneer extends QuoteProvider, AuctionEventListener {

  /**
   * Perform the clearing operation for the auction; match buyers with sellers
   * and inform the auction of any deals.
   */
  public void clear();

  /**
   * Code for handling a new shout in the auction. Subclasses should override
   * this method if they wish to provide different handling for different
   * auction rules.
   * 
   * @param shout
   *          The new shout to be processed
   * 
   * @exception IllegalShoutException
   *              Thrown if the shout is invalid in some way.
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
   * Specify which auction we are the auctioneer for.
   * 
   * @uml.property name="auction"
   */
  public void setAuction( Auction auction );

  /**
   * Find out which auction we are the auctioneer for.
   * 
   * @uml.property name="auction"
   * @uml.associationEnd inverse="auctioneer:uk.ac.liv.auction.core.Auction"
   */
  public Auction getAuction();

  public Iterator askIterator();

  public Iterator bidIterator();

  /**
   * Return true if the shouts of others are visible.
   */
  public boolean shoutsVisible();
  
  public boolean shoutAccepted( Shout shout ) throws ShoutsNotVisibleException;
  
  public boolean transactionsOccurred() throws ShoutsNotVisibleException;   

  public Shout getLastAsk() throws ShoutsNotVisibleException;
  
  public Shout getLastBid() throws ShoutsNotVisibleException;
  
  public Shout getLastShout() throws ShoutsNotVisibleException;
  
  /**
   * Perform any auctioneer-specific EndOfRoundEvent processing.
   * Different auction types are implemented by different auctioneers,
   * which perform different operations at the end of each round.   
   */
  public void endOfRoundProcessing();

  /**
   * Perform any auctioneer-specific EndOfAuctionEvent processing.
   * Different auction types are implemented by different auctioneers,
   * which perform different operations at the end of each auction.   
   */
  public void endOfAuctionProcessing();


  /**
   * Perform any auctioneer-specific EndOfDayEvent processing.
   * Different auction types are implemented by different auctioneers,
   * which perform different operations at the end of each day.   
   */
  public void endOfDayProcessing();
  
  /**
   * Get the account which holds any budget surplus or deficit for the
   * auctioneer.  This is useful when implementing non-budget-balanced
   * mechanisms.
   */
  public Account getAccount();

}