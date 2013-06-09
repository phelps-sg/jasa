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
 * Agents configured with this valuation policy will receive a unique private
 * value from a common set of values starting at <code>minValue</code> and
 * incrementing by <code>step</code> as each agent is assigned a valuation at
 * agent setup time. This is useful for quickly specifying supply or demand
 * curves with a constant "slope" (step).
 * </p>
 * 
 * @version $Revision$
 * @author Steve Phelps
 */

public abstract class IntervalValuer extends FixedValuer {

	public static final String P_DEF_BASE = "intervalvaluer";

	public static final String P_MINVALUE = "minvalue";

	public static final String P_STEP = "step";

	public IntervalValuer() {
	}

	public IntervalValuer(double minValue, double step) {
		setMinValue(minValue);
		setStep(step);
		initialise();
	}

	public void initialise() {
		if (firstValue()) {
			setNextValue(getMinValue());
			setFirstValue(false);
		} else {
			setNextValue(getNextValue() + getStep());
		}
		setValue(getNextValue());
	}

	public void reset() {
		setFirstValue(true);
	}

	public abstract void setMinValue(double value);

	public abstract double getMinValue();

	public abstract void setStep(double step);

	public abstract double getStep();

	public abstract void setNextValue(double value);

	public abstract double getNextValue();

	public abstract boolean firstValue();

	public abstract void setFirstValue(boolean firstValue);

}
