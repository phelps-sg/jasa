package net.sourceforge.jasa.init;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import net.sourceforge.jabm.Simulation;
import net.sourceforge.jabm.init.SimulationInitialiser;
import net.sourceforge.jasa.market.MarketSimulation;

public class ResetterSimulationInitialiser implements SimulationInitialiser {

	@Override
	public void initialise(ConfigurableListableBeanFactory beanFactory,
			Simulation simulation) {
		MarketSimulation marketSim = (MarketSimulation) simulation;
		marketSim.reset();
	}

}
