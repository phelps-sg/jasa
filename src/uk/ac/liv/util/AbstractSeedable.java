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


package uk.ac.liv.util;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import uk.ac.liv.prng.PRNGFactory;

/**
 * Abstract class for objects making use of a PRNG.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class AbstractSeedable implements Seedable {

  protected RandomElement prng = PRNGFactory.getFactory().create();

  /**
   *  Set the PRNG seed.
   */
  public void setSeed( long seed ) {
    prng = PRNGFactory.getFactory().create(seed);
  }

  public void seed( Seeder seeder ) {
    setSeed(seeder.nextSeed());
  }

}
