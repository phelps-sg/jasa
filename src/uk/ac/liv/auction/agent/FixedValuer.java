/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import uk.ac.liv.auction.core.Auction;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

/**
 * A valuation policy in which we maintain a fixed private valuation
 * independent of time or auction.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class FixedValuer implements Valuer, Serializable {

  protected double value;

  public static final String P_VALUE = "value";

  public FixedValuer() {
  }

  public FixedValuer( double value ) {
    this.value = value;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    value = parameters.getDouble(base.push(P_VALUE), null, 0);
  }

  public double determineValue( Auction auction ) {
    return value;
  }

  public void consumeUnit( Auction auction ) {
  }

  public void reset() {
  }

  public void setValue( double value ) {
    this.value = value;
  }

  public String toString() {
    return "(" + getClass() + " value:" + value + ")";
  }


}