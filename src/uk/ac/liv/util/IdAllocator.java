package uk.ac.liv.util;

/**
 * Utility class for handing out unique ids.
 *
 * A using class wishing to assign unique ids to each of its instances should
 * declare a static member variable:
 *
 * <code>
 *   static IdAllocator idAllocator = new IdAllocator();
 * </code>
 *
 * In its constructor it should use something like:
 *
 * <code>
 *   id = idAllocator.nextId();
 * </code>
 *
 * @author Steve Phelps
 */

public class IdAllocator {

  int nextId = 0;

  public IdAllocator() {
  }

  public synchronized int nextId() {
    return nextId++;
  }

}