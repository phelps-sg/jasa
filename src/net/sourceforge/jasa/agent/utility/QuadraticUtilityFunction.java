package net.sourceforge.jasa.agent.utility;

import net.sourceforge.jasa.agent.TradingAgent;
import net.sourceforge.jasa.market.Market;

public class QuadraticUtilityFunction extends AbstractUtilityFunction {

	protected double alpha;
	
	protected double beta;
	
	public QuadraticUtilityFunction(TradingAgent agent, double alpha,
			double beta) {
		super(agent);
		this.alpha = alpha;
		this.beta = beta;
	}

	public QuadraticUtilityFunction() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculatePayoff(Market auction, int quantity, double price) {
		double profit = calculateProfit(auction, quantity, price);
		return alpha * profit  +  beta * profit * profit;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	
}
