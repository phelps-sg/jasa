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

package uk.ac.liv.util;

import java.util.Comparator;
import java.util.HashMap;

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
 */

public class FastBinaryHeap extends BinaryHeap {

  protected HashMap map = new HashMap();

  public FastBinaryHeap( Comparator comparator ) {
    super(comparator);
  }

  public FastBinaryHeap() {
    super();
  }

  protected void set( int index, Object x ) {
    super.set(index, x);
    map.put(x, new Integer(index));
  }

  public void insert( Object x ) {
    // Prevent duplicates
    if ( map.get(x) != null ) {
      throw new IllegalArgumentException("Duplicates not allowed in FastBinaryHeap");
    }
    super.insert(x);
  }

  public int indexOf( Object x ) {
    if ( isEmpty() ) {
      return -1;
    }
    Integer index = (Integer) map.get(x);
    if ( index == null ) {
      return -1;
    }
    return index.intValue();
  }

  public Object removeFirst() {
    Object first = super.removeFirst();
    map.remove(first);
    return first;
  }

  public boolean remove( int index ) {
    if ( index < 1 ) {
      return false;
    }
    Object originalObject = get(index);
    boolean result = super.remove(index);
    map.remove(originalObject);
    return result;
  }

}