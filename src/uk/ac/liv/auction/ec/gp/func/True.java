package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;


public class True extends GPNode {

  public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem) {
    ((GPAuctionData) input).set(true);
  }

  public String toString() {
    return "True";
  }
}