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

package uk.ac.liv.auction.agent;

import uk.ac.liv.prng.GlobalPRNG;

import cern.jet.random.Uniform;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * A valuation policy in which we randomly determine our valuation across all
 * auctions and all units at agent-initialisation time. Valuations are drawn
 * from a uniform distribution with the specified range.
 * 
 * </p>
 * <p>
 * <b>Parameters </b> <br>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.minvalue</tt><br>
 * <font size=-1>double &gt;= 0 </font></td>
 * <td valign=top>(the minimum valuation)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.maxvalue</tt><br>
 * <font size=-1>double &gt;=0 </font></td>
 * <td valign=top>(the maximum valuation)</td>
 * <tr></table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class RandomValuer extends AbstractRandomValuer implements Serializable {
 
  public static final String P_MINVALUE = "minvalue";

  public static final String P_MAXVALUE = "maxvalue";

  static Logger logger = Logger.getLogger(RandomValuer.class);

  public RandomValuer() {
    super();
  }

  public RandomValuer( double minValue, double maxValue ) {
    super(minValue, maxValue);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    minValue = parameters.getDouble(base.push(P_MINVALUE), null, 0);
    maxValue = parameters.getDouble(base.push(P_MAXVALUE), null, minValue);
    initialise();
  }

  public void initialise() {
    distribution = new Uniform(minValue, maxValue, GlobalPRNG.getInstance());
    drawRandomValue();
  }

}
