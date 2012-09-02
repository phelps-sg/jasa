package net.sourceforge.jasa.agent.valuation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;

import net.sourceforge.jabm.event.RoundFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jabm.util.MutableDoubleWrapper;
import net.sourceforge.jasa.report.MarketPriceReportVariables;

public class GeometricBrownianMotionPriceProcess extends
		MarketPriceReportVariables implements InitializingBean {

	protected double currentPrice;
	
	protected double initialPrice;
	
	protected double weinerProcess;
	
	protected double drift;
	
	protected double volatility;
	
	protected double dt;
	
	protected MutableDoubleWrapper priceWrapper = new MutableDoubleWrapper();
	
	protected RandomEngine prng;
	
	protected Normal noiseDistribution;
	
	@Override
	public void eventOccurred(SimEvent event) {
		super.eventOccurred(event);
		if (event instanceof SimulationStartingEvent) {
			onSimulationStarting((SimulationStartingEvent) event);
		}
	}
	
	public void onSimulationStarting(SimulationStartingEvent event) {
		initialise();
	}
	
	@Override
	public void onRoundFinished(RoundFinishedEvent event) {
		weinerProcess = noiseDistribution.nextDouble() * Math.sqrt(dt);
//		System.out.println(weinerProcess);
		currentPrice *= Math.exp((drift - (volatility * volatility) / 2) * dt
				+ volatility * weinerProcess);
		priceWrapper.setValue(currentPrice);
		super.onRoundFinished(event);
	}
//
//	@Override
//	public Map<Object, Number> getVariableBindings() {
//		HashMap<Object, Number> result = new HashMap<Object, Number>();
//		result.put("gbm.price", this.currentPrice);
//		return result;
//	}

	@Override
	public String getName() {
		return "GBM";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		noiseDistribution = new Normal(0.0, 1.0, prng);
		initialPrice = priceWrapper.getValue();
	}
	
	public void initialise() {
		currentPrice = initialPrice;
		weinerProcess = 0.0;
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

	@Override
	public double getPrice(RoundFinishedEvent event) {
		return this.currentPrice;
	}

	public MutableDoubleWrapper getPriceWrapper() {
		return priceWrapper;
	}

	public void setPriceWrapper(MutableDoubleWrapper priceWrapper) {
		this.priceWrapper = priceWrapper;
	}
	
	
	
}
