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

import uk.ac.liv.util.CummulativeDistribution;

import ec.simple.SimpleFitness;

public class WeightedAuctioneerFitness extends SimpleFitness
    implements AuctioneerFitness {

  static float w;

  static final String P_W = "w";


  /*
  public void setup(final EvolutionState state, Parameter base) {
    super.setup(state,base);
    w = state.parameters.getFloat(base.push(P_W), null, 0);
  } */


  public void compute( CummulativeDistribution efficiency,
                          CummulativeDistribution buyerMP,
                          CummulativeDistribution sellerMP,
                          boolean misbehaved ) {

    if ( misbehaved ) {
      fitness = 0f;
      return;
    }

    float ea =
      NormalisationFunctions.efficiencyFitness(efficiency.getMean());

    float mpb =
      NormalisationFunctions.mpFitness(buyerMP.getMean());

    float mps =
      NormalisationFunctions.mpFitness(sellerMP.getMean());

    fitness = (1-w)*(mpb+mps)/2 + w*ea;

  }

  public static void setW( float w ) {
    WeightedAuctioneerFitness.w = w;
  }

}