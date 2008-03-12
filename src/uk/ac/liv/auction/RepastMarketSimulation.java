/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.auction;

import java.awt.Dimension;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.plot.OpenGraph;
import uchicago.src.sim.engine.Controller;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Discrete2DSpace;
import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.stats.GraphReport;
import uk.ac.liv.auction.stats.RepastGraphSequence;
import uk.ac.liv.auction.ui.DrawableAgentAdaptor;
import uk.ac.liv.auction.ui.RepastAuctionConsoleGraph;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.prng.PRNGFactory;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * <p>
 * A RePast model of an auction simulation. This application takes as an
 * argument the name of a parameter file describing an auction experiment, and
 * proceeds to run that experiment interactively using the RePast framework. For
 * unattended batch experiments, use the MarketSimulation application.
 * 
 * @see MarketSimulation
 *      </p>
 * 
 * <p>
 * <b>Parameters </b> <br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base </i> <tt>.auction</tt><br>
 * <font size=-1>classname inherits uk.ac.liv.auction.core.RoundRobinAuction
 * </font></td>
 * <td valign=top>(the class of auction to use)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class RepastMarketSimulation extends SimModelImpl implements
    Serializable {

	/**
	 * The auction used in this simulation.
	 * 
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	protected RandomRobinAuction auction;

	/**
	 * @uml.property name="parameterFileName"
	 */
	protected String parameterFileName;

	/**
	 * @uml.property name="parameterDescriptors"
	 */
	protected Hashtable parameterDescriptors;

	/**
	 * @uml.property name="schedule"
	 * @uml.associationEnd
	 */
	protected Schedule schedule;

	/**
	 * @uml.property name="graph"
	 * @uml.associationEnd
	 */
	protected OpenSequenceGraph graph;

	/**
	 * @uml.property name="displaySurface"
	 * @uml.associationEnd
	 */
	protected DisplaySurface displaySurface;

	/**
	 * @uml.property name="agentSpace"
	 * @uml.associationEnd inverse="this$0:uk.ac.liv.auction.RepastMarketSimulation$AgentSpace"
	 */
	protected AgentSpace agentSpace;

	/**
	 * @uml.property name="auxGraphs"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="uchicago.src.sim.analysis.plot.OpenGraph"
	 */
	protected LinkedList auxGraphs = new LinkedList();

	protected static RepastMarketSimulation modelSingleton;

	public static final String P_AUCTION = "auction";

	public static final String P_SIMULATION = "simulation";

	static Logger logger = Logger.getLogger("JASA");

	public static void main(String[] args) {

		SimInit init = new SimInit();

		if (args.length < 1) {
			fatalError("You must specify a parameter file");
		}

		modelSingleton = new RepastMarketSimulation(args[0]);

		init.loadModel(modelSingleton, null, false);
	}

	public RepastMarketSimulation(String parameterFileName) {
		this.parameterFileName = parameterFileName;
		parameterDescriptors = new Hashtable();
		schedule = new Schedule();
		schedule.scheduleActionBeginning(1, this, "step");
		schedule.scheduleActionAtEnd(this, "end");
	}

	public RepastMarketSimulation() {
		this(null);
	}

	public static RepastMarketSimulation getModelSingleton() {
		return modelSingleton;
	}

	public void setup() {

		try {

			gnuMessage();

			File file = new File(parameterFileName);
			if (!file.canRead()) {
				fatalError("Cannot read parameter file " + parameterFileName);
			}

			org.apache.log4j.PropertyConfigurator.configure(parameterFileName);

			ParameterDatabase parameters = new ParameterDatabase(file);
			setup(parameters, new Parameter(P_SIMULATION));

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	public void setup(ParameterDatabase parameters, Parameter base) {

		logger.debug("Setup... ");

		GlobalPRNG.setup(parameters, base);

		auction = (RandomRobinAuction) parameters.getInstanceForParameterEq(base
		    .push(P_AUCTION), null, RandomRobinAuction.class);

		auction.setup(parameters, base.push(P_AUCTION));

		logger.info("prng = " + PRNGFactory.getFactory().getDescription());
		logger.info("seed = " + GlobalPRNG.getSeed() + "\n");

		logger.debug("Setup complete.");

	}

	public void begin() {
		buildDisplay();
		auction.begin();
	}

	public void step() {
		try {
			auction.step();
		} catch (AuctionClosedException e) {
			getController().stopSim();
		}
		if (graph != null) {
			graph.step();
		}
		if (auxGraphs != null) {
			Iterator i = auxGraphs.iterator();
			while (i.hasNext()) {
				OpenGraph auxGraph = (OpenGraph) i.next();
				auxGraph.step();
			}
		}
		displaySurface.updateDisplay();
		// ProbeUtilities.updateProbePanels();
	}

	public void end() {
		auction.end();
		auction.generateReport();
	}

	public String getName() {
		return "JASA auction simulation";
	}

	/**
	 * @uml.property name="parameterFileName"
	 */
	public String getParameterFileName() {
		return parameterFileName;
	}

	/**
	 * @uml.property name="parameterFileName"
	 */
	public void setParameterFileName(String parameterFileName) {
		this.parameterFileName = parameterFileName;
	}

	/**
	 * @uml.property name="schedule"
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	public String[] getInitParam() {
		return new String[] { "parameterFileName" };
	}

	public static void gnuMessage() {
		System.out.println(JASAVersion.getGnuMessage());
	}

	protected static void fatalError(String message) {
		System.err.println("ERROR: " + message);
		System.exit(1);
	}

	public void generateNewSeed() {
		GlobalPRNG.generateNewSeed();
		logger.debug("Repast is changing the PRNG seed");
	}

	/**
	 * @uml.property name="parameterDescriptors"
	 */
	public Hashtable getParameterDescriptors() {
		return parameterDescriptors;
	}

	public String getPropertiesValues() {
		return "";
	}

	public long getRngSeed() {
		return GlobalPRNG.getSeed();
	}

	public double getTickCount() {
		return auction.getAge();
	}

	public void setRngSeed(long seed) {
		logger.debug("Repast is changing the PRNG seed to " + seed);
		logger.warn("PRNG seed changed to " + seed);
		GlobalPRNG.initialiseWithSeed(seed);
	}

	public void addGraphSequence(RepastGraphSequence graphSequence) {
		OpenSequenceGraph graph = new OpenSequenceGraph(graphSequence.getName(),
		    this);
		graph.addSequence(graphSequence.getName(), graphSequence);
		auxGraphs.add(graph);
	}

	protected void buildDisplay() {

		GraphReport graphLogger;
		if ((graphLogger = GraphReport.getSingletonInstance()) != null) {
			graph = new RepastAuctionConsoleGraph("JASA graph for "
			    + auction.getName(), this, graphLogger);
			graph.display();
		}

		Iterator i = auxGraphs.iterator();
		while (i.hasNext()) {
			OpenGraph graph = (OpenGraph) i.next();
			graph.display();
		}

		displaySurface = new DisplaySurface(this, "JASA agents for "
		    + auction.getName());
		agentSpace = new AgentSpace(auction);
		Object2DDisplay agentDisplay = new Object2DDisplay(agentSpace);
		displaySurface.addDisplayableProbeable(agentDisplay, "agents");
		displaySurface.setPreferredSize(new Dimension(640, 480));
		displaySurface.display();
		addSimEventListener(displaySurface);

		((Controller) getController()).UPDATE_PROBES = true;
	}

	class AgentMatrix implements BaseMatrix {

		protected ArrayList agents;

		protected int height;

		protected int width;

		public AgentMatrix(ArrayList agents, int height, int width) {
			this.agents = agents;
			this.height = height;
			this.width = width;
		}

		public Object get(int col, int row) {
			int abs = row * (width - 1) + col;
			if (abs >= agents.size()) {
				return new DrawableAgentAdaptor(auction);
			}
			DrawableAgentAdaptor agent = (DrawableAgentAdaptor) agents.get(abs);
			if (agent == null) {
				return new DrawableAgentAdaptor(auction);
			}
			return agent;
		}

		public int getNumCols() {
			// TODO Auto-generated method stub
			return width;
		}

		public int getNumRows() {
			// TODO Auto-generated method stub
			return height;
		}

		public void put(int col, int row, Object obj) {
			// TODO Auto-generated method stub

		}

		public Object remove(int col, int row) {
			// TODO Auto-generated method stub
			return null;
		}

		public int size() {
			// TODO Auto-generated method stub
			return agents.size();
		}

		public void trim() {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * @author Steve Phelps
	 * @version $Revision$
	 */
	class AgentSpace implements Discrete2DSpace {

		protected RandomRobinAuction auction;

		protected int width;

		protected int height;

		protected ArrayList agents;

		protected AgentMatrix matrix;

		public AgentSpace(RandomRobinAuction auction, int width) {
			this.width = width;
			this.auction = auction;
			height = auction.getNumberOfRegisteredTraders() / width;
			height += (auction.getNumberOfRegisteredTraders() % width == 0) ? 0 : 1;

			agents = new ArrayList();
			Iterator i = auction.getTraderIterator();
			while (i.hasNext()) {
				AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
				agents.add(new DrawableAgentAdaptor(auction, agent));
			}
			matrix = new AgentMatrix(agents, width, height);
		}

		/**
		 * @uml.property name="agents"
		 */
		public Collection getAgents() {
			return agents;
		}

		public AgentSpace(RandomRobinAuction auction) {
			this(auction, (int) Math.sqrt(auction.getNumberOfRegisteredTraders()));
		}

		/**
		 * @uml.property name="matrix"
		 */
		public BaseMatrix getMatrix() {
			return matrix;
		}

		public Object getObjectAt(int x, int y) {
			// x and y switched to work around a possible
			// bug in RePast:
			// x and y are suspected to take value
			// from the range of the other's.
			return matrix.get(x, y);
		}

		public Dimension getSize() {
			return new Dimension(640, 480);
		}

		public int getSizeX() {
			return width;
		}

		public int getSizeY() {
			return height;
		}

		public double getValueAt(int x, int y) {
			return 0;
		}

		public void putObjectAt(int x, int y, Object object) {
		}

		public void putValueAt(int x, int y, double value) {
		}

	}

}
