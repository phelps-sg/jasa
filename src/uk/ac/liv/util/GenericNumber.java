package uk.ac.liv.util;

/**
 * @author Steve Phelps
 */

public abstract class GenericNumber extends Number {

  public abstract GenericNumber multiply( GenericNumber other );

  public abstract GenericNumber add( GenericNumber other );

  public abstract GenericNumber subtract( GenericNumber other );

  public abstract GenericNumber divide( GenericNumber other );

}