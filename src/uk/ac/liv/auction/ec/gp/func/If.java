package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;


public class If extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {

    children[0].eval(state,thread,input,stack,individual,problem);
    if ( ((GPAuctionData) input).getBoolData() ) {
      children[1].eval(state,thread,input,stack,individual,problem);
    }
  }

  public String toString() {
    return "If";
  }

}