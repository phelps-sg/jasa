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

import org.apache.log4j.Logger;

public class Debug {

  static Logger logger = Logger.getLogger(Debug.class);

  public Debug() {
  }

  public static void assertTrue( String message, boolean condition ) {
    if ( ! condition ) {
      logger.error("*** ASSERTION FAILED: " + message);
      throw new Error(message);
    }
  }

  public static void assertTrue( boolean condition ) {
    assertTrue("",condition);
  }

  public static void println( String message ) {
    logger.debug(message);
  }
}