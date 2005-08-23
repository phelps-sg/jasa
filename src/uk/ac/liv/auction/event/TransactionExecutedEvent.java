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
   * 
   * @uml.property name="ask"
   * @uml.associationEnd multiplicity="(1 1)"
   */
  protected Shout ask;

  /**
   * The offers that led to this transaction.
   * 
   * @uml.property name="bid"
   * @uml.associationEnd multiplicity="(1 1)"
   */
  protected Shout bid;

  /**
   * The price at which the good was sold for.
   * 
   * @uml.property name="price"
   */
  protected double price;

  /**
   * The quantity of the good that was sold.
   * 
   * @uml.property name="quantity"
   */
  protected int quantity;

  public TransactionExecutedEvent( Auction auction, int time, Shout ask,
      Shout bid, double price, int quantity ) {
    super(auction, time);
    this.ask = ask;
    this.bid = bid;
    this.price = price;
    this.quantity = quantity;
  }

  /**
   * @uml.property name="ask"
   */
  public Shout getAsk() {
    return ask;
  }

  /**
   * @uml.property name="bid"
   */
  public Shout getBid() {
    return bid;
  }

  /**
   * @uml.property name="price"
   */
  public double getPrice() {
    return price;
  }

  /**
   * @uml.property name="quantity"
   */
  public int getQuantity() {
    return quantity;
  }

  public int getTime() {
    return time;
  }
}
