/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ec.gp.func;

/**
 * @author Steve Phelps
 *
 */

import ec.*;
import ec.gp.*;
import ec.util.*;

import uk.ac.liv.util.*;
import uk.ac.liv.ec.gp.func.GPGenericData;



public class LongERC extends ERC {

  public long value;

  public void resetNode( final EvolutionState state, final int thread ) {
    value = state.random[thread].nextLong();
  }

  public int nodeHashCode() {
    // a reasonable hash code
    return this.getClass().hashCode() + (int) value;
  }

  public boolean nodeEquals(final GPNode node) {
    if (this.getClass() != node.getClass()) {
      return false;
    }
    return (((LongERC)node).value == value);
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

    if (dret.type != DecodeReturn.T_LONG) {
        // restore the position and the string; it was an error
        dret.data = data;
        dret.pos = pos;
        return false;
    }

    // store the data
    value = dret.l;
    return true;
  }

  public String name() {
    return "LongERC";
  }

  public String toStringForHumans() {
    return "" + value;
  }

  public void eval( EvolutionState state,
                    int thread,
                    GPData input,
                    ADFStack stack,
                    GPIndividual individual,
                    Problem problem ) {

    GPGenericData generic = (GPGenericData) input;
    generic.data = FastLong.newFastLong(value);
  }

}

