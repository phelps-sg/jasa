package uk.ac.liv.ec.gp.func;

import ec.gp.*;


/**
 * @author Steve Phelps
 */

public class GPBoolData extends GPGenericData {

  // This will hide the generic Object data
  public boolean data;

  public GPData copyTo( GPData other ) {
    ((GPBoolData) other).data = this.data;
    return other;
  }

}