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

package uk.ac.liv.ec;

import ec.EvolutionState;
import ec.Evolve;
import ec.Population;

import ec.util.Parameter;


/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class EvolutionStateSingleton {

  protected static EvolutionState singletonInstance;

  public static final String P_POP = "pop";
  
  public static void initialise( String paramFileName ) {    
    EvolutionStateSingleton.singletonInstance = 
      Evolve.make( new String[] {"-file", paramFileName} );
    EvolutionStateSingleton.partiallyInitialise();
  }
  
  public static EvolutionState getInstance() {
    return singletonInstance;
  }
  
  protected static void partiallyInitialise() {
    singletonInstance.setup(singletonInstance, null);  // a garbage Parameter
    Parameter base = new Parameter(P_POP);
    singletonInstance.population = (Population) singletonInstance.parameters.getInstanceForParameterEq(base,null,Population.class);  // Population.class is fine
    singletonInstance.population.setup(singletonInstance, base);
  }
}
