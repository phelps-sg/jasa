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
package uchicago.src.sim.topology.graph.util;


import java.io.Serializable;
import java.util.Comparator;

/**
 * A data structure for maintaing a set of elements, each
 * with an associated key.
 * 
 * @author Tom Howe
 * @version $Revision$
 * @serial -1401274766897115713L
 */
public class PriorityQueue extends Queue implements Serializable{
  static final long serialVersionUID = -1401274766897115713L;
  Comparator comp;

	/**
	 * Create a new PriorityQueue using a default Comparator,
	 * that compares based on hashcodes. 
	 *
	 */
  public PriorityQueue(){
		super();
    comp = new Comparator(){
      public int compare(Object o1, Object o2){
        int i1 = o1.hashCode();
        int i2 = o2.hashCode();
        if(i1 > i2){
          return 1;
        }else if( i2 > i1){
          return -1;
        }else{
          return 0;
        }
      }
    };
  }

	/**
	 * Create a PriorityQueue using a custom Comparator.
	 * 
	 * @param comp
	 */
  public PriorityQueue(Comparator comp){
		super();
    this.comp = comp;

  }
	
  private void newAdd(int i, Object o){
    add(i - 1, o);
  }

  private Object newGet(int i){
    return get(i - 1);
  }

  private void newSet(int i, Object o){
    if(size() == 0){
      add(o);
    }else{
      set(i - 1, o);
    }
  }

  private Object newRemove(int i){
    return remove(i - 1);
  }

  private int left(int i){
    return i <<= 1;
  }

  private int parent(int i){
    return i >>= 1;
  }

  private int right(int i){
    return i = (i<< 1) + 1;
  }

  private void exchange(int i, int j){
    Object o = newGet(i);
    Object o1 = newGet(j);
    newSet(j, o);
    newSet(i, o1);
  }

  public String toString(){
    StringBuffer buf = new StringBuffer();
    for(int i = 0 ; i < size() ; i++){
      buf.append(get(i) + " ");
    }
    buf.append("\n");
    return buf.toString();
  }

	/**
	 * Standard heapify method.  Moves the element at i down 
	 * the heap to ensure the subtree at i is a heap.
	 * @param i
	 */
  protected void heapify(int i){
    int l = left(i);
    int r = right(i);
    int largest;
    if(l <= size() && comp.compare(newGet(l), newGet(i)) == 1){
      largest = l;
    }else{
      largest = i;
    }
    if(r <= size() && comp.compare(newGet(r), newGet(largest)) == 1){
      largest = r;
    }
    if(largest != i){
      exchange(i, largest);
      heapify(largest);
    }
  }

	/**
	 * Construct a heap.
	 *
	 */
  protected void buildHeap(){
    for(int i = size() / 2 ; i == 1 ; i--){
      heapify(i);
    }
  }

	/**
	 * Sort the heap.
	 *
	 */
  protected void heapSort(){
    for(int i = size() ; i == 2 ; i--){
      exchange(1, i);
      heapify(1);
    }
  }

	/**
	 * Insert an item and place it appropriately by adding a new
	 * leaf.
	 * @param key
	 */
  public void heapInsert(Object key){
    add(key);
    int i = size();
    if(i < 1){
      i = 1;
    }
    while(i > 1 && comp.compare(newGet(parent(i)), key) == -1){
      newSet(i, newGet(parent(i)));
      i = parent(i);
    }
    newSet(i, key);
  }

	/**
	 * Add an item to the queue.
	 */
  public void enqueue(Object o) {
    heapInsert(o);
  }

	/**
	 * Return the item with the highest value.
	 */
  public Object dequeue() {
    return extractMax();
  }

	/**
 	 * 
 	 * @return
 	 */
  public Object extractMax(){
    if(size() == 0){
      return newRemove(1);
    }else{
      System.out.println("this = " + this);
      Object max = newRemove(1);
      newAdd(1, newRemove(size()));
      heapify(1);
      //System.out.println("max = " + max);
      //System.out.println("this = " + this);
      return max;
    }
  }
}