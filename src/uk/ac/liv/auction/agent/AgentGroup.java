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

package uk.ac.liv.auction.agent;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class AgentGroup {
  
  protected String description;

  public static final int MAX_GROUPS = 100;
  
  private static AgentGroup[] groups = new AgentGroup[MAX_GROUPS];
  
  public AgentGroup( String description ) {
    this.description = description;
  }
  
  public String toString() {
    return "(" + getClass() + " description:\"" + description + "\")";
  }
  
  public static AgentGroup getAgentGroup( int n ) {
    if ( groups[n] == null ) {
      groups[n] = new AgentGroup("group " + n);
    }
    return groups[n];
  }

}
