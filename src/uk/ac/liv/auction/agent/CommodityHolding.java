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
package uk.ac.liv.auction.agent;

public class CommodityHolding {

  protected int quantity;
  
  protected TradingAgent owner;
  
  public CommodityHolding() {
    this(0);
  }
  
  public CommodityHolding( int quantity ) {
    this.quantity = quantity;
  }
  
  public void add( int quantity ) {
    this.quantity += quantity;
  }
  
  public void remove( int quantity ) {
    this.quantity -= quantity;
  }
  
  public void transfer( CommodityHolding other, int quantity ) {
    this.remove(quantity);
    other.add(quantity);    
  }

  public TradingAgent getOwner() {
    return owner;
  }

  public void setOwner( TradingAgent owner ) {
    this.owner = owner;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity( int quantity ) {
    this.quantity = quantity;
  }
  
  

}
