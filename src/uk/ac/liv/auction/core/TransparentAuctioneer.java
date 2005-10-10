/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import java.util.HashSet;

public abstract class TransparentAuctioneer extends AbstractAuctioneer {

  /**
   * The set of shouts that have been matched in the current round.
   * 
   */
  protected HashSet acceptedShouts = new HashSet();
  
  protected Shout.MutableShout lastAsk = new Shout.MutableShout();
  
  protected Shout.MutableShout lastBid = new Shout.MutableShout();
  
  protected Shout lastShout;
  
  public TransparentAuctioneer() {
    super();
  }

  public TransparentAuctioneer( Auction auction ) {
    super(auction);
  }


  public boolean shoutsVisible() {    
    return true;
  }
  
  public void recordMatch( Shout ask, Shout bid ) {
    assert ask.isAsk();
    assert bid.isBid();
    acceptedShouts.add(ask);
    acceptedShouts.add(bid);
  }
     
  protected void newAsk( Shout ask ) throws DuplicateShoutException {   
    super.newAsk(ask);
    lastAsk.copyFrom(ask);
    lastShout = ask;
  }

  protected void newBid( Shout bid ) throws DuplicateShoutException {  
    super.newBid(bid);
    lastBid.copyFrom(bid);
    lastShout = bid;
  }

  public boolean shoutAccepted( Shout shout ) throws ShoutsNotVisibleException {
    return acceptedShouts.contains(shout);
  }

  public boolean transactionsOccurred() throws ShoutsNotVisibleException {
    return !acceptedShouts.isEmpty();
  }

  public void endOfRoundProcessing() {
    acceptedShouts.clear();
  }

  public void reset() {
    super.reset();
    acceptedShouts.clear();   
    lastShout = null;
  }

  public Shout getLastAsk() {
    return lastAsk;
  }


  public Shout getLastBid() {
    return lastBid;
  }


  public Shout getLastShout() {
    return lastShout;
  }



}
