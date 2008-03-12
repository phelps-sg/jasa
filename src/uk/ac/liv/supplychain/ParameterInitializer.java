package uk.ac.liv.supplychain;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.util.Parameterizable;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

public abstract class ParameterInitializer implements Parameterizable {

	public static final String P_DEF_BASE = "margin";

	public static final String P_MINVALUE = "minvalue";

	public static final String P_MAXVALUE = "maxvalue";

	protected AbstractContinousDistribution distribution;

	/**
	 * The minimum valuation to use.
	 */
	protected double minValue;

	/**
	 * The maximum valuation to use.
	 */
	protected double maxValue;

	public ParameterInitializer() {
		// super();
		// TODO Auto-generated constructor stub
	}

	public abstract double getValue();

	public void setup(ParameterDatabase parameters, Parameter base) {

		Parameter defBase = base.push(P_DEF_BASE);

		minValue = parameters.getDouble(defBase.push(P_MINVALUE), null, 0);
		maxValue = parameters.getDouble(defBase.push(P_MAXVALUE), null, minValue);
		initialise();
	}

	public void initialise() {
		distribution = new Uniform(minValue, maxValue, GlobalPRNG.getInstance());
	}

}
