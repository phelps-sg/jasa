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

import ec.EvolutionState;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.ec.EvolutionStateSingleton;
import uk.ac.liv.ec.MixedSubpopulation;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class EcjSpeciesFactory extends GPGenericIndividualFactory {
  
  protected int speciesIndex;
  
  protected int subPopulationIndex;
  
  public static final String P_SPECIES = "species";
  public static final String P_SUBPOP = "subpop";
  
  public GPGenericIndividual create() {
    try {
      
      EvolutionState state = EvolutionStateSingleton.getInstance();
      MixedSubpopulation subPop = 
        (MixedSubpopulation) state.population.subpops[subPopulationIndex];
      
      GPGenericIndividual individual = (GPGenericIndividual) 
        subPop.getSpecies(speciesIndex).newIndividual(state, subPop, null);
      
      individual.setGPContext(state, 0, null, null);
      
      return individual;
      
    } catch ( CloneNotSupportedException e ) {
      throw new Error(e);
    }    
  }
  
  public void setup( ParameterDatabase parameters, Parameter base ) {
    speciesIndex = parameters.getInt(base.push(P_SPECIES));
    subPopulationIndex = parameters.getInt(base.push(P_SUBPOP));    
  }
}

