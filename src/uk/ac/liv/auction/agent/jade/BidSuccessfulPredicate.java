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

package uk.ac.liv.auction.agent.jade;

import jade.content.Predicate;

/**
 * A predicate stating information about a successful bid.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class BidSuccessfulPredicate implements Predicate {

  String seller;

  int quantity;

  double price;

  ACLShout shout;

  public BidSuccessfulPredicate() {
  }

  public String getSeller() { return seller; }
  public int getQuantity() { return quantity; }
  public double getPrice() { return price; }
  public ACLShout getShout() { return shout; }

  public void setSeller( String seller ) { this.seller = seller; }
  public void setQuantity( int quantity ) { this.quantity = quantity; }
  public void setPrice( double price ) { this.price = price; }
  public void setShout( ACLShout shout ) { this.shout = shout; }

}