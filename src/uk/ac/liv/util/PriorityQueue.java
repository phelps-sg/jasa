package uk.ac.liv.util;


public interface PriorityQueue {

  public Object removeFirst();

  public Object getFirst();

  public void insert( Object o );

  public boolean isEmpty();

  public void transfer( PriorityQueue other );

  public boolean remove( Object o );
}