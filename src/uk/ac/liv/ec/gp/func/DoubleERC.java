package uk.ac.liv.ec.gp.func;


/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

import ec.*;
import ec.gp.*;
import ec.util.*;

import uk.ac.liv.util.*;
import uk.ac.liv.ec.gp.func.GPGenericData;



public class DoubleERC extends ERC {

  public double value;

  public void resetNode( final EvolutionState state, final int thread ) {
    value = state.random[thread].nextDouble() * 2 - 1.0;
  }

  public int nodeHashCode() {
    // a reasonable hash code
    return this.getClass().hashCode() + Float.floatToIntBits((float)value);
  }

  public boolean nodeEquals(final GPNode node) {
    if (this.getClass() != node.getClass()) {
      return false;
    }
    return (((DoubleERC)node).value == value);
  }

  public String encode() {
    return Code.encode(value);
  }

  public boolean decode(DecodeReturn dret) {
    // store the position and the string in case they
    // get modified by Code.java
    int pos = dret.pos;
    String data = dret.data;

    // decode
    Code.decode(dret);

    if (dret.type != DecodeReturn.T_DOUBLE) {
        // restore the position and the string; it was an error
        dret.data = data;
        dret.pos = pos;
        return false;
    }

    // store the data
    value = dret.d;
    return true;
  }

  public String name() {
    return "DoubleERC";
  }

  public String toStringForHumans() {
    return "" + (float)value;
  }

  public void eval( EvolutionState state,
                    int thread,
                    GPData input,
                    ADFStack stack,
                    GPIndividual individual,
                    Problem problem ) {

    GPGenericData generic = (GPGenericData) input;
    generic.data = new GenericDouble(new Double(value));
  }

}

