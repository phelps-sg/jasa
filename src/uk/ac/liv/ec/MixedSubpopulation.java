/*
 * JASA Java Auction Simulator API Copyright (C) 2001-2004 Steve Phelps
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package uk.ac.liv.ec;

import ec.EvolutionState;
import ec.Subpopulation;
import ec.Species;
import ec.Individual;
import ec.Fitness;

import ec.util.Parameter;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class MixedSubpopulation extends Subpopulation {

  protected Species[] speciesArray;

  protected int[] numIndividuals;

  protected int numSpecies;

  public static final String P_NUMSPECIES = "numspecies";

  public static final String P_NUMINDIVIDUALS = "numindividuals";

  public void setup( EvolutionState state, Parameter base ) {

    int size = state.parameters.getInt(base.push(P_SUBPOPSIZE), null, 1);

    individuals = new Individual[size];

    f_prototype = 
      (Fitness) state.parameters.getInstanceForParameter(base.push(P_FITNESS), 
                                                          null, Fitness.class);
    f_prototype.setup(state, base.push(P_FITNESS));

    numSpecies = state.parameters.getInt(base.push(P_NUMSPECIES), null);
    speciesArray = new Species[numSpecies];
    numIndividuals = new int[numSpecies];

    for( int i = 0; i < numSpecies; i++ ) {
      Parameter speciesParam = base.push(P_SPECIES).push(i + "");
      speciesArray[i] = (Species) state.parameters.getInstanceForParameter(
          speciesParam, null, Species.class);
      speciesArray[i].setup(state, speciesParam);
      numIndividuals[i] = 
        state.parameters.getInt(speciesParam.push(P_NUMINDIVIDUALS), null);
    }

    species = speciesArray[0];
  }

  public Species getSpecies( int n ) {
    return speciesArray[n];
  }
  
  public void populate( EvolutionState state ) {
    try {
      int currentIndividual = 0;
      for( int s = 0; s < numSpecies; s++ ) {
        for( int i = 0; i < numIndividuals[s]; i++ ) {
          individuals[currentIndividual++] = speciesArray[s].newIndividual(
              state, this, (Fitness) (f_prototype.protoClone()));
        }
      }
    } catch ( CloneNotSupportedException e ) {
      throw new Error(e);
    }
  }

}