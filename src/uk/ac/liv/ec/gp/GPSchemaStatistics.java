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

import java.io.StringReader;
import java.util.Iterator;

import scheme.kernel.ScmException;
import scheme.kernel.ScmObject;
import scheme.kernel.ScmParser;

import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Distribution;

import ec.EvolutionState;

import ec.util.Parameter;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPSchemaStatistics extends GPBestStatistics {

  protected int numSchema;
  
  protected ScmObject[] schema;
  
  public static final String P_SCHEMA = "schema";
  

  public void setup( EvolutionState state, Parameter base ) {
    super.setup(state, base);

    numSchema = state.parameters.getInt(base.push(P_SCHEMA).push("n"));

    schema = new ScmObject[numSchema];

    try {
      
      for ( int i = 0; i < numSchema; i++ ) {
        
        String schemeCode = 
          state.parameters.getString(base.push(P_SCHEMA).push("" + i));
   
        schema[i] = 
          new ScmParser(new StringReader(schemeCode)).read();
      }
      
    } catch ( ScmException e ) {
      throw new Error(e);
    }
  }
    
  public void postBreedingStatistics( EvolutionState state ) {
    super.postBreedingStatistics(state);
    println("");
    println("Schema Statistics");
    println("-----------------");
    println("");
    for( int i=0; i<numSchema; i++ ) {
      println("Schema " + i + " " + schema[i].toDisplayString() + ":");
      Distribution schemaStats = schemaFitness(schema[i]);
      println(schemaStats.toString());
    }    
  }
  
  public Distribution schemaFitness( ScmObject code ) {
    CummulativeDistribution f = new CummulativeDistribution();
    
    Iterator i = allIndividualsIterator();
    
    while ( i.hasNext() ) {
      GPSchemeIndividual ind = (GPSchemeIndividual) i.next();
      ScmObject indScm = ind.toScheme();
      println("Checking  " +  indScm.toDisplayString() + ".. ");
      if ( matches(code, ind.toScheme()) ) {
        f.newData(ind.fitness.fitness());
      }
    }
    
    return f;
  }
  
  public boolean matches( ScmObject schema, ScmObject tree ) {
    //TODO
    return ( schema.isEqual(tree) );
  }
  
}
