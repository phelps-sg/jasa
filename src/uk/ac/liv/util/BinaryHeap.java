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

package uk.ac.liv.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Collection;
import java.util.Vector;

import java.io.Serializable;

/**
 * This Collection class represents a binary heap, also known as a priority queue.
 *
 * The underlying data structure is a Vector.  Items are organised in such
 * a way that retrieving the smallest, or largest, item can be done in O(1) time.
 *
 * @author Steve Phelps
 *
 */

public class BinaryHeap implements Collection, PriorityQueue, Serializable {

  /**
   * The default initial capacity of the underlying Vector.
   */
  static final int DEFAULT_CAPACITY = 100;

  /**
   * Used to assign a unique id to each heap.
   */
  static Integer idAllocator = new Integer(0);

  /**
   * A unique id for this heap.  Its used mainly for debugging purposes.
   */
  int id;


  /**
   * The underlying Vector data structure holding the elements of the heap.
   */
  HeapContents contents;

  /**
   * The Comparator used to order items in the heap.  If it is not present then the
   * compareTo method of the Comparable interface is used.
   */
  Comparator comparator;


  /**
   * Inner class used to iterate over items in the heap.
   */
  class HeapIterator implements Iterator {

      int currentIndex = 1;

      public HeapIterator() {
      }

      public boolean hasNext() {
        return currentIndex <= size();
      }

      public Object next() {
        return get(currentIndex++);
      }

      public void remove() {
      }

    }

  /**
   * Construct the binary heap.  Objects will be ordered according to the Comparable
   * interface.
   */
  public BinaryHeap( )
  {
    this(DEFAULT_CAPACITY);
  }

  /**
    * Construct the binary heap.
    *
    * @param comparator If this comparator is non-null then it is used to order items in the heap.
    * @param capacity The initial capacity of the underlying Vector
    */
  public BinaryHeap( Comparator comparator, int capacity ) {
    synchronized(idAllocator) {
      id = idAllocator.intValue();
      idAllocator = new Integer(id+1);
    }
    contents = new HeapContents(capacity);
    this.comparator = comparator;
  }

  /**
   * Construct the binary heap.
   * @param capacity The initial capcity of the underlying Vector
   */
  public BinaryHeap( int capacity ) {
    this(null, capacity);
  }

  public BinaryHeap( Comparator comparator ) {
    this(comparator, DEFAULT_CAPACITY);
  }

  /**
   * Compares two objects using either the heap's comparator, if it is present, or
   * the result of the compareTo method on o1.  Both o1 and o2 must implement
   * Comparable if no comparator is present.
   *
   * @param o1 The first object to compare
   * @param o2 The second object to compare
   */
  public int compare( Object o1, Object o2 ) {
    if ( comparator != null ) {
      return comparator.compare(o1,o2);
    } else {
      return ((Comparable) o1).compareTo((Comparable) o2);
    }
  }

  public Iterator iterator() {
    return new HeapIterator();
  }


  /**
   * Transfer the first item of this heap into the second heap.
   *
   * @param toOther The heap to transfer to
   */
  public void transfer( PriorityQueue toOther ) {
    // if ( ! isEmpty() ) {
      toOther.insert(removeFirst());
    // }
  }

  /**
   * Insert into the priority queue, maintaining heap order.
   * Duplicates are allowed.
   *
   * @param x the item to insert.
   */
  public void insert( Object x ) {
    int index = size()+1;
    set(index, x);
    percolateUp(index);
  }

  public boolean add( Object x ) {
    insert(x);
    return true;
  }

  /**
   * Find the smallest item in the priority queue.
   *
   * @return the smallest item, or null, if empty.
   */
  public Object getFirst() {
    if( isEmpty() ) {
      return null;
    }
    return get(1);
  }

