/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

import ec.gp.*;

import huyd.poolit.*;


public class GPGenericData extends GPData {

  public Object data;

  private static Pooler pooler;

  static final int DEFAULT_POOL_SIZE = 1000;

  static int poolSize = DEFAULT_POOL_SIZE;

  public GPGenericData() {
  }

  public static GPGenericData newGPGenericData() {
    try {
      initialisePool();
      return (GPGenericData) pooler.fetch();
    } catch ( FetchException e ) {
      e.printStackTrace();
      return new GPGenericData();
    }
  }

  public void release() {
    pooler.release(this);
  }

  public GPData copyTo( GPData other ) {
    ((GPGenericData) other).data = this.data;
    return other;
  }

  public String toString() {
    return "(" + getClass() + " data:" + data + ")";
  }

  public static void setPoolSize( int poolSize ) {
    GPGenericData.poolSize = poolSize;
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