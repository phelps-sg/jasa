package net.sourceforge.jasa.init;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import net.sourceforge.jasa.market.MarketSimulation;
import net.sourceforge.jasa.sim.Simulation;
import net.sourceforge.jasa.sim.init.SimulationInitialiser;

public class ResetterSimulationInitialiser implements SimulationInitialiser {

	@Override
	public void initialise(ConfigurableListableBeanFactory beanFactory,
			Simulation simulation) {
		MarketSimulation marketSim = (MarketSimulation) simulation;
		marketSim.reset();
	}

}
