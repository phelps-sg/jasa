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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

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

  public static final String P_MINVALUE = "minvalue";

  public static final String P_STEP = "step";

  public void setup( ParameterDatabase parameters, Parameter base ) {
    setMinValue(parameters.getDouble(base.push(P_MINVALUE), null, 0));
    setStep(parameters.getDouble(base.push(P_STEP), null, 0));
    if ( firstValue() ) {
      setNextValue(getMinValue());
      setFirstValue(false);
    } else {
      setNextValue(getNextValue() + getStep());
    }
    setValue(getNextValue());
  }

  protected abstract void setMinValue( double value );

  protected abstract double getMinValue();

  protected abstract void setStep( double step );

  protected abstract double getStep();

  protected abstract void setNextValue( double value );

  protected abstract double getNextValue();

  protected abstract boolean firstValue();

  protected abstract void setFirstValue( boolean firstValue );

}
