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


package uk.ac.liv.util;

import org.ascentphase.poolit.*;
import org.ascentphase.poolit.poolers.*;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class FastDoublePool {

  private static Pooler pooler;

  static final int DEFAULT_POOL_SIZE = 1000;

  static int poolSize = DEFAULT_POOL_SIZE;

  public static FastDouble fetch() {
    try {
      initialisePool();
      return (FastDouble) pooler.fetch();
    } catch ( FetchException e ) {
      e.printStackTrace();
      return new FastDouble();
    }
  }

  public static FastDouble fetch( double value ) {
    FastDouble n = fetch();
    n.setValue(value);
    return n;
  }

  public static void release( FastDouble object ) {
    pooler.release(object);
  }

  public static void setPoolSize( int poolSize ) {
    FastDoublePool.poolSize = poolSize;
  }

  protected static void initialisePool() {
    if ( pooler == null ) {
      try {
        pooler = new FixedPooler(FastDouble.class, poolSize);
      } catch ( CreateException e ) {
        e.printStackTrace();
        throw new Error(e);
      }
    }
  }

}