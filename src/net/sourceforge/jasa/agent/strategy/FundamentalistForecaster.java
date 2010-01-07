package net.sourceforge.jasa.agent.strategy;

public class FundamentalistForecaster implements ReturnForecaster {

	protected int timeHorizon;
	
	protected double fundamentalPrice;

	public FundamentalistForecaster() {
	}
	
	@Override
	public double getReturnForecast(double currentPrice) {
		if (Double.isInfinite(currentPrice) || Double.isNaN(currentPrice)) {
			return 0.0;
		} 
		double r = Math.log(fundamentalPrice / currentPrice);
		return r / timeHorizon;
	}


	@Override
	public void setStrategy(ReturnForecastStrategy strategy) {
		// TODO Auto-generated method stub
		
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
