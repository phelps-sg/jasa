package uk.ac.liv.prng;

import edu.cornell.lassp.houle.RngPack.RandomElement;
import edu.cornell.lassp.houle.RngPack.Ranmar;

/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

public class Mar extends PRNGFactory {

  public RandomElement create() {
    return new Ranmar();
  }

  public RandomElement create( long seed ) {
    return new Ranmar(seed);
  }

  public String getDescription() {
    return "Lagged Fibonacci generator (Marsaglia and Zaman)";
  }
}
