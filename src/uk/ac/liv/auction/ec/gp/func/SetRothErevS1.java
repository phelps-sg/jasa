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

import uk.ac.liv.ai.learning.RothErevLearner;

import uk.ac.liv.ec.gp.GPSchemeNode;
import uk.ac.liv.ec.gp.GPGenericIndividual;
import uk.ac.liv.ec.gp.func.GPGenericData;

import uk.ac.liv.util.UntypedNumber;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class SetRothErevS1 extends GPSchemeNode {

  public void eval( GPGenericData input ) {
    
    evaluateChild(0, input);
    UntypedNumber scaling = (UntypedNumber) input.data;
    
    GPTradingStrategy strategy = (GPTradingStrategy)
        ((GPGenericIndividual) currentIndividual).getGPObject();
    
    RothErevLearner learner = 
      (RothErevLearner) strategy.getRlLearner();
    
    learner.setScaling(scaling.doubleValue());
  }
  
  public String toString() {
    return "SetRothErevS1";
  }
  
}
