package uk.ac.liv.util;

public interface Pooled extends Cloneable {

  public void release();

  public Object newCopy();

}