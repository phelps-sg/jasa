package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.auction.core.Shout;

import ec.gp.*;
import ec.*;

import uk.ac.liv.ec.gp.func.*;


public class IsBid extends GPNode {

  public void eval(EvolutionState state, int thread, GPData input,
                    ADFStack stack, GPIndividual individual, Problem problem) {
    GPAuctioneer auctioneer = (GPAuctioneer) individual;
    Shout shout = auctioneer.getCurrentShout();
    ((GPGenericData) input).data = new Boolean(shout.isBid());
  }

  public String toString() {
    return "IsBid";
  }
}