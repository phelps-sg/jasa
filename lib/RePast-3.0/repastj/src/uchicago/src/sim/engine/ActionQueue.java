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
package uchicago.src.sim.engine;

import java.util.NoSuchElementException;

/**
 * Priority Queue for Action objects.
 */
public class ActionQueue {

  private int maxSize, origMax;
  private int currentSize = 0;
  private boolean orderOk = true;
  private BasicAction[] array;

  // this is used when an action is removed. We replace the removed BasicAction
  // with this one. By replacing we don't need to fix the heap.
  static class EmptyAction extends BasicAction {
    EmptyAction (double nextTime) {
      this.nextTime = nextTime;
      this.setUpdater (BasicAction.ONE_TIME_UPDATER);
    }

    public void execute () {
    }

    public void addToGroup (ScheduleGroup group) {
    }
  }

  // our top most root BasicAction
  static class DummyAction extends BasicAction {

    DummyAction () {
      nextTime = Double.NEGATIVE_INFINITY;
    }

    public void execute () {
    }
  }

  public ActionQueue () {
    this (6);
  }

  public ActionQueue (int size) {
    maxSize = size;
    origMax = size;
    allocateArray (size);
    BasicAction root = new DummyAction ();
    array[0] = root;
  }

  private void checkSize () {
    if (currentSize == maxSize) {
      BasicAction[] old = array;
      allocateArray (maxSize * 2);
      System.arraycopy (old, 0, array, 0, old.length);
      // just to be sure
      old = null;
      maxSize *= 2;
    }
  }

  private void percolateDown (int hole) {
    if (currentSize > 0) {
      int child;
      BasicAction tmp = array[hole];
      for (; hole * 2 <= currentSize; hole = child) {
        child = hole * 2;
        if (child != currentSize &&
                array[child + 1].nextTime < array[child].nextTime)
          child++;

        if (array[child].nextTime < tmp.nextTime)
          array[hole] = array[child];
        else
          break;
      }

      array[hole] = tmp;
    }
  }

  /**
   * Find the specified Action in the queue and void it. Voiding means
   * replace that action with an empty action that does nothing.
   *
   * @return returns true if the action is found and voided, false if
   * this ActionQueue does not contain the specified BasicAction.
   *
   */
  public boolean voidAction (BasicAction action) {
    boolean found = false;
    for (int i = 0, n = array.length; i < n; i++) {
      if (action.equals (array[i])) {
        array[i] = new EmptyAction (action.nextTime);
        found = true;
      }
    }

    return found;
  }


  /**
   * Insert the specified action into the heap. If heap order is being
   * maintained, that is, it is not already invalidated, percolate action
   * up as needed.
   */
  public void insert (BasicAction action) {
    if (!orderOk) {
      toss (action);
      return;
    }

    checkSize ();
    int hole = ++currentSize;
    if (hole != 1) {
      for (; action.nextTime < array[hole / 2].nextTime; hole /= 2)
        array[hole] = array[hole / 2];
    }
    array[hole] = action;
  }

  public BasicAction peekMin () {
    if (currentSize == 0)
      throw new NoSuchElementException ("Queue is Empty");
    if (!orderOk) fixHeap ();
    return array[1];
  }

  public BasicAction popMin () {
    BasicAction a = peekMin ();
    array[1] = array[currentSize--];
    percolateDown (1);
    return a;
  }

  public void toss (BasicAction action) {
    checkSize ();
    array[++currentSize] = action;

    if (currentSize != 1) {
      // is action < its parent node.
      if (action.nextTime < array[currentSize / 2].nextTime) orderOk = false;
    }
  }

  public void clear () {
    currentSize = 0;
    orderOk = true;
    allocateArray (origMax);
    maxSize = origMax;
    BasicAction root = new DummyAction ();
    array[0] = root;
  }

  public void fixHeap () {
    for (int i = currentSize / 2; i > 0; i--) {
      percolateDown (i);
    }
    orderOk = true;
  }

  public boolean isEmpty () {
    return currentSize == 0;
  }

  public int size () {
    return currentSize;
  }

  private void allocateArray (int newMaxSize) {
    array = new BasicAction[newMaxSize + 1];
  }

  public String toString () {

    return super.toString () + ", size: " + currentSize;
  }
}
