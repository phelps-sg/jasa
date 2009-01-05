package uchicago.src.sim.parameter.rpl;

/**
 * Defines common behavior for Dynamic RPLParameters.
 *
 * @version $Revision$ $Date$
 */

public abstract class AbstractDynamicParameter extends AbstractRPLParameter {

  /**
   * Creates a AbstractDynamicParameter with the specified name.
   *
   * @param name the name of the parameter.
   */
  public AbstractDynamicParameter(String name) {
    super(name);
  }

  /**
   * Adds this AbstractDynamicParameter to the specified parent. This
   * calls <code>parent.addChildDynamic</code>.
   *
   * @param parent the parent RPLParameter to add this as a child
   */
  public void addToParent(RPLParameter parent) {
    parent.addChildParameter(this);
  }

  /**
   * Increments the value of this RPLParameter to the "next" value.
   *
   * @return true if the increment was successful otherwise false.
   */
  public boolean next() {
    boolean incremented = false;
    // call next on children.
    if (childParams.size() > 0) {
      // increment current child
      RPLParameter parameter = (RPLParameter) childParams.get(curIndex);
      incremented = parameter.next();
      if (!incremented) {
        // we only increment the index here, NOT get child and call next
        // because we need use the initial parameters of the next child.
        curIndex++;
        incremented = curIndex < childParams.size();
        if (incremented) {
          parameter = (RPLParameter) childParams.get(curIndex);
          parameter.reset();
        }
      }
    }

    if (!incremented) {
      incremented = incrementSelf();
      if (incremented) {
        curIndex = 0;
        for (int i = 0, n = childParams.size(); i < n; i++) {
          RPLParameter parameter = (RPLParameter) childParams.get(i);
          parameter.reset();
        }
      }
    }

    return incremented;
  }

  /**
   * Resets this AbstractDynamicParameter to its initial value.
   */
  public void reset() {
    curIndex = 0;
    for (int i = 0, n = childParams.size(); i < n; i++) {
      RPLParameter parameter = (RPLParameter) childParams.get(i);
      parameter.reset();
    }
  }

  /**
   * Increments this AbstractDynamicParameter only. This should not
   * increment this parameter's children.
   *
   * @return true if the increment was successful (i.e. there was a "next"
   * value), otherwise false.
   */
  protected abstract boolean incrementSelf();


}
