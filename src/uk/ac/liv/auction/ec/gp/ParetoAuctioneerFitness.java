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


package uk.ac.liv.auction.ec.gp;

import ec.multiobjective.MultiObjectiveFitness;

import uk.ac.liv.util.CummulativeDistribution;


public class ParetoAuctioneerFitness extends MultiObjectiveFitness
    implements AuctioneerFitness {


  public void compute( CummulativeDistribution efficiency,
                          CummulativeDistribution buyerMP,
                          CummulativeDistribution sellerMP,
                          boolean misbehaved ) {

    if ( misbehaved ) {
      for( int i=0; i<multifitness.length; i++ ) {
        multifitness[i] = 0;
      }
      return;
    }

    multifitness[0] =
      NormalisationFunctions.efficiencyFitness(efficiency.getMean());

    multifitness[1] =
      NormalisationFunctions.mpFitness(sellerMP.getMean());

    multifitness[2] =
      NormalisationFunctions.mpFitness(buyerMP.getMean());
  }



}