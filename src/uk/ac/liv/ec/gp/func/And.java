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

package uk.ac.liv.ec.gp.func;

import uk.ac.liv.ec.gp.*;

public class And extends GPSchemeNode {

  public void eval( GPGenericData input ) {                      

  	evaluateChild(0, input);    
    Boolean result1 = (Boolean) input.data;

    evaluateChild(1, input);    
    Boolean result2 = (Boolean) input.data;

    input.data = new Boolean(result1.booleanValue() && result2.booleanValue());
  }

  public String toString() {
    return "And";
  }
}