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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.liv.auction.agent.AbstractTradingAgent;
import uk.ac.liv.auction.agent.TradingAgent;
import uk.ac.liv.auction.config.CaseEnumConfig;
import uk.ac.liv.auction.stats.ReportVariable;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.prng.PRNGFactory;
import uk.ac.liv.supplychain.SupplyChainRandomRobinAuction;
import uk.ac.liv.util.SummaryStats;
import uk.ac.liv.util.Distribution;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.CSVWriter;
import uk.ac.liv.util.io.DataWriter;
import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * <p>
 * The main JASA application class. This application takes as an argument the
 * name of a parameter file describing an auction experiment, and proceeds to
 * run that experiment. This application can be used to run many iterations of
 * an experiment in batch-mode, or even many iterations of several experiments
 * with different settings, in contrast to the interactive mode provided by
 * RepastMarketSimulation.
 * 
 * @see RepastMarketSimulation
 * 
 * </p>
 * 
 * <p>
 * <b>Parameters</b><br>
 * 
 * <table>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.caseenum</tt><br>
 * <font size=-1></font></td>
 * <td valign=top>(the parameter base for
 * uk.ac.liv.auction.config.CaseEnumConfig to set up a set of different auctions
 * to run)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.caseenum.i</tt><br>
 * <font size=-1> classname inherits uk.ac.liv.auction.config.CaseEnum </font></td>
 * <td valign=top>(the enumeration of different values of a property to
 * generate a set of different auction settings)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.auction</tt><br>
 * <font size=-1>classname inherits uk.ac.liv.auction.core.RoundRobinAuction</font></td>
 * <td valign=top>(the class of auction to use)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.iterations</tt><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of repetitions of this experiment to sample)</td>
 * </tr>
 * 
 * <tr>
 * <td valign=top><i>base</i><tt>.writer</tt><br>
 * <font size=-1>classname inherits uk.ac.liv.auction.io.DataWriter</font></td>
 * <td valign=top>(the data writer used to record results if running a batch of
 * experiments)</td>
 * </tr>
 * 
 * </table>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class SupplyChainSimulation implements Serializable, Runnable {

	/**
	 * The auctions used in this simulation.
	 */
	protected SupplyChainRandomRobinAuction[] auction;

	/**
	 * The number of repeatitions of this experiment to sample.
	 */
	protected int iterations = 0;

	protected boolean verbose = true;

	/**
	 * If running more than one iteration, then write batch statistics to this
	 * DataWriter.
	 */
	protected static DataWriter resultsFile = null;

	public static final String P_DEF_BASE = "randomrobinauction";

	public static final String P_NUM_AUCTION_TYPES = "n";

	public static final String P_AUCTION = "auction";

	public static final String P_AUCTION_NAME = "name";

	public static final String P_AGENT = "agenttype";

	public static final String P_AGENT_BUYS = "buysInAuction";

	public static final String P_AGENT_SELLS = "sellsInAuction";

	public static final String P_NUM_AGENTS = "numagents";

	public static final String P_CASEENUM = "caseenum";

	public static final String P_SIMULATION = "simulation";

	public static final String P_ITERATIONS = "iterations";

	public static final String P_WRITER = "writer";

	public static final String P_VERBOSE = "verbose";

	static Logger logger = Logger.getLogger("JASA");

	public static void main(String[] args) {

		try {

			gnuMessage();

			if (args.length < 1) {
				fatalError("You must specify a parameter file");
			}

			String fileName = args[0];
			File file = new File(fileName);
			if (!file.canRead()) {
				fatalError("Cannot read parameter file " + fileName);
			}

			org.apache.log4j.PropertyConfigurator.configure(fileName);

			ParameterDatabase parameters = new ParameterDatabase(file, args);
			Parameter base = new Parameter(P_SIMULATION);

			try {
				resultsFile = (DataWriter) parameters.getInstanceForParameter(base
				    .push(P_WRITER), null, DataWriter.class);
				if (resultsFile instanceof Parameterizable) {
					((Parameterizable) resultsFile)
					    .setup(parameters, base.push(P_WRITER));
				}
			} catch (ParamClassLoadException e) {
				resultsFile = null;
			}

			CaseEnumConfig caseEnumConfig = new CaseEnumConfig();
			caseEnumConfig.setup(parameters, base.push(P_CASEENUM));

			if (caseEnumConfig.getCaseEnumNum() == 0) {
				runSingleExperimentSet(parameters, base);
			} else {
				runBatchExperimentSet(parameters, base, caseEnumConfig);
			}

			if (resultsFile != null) {
				resultsFile.close();
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	private static void runSingleExperimentSet(ParameterDatabase parameters,
	    Parameter base) throws Exception {

		SupplyChainSimulation simulation = new SupplyChainSimulation();
		simulation.setup(parameters, base);
		simulation.run();
	}

	private static void runBatchExperimentSet(ParameterDatabase parameters,
	    Parameter base, CaseEnumConfig caseEnumConfig) throws Exception {

		while (true) {

			caseEnumConfig.apply(parameters, base.push(P_AUCTION));

			// run simulation under the current combination of cases
			String s = "*   " + caseEnumConfig.getCurrentDesc() + "   *";
			String stars = "";
			for (int i = 0; i < s.length(); i++)
				stars += "*";
			logger.info("\n");
			logger.info(stars);
			logger.info(s);
			logger.info(stars);
			logger.info("\n");

			runSingleExperimentSet(parameters, base);

			if (!caseEnumConfig.next())
				break;
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

			// TODO: auctionName seems to be useless
			String auctionName = parameters.getStringWithDefault(base
			    .push(P_AUCTION_NAME), null, "Auction " + (auction[a]).getId());
		}// for

		iterations = parameters.getIntWithDefault(base.push(P_ITERATIONS), null,
		    iterations);

		verbose = parameters.getBoolean(base.push(P_VERBOSE), null, verbose);

		logger.info("prng = " + PRNGFactory.getFactory().getDescription());
		logger.info("seed = " + GlobalPRNG.getSeed() + "\n");

		logger.debug("Setup of " + numauctions + " auctions complete.");
	}// setupAuctions

	public void setupAgents(ParameterDatabase parameters, Parameter base) {
		logger.debug("SetupAgents... ");

		Parameter defBase = new Parameter(P_DEF_BASE);

		Parameter typeParam = base.push(P_AGENT);
		Parameter defTypeParam = defBase.push(P_AGENT);

		int numAgentTypes = parameters.getInt(typeParam.push("n"), defTypeParam
		    .push("n"), 1);

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

	public void run() {
		if (iterations <= 0) {
			runSingleExperiment();
		} else {
			runBatchExperiment(iterations);
		}
	}

	public void runSingleExperiment() {
		logger.info("Running auction...");

		if (verbose) {
			for (int a = 0; a < auction.length; a++) {
				logger.info((auction[a]).getAuctioneer().toString());
			}
			logger.info("");
		}

		for (int a = 0; a < auction.length; a++)
			auction[a].begin();

		while (!(auction[0].closed()))
			for (int a = 0; a < auction.length; a++)
				auction[a].run();

		for (int a = 0; a < auction.length; a++)
			auction[a].end();

		logger.info("Auction finished.");
		for (int a = 0; a < auction.length; a++) {
			System.out.println("****** AUCTION " + auction[a].getName() + " ******");
			auction[a].generateReport();

		}
	}

	public void runBatchExperiment(int n) {
		HashMap resultsStats = new HashMap();

		if (verbose) {
			logger.info("auctioneer:");
			for (int a = 0; a < auction.length; a++) {
				logger.info(auction[a].getAuctioneer().toString());
			}
			logger.info("");
		}

		for (int i = 0; i < n; i++) {
			if (verbose) {
				logger.info("Running experiment " + (i + 1) + " of " + n + "... ");
			}
			for (int a = 0; a < auction.length; a++) {
				auction[a].reset();
			}
			for (int a = 0; a < auction.length; a++) {
				auction[a].run();
			}
			for (int a = 0; a < auction.length; a++) {
				recordResults(auction[a].getResults(), resultsStats);
			}
			if (verbose) {
				logger.info("done.\n");
			}
		}

		finalReport(resultsStats);
	}

	public static void gnuMessage() {
		System.out.println(JASAVersion.getGnuMessage());
	}

	protected void finalReport(Map resultsStats) {
		logger.info("\nResults");
		logger.info("-------");
		ArrayList variableList = new ArrayList(resultsStats.keySet());
		Collections.sort(variableList);
		Iterator i = variableList.iterator();
		while (i.hasNext()) {
			ReportVariable var = (ReportVariable) i.next();
			logger.info("");
			Distribution stats = (Distribution) resultsStats.get(var);
			stats.log();
		}
	}

	protected void recordResults(Map results, Map resultsStats) {
		ArrayList vars = new ArrayList(results.keySet());
		if (resultsFile != null && resultsFile instanceof CSVWriter) {
			((CSVWriter) resultsFile).setNumColumns(results.size());
		}
		Collections.sort(vars);
		Iterator i = vars.iterator();
		while (i.hasNext()) {
			ReportVariable var = (ReportVariable) i.next();
			Object value = results.get(var);
			if (value instanceof Number) {
				double v = ((Number) value).doubleValue();
				if (!Double.isNaN(v)) {
					SummaryStats varStats = (SummaryStats) resultsStats
					    .get(var);
					if (varStats == null) {
						varStats = new SummaryStats(var.toString());
						resultsStats.put(var, varStats);
					}
					varStats.newData(v);
				}
			}
			if (resultsFile != null) {
				resultsFile.newData(value);
			}
		}

		if (resultsFile != null) {
			resultsFile.flush();
		}
	}

	/**
	 * Register a new trader in the auction. NOTE: taken by Thierry from
	 * RandomRobinAuction
	 */
	public void register(TradingAgent aTrader,
	    SupplyChainRandomRobinAuction anAuction) {
		LinkedList registeredTraders = anAuction.getRegisteredTraders();
		registeredTraders.add(aTrader);
		activate(aTrader, anAuction);
	}// register

	/**
	 * Activate a new trader in the auction. NOTE: taken by Thierry from
	 * RandomRobinAuction
	 */
	protected void activate(TradingAgent aTrader,
	    SupplyChainRandomRobinAuction anAuction) {
		LinkedList activeTraders = anAuction.getActiveTraders();
		activeTraders.add(aTrader);
		// TODO
		// addAuctionEventListener(aTrader);
	}

	protected static void fatalError(String message) {
		System.err.println("ERROR: " + message);
		System.exit(1);
	}

}// class
