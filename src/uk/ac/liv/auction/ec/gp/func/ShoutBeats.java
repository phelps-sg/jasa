package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.Shout;

import uk.ac.liv.gp.func.*;

import ec.gp.*;
import ec.*;


public class ShoutBeats extends GPNode {


  public void eval( EvolutionState state, int thread, GPData input,
                    ADFStack stack, GPIndividual individual, Problem problem ) {
    boolean result;
    children[0].eval(state,thread,input,stack,individual,problem);
    Shout shout1 = ((GPAuctionData) input).getShoutData();
//    Shout shout1 = ((GPAuctioneer) individual).getCurrentShout();
    children[0].eval(state,thread,input,stack,individual,problem);
    Shout shout2 = ((GPAuctionData) input).getShoutData();
    if ( shout1 != null ) {
      if ( shout2 == null ) {
        result = true;
      } else {
        result = shout1.compareTo(shout2) > 0;
      }
    } else {
      result = false;
    }
    ((GPAuctionData) input).set(result);
  }


  public String toString() {
    return "ShoutBeats";
  }
}