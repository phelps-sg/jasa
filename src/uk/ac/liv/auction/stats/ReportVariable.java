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

package uk.ac.liv.auction.stats;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class ReportVariable implements Comparable {

  protected String name;
  
  protected String description;
  
  public ReportVariable( String name, String description ) {
    this.name = name;
    this.description = description;
  }
  
  public String getDescription() {
    return description;
  }
  
  public String getName() {
    return name;
  }
  
  public String toString() {
    return name + " (" + description + ")";
  }
  
  public int compareTo( Object other ) {
    return this.name.compareTo(((ReportVariable) other).name);
  }
  
  public boolean equals( Object other ) {
    return this.name.equals(((ReportVariable) other).name);
  }
  
  public int hashCode() {
    return name.hashCode();
  }
}
