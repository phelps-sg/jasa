/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import cern.jet.random.Uniform;

/**
 * A Vector like collection class that maintains a specified size by either
 * randomly removing elements of the collection or removing elements through
 * the Remover class. On any add operation the new objects are added and
 * if the size of the collection is over the limit, the appropriate number
 * of objects are then removed. <b>Note that on a random remove objects that
 * have just been added may be removed.</b>
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class ConservationCollection {

  private Remover remover = null;
  private int limit;
  private Vector vector;
  //private boolean removeFirst;

  /**
   * Creates a ConservationCollection with the specified size limit.
   *
   * @param sizeLimit the size limit of this ConservationCollection.
   */

  public ConservationCollection(int sizeLimit)
  {
    limit = sizeLimit;
    vector = new Vector(sizeLimit + 25);
  }

  private void conserve(int amount) {
    if (remover == null) {
      for (int i = 0; i < amount; i++) {
        int j = Uniform.staticNextIntFromTo(0, vector.size() - 1);
        vector.remove(j);
      }
    } else {
      vector = remover.remove(vector, amount);
    }
  }

  /**
   * Adds the specified object to the collection. If the addition of this
   * object to collection puts the collection over the size limit, an object
   * in the collection removed according to the Rmoeve
   *
   * @param o the object to add
   */
  public boolean add(Object o) {
    vector.add(o);
    if (vector.size() > limit) {
      conserve(1);
    }

    return true;
  }

  public void add(int index, Object element) {
    vector.add(index, element);
    if (vector.size() > limit) {
      conserve(1);
    }
  }

  public boolean addAll(Collection c) {
    vector.addAll(c);
    if (vector.size() > limit) {
      conserve(vector.size() - limit);
    }
    return true;
  }

  public boolean addAll(int index, Collection c) {
    vector.addAll(index, c);
    if (vector.size() > limit) {
      conserve(vector.size() - limit);
    }

    return true;
  }

  public void clear() {
    vector.clear();
  }

  public boolean contains(Object o) {
    return vector.contains(o);
  }

  public boolean containsAll(Collection c) {
    return vector.contains(c);
  }

  public boolean equals(Object o) {
    if (o instanceof ConservationCollection) {
      ConservationCollection c = (ConservationCollection)o;
      return vector == c.vector;
    }

    return false;
  }

  public Object get(int index) {
    return vector.get(index);
  }

  public int hashCode() {
    return vector.hashCode();
  }

  public int indexOf(Object o) {
    return vector.indexOf(o);
  }

  public boolean isEmpty() {
    return vector.isEmpty();
  }

  public Iterator iterator() {
    return listIterator();
  }

  public int lastIndexOf(Object o) {
    return vector.lastIndexOf(o);
  }

  public ListIterator listIterator() {
    return listIterator(0);
  }

  public ListIterator listIterator(int index) {
    ListIterator iter = vector.listIterator(index);
    return new ConservationIterator(iter, this);
  }

  public Object remove(int index) {
    return vector.remove(index);
  }

  public boolean remove(Object o) {
    return vector.remove(o);
  }

  public boolean removeAll(Collection c) {
    return vector.remove(c);
  }

  public boolean retainAll(Collection c) {
    return vector.retainAll(c);
  }

  public Object set(int index, Object element) {
    return vector.set(index, element);
  }

  public int size() {
    return vector.size();
  }

  public List subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException("subList not supported by ConservationCollection");
  }

  public Object[] toArray() {
    return vector.toArray();
  }

  public Object[] toArray(Object[] a) {
    return vector.toArray(a);
  }

  class ConservationIterator implements ListIterator {

    ListIterator i;
    ConservationCollection collection;

    public ConservationIterator(ListIterator iter, ConservationCollection c) {
      i = iter;
      collection = c;
    }

    public void add(Object o) {
      if (collection.vector.size() >= limit) {
        throw new IllegalArgumentException("ConservationCollection is at its limit");
      } else {
        i.add(o);
      }
    }

    public boolean hasNext() {
      return i.hasNext();
    }

    public boolean hasPrevious() {
      return i.hasPrevious();
    }

    public Object next() {
      return i.next();
    }

    public int nextIndex() {
      return i.nextIndex();
    }

    public Object previous() {
      return i.previous();
    }

    public int previousIndex() {
      return i.previousIndex();
    }

    public void remove() {
      i.remove();
    }

    public void set(Object o) {
      i.set(o);
    }
  }
}



