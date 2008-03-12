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
import java.text.DecimalFormat;
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
import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.stats.RepastGraphSequence;
import uk.ac.liv.auction.ui.DrawableAgentAdaptor;
import uk.ac.liv.auction.ui.RepastAuctionConsoleGraph;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.prng.PRNGFactory;
import uk.ac.liv.supplychain.MyGraphReport;
import uk.ac.liv.supplychain.SupplyChainRandomRobinAuction;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * <p>
 * A RePast model of a supply chain simulation. This application takes as an
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

public class RepastSupplyChainSimulation extends SimModelImpl implements
    Serializable {

	boolean display = false;

	/**
	 * The auction used in this simulation.
	 * 
	 * @uml.property name="auction"
	 * @uml.associationEnd
	 */
	protected SupplyChainRandomRobinAuction[] auction;

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
	protected RepastAuctionConsoleGraph[] graph;

	/**
	 * @uml.property name="displaySurface"
	 * @uml.associationEnd
	 */
	protected DisplaySurface[] displaySurface;

	/**
	 * @uml.property name="agentSpace"
	 * @uml.associationEnd inverse="this$0:uk.ac.liv.auction.RepastMarketSimulation$AgentSpace"
	 */
	protected AgentSpace[] agentSpace;

	/**
	 * @uml.property name="auxGraphs"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="uchicago.src.sim.analysis.plot.OpenGraph"
	 */
	protected LinkedList auxGraphs = new LinkedList();

	protected static RepastSupplyChainSimulation modelSingleton;

	public static final String P_DEF_BASE = "randomrobinauction";

	public static final String P_NUM_AUCTION_TYPES = "n";

	public static final String P_AUCTION = "auction";

	public static final String P_AUCTION_NAME = "name";

	public static final String P_AGENT = "agenttype";

	public static final String P_NUM_AGENTS = "numagents";

	public static final String P_SIMULATION = "simulation";

	static Logger logger = Logger.getLogger("JASA");

	public static void main(String[] args) {

		SimInit init = new SimInit();

		if (args.length < 1) {
			fatalError("You must specify a parameter file");
		}

		modelSingleton = new RepastSupplyChainSimulation(args[0]);

		init.loadModel(modelSingleton, null, false);
	}

	public RepastSupplyChainSimulation(String parameterFileName) {
		this.parameterFileName = parameterFileName;
		parameterDescriptors = new Hashtable();
		schedule = new Schedule();
		schedule.scheduleActionBeginning(1, this, "step");
		schedule.scheduleActionAtEnd(this, "end");
	}

	public RepastSupplyChainSimulation() {
		this(null);
	}

	public static RepastSupplyChainSimulation getModelSingleton() {
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
		setupAuctions(parameters, base);
		setupAgents(parameters, base);
	}

	public void setupAuctions(ParameterDatabase parameters, Parameter base) {

		logger.debug("SetupAuctions... ");

		GlobalPRNG.setup(parameters, base);

		Parameter defBase = new Parameter(P_DEF_BASE);
		Parameter typeParam = base.push(P_AUCTION);
		Parameter defTypeParam = defBase.push(P_AUCTION);

		int numauctions = parameters.getInt(typeParam.push(P_NUM_AUCTION_TYPES),
		    defTypeParam.push(P_NUM_AUCTION_TYPES), 1);
		auction = new SupplyChainRandomRobinAuction[numauctions];

		for (int a = 0; a < numauctions; a++) {

			Parameter typeParamT = typeParam.push("" + a);
			Parameter defTypeParamT = defTypeParam.push("" + a);

			auction[a] = (SupplyChainRandomRobinAuction) parameters
			    .getInstanceForParameterEq(typeParamT, defTypeParamT,
			        SupplyChainRandomRobinAuction.class);
			auction[a].setup(parameters, typeParamT);
			auction[a].initialiseTransactionPrice(auction[0].getMaximumRounds());

			String auctionName = parameters.getStringWithDefault(base
			    .push(P_AUCTION_NAME), null, "Auction " + (auction[a]).getId());
		}// for

		/*
		 * comming from SupplyChainSimulation, but not used in
		 * RepastMarketSimulation:
		 * 
		 * iterations = parameters.getIntWithDefault(base.push(P_ITERATIONS), null,
		 * iterations);
		 * 
		 * verbose = parameters.getBoolean(base.push(P_VERBOSE), null, verbose);
		 */

		logger.info("prng = " + PRNGFactory.getFactory().getDescription());
		logger.info("seed = " + GlobalPRNG.getSeed() + "\n");

		logger.debug("Setup complete.");
	}

	public void setupAgents(ParameterDatabase parameters, Parameter base) {
		logger.debug("SetupAgents... ");

		Parameter defBase = new Parameter(P_DEF_BASE);

		Parameter typeParam = base.push(P_AGENT);
		Parameter defTypeParam = defBase.push(P_AGENT);

		int numAgentTypes = parameters.getInt(typeParam.push("n"), defTypeParam
		    .push("n"), 1);

		logger.debug("number of agents groups=" + numAgentTypes);

		for (int t = 0; t < numAgentTypes; t++) {

			Parameter typeParamT = typeParam.push("" + t);
			Parameter defTypeParamT = defTypeParam.push("" + t);

			int numAgents = parameters.getInt(typeParamT.push(P_NUM_AGENTS),
			    defTypeParamT.push(P_NUM_AGENTS).push(P_NUM_AGENTS), 0);

			logger.info("Configuring agent population " + t + ":\n\t" + numAgents
			    + " agents of type " + parameters.getString(typeParamT, null));
			for (int i = 0; i < numAgents; i++) {
				TradingAgent agent = (TradingAgent) parameters.getInstanceForParameter(
				    typeParamT, defTypeParamT, TradingAgent.class);
				((uk.ac.liv.supplychain.SupplyChainAgent) agent).setup(parameters,
				    typeParamT, auction);

				// System.out.println("Population " +t+ " Agent " + i);
				// ((uk.ac.liv.supplychain.SupplyChainAgent)
				// agent).printSetup();

				for (int a = 0; a < auction.length; a++) {
					if (((uk.ac.liv.supplychain.SupplyChainAgent) agent)
					    .isBuyer(auction[a])
					    || ((uk.ac.liv.supplychain.SupplyChainAgent) agent)
					        .isSeller(auction[a])) {
						(auction[a]).register(agent);
					}// if
				}// for
				if (i == 0 && agent instanceof AbstractTradingAgent) {
					logger.info("\tUsing "
					    + ((AbstractTradingAgent) agent).getStrategy().getClass());
				}// if
			}// for
			logger.info("done.\n");
		}// for
	}// setupAgents

	public void begin() {
		buildDisplay();
		for (int a = 0; a < auction.length; a++)
			auction[a].begin();
	}

	public void step() {
		// TODO: why is step() called after the beginning of a round?
		// TODO
		/*
		 * Iterator wewe =
		 * MyGraphReport.getSingletonInstance().getSequenceIterator();
		 * RepastGraphSequence sequenceAsk = (RepastGraphSequence) wewe.next();
		 * double ask = sequenceAsk.getSValue();
		 * System.out.println("RepastSupplyChainSimulation.step: Mean ask quote =
		 * "+ask); RepastGraphSequence sequenceBid = (RepastGraphSequence)
		 * wewe.next(); double bid = sequenceAsk.getSValue();
		 * System.out.println("RepastSupplyChainSimulation.step: Mean bid quote =
		 * "+bid); RepastGraphSequence sequenceTrans = (RepastGraphSequence)
		 * wewe.next(); System.out.println("RepastSupplyChainSimulation.step: Mean
		 * trans quote = "+sequenceTrans.getSValue());
		 * //auction[0].setTransactionPrice( (ask + bid) / 2 );
		 */

		/*
		 * if (true) {
		 * System.out.println("\n**********************"+auction[0].getAge()+"
		 * RepastSupplyChainSimulation.step Auction "+auction[0].getId()+ " previous
		 * transaction prices:"); DecimalFormat df = new
		 * DecimalFormat("########.0000"); for ( int i=0 ; i<auction.length ; i++) {
		 * System.out.print("[Auc"+i+" current: "); for ( int j=0 ; j<(int)auction[0].getAge()+1 ;
		 * j++) System.out.print( ((SupplyChainRandomRobinAuction)
		 * auction[i]).getTransactionPrice( j ) +","); System.out.println("] "); }
		 * System.out.println("**********************"); }
		 */

		try {
			for (int a = 0; a < auction.length; a++) {
				if (display)
					System.out.println("\n*********" + auction[0].getAge()
					    + " RepastSupplyChainSimulation.step Auction "
					    + auction[a].getId() + "*********");
				auction[a].step();
			}
		} catch (AuctionClosedException e) {
			getController().stopSim();
		}
		for (int a = 0; a < auction.length; a++) {
			graph[a].step();
		}
		if (auxGraphs != null) {
			Iterator i = auxGraphs.iterator();
			while (i.hasNext()) {
				OpenGraph auxGraph = (OpenGraph) i.next();
				auxGraph.step();
			}
		}
		for (int i = 0; i < auction.length; i++)
			displaySurface[i].updateDisplay();
		// ProbeUtilities.updateProbePanels();
	}

	public void end() {
		for (int a = 0; a < auction.length; a++)
			auction[a].end();
		// auction.end();

		for (int a = 0; a < auction.length; a++)
			auction[a].generateReport();
		// auction.generateReport();

		printClearingPrices();
	}

	/**
	 * 
	 */
	private void printClearingPrices() {
		DecimalFormat df = new DecimalFormat("################.00");
		for (int j = 0; j < (int) auction[0].getAge() + 1; j++) {
			System.out.print("\n" + j);
			for (int i = 0; i < auction.length; i++)
				System.out.print("\t"
				    + i
				    + "\t"
				    + df.format(((SupplyChainRandomRobinAuction) auction[i])
				        .getTransactionPrice(j)));
			// System.out.print( ((SupplyChainRandomRobinAuction)
			// auction[i]).getTransactionPrice( j ) +"\t");
		}
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
		return auction[0].getAge();
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

		uk.ac.liv.auction.stats.GraphReport graphLogger;
		graph = new RepastAuctionConsoleGraph[auction.length];
		for (int i = 0; i < auction.length; i++) {
			if ((graphLogger = MyGraphReport.getSingletonInstance(i)) != null) {
				graph[i] = new RepastAuctionConsoleGraph("JASA graph for "
				    + auction[i].getName(), this, graphLogger);
				graph[i].display();
			}
		}

		Iterator i = auxGraphs.iterator();
		while (i.hasNext()) {
			OpenGraph graph = (OpenGraph) i.next();
			graph.display();
		}

		displaySurface = new DisplaySurface[auction.length];
		agentSpace = new AgentSpace[auction.length];
		for (int idx = 0; idx < auction.length; idx++) {
			displaySurface[idx] = new DisplaySurface(this, "JASA agents for "
			    + auction[idx].getName());
			agentSpace[idx] = new AgentSpace(auction[idx]);
			Object2DDisplay agentDisplay = new Object2DDisplay(agentSpace[idx]);
			displaySurface[idx].addDisplayableProbeable(agentDisplay, "agents");
			displaySurface[idx].setPreferredSize(new Dimension(640, 480));
			displaySurface[idx].display();
			addSimEventListener(displaySurface[idx]);
		}// for

		((Controller) getController()).UPDATE_PROBES = true;
	}

	class AgentMatrix implements BaseMatrix {

		protected RandomRobinAuction auction;

		protected ArrayList agents;

		protected int height;

		protected int width;

		public AgentMatrix(ArrayList agents, int height, int width) {
			this.agents = agents;
			this.height = height;
			this.width = width;
		}

		public AgentMatrix(ArrayList agents, int height, int width,
		    RandomRobinAuction auction) {
			this(agents, height, width);
			this.auction = auction;
		}

		public Object get(int col, int row) {
			int abs = row * (width - 1) + col;
			if (abs >= agents.size()) {
				// TODO: adapter pour un par enchere
				return new DrawableAgentAdaptor(this.auction);
			}
			DrawableAgentAdaptor agent = (DrawableAgentAdaptor) agents.get(abs);
			if (agent == null) {
				// TODO: adapter pour un par enchere
				return new DrawableAgentAdaptor(this.auction);
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
			if (width != 0)
				height = auction.getNumberOfRegisteredTraders() / width;
			else
				height = 1;

			if (width != 0)
				height += (auction.getNumberOfRegisteredTraders() % width == 0) ? 0 : 1;

			agents = new ArrayList();
			Iterator i = auction.getTraderIterator();
			while (i.hasNext()) {
				AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
				agents.add(new DrawableAgentAdaptor(auction, agent));
			}
			matrix = new AgentMatrix(agents, width, height);
			matrix = new AgentMatrix(agents, width, height, auction);
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
