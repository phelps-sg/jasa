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

import huyd.poolit.*;

import uk.ac.liv.auction.agent.TraderAgent;

/**
 * @author Steve Phelps
 */

public class ShoutPool {

  private static Pooler pooler;

  static final int DEFAULT_POOL_SIZE = 10000;

  static int poolSize = DEFAULT_POOL_SIZE;

  public static Shout fetch() {
    try {
      initialisePool();
      return (Shout) pooler.fetch();
    } catch ( FetchException e ) {
      e.printStackTrace();
      return new Shout();
    }
  }

  public static Shout fetch( TraderAgent agent, int quantity, double price,
                              boolean isBid ) {
    Shout s = fetch();
    s.setIsBid(isBid);
    s.setAgent(agent);
    s.setPrice(price);
    s.setQuantity(quantity);
    return s;
  }

  public static void release( Shout object ) {
    pooler.release(object);
  }

  public static void setPoolSize( int poolSize ) {
    ShoutPool.poolSize = poolSize;
  }

  protected static void initialisePool() {
    if ( pooler == null ) {
      try {
        pooler = new FixedPooler(Shout.class, poolSize);
      } catch ( CreateException e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
    }
  }

}