package net.sourceforge.jasa.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import net.sourceforge.jabm.Population;
import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.AbstractModel;
import net.sourceforge.jabm.event.InteractionsFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationEvent;
import net.sourceforge.jabm.event.SimulationStartingEvent;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class TradeNetworkReport extends AbstractModel implements
		 Report, Serializable {
	
	protected Graph<Agent, TransactionList> graph = 
		new DirectedSparseGraph<Agent, TransactionList>();
	
	protected DijkstraShortestPath<Agent, TransactionList> distanceMetric;
	
	protected double maximumInvestment = Double.NEGATIVE_INFINITY;

	public int interactions;

	protected double discountFactor = 0.95;
	
	protected double threshold = 0.1;
	
	protected Population population;
	
	static Logger logger = Logger.getLogger(TradeNetworkReport.class);

	public TradeNetworkReport() {
		super();
//		Transformer<RelationshipStrength, Number> transformer = 
//				new Transformer<RelationshipStrength, Number>() {
//
//			public Number transform(RelationshipStrength edge) {
//				return 1.0 / edge.getValue();
//			}
//
//		};
		distanceMetric = new DijkstraShortestPath<Agent, TransactionList>(graph);
	}

	public void eventOccurred(SimEvent ev) {
		if (ev instanceof TransactionExecutedEvent) {
			onTransactionExecuted((TransactionExecutedEvent) ev);
		} else if (ev instanceof SimulationStartingEvent) {
			onSimulationStarting((SimulationStartingEvent) ev);
		} else if (ev instanceof InteractionsFinishedEvent) {
			onInteractionsFinished((InteractionsFinishedEvent) ev);
		}
	}
	
	public void onInteractionsFinished(InteractionsFinishedEvent ev) {
		interactions++;
		this.distanceMetric.reset();
//		if ((interactions % 1000) == 0) {
//			clearEdges();
//		}
	}

	public void onSimulationStarting(SimulationStartingEvent event) {		
		resetGraph(event);
	}
	
	public void clearEdges() {
		ArrayList<TransactionList> edges = new ArrayList<TransactionList>(
				graph.getEdges());
		for (TransactionList edge : edges) {
			graph.removeEdge(edge);
		}
	}

	public void clearVertices() {
		ArrayList<Agent> vertices = new ArrayList<Agent>(graph.getVertices());		
		for(Agent vertex : vertices) {
			graph.removeVertex(vertex);
		}
	}
	
	public void resetGraph(SimulationEvent event) {
		maximumInvestment = Double.NEGATIVE_INFINITY;
		clearGraph();
		Population population = 
			event.getSimulation().getSimulationController().getPopulation();
		for(Agent agent : population.getAgents()) {
			graph.addVertex(agent);
		}
	}
	
	public void clearGraph() {
		clearEdges();
		clearVertices();
	}


	public void onTransactionExecuted(TransactionExecutedEvent event) {
		Agent x = event.getAsk().getAgent();
		Agent y = event.getBid().getAgent();		
		record(x, y, event);
	}
	
	public void record(Agent x, Agent y, TransactionExecutedEvent transaction) {
		assert x != null && y != null;
		TransactionList edge = graph.findEdge(x, y);
		if (edge == null) {
			edge = new TransactionList();
			edge.add(transaction);
			if (edge.getValue() >= threshold) graph.addEdge(edge, x, y);
		} else {
			edge.add(transaction);
//			edge.setValue(discountFactor  * edge.getValue() + (1 - discountFactor) * amount);
		}
		if (edge.getValue() > maximumInvestment) {
			maximumInvestment = edge.getValue();
		}
		if (edge.getValue() < threshold) {
			graph.removeEdge(edge);
		}
		LinkedList<TransactionList> removals = new LinkedList<TransactionList>();
		for(TransactionList otherEdge : graph.getEdges()) {
			if (otherEdge != edge) {
				otherEdge.add();
				if (otherEdge.getValue() < threshold) {
					removals.add(otherEdge);
				}
			}
		}
		for(TransactionList deleteEdge : removals) {
			graph.removeEdge(deleteEdge);
		}
	}
	
	public double getMaximumInvestment() {
		return maximumInvestment;
	}

	public Map<Object, Number> getVariableBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	public Graph<Agent, TransactionList> getGraph() {
		return graph;
	}

	public double getDiscountFactor() {
		return discountFactor;
	}

	public void setDiscountFactor(double discountFactor) {
		this.discountFactor = discountFactor;
		if (logger.isDebugEnabled()) logger.debug("discountFactor = " + discountFactor);
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public Collection<Agent> getNeighbours(Agent agent) {
		return graph.getNeighbors(agent);
	}
	
	public double edgeStrength(Agent i, Agent j) {
		TransactionList edge = graph.findEdge(i, j);
		if (edge != null) {
			return edge.getValue();
		} else {
			return 0.0;
		}
	}
	
	public double vertexStrength(Agent i) {
		double s = 0.0;
		for(TransactionList edge : graph.getIncidentEdges(i)) {
			s += edge.getValue();
		}
		return s;
	}
	
	public int degree(Agent i) {
		return graph.degree(i);
	}
	
	public int outDegree(Agent i) {
		return graph.outDegree(i);
	}
	
	public double getDistance(Agent i, Agent j) {
		Number dist = this.distanceMetric.getDistance(i, j);
		if (dist == null) {
			return 0.0;
		} else {
			return dist.doubleValue();
		}
	}

	public Population getPopulation() {
		return population;
	}

	public void setPopulation(Population population) {
		this.population = population;
	}
	
	
	public static class TransactionList {
		
		double value = 0.0;
		
		static double recency = 0.05;
		
		public TransactionList() {
			super();
		}
		
		public void add(TransactionExecutedEvent e) {
			value = recency * value(e) + (1 - recency) * value;
		}
		
		public void add() {
			value = (1 - recency) * value;
		}
		
		public double value(TransactionExecutedEvent event) {
			return event.getPrice() * event.getQuantity();
		}
		
		public double getValue() {
			return value;
		}
		
	}

}
