package uk.ac.liv.ec.gp.func;

import ec.gp.*;


/**
 * @author Steve Phelps
 */

public class GPIntData extends GPData {

  public int data;

  public GPData copyTo( GPData other ) {
    ((GPIntData) other).data = this.data;
    return other;
  }

}