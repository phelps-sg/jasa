package uk.ac.liv.ec.gp.func;

import ec.gp.*;


public class GPGenericData extends GPData {

  public Object data;

  public GPGenericData() {
  }

  public GPData copyTo( GPData other ) {
    ((GPGenericData) other).data = this.data;
    return other;
  }

  public String toString() {
    return "(" + getClass() + " data:" + data + ")";
  }

}