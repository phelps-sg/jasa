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

package uk.ac.liv.auction.ec.gp.func;

import uk.ac.liv.ec.gp.GPSchemeNode;
import uk.ac.liv.ec.gp.func.GPGenericData;

import uk.ac.liv.prng.GlobalPRNG;

import uk.ac.liv.util.UntypedNumber;
import uk.ac.liv.util.UntypedDouble;

import edu.cornell.lassp.houle.RngPack.RandomElement;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class Perterb extends GPSchemeNode {

  public void eval( GPGenericData input ) { 
    
    evaluateChild(0, input);
    double price = ((UntypedNumber) input.data).doubleValue();
    
    evaluateChild(1, input);
    double scaling = ((UntypedNumber) input.data).doubleValue();
    
    RandomElement prng = GlobalPRNG.getInstance();
    double relative = prng.uniform(0, scaling);
    double absolute = prng.uniform(0, scaling);
    input.data = new UntypedDouble(relative*price + absolute);    
  }

  public String toString() {
    return "Perterb";
  }

}
