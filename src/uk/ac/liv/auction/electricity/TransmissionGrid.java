/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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