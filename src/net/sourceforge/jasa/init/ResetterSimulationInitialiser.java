package net.sourceforge.jasa.init;

import net.sourceforge.jabm.Simulation;
import net.sourceforge.jabm.init.SimulationInitialiser;
import net.sourceforge.jasa.market.MarketSimulation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class ResetterSimulationInitialiser implements SimulationInitialiser {

	@Override
	public void initialise(ConfigurableListableBeanFactory beanFactory,
			Simulation simulation) {
		MarketSimulation marketSim = (MarketSimulation) simulation;
		marketSim.reset();
	}

}
