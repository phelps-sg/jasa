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

package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.ec.gp.func.*;

import uk.ac.liv.util.FastDouble;

import uk.ac.liv.auction.core.Shout;


public class LastBid extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input, 
                      ADFStack stack, GPIndividual individual, Problem problem ) {
    GPTradingStrategy strategy = (GPTradingStrategy) individual;
    Shout lastBid = strategy.getAuction().getLastBid();
    double price;
    if ( lastBid == null ) {
      price = -1;
    } else {
      price = lastBid.getPrice();
    }
    ((GPGenericData) input).data = FastDouble.newFastDouble(price);  
  }

  public String toString() {
    return "LastBid";
  }
}