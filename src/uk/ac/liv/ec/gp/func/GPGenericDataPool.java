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


package uk.ac.liv.ec.gp.func;

import huyd.poolit.*;

/**
 * @author Steve Phelps
 */

public class GPGenericDataPool {

  private static Pooler pooler;

  static final int DEFAULT_POOL_SIZE = 1000;

  static int poolSize = DEFAULT_POOL_SIZE;

  public static GPGenericData fetch() {
    try {
      initialisePool();
      return (GPGenericData) pooler.fetch();
    } catch ( FetchException e ) {
      e.printStackTrace();
      return new GPGenericData();
    }
  }

  public static void release( GPGenericData object ) {
    pooler.release(object);
  }

  public static void setPoolSize( int poolSize ) {
    GPGenericDataPool.poolSize = poolSize;
  }

  protected static void initialisePool() {
    if ( pooler == null ) {
      try {
        pooler = new FixedPooler(GPGenericData.class, poolSize);
      } catch ( CreateException e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
    }
  }

}