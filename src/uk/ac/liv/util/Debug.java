/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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


public class Debug {

  public Debug() {
  }

  public static void assert( String message, boolean condition ) {
    if ( ! condition ) {
      System.err.println("*** ASSERTION FAILED: " + message);
      throw new Error(message);
    }
  }

  public static void assert( boolean condition ) {
    assert("",condition);
  }

  public static void println( String message ) {
    System.out.println("DEBUG: " + message);
  }
}