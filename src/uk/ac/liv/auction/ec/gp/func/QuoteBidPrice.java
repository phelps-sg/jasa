/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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

import uk.ac.liv.auction.core.QuoteProvider;
import uk.ac.liv.auction.core.MarketQuote;

import uk.ac.liv.util.GenericDouble;


public class QuoteBidPrice extends GPNode {

  public void eval( EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual, Problem problem ) {
    QuoteProvider qp = (QuoteProvider) individual;
    MarketQuote quote = qp.getQuote();
    Double quoteBidPrice = new Double( quote.getBid() );
    ((GPGenericData) input).data = new GenericDouble(quoteBidPrice);
  }

  public String toString() {
    return "QuoteBidPrice";
  }

}