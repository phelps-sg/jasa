package net.sourceforge.jasa.agent.valuation;

import net.sourceforge.jasa.agent.strategy.AbstractReturnForecaster;
import net.sourceforge.jasa.market.Market;

public class FundamentalistForecaster extends AbstractReturnForecaster {

	protected int timeHorizon;
	
	protected double fundamentalPrice;

	public FundamentalistForecaster() {
	}
	
	@Override
	public double determineValue(Market market) {
		double currentPrice = market.getQuote().getMidPoint();
		if (Double.isInfinite(currentPrice) || Double.isNaN(currentPrice)) {
			return 0.0;
		} 
		double r = Math.log(fundamentalPrice / currentPrice);
		return r ;
	}

	public int getTimeHorizon() {
		return timeHorizon;
	}

	public void setTimeHorizon(int timeHorizon) {
		this.timeHorizon = timeHorizon;
	}

	public double getFundamentalPrice() {
		return fundamentalPrice;
	}

	public void setFundamentalPrice(double fundamentalPrice) {
		this.fundamentalPrice = fundamentalPrice;
	}
	
}
