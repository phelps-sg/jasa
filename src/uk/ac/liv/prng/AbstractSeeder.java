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

import uk.ac.liv.prng.*;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Seeder;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractSeeder extends AbstractSeedable
    implements  Parameterizable, Seeder {

  protected long prngSeed;
  
  protected long currentSeed;

  public static final String P_PRNG = "prng";
  public static final String P_SEED = "seed";

  public void setup( ParameterDatabase parameters, Parameter base ) {

    uk.ac.liv.prng.PRNGFactory.setup(parameters, base.push(P_PRNG));

    long defaultSeed = PRNGFactory.getFactory().create().ClockSeed();
    
    prngSeed =
        parameters.getLongWithDefault(base.push(P_SEED), null, defaultSeed);
                                      
    setSeed(prngSeed);
  }

  public long nextSeed() {
    return currentSeed += prng.choose(7, 1000);
  }


}