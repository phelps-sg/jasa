package net.sourceforge.jasa.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import net.sourceforge.jabm.agent.Agent;
import net.sourceforge.jabm.event.InteractionsFinishedEvent;
import net.sourceforge.jabm.event.SimEvent;
import net.sourceforge.jabm.event.SimulationFinishedEvent;
import net.sourceforge.jabm.report.ReportWithGUI;
import net.sourceforge.jabm.report.WeightedEdge;
import net.sourceforge.jasa.agent.AbstractTradingAgent;
import net.sourceforge.jasa.agent.MarketMakerAgent;
import net.sourceforge.jasa.agent.valuation.LinearWeightedReturnForecaster;
import net.sourceforge.jasa.agent.valuation.ReturnForecastValuationPolicy;
import net.sourceforge.jasa.report.TradeNetworkReport;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.pf.joi.Inspector;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;

public class TradeNetworkView  implements ReportWithGUI,
		Serializable {

	protected TradeNetworkReport tradeNetwork;

	protected VisualizationViewer<Agent, WeightedEdge> viewer;

	protected SpringLayout<Agent, WeightedEdge> layout;

	protected int updates = 0;

	protected JCheckBox dyadsCheckBox;

	protected boolean dyadsOnly = false;

	// protected JButton exportButton;

	protected int interactions;
	
	protected int updateFrequency = 10;

	protected Graph<Agent, WeightedEdge> myGraph;

	public String fileName = "data/graph.net";

	public static final DecimalFormat fitnessFormatter = new DecimalFormat(
			"#,###,###.00");

	public static final DecimalFormat scoreFormatter = new DecimalFormat(
			"###.00");

	static Logger logger = Logger.getLogger(TradeNetworkView.class);

	public TradeNetworkView(
			final TradeNetworkReport tradeNetwork) {
		
		this.tradeNetwork = tradeNetwork;
		myGraph = new DirectedSparseGraph<Agent, WeightedEdge>();
		layout = new SpringLayout<Agent, WeightedEdge>(myGraph);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				intialiseGUI();
			}
		});
	}
		
		
	public void intialiseGUI() {
		// PluggableRenderer pr = new PluggableRenderer();
		// pr.setVertexStringer(this);
		// pr.setEdgeStringer(this);
		// pr.setVertexPaintFunction(this);
		// viewer = new VisualizationViewer(layout, pr);
		viewer = new VisualizationViewer<Agent, WeightedEdge>(layout);
		viewer.setSize(2024, 1600);
		layout.setSize(new Dimension(2024,1600));
		
//		viewer.getRenderContext().setVertexLabelTransformer(
//				new Transformer<Agent, String>() {
//					public String transform(Agent agent) {
//						int i = 0;
//						for (Set<Agent> community : tradeNetwork
//								.getCommunities()) {
//							if (community.contains(agent)) {
//								return i + "";
//							}
//							i++;
//						}
//						return "";
//						return tradeNetwork.getCommunities().size() + "";
//				}
//				});
		
		viewer.getRenderContext().setVertexFillPaintTransformer(
				new Transformer<Agent, Paint>() {
					public Paint transform(Agent agent) {
						Color result = null;
						float alpha = 0.3f;
						if (agent instanceof MarketMakerAgent) {
							alpha = 1.0f;
						} else {
							alpha = 0.3f;
						}
						LinearWeightedReturnForecaster forecaster = 
								(LinearWeightedReturnForecaster) ((ReturnForecastValuationPolicy) ((AbstractTradingAgent) agent)
								.getValuationPolicy()).getForecaster();
						double[] weights = forecaster.getWeights();
						float[] colors = new float[3];
						float maxWeight = Float.NEGATIVE_INFINITY;
						for (int i = 0; i < weights.length; i++) {
							if (Math.abs(weights[i]) > maxWeight) {
								maxWeight = (float) Math.abs(weights[i]);
							}
						}
						for (int i = 0; i < colors.length; i++) {
							colors[i] = (float) Math.abs(weights[i])
									/ maxWeight;
						}
						result = new Color(colors[0], colors[1], colors[2],
								alpha);
						return result;
					}
				});
		
		viewer.getRenderContext().setEdgeStrokeTransformer(
				new Transformer<WeightedEdge, Stroke>() {
					public Stroke transform(WeightedEdge strength) {
						double r = strength.getValue();
//								/ tradeNetwork.getMaximumInvestment();
						return new BasicStroke((float) r * 2.0f); 
					}
				});
		
		viewer.getRenderContext().setEdgeLabelTransformer(
				new Transformer<WeightedEdge, String>() {
					public String transform(WeightedEdge strength) {
						return scoreFormatter.format(strength.getValue());
					}
				});
		
		viewer.setDoubleBuffered(true);
		
		// viewer.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		DefaultModalGraphMouse<Agent, TradeNetworkReport.TransactionList> gm = new DefaultModalGraphMouse<Agent, TradeNetworkReport.TransactionList>();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		GraphMousePlugin plugin = new AgentPickingGraphMousePlugin();
		gm.add(plugin);
		viewer.setGraphMouse(gm);

		//
		//
		// viewer.getRenderContext().setEdgeLabelTransformer(
		// new ToStringLabeller());
//		Container contentPane = getContentPane();
//		contentPane.setLayout(new BorderLayout());

//		dyadsCheckBox = new JCheckBox("Dyads only", dyadsOnly);
//		dyadsCheckBox.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				dyadsOnly = dyadsCheckBox.getModel().isSelected();
//				// updateGraph();
//			}
//		});
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// contentPane.add(thresholdSlider, BorderLayout.SOUTH);
		// contentPane.add(dyadsCheckBox, BorderLayout.NORTH);

		// exportButton = new JButton("Export");
		// exportButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// exportGraph();
		// }
		// });
		viewer.setPreferredSize(new Dimension(1024,800));
//		contentPane.add(viewer, BorderLayout.CENTER);
		// contentPane.add(exportButton, BorderLayout.SOUTH);

		// updateVertices();
//		setTitle("JASA: Trade network graph");
		
//		pack();	
//		setVisible(true);
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
					Graph<Agent, WeightedEdge> graph = tradeNetwork
							.getGraph();
					Collection<WeightedEdge> edges = new LinkedList<WeightedEdge>(
							graph.getEdges());
					for (WeightedEdge edge : edges) {
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
		Collection<WeightedEdge> currentEdges = new LinkedList<WeightedEdge>(
				myGraph.getEdges());
		for (WeightedEdge edge : currentEdges) {
			myGraph.removeEdge(edge);
		}
	}

	public int getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	@Override
	public String getName() {
		return "Trade network";
	}
	
	public static class AgentPickingGraphMousePlugin<V, E> extends
			PickingGraphMousePlugin<V, E> {

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				Point2D ip = e.getPoint();
				VisualizationViewer<V, E> vv = (VisualizationViewer) e.getSource();
				GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
				Layout<V, E> layout = vv.getGraphLayout();
				vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
				if (vertex != null) {
					Inspector.inspect(vertex);
				}
			}
		}

	}

	@Override
	public JComponent getComponent() {
		return viewer;
	}
	
}
