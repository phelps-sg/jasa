/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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