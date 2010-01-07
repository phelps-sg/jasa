package net.sourceforge.jasa.agent.strategy;

public interface ReturnForecaster {

	public double getReturnForecast(double currentPrice);

	public void setStrategy(ReturnForecastStrategy strategy);
}
