package uchicago.src.sim.parameter.rpl;

import java.util.Iterator;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 *
 * @version $Revision$ $Date$
 */

public interface RPLParameter {

  /**
   * Increments the value of this RPLParameter to the "next" value.
   *
   * @return true if the increment was successful otherwise false.
   */
  public boolean next();

  /**
   * Gets the name of this RPLParameter.
   */
  public String getName();

  /**
   * Sets the model parameter to the current value of this RPLParameter.
   *
   * @param model the model whose parameter we want to set
   * @throws RepastException if there is an error setting the parameter
   */
  public void setModelParameter(SimModel model) throws RepastException;

  /**
   * Adds the specified RPLParameter as a child parameter of this RPLParameter.
   *
   * @param child the child RPLParameter
   */
  public void addChildParameter(RPLParameter child);

  /**
   * Adds the specified RPLParameter as a child constant parameter of this
   * RPLParameter.
   *
   * @param child the child RPLParameter to add
   */
  public void addChildConstant(RPLParameter child);

  /**
   * Adds the this RPLParameter to as a child of the specified parent RPLParameter.
   * This defines a double dispatch so that RPLParameter that are constants
   * can add themselves to parents as constants with <code>addChildConstant</code>
   * and RPLParameter that are not constants can add themselve with
   * <code>addChildParameter</code>
   *
   * @param parent the parent RPLParameter
   */
  public void addToParent(RPLParameter parent);

  /**
   * Resets this RPLParameter to its initial value.
   */
  public void reset();

  /**
   * Gets the current value of this RPLParameter.
   *
   * @param model the model associated with this RPLParameter
   * @throws RepastException if the value cannot be returned
   */
  public Object getValue(SimModel model) throws RepastException;

  /**
   * Gets an Iterator over the child constant parameters of this RPLParameter.
   */
  public Iterator constantIterator();

  /**
   * Gets an Iterator over the child constant parameters of this RPLParameter.
   */ 
  public Iterator parameterIterator();
}
