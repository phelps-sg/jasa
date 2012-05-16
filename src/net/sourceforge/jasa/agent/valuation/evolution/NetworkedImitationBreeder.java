package net.sourceforge.jasa.agent.valuation.evolution;

import org.springframework.beans.factory.annotation.Required;

import edu.uci.ics.jung.graph.Graph;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.evolution.FitnessProportionateBreeder;
import net.sourceforge.jabm.report.WeightedEdge;
import net.sourceforge.jasa.report.TradeNetworkReport;

public class NetworkedImitationBreeder extends FitnessProportionateBreeder {

	protected TradeNetworkReport tradeNetwork;

	@Override
	public AgentList reproduce(AgentList currentGeneration) {
		AgentList nextGeneration = currentGeneration;
		Graph<Agent, WeightedEdge> graph = tradeNetwork.getGraph();
		for (Agent agent : graph.getVertices()) {
			if (graph.degree(agent) > 0) {
				AgentList neighbours = new AgentList(graph.getNeighbors(agent));
				double[] cummulativeFitnesses = cummulativeFitnesses(neighbours);
				if (totalFitness > 0 && !Double.isNaN(totalFitness)
						&& !Double.isInfinite(totalFitness)) {
					int j = choose(cummulativeFitnesses);
					reproduce(agent, neighbours.get(j));
				}
			}
		}
		return nextGeneration;
	}

	public TradeNetworkReport getTradeNetwork() {
		return tradeNetwork;
	}

	@Required
	public void setTradeNetwork(TradeNetworkReport tradeNetwork) {
		this.tradeNetwork = tradeNetwork;
	}
	
}
