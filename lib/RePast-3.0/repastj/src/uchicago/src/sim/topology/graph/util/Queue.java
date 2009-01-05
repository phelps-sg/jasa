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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/**
 * A simple FIFO queue.
 * 
 * @author Tom Howe
 * @version $Revision$
 * @serial -3920224324917979463L
 */
public class Queue implements List, Serializable{
  static final long serialVersionUID = -3920224324917979463L;
  private List data;

	/**
	 * Create a new Queue.
	 *
	 */
  public Queue(){
    data = new ArrayList(10);
  }

	/**
	 * Add an item into the queue.
	 * 
	 * @param o
	 */
  public void enqueue(Object o){
    data.add(o);
  }

	/**
	 * Remove an item from the queue.
	 * @return
	 */
  public Object dequeue(){
    return data.remove(0);
  }

	/**
	 * Clear the queue.
	 */
  public void clear(){
    data.clear();
  }

	/**
	 * Determine if the given object is in this queue.
	 * 
	 * @param o
	 * @return
	 */
  public boolean contains(Object o){
    return data.contains(o);
  }

	/**
	 * Return the size of the queue.
	 * @return
	 */
  public int size(){
    return data.size();
  }

	/**
	 * @param arg0
	 * @param arg1
	 */
	public void add(int arg0, Object arg1) {
		data.add(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean add(Object arg0) {
		return data.add(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public boolean addAll(int arg0, Collection arg1) {
		return data.addAll(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean addAll(Collection arg0) {
		return data.addAll(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean containsAll(Collection arg0) {
		return data.containsAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return data.equals(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public Object get(int arg0) {
		return data.get(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return data.hashCode();
	}

	/**
	 * @param arg0
	 * @return
	 */
	public int indexOf(Object arg0) {
		return data.indexOf(arg0);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * @return
	 */
	public Iterator iterator() {
		return data.iterator();
	}

	/**
	 * @param arg0
	 * @return
	 */
	public int lastIndexOf(Object arg0) {
		return data.lastIndexOf(arg0);
	}

	/**
	 * @return
	 */
	public ListIterator listIterator() {
		return data.listIterator();
	}

	/**
	 * @param arg0
	 * @return
	 */
	public ListIterator listIterator(int arg0) {
		return data.listIterator(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public Object remove(int arg0) {
		return data.remove(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean remove(Object arg0) {
		return data.remove(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean removeAll(Collection arg0) {
		return data.removeAll(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean retainAll(Collection arg0) {
		return data.retainAll(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public Object set(int arg0, Object arg1) {
		return data.set(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public List subList(int arg0, int arg1) {
		return data.subList(arg0, arg1);
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return data.toArray();
	}

	/**
	 * @param arg0
	 * @return
	 */
	public Object[] toArray(Object[] arg0) {
		return data.toArray(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return data.toString();
	}

}
