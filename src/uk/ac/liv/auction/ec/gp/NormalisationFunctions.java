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



package uk.ac.liv.auction.ec.gp;

/**
 * Normalisation functions mapping market metrics onto noramalised
 * fitness values in the range [0,1].
 *
 * @author Steve Phelps
 */

public class NormalisationFunctions {


  /**
   * market-power normalisation function.
   */
  protected static float mpFitness( double mp ) {
    return (float) (1 / (Math.abs(mp) + 1));
  }

  /**
   * market-efficiency normalisation function.
   */

  protected static float efficiencyFitness( double eA ) {
    float fitness = (float) eA/100;
    if ( fitness > 1 && Math.abs(fitness) < 2 ) {
      return 1f;
    } else {
      return fitness;
    }
  }


}