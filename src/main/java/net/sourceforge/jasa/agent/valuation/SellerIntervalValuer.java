/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2013 Steve Phelps
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

package net.sourceforge.jasa.agent.valuation;


/**
 * <p>
 * Sellers configured with this valuation policy will receive a unique private
 * value from a common set of values starting at <code>minValue</code> and
 * incrementing by <code>step</code> as each agent is assigned a valuation.
 * </p>
 * 
 * @version $Revision$
 * @author Steve Phelps
 */

@SuppressWarnings("static-access")
public class SellerIntervalValuer extends IntervalValuer {

	/**
	 * The minimum valuation that any buyer will receive.
	 */
	protected static double minValue;

	/**
	 * The increment in valuation to use
	 */
	protected static double step;

	protected static double nextValue;

	protected static boolean firstValue = true;

	public SellerIntervalValuer() {
	}

	public SellerIntervalValuer(double minValue, double step) {
		super(minValue, step);
	}

	public boolean firstValue() {
		return firstValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public double getNextValue() {
		return nextValue;
	}

	public double getStep() {
		return step;
	}

	public void setFirstValue(boolean firstValue) {
		this.firstValue = firstValue;
	}

	public void setMinValue(double value) {
		this.minValue = value;
	}

	public void setNextValue(double value) {
		this.nextValue = value;
	}

	public void setStep(double step) {
		this.step = step;
	}

}
