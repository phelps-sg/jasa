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

package uk.ac.liv.auction.ec.ga;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.*;

import uk.ac.liv.ai.learning.RothErevLearner;

import ec.vector.DoubleVectorIndividual;

/**
 * @author Steve Phelps
 */

public class GAElectricityTraderContainer extends DoubleVectorIndividual {

  ElectricityTrader trader = null;

  public ElectricityTrader getTrader() {
    double propensities[] = (double []) getGenome();
    ((RothErevLearner) ((StimuliResponseStrategy) trader.getStrategy()).getLearner()).setPropensities(propensities);
    trader.reset();
    return trader;
  }

  public void setTrader( ElectricityTrader trader ) {
    this.trader = trader;
  }

}
