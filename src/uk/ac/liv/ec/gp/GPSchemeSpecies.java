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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import java.util.Iterator;

import uk.ac.liv.ec.gp.GPSchemeIndividual;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Subpopulation;

import ec.gp.GPFunctionSet;
import ec.gp.GPSpecies;
import ec.gp.GPFuncInfo;
import ec.gp.GPAtomicType;

import ec.util.Parameter;

import scheme.kernel.ScmPair;
import scheme.kernel.ScmObject;
import scheme.kernel.ScmSymbol;
import scheme.kernel.ScmParser;
import scheme.kernel.ScmException;

import scheme.extensions.ScmJavaObject;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPSchemeSpecies extends GPSpecies {
	
	protected ScmPair environment = ScmPair.NULL;
	
	protected ScmObject schemeObject;
	
	protected String schemeCode;
	
	protected String functionSetName;
	
	protected EvolutionState state;
	
	public static final String P_CODE = "code";
	public static final String P_FS = "fs";
	
	public static final String FS_SYSTEM = "system";
	
	public GPSchemeSpecies() {
		super();		
	}
	
	public void setup( EvolutionState state, Parameter base ) {
		
		super.setup(state, base);
		
		try {
			this.state = state;			
			functionSetName = state.parameters.getString(base.push(P_FS), null);		
			buildEnvironment(functionSetName);
			buildEnvironment(FS_SYSTEM);
			schemeCode = state.parameters.getString(base.push(P_CODE), null);				
			schemeObject = new ScmParser(new StringReader(schemeCode)).read();
			((GPSchemeIndividual) i_prototype).setTree(schemeObject, environment);
		} catch ( ScmException e ) {
			throw new Error(e);
		}			
	}
	
	public Individual newIndividual(EvolutionState state,
			Subpopulation _population, Fitness _fitness, LineNumberReader reader)
			throws IOException, CloneNotSupportedException {
		throw new Error("method not implemented");		
	}

	
	public Individual newIndividual( EvolutionState state,
																		Subpopulation _population, 
																		Fitness _fitness )
			throws CloneNotSupportedException {
		
		Individual newIndividual = (Individual) i_prototype.protoClone();
		newIndividual.fitness = _fitness;
		newIndividual.evaluated = false;
		return newIndividual;
	}
	
	protected void buildEnvironment( String fsName) {		
		System.out.println("Building environment for function set " + fsName);
		GPFunctionSet functionSet = (GPFunctionSet) GPFunctionSet.all.get(fsName);				
		Iterator gpNodeIt = functionSet.nodes_h.keySet().iterator();
		while ( gpNodeIt.hasNext() ) {
			GPAtomicType type = (GPAtomicType) gpNodeIt.next();				
			GPFuncInfo[] nodes = (GPFuncInfo[]) functionSet.nodes_h.get(type);
			for( int i=0; i<nodes.length; i++ ) {											
				makeEnvironmentMapping(nodes[i].node);
			}
		}
	}			
	
	protected void makeEnvironmentMapping( Object object ) {
		ScmSymbol nodeName = new ScmSymbol(object.toString());
		ScmJavaObject node = new ScmJavaObject(object);
		System.out.println("Mapping " + nodeName + " to " + node);
		ScmPair mapping = new ScmPair(nodeName, node);	
		environment = new ScmPair(mapping, environment);
	}
	
}
