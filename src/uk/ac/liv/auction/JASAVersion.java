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
package uk.ac.liv.auction;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class JASAVersion {
  
  static final int minorVersion = 34;
  
  static final int majorVersion = 0;

  public static final String GNU_MESSAGE =
    "\n" +
    "JASA v" + getVersion() + " - (C) 2001-2005 Steve Phelps\n" +
    "JASA comes with ABSOLUTELY NO WARRANTY. This is free software,\n" +
    "and you are welcome to redistribute it under certain conditions;\n" +
    "see the GNU General Public license for more details.\n\n" +
    "This is alpha test software.  Please report any bugs, issues\n" +
    "or suggestions to sphelps@csc.liv.ac.uk.\n";
  
  public static String getVersion() {
    return majorVersion + "." + minorVersion; 
  }
  
  public static int getMinorVersion() {
    return minorVersion;
  }
  
  public static int getMajorVersion() {
    return majorVersion;
  }
  
  public static String getGnuMessage() {
    return GNU_MESSAGE;
  }
  
  public static void main( String[] args ) {
    System.out.println("JASA v" + getVersion());
  }
  
  
}
