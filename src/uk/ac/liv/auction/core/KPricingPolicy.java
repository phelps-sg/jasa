/**
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

package uk.ac.liv.auction.core;

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Abstract superclass for auctioneer pricing policies parameterised by
 * k.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class KPricingPolicy implements PricingPolicy, Parameterizable {

  protected double k;

  public static final String P_K = "k";

  public KPricingPolicy() {
    this(0);
  }

  public KPricingPolicy( double k ) {
    this.k = k;
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    k = parameters.getDoubleWithDefault(base.push(P_K), null, 0);
  }


  public void setK( double k ) {
    this.k = k;
  }

  public double getK() {
    return k;
  }

  public double kInterval( double a, double b ) {
    return k*a + (1-k)*b;
  }

}