  /**
   * Test if the priority queue is logically empty.
   *
   * @return true if empty, false otherwise.
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  public boolean retainAll( Collection other ) {
    throw new UnsupportedOperationException("BinaryHeap does not implement retainAll");
  }

  public boolean removeAll( Collection other ) {
    throw new UnsupportedOperationException("BinaryHeap does not implement removeAll");
  }

  public boolean addAll( Collection other ) {
    throw new UnsupportedOperationException("BinaryHeap does not implement addAll");
  }

  public boolean containsAll( Collection other ) {
    throw new UnsupportedOperationException("BinaryHeap does not implement containsAll");
  }

  /**
   * Make the priority queue logically empty.
   */
  public void clear() {
    contents.clear();
  }

  public int indexOf( Object key ) {
    for( int i=1; i<=contents.getCurrentSize(); i++ ) {
      if ( key.equals(get(i)) ) {
        return i;
      }
    }
    return -1;
  }

  public boolean contains( Object key ) {
    return indexOf(key) > 0;
  }

  public boolean remove( Object x ) {
    return remove(indexOf(x));
  }

  public boolean remove( int index ) {
    if ( index < 1 ) {
      return false;
    }
    Object x = get(size());
    set(index, x);
    contents.shrink();
    if ( index <= size() ) {
      if ( index > 1 && compare(x, get(index/2)) < 0 ) {
        percolateUp(index);
      } else {
        percolateDown(index);
      }
    }
    return true;
  }

  /**
   * Remove the smallest item from the priority queue.
   * @return the smallest item, or null, if empty.
   */
  public Object removeFirst() {
    if( isEmpty() ) {
        return null;
    }

    Object minItem = getFirst();
    if ( size() > 1 ) {
      set(1, get(size()));
      percolateDown(1);
    }
    contents.shrink();

    return minItem;
  }

  public String toString() {
    StringBuffer out = new StringBuffer("(" + getClass() + " id:" + id + " size:" + size() + " contents:(\n");
    Iterator i = new HeapIterator();
    while ( i.hasNext() ) {
      out.append("\t" + i.next() + "\n");
    }
    out.append("))");
    return out.toString();
  }

  public Object[] toArray() {
    return contents.toArray();
  }

  public Object[] toArray( Object[] a ) {
    return contents.toArray(a);
  }

  public int size() {
    return contents.getCurrentSize();
  }


  /**
   * Internal method to percolate down in the heap.
   *
   * @param hole the index at which the percolate begins.
   */
  protected void percolateDown( int hole ) {
    int child;
    Object tmp = get(hole);

    for( ; hole * 2 <= size(); hole = child ) {
      child = hole * 2;
      if( child != size() &&
              compare(get(child+1), get(child)) < 0 )
          child++;
      if( compare(get(child), tmp) < 0 ) {
        set(hole, get(child));
      } else {
        break;
      }
    }
    set(hole, tmp);
  }

  protected void percolateUp( int hole ) {
    Object x = get(hole);
    for( ; hole > 1 && compare(x, get(hole/2)) < 0; hole /= 2 ) {
      set(hole, get(hole/2));
    }
    set(hole,x);
  }


  protected void set( int index, Object x ) {
    contents.set(index, x);
  }

  protected Object get( int index ) {
    return contents.get(index);
  }

}


/**
 * Basically a wrapper for Vector with indexing starting at 1.
 */

class HeapContents implements Serializable {

  /**
   * The underlying data
   */
  private Vector contents;

 /**
   * The current size of the heap.
   */
  private int currentSize, maxSize = 0;

  public HeapContents( int capacity ) {
    contents = new Vector(capacity);
    currentSize = 0;
  }

  protected void grow( int increment ) {
    currentSize += increment;
    contents.ensureCapacity(currentSize);
  }

  public void set( int index, Object obj ) {
    if ( index > currentSize ) {
      grow(index-currentSize);
    }
    if ( index > maxSize ) {
      maxSize = index;
      contents.add(index-1, obj);
    } else {
      contents.set(index-1, obj);
    }
  }

  public Object get( int index ) {
    return contents.get(index-1);
  }

  public void shrink() {
    currentSize--;
  }

  public int getCurrentSize() {
    return currentSize;
  }

  public void clear() {
    currentSize = 0;
  }

  public Object[] toArray() {
    return contents.toArray();
  }

  public Object[] toArray( Object[] a ) {
    return contents.toArray(a);
  }
}
