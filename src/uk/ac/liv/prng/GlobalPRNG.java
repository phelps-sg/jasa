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

package uk.ac.liv.prng;

import edu.cornell.lassp.houle.RngPack.RandomElement;
import edu.cornell.lassp.houle.RngPack.RandomSeedable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GlobalPRNG {
	
	protected static RandomElement prng;
	
	protected static long seed;
	
	public static final String P_SEED = "seed";
	public static final String P_PRNG = "prng";
	
  public static void setup( ParameterDatabase parameters, Parameter base ) {

    uk.ac.liv.prng.PRNGFactory.setup(parameters, base.push(P_PRNG));

    long defaultSeed = RandomSeedable.ClockSeed();
    
    seed =
        parameters.getLongWithDefault(base.push(P_SEED), null, defaultSeed);
                                      
    prng = PRNGFactory.getFactory().create(seed);
  }
  
	public static RandomElement getInstance() {
		return prng;	
	}
	
	public static long getSeed() {
		return seed;
	}
	
	public static void initialiseWithSeed( long seed ) {
		GlobalPRNG.seed = seed;
		prng = PRNGFactory.getFactory().create(seed);
	}
	
}
