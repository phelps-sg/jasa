package uk.ac.liv.ec.gp.func;

import ec.gp.*;


/**
 * @author Steve Phelps
 */

public class GPDoubleData extends GPData {

  public double data;

  public GPData copyTo( GPData other ) {
    ((GPDoubleData) other).data = this.data;
    return other;
  }

}