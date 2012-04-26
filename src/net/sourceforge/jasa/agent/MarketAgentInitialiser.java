package net.sourceforge.jasa.agent;

import java.io.Serializable;

import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.init.BasicAgentInitialiser;
import net.sourceforge.jasa.market.Market;

public class MarketAgentInitialiser extends BasicAgentInitialiser implements
		Serializable {

	protected Market market;

	@Override
	public void initialise(Population population) {
		super.initialise(population);
		for (Agent agent : population.getAgents()) {
			TradingAgent trader = (TradingAgent) agent;
			trader.register(market);
		}
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

}
