package net.sourceforge.jasa.agent.valuation;

import java.io.Serializable;

import net.sourceforge.jasa.market.Market;

public class FundamentalistForecaster extends ReturnForecasterWithTimeHorizon
		implements Serializable {

	protected double fundamentalPrice;

	public FundamentalistForecaster() {
	}
	
	@Override
	public double getNextPeriodReturnForecast(Market market) {
		double currentPrice = market.getCurrentPrice();
		if (Double.isInfinite(currentPrice) || Double.isNaN(currentPrice)) {
			return 0.0;
		} 
		if (currentPrice < 10E-5) {
			currentPrice = 10E-5;
		}
		double r = Math.log(fundamentalPrice / currentPrice);
		return r;
	}

	public double getFundamentalPrice() {
		return fundamentalPrice;
	}

	public void setFundamentalPrice(double fundamentalPrice) {
		this.fundamentalPrice = fundamentalPrice;
	}
	
}
