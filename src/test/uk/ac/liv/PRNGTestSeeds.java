/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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


package test.uk.ac.liv;

/**
 * The PRNG seed to use for deterministing unit-testing of seedable classes.
 * This was introduced for ecj10, which uses a seed based on the
 * current system time when using the null argument constructor.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class PRNGTestSeeds {

  /**
   * The seed to use for all unit tests.
   */
  public static final long UNIT_TEST_SEED = 1465187;

}