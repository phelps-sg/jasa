package uk.ac.liv.util;

import java.util.Iterator;

/**
 * An iterator that destructively iterates over a PriorityQueue,
 * that is each item that is returned is removed from the top of the heap.
 *
 * @author Steve Phelps
 */

public class QueueDisassembler implements Iterator {

  private PriorityQueue queue;

  public QueueDisassembler( PriorityQueue queue ) {
    this.queue = queue;
  }

  public boolean hasNext() {
    return ! queue.isEmpty();
  }

  public Object next() {
    return queue.removeFirst();
  }

  public void remove() {
  }

}