package uk.ac.liv.supplychain;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * Allows several agents to uniformly divide the interval [minvalue, maxvalue].
 * This only works when numintervals>=2, else a random number from [minvalue,
 * maxvalue] is returned.
 * 
 * @author moyaux
 * 
 */

public class DoubleFromInterval extends ParameterInitializer {

	public static final String P_DEF_BASE = "margin";

	public static final String P_NUM_INTERVALS = "numintervals";

	protected static double previousAllocatedValue = Double.NaN;

	protected static boolean firstAllocatedValue = true;

	protected double numberOfIntervals = 0;

	protected double toReturn = 0;

	public DoubleFromInterval() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setup(ParameterDatabase parameters, Parameter base) {
		super.setup(parameters, base);

		Parameter defBase = base.push(P_DEF_BASE);
		numberOfIntervals = parameters.getDouble(defBase.push(P_NUM_INTERVALS),
		    null, 0);

		if (numberOfIntervals < 2)
			toReturn = distribution.nextDouble();
		else if (firstAllocatedValue) {
			toReturn = minValue;
			firstAllocatedValue = false;
		} else
			toReturn = previousAllocatedValue
			    + ((maxValue - minValue) / numberOfIntervals);

		previousAllocatedValue = toReturn;
	}// setup

	public double getValue() {
		return toReturn;
	}// getValue

}
