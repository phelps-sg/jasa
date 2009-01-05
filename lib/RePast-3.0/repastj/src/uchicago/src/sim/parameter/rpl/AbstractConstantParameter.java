package uchicago.src.sim.parameter.rpl;

/**
 * Defines common behavior for constant RPLParameters.
 *
 * @version $Revision$ $Date$
 */

public abstract class AbstractConstantParameter extends AbstractRPLParameter {

  /**
   * Creates an AbstractConstantParameter with the specified name.
   *
   * @param name the name of the parameter.
   */
  public AbstractConstantParameter(String name) {
    super(name);
  }

  /**
   * Adds this AbstractConstantParameter to the specified parent. This
   * calls <code>parent.addChildConstant</code>.
   *
   * @param parent the parent RPLParameter to add this as a child
   */
  public void addToParent(RPLParameter parent) {
    parent.addChildConstant(this);
  }

  /**
   * Resets this AbstractConstantParameter. As constants only have a single
   * constant value this does nothing.
   */
  public void reset() {}

  /**
   * Constants do not have "next" value and so this throws an
   * UnsupportedOperationException if called.
   *
   * @return throws UnsupportedOperationException
   * @throws UnsupportedOperationException
   */
  public boolean next() {
    throw new UnsupportedOperationException("next() not supported on constant parameters");
  }
}
