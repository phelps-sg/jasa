package uk.ac.liv.util;

/**
 * An abstract class encapsulating numbers for weakly-typed
 * arithmetic operations.
 *
 * @author Steve Phelps
 *
 */

public abstract class GenericNumber extends Number implements Comparable {

  public abstract GenericNumber multiply( GenericNumber other );

  public abstract GenericNumber add( GenericNumber other );

  public abstract GenericNumber subtract( GenericNumber other );

  public abstract GenericNumber divide( GenericNumber other );

}