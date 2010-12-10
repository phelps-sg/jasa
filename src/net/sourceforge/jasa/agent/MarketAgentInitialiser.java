package net.sourceforge.jasa.agent;

import java.io.Serializable;

import net.sourceforge.jasa.market.MarketFacade;

import net.sourceforge.jasa.sim.Agent;
import net.sourceforge.jasa.sim.Population;
import net.sourceforge.jasa.sim.init.BasicAgentInitialiser;

public class MarketAgentInitialiser extends BasicAgentInitialiser implements
		Serializable {

	protected MarketFacade market;

	@Override
	public void initialise(Population population) {
		super.initialise(population);
		for (Agent agent : population.getAgents()) {
			TradingAgent trader = (TradingAgent) agent;
			trader.register(market);
		}
	}

	public MarketFacade getMarket() {
		return market;
	}

	public void setMarket(MarketFacade market) {
		this.market = market;
	}

}
