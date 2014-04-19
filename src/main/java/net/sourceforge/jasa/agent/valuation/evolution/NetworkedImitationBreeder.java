package net.sourceforge.jasa.agent.valuation.evolution;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.agent.AgentList;
import net.sourceforge.jabm.evolution.FitnessProportionateBreeder;
import net.sourceforge.jabm.report.WeightedEdge;
import net.sourceforge.jasa.report.TradeNetworkReport;

import org.springframework.beans.factory.annotation.Required;

import edu.uci.ics.jung.graph.Graph;

public class NetworkedImitationBreeder extends FitnessProportionateBreeder {

	protected TradeNetworkReport tradeNetwork;
	
	protected Agent currentAgent;
	
	@Override
	public AgentList reproduce(AgentList currentGeneration) {
		AgentList nextGeneration = currentGeneration;
		Graph<Agent, WeightedEdge> graph = tradeNetwork.getGraph();
		for (Agent agent : graph.getVertices()) {
			this.currentAgent = agent;
			if (graph.degree(currentAgent) > 0) {
				AgentList neighbours = new AgentList(graph.getNeighbors(currentAgent));
				double[] cummulativeFitnesses = cummulativeFitnesses(neighbours);
				if (totalFitness > 0 && !Double.isNaN(totalFitness)
						&& !Double.isInfinite(totalFitness)) {
					int j = choose(cummulativeFitnesses);
					reproduce(currentAgent, neighbours.get(j));
				}
			}
		}
		return nextGeneration;
	}
	
	@Override
	public double getFitness(Agent i) {
		double weight = 0;
		Graph<Agent, WeightedEdge> graph = tradeNetwork.getGraph();
		WeightedEdge inEdge = graph.findEdge(i,  currentAgent);
		if (inEdge != null) {
			weight += inEdge.getValue();
		}
		WeightedEdge outEdge = graph.findEdge(currentAgent, i);
		if (outEdge != null) {
			weight += outEdge.getValue();
		}
		return super.getFitness(i) * weight;
	}

	public TradeNetworkReport getTradeNetwork() {
		return tradeNetwork;
	}

	@Required
	public void setTradeNetwork(TradeNetworkReport tradeNetwork) {
		this.tradeNetwork = tradeNetwork;
	}
	
}
