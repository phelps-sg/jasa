/**
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

package net.sourceforge.jasa.market.rules;

import java.io.Serializable;

import net.sourceforge.jabm.util.Parameterizable;

/**
 * Abstract superclass for auctioneer pricing policies parameterised by k.
 * 
 * <p>
 * <b>Parameters </b> <br>
 * </p>
 * <table>
 * <tr>
 * <td valign=top><i>base </i> <tt>.k</tt><br>
 * <font size=-1>0 <=int <=1 </font></td>
 * <td valign=top>(determining a value in a given price range)</td>
 * <tr></table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class KPricingPolicy implements Serializable, PricingPolicy,
    Parameterizable {

	protected double k = 0.5;

	public KPricingPolicy() {
		this(0);
	}

	public KPricingPolicy(double k) {
		this.k = k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public double getK() {
		return k;
	}

	public double kInterval(double a, double b) {
		return k * b + (1 - k) * a;
	}

	public String toString() {
		return "(" + getClass().getSimpleName() + " k:" + k + ")";
	}

}