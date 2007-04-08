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

import java.io.Serializable;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.util.Parameterizable;

/**
 * 
 * An abstract implementation of FixedQuantityStrategy.
 * 
 * </p>
 * <p>
 * <b>Parameters</b><br>
 * <table>
 * <tr>
 * <td valign=top><i>base</i><tt>.quantity</tt><br>
 * <font size=-1>int &gt;= 0</font></td>
 * <td valign=top>(the quantity to bid for in each auction round)</td>
 * </tr>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class FixedQuantityStrategyImpl extends AbstractStrategy
    implements FixedQuantityStrategy, Parameterizable, Serializable {

  int quantity = 1;

  public static final String P_DEF_BASE = "fixedquantitystrategy";

  static final String P_QUANTITY = "quantity";

  public FixedQuantityStrategyImpl( AbstractTradingAgent agent ) {
    super(agent);
  }

  public FixedQuantityStrategyImpl() {
    this(null);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    quantity = parameters.getIntWithDefault(base.push(P_QUANTITY), 
    		new Parameter(P_DEF_BASE).push(P_QUANTITY), quantity);
  }

  public void setQuantity( int quantity ) {
    this.quantity = quantity;
  }

  public int getQuantity() {
    return quantity;
  }

  public int determineQuantity( Auction auction ) {
    return quantity;
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    shout.setQuantity(quantity);
    return super.modifyShout(shout);
  }

}