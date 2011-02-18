/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa.agent.strategy;

import java.io.Serializable;

import net.sourceforge.jabm.util.Parameterizable;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.Order;

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
 * <td valign=top>(the quantity to bid for in each market round)</td>
 * </tr>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class FixedQuantityStrategyImpl extends AbstractTradingStrategy
    implements FixedQuantityStrategy, Parameterizable, Serializable {

	int quantity = 1;

	public FixedQuantityStrategyImpl(AbstractTradingAgent agent) {
		super(agent);
	}

	public FixedQuantityStrategyImpl() {
		this(null);
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public int determineQuantity(Market auction) {
		return quantity;
	}

	public boolean modifyShout(Order shout) {
		shout.setQuantity(quantity);
		return super.modifyShout(shout);
	}

}