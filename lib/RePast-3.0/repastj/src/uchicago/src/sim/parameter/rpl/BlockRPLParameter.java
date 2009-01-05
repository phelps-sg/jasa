package uchicago.src.sim.parameter.rpl;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.RepastException;

/**
 * RPLParameter created from a block definition. A BlockRPLParameter
 * has no value of its own and only operates on its children.
 *
 * @version $Revision$ $Date$
 */

public class BlockRPLParameter extends AbstractDynamicParameter {

  /**
   * Creates a BlockRPLParameter with the specified name.
   *
   * @param name the name of the block
   */
  public BlockRPLParameter(String name) {
    super(name);
    type = null;

  }

  /**
   * Increments value of the current child of this BlockRPLParameter.
   * If that child cannot be incremented, the next child of this
   * BlockRPLParameter is incremented and so on. If no child can be
   * incremented then this returns false.
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
        /*
        if (!incremented) {
          // so we will be ready next time.
          curIndex = 0;
          for (int i = 0, n = childParams.size(); i < n; i++) {
            RPLParameter p = (RPLParameter) childParams.get(i);
            p.reset();
          }
        }
        */
      }
    }

    return incremented;
  }

  /**
   * Throws an UnsupportedOperationException as a BlockRPLParameter has no value and thus
   * cannot set the parameter of a model to this BlockRPLParameter's current
   * value.
   *
   * @param model
   * @throws UnsupportedOperationException
   */
  protected void invokeSet(SimModel model) throws RepastException {
    throw new UnsupportedOperationException("invokeSet(SimModel) not supported by BlockRPLParameter");
  }

  /**
   * Throws an UnsupportedOperationException as a BlockRPLParameter has no value
   * to get.
   *
   * @throws UnsupportedOperationException
   */
  protected Object getValue() {
    throw new UnsupportedOperationException("getValue() not supported by BlockRPLParameter");
  }

  /**
   * Sets the model parameter to the current values of the child RPLParameter
   *  of this BlockRPLParameter.
   *
   * @param model the model whose parameter we want to set
   * @throws RepastException if there is an error setting the parameter
   */
  public void setModelParameter(SimModel model) throws RepastException {
    for (int i = 0, n = childConsts.size(); i < n; i++) {
      RPLParameter parameter = (RPLParameter) childConsts.get(i);
      parameter.setModelParameter(model);
    }

    RPLParameter parameter = (RPLParameter) childParams.get(curIndex);
    parameter.setModelParameter(model);
  }

  /**
   * Throws an UnsupportedOperationException as a BlockRPLParameter has no value
   * to increment.
   *
   * @throws UnsupportedOperationException
   */
  protected boolean incrementSelf() {
    throw new UnsupportedOperationException("incrementSelf() not supported by BlockRPLParameter");
  }

  public String toString() {
    return name + "[block]";
  }
}
