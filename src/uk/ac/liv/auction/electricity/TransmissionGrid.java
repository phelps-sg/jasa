package uk.ac.liv.auction.electricity;

import java.util.HashMap;

/**
 * A class representing a fully-connected, bi-directional
 * graph of available transmission capacity (ATC) between traders
 * in an electricity market.
 *
 * @author Steve Phelps
 */

public class TransmissionGrid {

  HashMap graph;

  public TransmissionGrid( HashMap graph ) {
    this.graph = graph;
  }

  /**
   * Get the available transmission capacitity (ATC) between
   * two traders in the grid.
   */
  public int getATC( ElectricityTrader x, ElectricityTrader y ) {
    HashMap edges = (HashMap) graph.get(x);
    Integer atc = (Integer) edges.get(y);
    return atc.intValue();
  }


}