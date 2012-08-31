package net.sourceforge.jasa.agent.valuation;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.Report;

public class GeometricBrownianMotionPriceProcess extends Number implements
		Report, InitializingBean {

	protected Double currentPrice;
	
	protected Double initialPrice;
	
	protected double weinerProcess;
	
	protected double drift;
	
	protected double volatility;
	
	protected double dt;
	
	protected RandomEngine prng;
	
	protected Normal noiseDistribution;
	
	@Override
	public void eventOccurred(SimEvent event) {
		if (event instanceof RoundFinishedEvent) {
			onRoundFinished((RoundFinishedEvent) event);
		} else if (event instanceof SimulationStartingEvent) {
			onSimulationStarting((SimulationStartingEvent) event);
		}
	}
	
	public void onSimulationStarting(SimulationStartingEvent event) {
		initialise();
	}
	
	public void onRoundFinished(RoundFinishedEvent event) {
		weinerProcess += noiseDistribution.nextDouble() * Math.sqrt(dt);
		currentPrice *= Math.exp((drift - (volatility * volatility) / 2) * dt
				+ volatility * weinerProcess);
	}

	@Override
	public Map<Object, Number> getVariableBindings() {
		return null;
	}

	@Override
	public String getName() {
		return "GBMprice";
	}

	@Override
	public int intValue() {
		return currentPrice.intValue();
	}

	@Override
	public long longValue() {
		return currentPrice.longValue();
	}

	@Override
	public float floatValue() {
		return currentPrice.floatValue();
	}

	@Override
	public double doubleValue() {
		return currentPrice.doubleValue();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		noiseDistribution = new Normal(0.0, 1.0, prng);
	}
	
	public void initialise() {
		currentPrice = initialPrice;
		weinerProcess = 0.0;
	}

	public Double getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(Double initialPrice) {
		this.initialPrice = initialPrice;
	}

	public double getDrift() {
		return drift;
	}

	public void setDrift(double drift) {
		this.drift = drift;
	}

	public double getVolatility() {
		return volatility;
	}

	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}

	public double getDt() {
		return dt;
	}

	public void setDt(double dt) {
		this.dt = dt;
	}

	public RandomEngine getPrng() {
		return prng;
	}

	@Required
	public void setPrng(RandomEngine prng) {
		this.prng = prng;
	}
	
}
