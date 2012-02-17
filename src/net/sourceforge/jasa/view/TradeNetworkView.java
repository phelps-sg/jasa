package net.sourceforge.jasa.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.InteractionsFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.report.Report;
import net.sourceforge.jasa.report.TradeNetworkReport;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

public class TradeNetworkView extends JFrame implements Report,
		Serializable {

	protected TradeNetworkReport tradeNetwork;

	protected VisualizationViewer<Agent, TradeNetworkReport.TransactionList> viewer;

	protected SpringLayout<Agent, TradeNetworkReport.TransactionList> layout;

	protected int updates = 0;

	protected JCheckBox dyadsCheckBox;

	protected boolean dyadsOnly = false;

	protected JLabel meanImageScore;

	// protected JButton exportButton;

	protected int interactions;
	
	protected int updateFrequency = 10;

	protected Graph<Agent, TradeNetworkReport.TransactionList> myGraph;

	public String fileName = "data/graph.net";

	public static final DecimalFormat fitnessFormatter = new DecimalFormat(
			"#,###,###.00");

	public static final DecimalFormat scoreFormatter = new DecimalFormat(
			"###.00");

	static Logger logger = Logger.getLogger(TradeNetworkView.class);

	public TradeNetworkView(
			final TradeNetworkReport relationshipTracker) {
		this.tradeNetwork = relationshipTracker;
		myGraph = new DirectedSparseGraph<Agent, TradeNetworkReport.TransactionList>();
		layout = new SpringLayout<Agent, TradeNetworkReport.TransactionList>(myGraph);
		// PluggableRenderer pr = new PluggableRenderer();
		// pr.setVertexStringer(this);
		// pr.setEdgeStringer(this);
		// pr.setVertexPaintFunction(this);
		// viewer = new VisualizationViewer(layout, pr);
		viewer = new VisualizationViewer<Agent, TradeNetworkReport.TransactionList>(layout);
//		viewer.getRenderContext().setVertexLabelTransformer(
//				new Transformer<Agent, String>() {
//					public String transform(Agent agent) {
//						return fitnessFormatter.format(agent.getPayoff()) + " " + ((Taggable) agent.getStrategy()).getTag();
//						// return ((ImageScoreAgent) agent).getImageScore() +
//						// "";
//						// return graph.degree(agent) + "";
//					}
////				});
//		viewer.getRenderContext().setVertexFillPaintTransformer(
//				new Transformer<Agent, Paint>() {
//					public Paint transform(Agent agent) {
//						Agent imageScoreAgent = (Agent) agent;
//							return new Color(0.0f, 1.0f, 0.0f,
//									(float) (imageScore - 0.5) * 2);
//						}
//					}
//				});
//		viewer.getRenderContext().setEdgeStrokeTransformer(
//				new Transformer<RelationshipTracker.TransactionList, Stroke>() {
//					public Stroke transform(RelationshipTracker.TransactionList strength) {
//						double r = strength.getValue()
//								/ relationshipTracker.getMaximumInvestment();
//						return new BasicStroke((float) r);
//					}
//				});
		viewer.getRenderContext().setEdgeLabelTransformer(
				new Transformer<TradeNetworkReport.TransactionList, String>() {
					public String transform(TradeNetworkReport.TransactionList strength) {
						return scoreFormatter.format(strength.getValue());
					}
				});
		viewer.setDoubleBuffered(true);
		// viewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		DefaultModalGraphMouse<Agent, TradeNetworkReport.TransactionList> gm = new DefaultModalGraphMouse<Agent, TradeNetworkReport.TransactionList>();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		viewer.setGraphMouse(gm);

		//
		//
		// viewer.getRenderContext().setEdgeLabelTransformer(
		// new ToStringLabeller());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		dyadsCheckBox = new JCheckBox("Dyads only", dyadsOnly);
		dyadsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dyadsOnly = dyadsCheckBox.getModel().isSelected();
				// updateGraph();
			}
		});
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// contentPane.add(thresholdSlider, BorderLayout.SOUTH);
		// contentPane.add(dyadsCheckBox, BorderLayout.NORTH);

		meanImageScore = new JLabel();

		// exportButton = new JButton("Export");
		// exportButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// exportGraph();
		// }
		// });
		viewer.setPreferredSize(new Dimension(1024,800));
		contentPane.add(meanImageScore, BorderLayout.NORTH);
		contentPane.add(viewer, BorderLayout.CENTER);
		// contentPane.add(exportButton, BorderLayout.SOUTH);

		// updateVertices();
		setTitle("JABM: Image score relationship graph");
		
		pack();
		setVisible(true);
	}

	public void eventOccurred(SimEvent event) {		
		if (event instanceof InteractionsFinishedEvent) {			
			onInteractionsFinished();
		} else if (event instanceof SimulationFinishedEvent) {
			onSimulationFinished();
		}
	}

	public void onInteractionsFinished() {
		interactions++;
		if ((interactions % updateFrequency) == 0) {
			updateGraph();
		}
	}
	
	public void onSimulationFinished() {
		clearGraph();
	}

	public void updateGraph() {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					viewer.getModel().getRelaxer().pause();
					clearEdges();
					Graph<Agent, TradeNetworkReport.TransactionList> graph = tradeNetwork
							.getGraph();
					Collection<TradeNetworkReport.TransactionList> edges = new LinkedList<TradeNetworkReport.TransactionList>(
							graph.getEdges());
					for (TradeNetworkReport.TransactionList edge : edges) {
						Pair<Agent> endPoints = graph.getEndpoints(edge);
						Agent first = endPoints.getFirst();
						Agent second = endPoints.getSecond();
						assert first != null && second != null;
						myGraph.addEdge(edge, first, second);
					}
					viewer.getModel().getRelaxer().resume();
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}

	public Map<Object, Number> getVariableBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearGraph() {
		clearEdges();
		clearVertices();
	}

	public void clearVertices() {
		Collection<Agent> currentVertices = new LinkedList<Agent>(
				myGraph.getVertices());
		for (Agent agent : currentVertices) {
			myGraph.removeVertex(agent);
		}
	}

	public void clearEdges() {
		Collection<TradeNetworkReport.TransactionList> currentEdges = new LinkedList<TradeNetworkReport.TransactionList>(
				myGraph.getEdges());
		for (TradeNetworkReport.TransactionList edge : currentEdges) {
			myGraph.removeEdge(edge);
		}
	}

	public int getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

}
