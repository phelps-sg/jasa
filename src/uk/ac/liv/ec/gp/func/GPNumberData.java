package uk.ac.liv.ec.gp.func;

import ec.gp.*;

import uk.ac.liv.util.GenericNumber;

/**
 * @author Steve Phelps
 */

public class GPNumberData extends GPData {

  public GenericNumber data;

  public GPData copyTo( GPData other ) {
    ((GPNumberData) other).data = this.data;
    return other;
  }

}