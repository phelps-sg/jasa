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

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class AgentIsSeller extends GPNode {

	public void eval( EvolutionState state, int thread, GPData input,
											ADFStack stack, GPIndividual individual, 
											Problem problem ) {
    
  	GPTradingStrategy strategy = 
  		(GPTradingStrategy) ((GPGenericIndividual) individual).getGPObject();
  	
  	Boolean isSeller = new Boolean(strategy.getAgent().isSeller());    
  	
		((GPGenericData) input).data = isSeller;
	}

	public String toString() {		
		return "AgentIsSeller";
	}
}
