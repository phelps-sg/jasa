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

package uk.ac.liv.auction.event;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

/**
 * An event that is fired every time a good is sold in an auction.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class TransactionExecutedEvent extends AuctionEvent {
  
  
  /**
   * The offers that led to this transaction.
   */
  protected Shout ask, bid;
  
  /**
   * The price at which the good was sold for.
   */
  protected double price;
  
  /**
   * The quantity of the good that was sold.
   */
  protected int quantity;

  public TransactionExecutedEvent ( Auction auction,
      							int time,
      							Shout ask, Shout bid,
      							double price, int quantity ) {
    super(auction, time);
    this.ask = ask;
    this.bid = bid;
    this.price = price;
    this.quantity = quantity;
  }
  
  public Shout getAsk() {
    return ask;
  }
  
  public Shout getBid() {
    return bid;
  }
  
  public double getPrice() {
    return price;
  }
  
  public int getQuantity() {
    return quantity;
  }
  
  public int getTime() {
    return time;
  }
}
