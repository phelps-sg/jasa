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

import uk.ac.liv.auction.agent.TraderAgent;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.IdAllocator;

import java.io.Serializable;

/**
 * <p>
 * A class representing a shout in an auction.  A shout may be either a bid
 * (offer to buy) or an ask (offer to sell).
 * </p>
 *
 * <p>
 * Shouts are mutable for performance reasons, hence care should be taken not
 * to rely on, e.g. shouts held in collections remaining constant.
 * </p>
 *
 * <p>
 * Note that equality is determined by reference equivalence, hence this
 * class's natural ordering is not consistent with equals.
 * </p>
 *
 * @author Steve Phelps
 *
 */

public class Shout implements Comparable, Cloneable, Serializable {

  /**
   * The number of items offered/wanted.
   */
  protected int quantity;

  /**
   * The price of this offer
   */
  protected double price;

  /**
   * The agent placing this offer
   */
  protected TraderAgent agent;

  /**
   * True if this shout is a bid.
   * False if this shout is an ask.
   */
  protected boolean isBid;


  /**
   * The unique id of this shout
   */
  protected int id = -1;

  /**
   * The child of this shout.
   */
  protected Shout child = null;

  /**
   * Used to allocate each agent with a unique id.
   */
  static IdAllocator idAllocator = new IdAllocator();

  public Shout( TraderAgent agent, int quantity, double price, boolean isBid ) {
    this(agent);
    this.quantity = quantity;
    this.price = price;
    this.isBid = isBid;
  }

  public Shout( TraderAgent agent ) {
    this();
    this.agent = agent;
  }

  public Shout() {
    this.id = idAllocator.nextId();
  }

  public int getQuantity() {  return quantity; }
  public double getPrice() {  return price; }
  public TraderAgent getAgent() { return agent; }
  public boolean isBid() { return isBid; }
  public boolean isAsk() { return ! isBid; }

  public void setQuantity( int quantity ) { this.quantity = quantity; }
  public void setPrice( double price ) { this.price = price; }
  public void setSelling() { this.isBid = false; }
  public void setBuying() { this.isBid = true; }
  public void setIsBid( boolean isBid ) { this.isBid = isBid; }
  protected void setAgent( TraderAgent agent ) { this.agent = agent; }

  public boolean satisfies( Shout other ) {
    if ( this.isBid() ) {
      return other.isAsk() && this.getPrice() >= other.getPrice();
    } else {
      return other.isBid() && other.getPrice() >= this.getPrice();
    }
  }

  public int compareTo( Object o ) {
    Shout other = (Shout) o;
    if ( price > other.price ) {
      return 1;
    } else if ( price < other.price ) {
      return -1;
    } else {
      return 0;
    }
    // return new Long(this.price).compareTo(new Long(other.getPrice()));
  }

  public boolean isValid() {
    if ( price < 0 ) {
      return false;
    }
    if ( quantity < 1 ) {
      return false;
    }
    return true;
  }

  /**
   * Reduce the quantity of this shout by excess and return a new
   * child shout containing the excess quantity.  After a split,
   * parent shouts keep a reference to their children.
   *
   * @param excess The excess quantity
   *
   */
  public Shout split( int excess ) {
    quantity -= excess;
    Shout newShout = new Shout(agent, excess, price, isBid);
    child = newShout;
    Debug.assertTrue(isValid());
    Debug.assertTrue(newShout.isValid());
    return newShout;
  }

  public Shout splat( int excess ) {
    Shout newShout = new Shout(agent, quantity - excess, price, isBid);
    quantity = excess;
    child = newShout;
    Debug.assertTrue(this.isValid());
    Debug.assertTrue(newShout.isValid());
    return newShout;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String toString() {
    return "(" + getClass() + " id:" + id + " quantity:" + quantity + " price:"
               + price + " isBid:" + isBid + " agent:" + agent + ")";
  }


  public static double maxPrice( Shout s1, Shout s2 ) {
    return Math.max(price(s1,Double.NEGATIVE_INFINITY), price(s2,Double.NEGATIVE_INFINITY));
  }

  public static double minPrice( Shout s1, Shout s2 ) {
    return Math.min(price(s1,Double.POSITIVE_INFINITY), price(s2,Double.POSITIVE_INFINITY));
  }


  private static double price( Shout s, double alt ) {
    if ( s == null ) {
      return alt;
    } else {
      return s.getPrice();
    }
  }

  /**
   * Get the child of this shout.  Shouts have children when they are split().
   *
   * @return The child Shout object, or null if this Shout is childless.
   */
  public Shout getChild() {
    return child;
  }

  public int getId() {
    return id;
  }

  protected void makeChildless() {
    child = null;
  }

  public void copyFrom( Shout other ) {
    setPrice(other.getPrice());
    setAgent(other.getAgent());
    setQuantity(other.getQuantity());
    setIsBid(other.isBid());
    id = other.getId();
    child = null;
  }

  public int hashCode() {
    return id;
  }

  public boolean equals( Object other ) {
    return id == ((Shout) other).id
                   && getAgent().equals(((Shout) other).getAgent());
  }

}
