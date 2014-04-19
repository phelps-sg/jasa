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
import net.sourceforge.jabm.report.RelationshipTracker;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jabm.report.WeightedEdge;
import net.sourceforge.jasa.event.TransactionExecutedEvent;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class TradeNetworkReport extends AbstractModel implements
		 Report, Serializable, RelationshipTracker {
	
	protected Graph<Agent, WeightedEdge> graph = 
		new DirectedSparseGraph<Agent, WeightedEdge>();
	
	protected DijkstraShortestPath<Agent, WeightedEdge> distanceMetric;
	
	protected double maximumWeight = Double.NEGATIVE_INFINITY;

	public int interactions;

	protected double alpha = 0.95;
	
	protected double threshold = 0.01;
	
	protected Population population;
	
	static Logger logger = Logger.getLogger(TradeNetworkReport.class);

//	protected Set<Set<Agent>> communities; 
//
//	protected EdgeBetweennessClusterer<Agent, WeightedEdge> clusterer;
//	
	public static final int COMMUNITY_N = 5;

	public TradeNetworkReport() {
		super();
//		this.communities = new HashSet<Set<Agent>>();
//		this.clusterer = 
//				new EdgeBetweennessClusterer<Agent, WeightedEdge>(COMMUNITY_N);
//		distanceMetric = 
				new DijkstraShortestPath<Agent, WeightedEdge>(graph);
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
//		this.distanceMetric.reset();
//		if ((interactions % 1000) == 0) {
//			computeCommunityStructure();
//		}
	}
//	
//	public Set<Set<Agent>> getCommunities() {
//		return communities;
//	}
//	
//	public void computeCommunityStructure() {
//		if (graph.getEdges().size() > COMMUNITY_N) {
//			communities = clusterer.transform(graph);
//		}
//	}

	public void onSimulationStarting(SimulationStartingEvent event) {		
		resetGraph(event);
	}
	
	public void clearEdges() {
		ArrayList<WeightedEdge> edges = new ArrayList<WeightedEdge>(
				graph.getEdges());
		for (WeightedEdge edge : edges) {
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
		maximumWeight = Double.NEGATIVE_INFINITY;
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
//		communities = new HashSet<Set<Agent>>();
	}

	public void onTransactionExecuted(TransactionExecutedEvent event) {
		Agent seller = event.getAsk().getAgent();
		Agent buyer = event.getBid().getAgent();		
		record(seller, buyer, event);
	}
	
	public void record(Agent x, Agent y, TransactionExecutedEvent transaction) {
		
		assert x != null && y != null;
		
		TransactionList edge = (TransactionList) graph.findEdge(x, y);
		
		if (edge == null) {
			edge = new TransactionList();
			edge.add(transaction);
			if (edge.getValue() >= threshold) {
				graph.addEdge(edge, x, y);
			}
		} else {
			edge.add(transaction);
		}
		
		if (edge.getValue() > maximumWeight) {
			maximumWeight = edge.getValue();
		}
		
		if (edge.getValue() < threshold) {
			graph.removeEdge(edge);
		}
		
		LinkedList<WeightedEdge> edgeRemovals = new LinkedList<WeightedEdge>();
		for(WeightedEdge otherEdge : graph.getEdges()) {
			if (otherEdge != edge) {
				((TransactionList) otherEdge).add();
				if (otherEdge.getValue() < threshold) {
					edgeRemovals.add(otherEdge);
				}
			}
		}
		for(WeightedEdge deleteEdge : edgeRemovals) {
			graph.removeEdge(deleteEdge);
		}
//		
//		LinkedList<Agent> vertexRemovals = new LinkedList<Agent>();
//		for(Agent i : graph.getVertices()) {
//			if (graph.inDegree(i) == 0 && graph.outDegree(i) == 0) {
//				vertexRemovals.add(i);
//			}
//		}
//		for(Agent i : vertexRemovals) {
//			graph.removeVertex(i);
//		}
	}
	
	public double getMaximumInvestment() {
		return maximumWeight;
	}

	public Map<Object, Number> getVariableBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	public Graph<Agent, WeightedEdge> getGraph() {
		return graph;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double discountFactor) {
		this.alpha = discountFactor;
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
		WeightedEdge edge = graph.findEdge(i, j);
		if (edge != null) {
			return edge.getValue();
		} else {
			return 0.0;
		}
	}
	
	public double vertexStrength(Agent i) {
		double s = 0.0;
		for(WeightedEdge edge : graph.getIncidentEdges(i)) {
			s += edge.getValue();
		}
		return s;
	}
	
	public int degree(Agent i) {
		return graph.degree(i);
	}
	
	@Override
	public int outDegree(Agent i) {
		return graph.outDegree(i);
	}

	@Override
	public int inDegree(Agent vertex) {
		return graph.inDegree(vertex);
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
	
	public class TransactionList implements WeightedEdge {
		
		double value = Double.NaN;
		
//		static double recency = 0.05;
		
		public TransactionList() {
			super();
		}
		
		public void add(TransactionExecutedEvent e) {
			if (Double.isNaN(value)) {
				value = value(e);
			} else {
				value = alpha * value(e) + (1 - alpha) * value;
			}
		}
		
		public void add() {
			value = (1 - alpha) * value;
		}
		
		public double value(TransactionExecutedEvent event) {
//			return (1.0/500.0) * event.getPrice() * event.getQuantity();
			return event.getQuantity();
		}
		
		public double getValue() {
			return value;
		}
		
	}

	@Override
	public String getName() {
		return "Trade network";
	}
	
}
