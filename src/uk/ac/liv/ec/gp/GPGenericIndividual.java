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

package uk.ac.liv.ec.gp;

import ec.util.Parameter;
import ec.EvolutionState;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPGenericIndividual extends GPSchemeIndividual implements Resetable {
	
	protected GPObject object;
	
	public static final String P_OBJECT = "object";
	
	public void setup( EvolutionState state, Parameter base ) {
		
		super.setup(state, base);
		
		object = (GPObject)
			state.parameters.getInstanceForParameter(base.push(P_OBJECT), null, 
																												GPObject.class);
		
		object.setGPIndividual(this);
		if ( object instanceof Parameterizable ) {
			((Parameterizable) object).setup(state.parameters, base.push(P_OBJECT));
		}
	}
	
	public GPObject getGPObject() {
		return object;
	}
	
	public Object protoClone() throws CloneNotSupportedException {
		GPGenericIndividual clonedIndividual = 
			(GPGenericIndividual) super.protoClone();
		clonedIndividual.object = (GPObject) object.protoClone();
		clonedIndividual.object.setGPIndividual(clonedIndividual);
		return clonedIndividual;
	}
	
	public void reset() {
		super.reset();
		context.getStack().reset();
	}
	
	
	
}
