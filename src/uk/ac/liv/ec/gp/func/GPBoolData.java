package uk.ac.liv.gp.func;

import ec.gp.*;


public class GPBoolData extends GPGenericData {

  public boolean data;

  public GPBoolData( boolean data ) {
    this.data = data;
  }

  public GPData copyTo(GPData parm1) {
    ((GPBoolData) parm1).data = this.data;
    return parm1;
  }

}