package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.ec.gp.func.*;

import uk.ac.liv.util.GenericDouble;

/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class PrivateValue extends GPNode {

  public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
    ((GPGenericData) input).data = new GenericDouble( new Double(((GPTradingStrategy) individual).getPrivateValue()) );
  }

  public String toString() {
    return "PrivateValue";
  }
}