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

import ec.EvolutionState;
import ec.Problem;

import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

import uk.ac.liv.ec.gp.GPGenericIndividual;
import uk.ac.liv.ec.gp.func.GPGenericData;

import uk.ac.liv.util.UntypedNumber;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class AdjustMargin extends GPNode {

	public void eval( EvolutionState state, int thread, GPData input,
											ADFStack stack, GPIndividual individual, 
											Problem problem ) {

    children[0].eval(state, thread, input, stack, individual, problem);
    UntypedNumber arg1 = (UntypedNumber) ((GPGenericData) input).data;
    
  	GPTradingStrategy strategy = 
  		(GPTradingStrategy) ((GPGenericIndividual) individual).getGPObject();
  	
  	strategy.adjustMargin(arg1.doubleValue());    
	}

	public String toString() {		
		return "AdjustMargin";
	}
}
