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

import uk.ac.liv.util.AbstractSeedable;

import uk.ac.liv.prng.PRNGFactory;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A valuation policy in which we randomly determine our valuation
 * across all auctions and all units at initialisation.
 *
 * @author Steve Phelps
 * @version $Revision$
 */


public class RandomValuer extends AbstractSeedable
    implements Valuer, Serializable {

  protected double value;

  protected double minValue;

  protected double maxValue;

  public static final String P_MINVALUE = "minvalue";
  public static final String P_MAXVALUE = "maxvalue";

  static Logger logger = Logger.getLogger(RandomValuer.class);

  public RandomValuer() {
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {
    minValue = parameters.getDouble(base.push(P_MINVALUE), null, 0);
    maxValue = parameters.getDouble(base.push(P_MAXVALUE), null, 0);
  }

  public double determineValue( Auction auction ) {
    return value;
  }

  public void consumeUnit( Auction auction ) {
  }

  public void reset() {
    initialise();
  }

  public void initialise() {
    drawRandomValue();
  }


  protected void drawRandomValue() {
    value = minValue + prng.raw()*(maxValue-minValue);
  }

}
