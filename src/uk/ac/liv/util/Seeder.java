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

/**
 * Classes implementing this interface are able to provide a series
 * of PRNG seeds for deep-seeding other objects.
 *
 * @see Seedable
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public interface Seeder {

  public long nextSeed();

}
