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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ascentphase.poolit.*;
import org.ascentphase.poolit.poolers.*;

/**
 * <p>
 * A BinaryHeap that can perform search and removal operations
 * in O(1) time with the aid of a built-in hashing mechanism.
 * </p>
 *
 * <p>
 * Note that duplicates are not allowed in FastBinaryHeap.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class FastBinaryHeap extends BinaryHeap {

  protected IndexMap map = new IndexMap();

  static final int DEFAULT_INDEX_POOL_SIZE = 100000;

  static int indexPoolSize = DEFAULT_INDEX_POOL_SIZE;

  public FastBinaryHeap( Comparator comparator ) {
    super(comparator);
  }

  public FastBinaryHeap() {
    super();
  }

  public static void setIndexPoolSize( int indexPoolSize ) {
    FastBinaryHeap.indexPoolSize = indexPoolSize;
  }

  protected void set( int index, Object x ) {
    super.set(index, x);
    map.associate(x, index);
  }

  public void insert( Object x ) {
    // Prevent duplicates
    if ( map.getIndex(x) != -1 ) {
      throw new IllegalArgumentException("Duplicates not allowed in FastBinaryHeap");
    }
    super.insert(x);
  }

  public int indexOf( Object x ) {
    if ( isEmpty() ) {
      return -1;
    }
    return map.getIndex(x);
  }

  public Object removeFirst() {
    Object first = super.removeFirst();
    map.forget(first);
    return first;
  }

  public boolean remove( int index ) {
    if ( index < 1 ) {
      return false;
    }
    Object originalObject = get(index);
    boolean result = super.remove(index);
    map.forget(originalObject);
    return result;
  }

  public void clear() {
    map.clear();
    super.clear();
  }

}

class IndexMap {

  HashMap map = new HashMap();

  static Pooler intPool = null;


  public IndexMap( int poolSize ) {
    map = new HashMap();
    if ( intPool == null ) {
      try {
        intPool = new FixedPooler(MutableIntWrapper.class, poolSize);
      } catch ( CreateException e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
    }
  }

  public IndexMap() {
    this(FastBinaryHeap.indexPoolSize);
  }

  public void associate( Object key, int index ) {
    MutableIntWrapper indexWrapper = (MutableIntWrapper) map.get(key);
    if ( indexWrapper != null ) {
      indexWrapper.value = index;
    } else {
      try {
        indexWrapper = (MutableIntWrapper) intPool.fetch();
      } catch ( FetchException e ) {
        System.err.println("WARNING " + getClass() + ":");
        e.printStackTrace();
        indexWrapper = new MutableIntWrapper();
      }
      indexWrapper.value = index;
      map.put(key, indexWrapper);
    }
  }

  public void forget( Object key ) {
    MutableIntWrapper indexWrapper = (MutableIntWrapper) map.get(key);
    if ( indexWrapper != null ) {
      intPool.release(indexWrapper);
      map.remove(key);
    }
  }

  public int getIndex( Object key ) {
    MutableIntWrapper indexWrapper = (MutableIntWrapper) map.get(key);
    if ( indexWrapper == null ) {
      return -1;
    }
    return indexWrapper.intValue();
  }

  public void clear() {
    Iterator i = map.entrySet().iterator();
    while ( i.hasNext() ) {
      intPool.release((MutableIntWrapper) ((Map.Entry) i.next()).getValue());
    }
    map.clear();
  }

}

