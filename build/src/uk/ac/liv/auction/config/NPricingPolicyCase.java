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

package uk.ac.liv.auction.config;

import uk.ac.liv.auction.core.NPricingPolicy;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Defines the number of the latest successful shout pairs used to determine the next
 * clearing price
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */
public class NPricingPolicyCase implements ParameterBasedCase {

  /**
   * @uml.property name="n"
   */
  private int n;

  public NPricingPolicyCase() {
  }

  public void setValue( String value ) {
    this.n = Integer.parseInt(value);
  }

  public String toString() {
    return String.valueOf(n);
  }

  public void apply( ParameterDatabase pdb, Parameter base ) {
    pdb.set(base.push(NPricingPolicy.P_N), String.valueOf(n));
  }
}