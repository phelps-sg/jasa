package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;

public class And extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input,
                      ADFStack stack, GPIndividual individual, Problem problem ) {
    children[0].eval(state,thread,input,stack,individual,problem);
    boolean result1 = ((GPAuctionData) input).getBoolData();
    if ( ! result1 ) {
      // short-circuit
      ((GPAuctionData) input).set(false);
      return;
    }

    children[1].eval(state,thread,input,stack,individual,problem);
    boolean result2 = ((GPAuctionData) input).getBoolData();

    ((GPAuctionData) input).set( result1 && result2 );

  }

  public String toString() {
    return "And";
  }
}