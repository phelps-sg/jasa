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

package uk.ac.liv.auction.electricity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import uk.ac.liv.ai.learning.DiscreteLearner;
import uk.ac.liv.ai.learning.Learner;
import uk.ac.liv.auction.agent.AdaptiveStrategy;
import uk.ac.liv.auction.agent.FixedQuantityStrategy;
import uk.ac.liv.auction.agent.Strategy;
import uk.ac.liv.auction.core.AbstractAuctioneer;
import uk.ac.liv.auction.core.Auctioneer;
import uk.ac.liv.auction.core.KPricingPolicy;
import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.stats.PriceStatisticsReport;
import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.util.SummaryStats;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.io.CSVWriter;
import uk.ac.liv.util.io.DataWriter;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

/**
 * <p>
 * An implementation of the fitness-landscape experiment described in the
 * University of Liverpool Computer Science Department technical report number
 * ULCS-02-031. This work is based on the work of Nicolaisen, Petrov, and
 * Tesfatsion described in:
 * </p>
 * <br>
 * <p>
 * "Market Power and Efficiency in a Computational Electricity Market with
 * Discriminatory Double-Auction Pricing" <br>
 * IEEE Transactions on Evolutionary Computation, Vol. 5, No. 5. 2001
 * </p>
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class ElectricityExperiment implements Parameterizable, Runnable {

	/**
	 * @uml.property name="outputDir"
	 */
	protected String outputDir = "/tmp";

	/**
	 * @uml.property name="maxRounds"
	 */
	protected int maxRounds = 1000;

	/**
	 * @uml.property name="iterations"
	 */
	protected int iterations = 100;

	/**
	 * @uml.property name="auctioneerKSamples"
	 */
	protected int auctioneerKSamples = 10;

	/**
	 * @uml.property name="minK"
	 */
	protected double minK = 0;

	/**
	 * @uml.property name="maxK"
	 */
	protected double maxK = 1;

	/**
	 * @uml.property name="deltaK"
	 */
	protected double deltaK = 0.01;

	/**
	 * @uml.property name="numBuyers"
	 */
	protected int numBuyers;

	/**
	 * @uml.property name="numSellers"
	 */
	protected int numSellers;

	/**
	 * @uml.property name="buyerCapacity"
	 */
	protected int buyerCapacity;

	/**
	 * @uml.property name="sellerCapacity"
	 */
	protected int sellerCapacity;

	/**
	 * @uml.property name="sellerStrategies"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected FixedQuantityStrategy[] sellerStrategies;

	/**
	 * @uml.property name="buyerStrategies"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected FixedQuantityStrategy[] buyerStrategies;

	/**
	 * @uml.property name="dataFile"
	 * @uml.associationEnd
	 */
	protected DataWriter dataFile;

	/**
	 * @uml.property name="distributionFile"
	 * @uml.associationEnd readOnly="true"
	 */
	protected DataWriter distributionFile;

	/**
	 * @uml.property name="iterResults"
	 * @uml.associationEnd
	 */
	protected DataWriter iterResults;

	/**
	 * @uml.property name="strategyData"
	 * @uml.associationEnd
	 */
	protected DataWriter strategyData;

	/**
	 * @uml.property name="stats"
	 * @uml.associationEnd
	 */
	protected ElectricityStats stats;

	/**
	 * @uml.property name="auction"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected RandomRobinAuction auction = new RandomRobinAuction("electricity");

	/**
	 * @uml.property name="auctioneer"
	 * @uml.associationEnd
	 */
	protected Auctioneer auctioneer;

	/**
	 * @uml.property name="marketData"
	 * @uml.associationEnd
	 */
	protected PriceStatisticsReport marketData;

	/**
	 * @uml.property name="paramSummary"
	 */
	protected String paramSummary;

	/**
	 * @uml.property name="collectIterData"
	 */
	protected boolean collectIterData = false;

	/**
	 * @uml.property name="collectStrategyData"
	 */
	protected boolean collectStrategyData = false;

	/**
	 * @uml.property name="randomizer"
	 * @uml.associationEnd inverse="experiment:uk.ac.liv.auction.electricity.StandardRandomizer"
	 */
	protected StandardRandomizer randomizer;

	/**
	 * @uml.property name="efficiency"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats efficiency = new SummaryStats(
	    "EA");

	/**
	 * @uml.property name="mPB"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats mPB = new SummaryStats("MPB");

	/**
	 * @uml.property name="mPS"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats mPS = new SummaryStats("MPS");

	/**
	 * @uml.property name="pSA"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats pSA = new SummaryStats("PSA");

	/**
	 * @uml.property name="pBA"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats pBA = new SummaryStats("PBA");

	/**
	 * @uml.property name="pST"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats pST = new SummaryStats("PST");

	/**
	 * @uml.property name="pBT"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats pBT = new SummaryStats("PBT");

	/**
	 * @uml.property name="eAN"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats eAN = new SummaryStats("EAN");

	/**
	 * @uml.property name="mPBN"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats mPBN = new SummaryStats("MPBN");

	/**
	 * @uml.property name="mPSN"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats mPSN = new SummaryStats("MPSN");

	/**
	 * @uml.property name="sMPB"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats sMPB = new SummaryStats("SMPB");

	/**
	 * @uml.property name="sMPS"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats sMPS = new SummaryStats("SMPS");

	/**
	 * @uml.property name="sMPBN"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats sMPBN = new SummaryStats("SMPBN");

	/**
	 * @uml.property name="sMPSN"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats sMPSN = new SummaryStats("SMPSN");

	/**
	 * @uml.property name="pBCE"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats pBCE = new SummaryStats("PBCE");

	/**
	 * @uml.property name="pSCE"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats pSCE = new SummaryStats("PSCE");

	/**
	 * @uml.property name="equilibPrice"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats equilibPrice = new SummaryStats(
	    "EquilibPrice");

	/**
	 * @uml.property name="equilibQty"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats equilibQty = new SummaryStats(
	    "EquilibQty");

	/**
	 * @uml.property name="learningDelta"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected SummaryStats learningDelta = new SummaryStats(
	    "LD");

	/**
	 * @uml.property name="variables"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	protected SummaryStats[] variables = new SummaryStats[] {
	    efficiency, mPB, mPS, pBA, pSA, pBT, pST, eAN, mPBN, mPSN, sMPB, sMPS,
	    sMPBN, sMPSN, pBCE, pSCE, equilibPrice, equilibQty, learningDelta };

	static Logger logger = Logger.getLogger(ElectricityExperiment.class);

	static final String DEFAULT_PARAMETER_FILE = "examples/electricity.params";

	public static final String P_MAXROUNDS = "maxrounds";

	public static final String P_ITERATIONS = "iterations";

	public static final String P_OUTPUTDIR = "outputdir";

	public static final String P_ELECTRICITY = "electricity";

	public static final String P_AUCTIONEERKSAMPLES = "ksamples";

	public static final String P_KMIN = "k0";

	public static final String P_KMAX = "k1";

	public static final String P_KDELTA = "kdelta";

	public static final String P_AUCTIONEER = "auctioneer";

	public static final String P_CB = "cb";

	public static final String P_CS = "cs";

	public static final String P_NS = "ns";

	public static final String P_NB = "nb";

	public static final String P_SELLER_STRATEGY = "seller";

	public static final String P_BUYER_STRATEGY = "buyer";

	public static final String P_STRATEGY = "strategy";

	public static final String P_STATS = "stats";

	public static final String P_ITER_DATA = "iterdata";

	public static final String P_STRATEGY_DATA = "strategydata";

	public static final String P_RANDOMIZER = "randomizer";

	public static final String P_PRNG = "prng";

	static int DATAFILE_NUM_COLUMNS;

	static final int ITERRESULTS_NUM_COLUMNS = 9;

	public ElectricityExperiment() {
	}

	public void setup(ParameterDatabase parameters, Parameter base) {

		GlobalPRNG.setup(parameters, base);

		DATAFILE_NUM_COLUMNS = variables.length * 4 + 1;

		maxRounds = parameters.getIntWithDefault(base.push(P_MAXROUNDS), null,
		    maxRounds);

		auctioneerKSamples = parameters.getIntWithDefault(base
		    .push(P_AUCTIONEERKSAMPLES), null, auctioneerKSamples);

		minK = parameters.getDoubleWithDefault(base.push(P_KMIN), null, minK);
		maxK = parameters.getDoubleWithDefault(base.push(P_KMAX), null, maxK);
		deltaK = parameters.getDoubleWithDefault(base.push(P_KDELTA), null, deltaK);

		collectIterData = parameters.getBoolean(base.push(P_ITER_DATA), null,
		    collectIterData);

		collectStrategyData = parameters.getBoolean(base.push(P_STRATEGY_DATA),
		    null, collectStrategyData);

		iterations = parameters.getIntWithDefault(base.push(P_ITERATIONS), null,
		    iterations);

		outputDir = parameters.getStringWithDefault(base.push(P_OUTPUTDIR), null,
		    outputDir);

		auctioneer = (Auctioneer) parameters.getInstanceForParameter(base
		    .push(P_AUCTIONEER), null, AbstractAuctioneer.class);
		((Parameterizable) auctioneer).setup(parameters, base.push(P_AUCTIONEER));

		stats = (ElectricityStats) parameters.getInstanceForParameterEq(base
		    .push(P_STATS), null, ElectricityStats.class);

		randomizer = (StandardRandomizer) parameters.getInstanceForParameterEq(base
		    .push(P_RANDOMIZER), null, StandardRandomizer.class);
		randomizer.setExperiment(this);
		randomizer.setup(parameters, base.push(P_RANDOMIZER));

		numBuyers = parameters.getIntWithDefault(base.push(P_NB), null, 3);
		numSellers = parameters.getIntWithDefault(base.push(P_NS), null, 3);

		buyerCapacity = parameters.getIntWithDefault(base.push(P_CB), null, 10);
		sellerCapacity = parameters.getIntWithDefault(base.push(P_CS), null, 10);

		Parameter strategyParam = base.push(P_STRATEGY);

		buyerStrategies = new FixedQuantityStrategy[numBuyers];
		Parameter buyerStrategyParam = strategyParam.push(P_BUYER_STRATEGY);
		for (int i = 0; i < numBuyers; i++) {
			buyerStrategies[i] = (FixedQuantityStrategy) parameters
			    .getInstanceForParameter(buyerStrategyParam, null,
			        FixedQuantityStrategy.class);
			((Parameterizable) buyerStrategies[i]).setup(parameters,
			    buyerStrategyParam);
		}

		sellerStrategies = new FixedQuantityStrategy[numSellers];
		Parameter sellerStrategyParam = strategyParam.push(P_SELLER_STRATEGY);
		for (int i = 0; i < numSellers; i++) {
			sellerStrategies[i] = (FixedQuantityStrategy) parameters
			    .getInstanceForParameter(sellerStrategyParam, null,
			        FixedQuantityStrategy.class);
			((Parameterizable) sellerStrategies[i]).setup(parameters,
			    sellerStrategyParam);
		}

	}

	public static void main(String[] args) {

		try {

			String fileName = null;

			if (args.length < 1) {
				fileName = DEFAULT_PARAMETER_FILE;
			} else {
				fileName = args[0];
			}

			File file = new File(fileName);
			if (!file.canRead()) {
				System.err.println("Cannot read parameter file " + fileName);
				System.exit(1);
			}

			org.apache.log4j.PropertyConfigurator.configure(fileName);

			ParameterDatabase parameters = new ParameterDatabase(new File(fileName),
			    args);

			ElectricityExperiment experiment = new ElectricityExperiment();
			experiment.setup(parameters, new Parameter(P_ELECTRICITY));
			experiment.run();

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new Error(e.getMessage());
		}
	}

	public void run() {
		try {
			performExperiment();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void performExperiment() throws IOException {

		summariseParameters();

		int numTraders = numBuyers + numSellers;

		paramSummary = numSellers + "-" + numBuyers + "-" + sellerCapacity + "-"
		    + buyerCapacity;

		dataFile = new CSVWriter(new FileOutputStream(outputDir + "/" + "npt-"
		    + paramSummary + ".csv"), DATAFILE_NUM_COLUMNS);

		writeDataFileHeadings();

		marketData = new PriceStatisticsReport();
		((Resetable) auctioneer).reset();
		auction.setAuctioneer(auctioneer);
		auction.setReport(marketData);
		stats.setAuction(auction);

		registerTraders(auction, true, numSellers, sellerCapacity);
		registerTraders(auction, false, numBuyers, buyerCapacity);

		auction.setMaximumRounds(maxRounds);

		randomizer.generatePRNGseeds(iterations);

		double[][] randomizedPrivateValues = null;

		randomizedPrivateValues = randomizer.generateRandomizedPrivateValues(
		    numTraders, iterations);

		for (double k = minK; k <= maxK; k += deltaK) {

			experiment(k, randomizedPrivateValues);

			reportSummary(k);
			recordVariables(k);
		}

		dataFile.close();
	}

	public void experiment(double auctioneerK, double[][] randomizedPrivateValues)
	    throws FileNotFoundException {

		((KPricingPolicy) ((AbstractAuctioneer) auctioneer).getPricingPolicy())
		    .setK(auctioneerK);
		auction.reset();

		logger.info("\n*** Experiment with parameters");

		logger.info("k = " + auctioneerK);

		initIterResults(outputDir + "/iter-" + paramSummary + "-" + auctioneerK
		    + ".csv");

		resetVariables();

		String strategyDataFile = null;
		if (collectStrategyData) {
			strategyDataFile = outputDir + "/strategy-" + paramSummary + "-k"
			    + auctioneerK;
		}

		for (int i = 0; i < iterations; i++) {

			randomizer.randomizePrivateValues(randomizedPrivateValues, i);

			if (collectStrategyData) {
				initStrategyData(strategyDataFile + "-" + i);
			}

			auction.reset();
			auction.run();

			calculateStatistics();
			dumpIterResults();
			dumpStrategyData();
		}

	}

	protected void registerTraders(RandomRobinAuction auction,
	    boolean areSellers, int num, int capacity) {

		for (int i = 0; i < num; i++) {

			ElectricityTrader trader = new ElectricityTrader(capacity, 0, 0,
			    areSellers);

			FixedQuantityStrategy strategy = null;
			if (areSellers) {
				strategy = sellerStrategies[i];
			} else {
				strategy = buyerStrategies[i];
			}
			((Resetable) strategy).reset();
			trader.setStrategy(strategy);
			strategy.setAgent(trader);
			strategy.setQuantity(trader.getCapacity());

			// Register it in the auction
			auction.register(trader);
		}
	}

	protected void calculateStatistics() {

		stats.calculate();
		stats.calculateStrategicMarketPower();

		efficiency.newData(stats.getEA());
		mPB.newData(stats.getMPB());
		mPS.newData(stats.getMPS());
		sMPB.newData(stats.getSMPB());
		sMPS.newData(stats.getSMPS());
		pBA.newData(stats.getPBA());
		pSA.newData(stats.getPSA());
		eAN.newData(stats.getEA() / 100);
		mPBN.newData(mpNormalise(stats.getMPB()));
		mPSN.newData(mpNormalise(stats.getMPS()));
		sMPBN.newData(mpNormalise(stats.getSMPB()));
		sMPSN.newData(mpNormalise(stats.getSMPS()));
		pBT.newData(stats.getPBT());
		pST.newData(stats.getPST());
		pBCE.newData(stats.getPBCE());
		pSCE.newData(stats.getPSCE());

		double ep = (stats.getMinPrice() + stats.getMaxPrice()) / 2;
		double eq = stats.getQuantity();
		equilibPrice.newData(ep);
		equilibQty.newData(eq);

		Iterator i = auction.getTraderIterator();
		while (i.hasNext()) {
			ElectricityTrader t = (ElectricityTrader) i.next();
			Strategy s = t.getStrategy();
			if (s instanceof AdaptiveStrategy) {
				Learner l = ((AdaptiveStrategy) s).getLearner();
				learningDelta.newData(l.getLearningDelta());
			}
		}
	}

	protected void summariseParameters() {
		logger.info("\nUsing global parameters:\n");
		logger.info("maxRounds = " + maxRounds);
		logger.info("iterations = " + iterations);
		logger.info("auctioneer = " + auctioneer + "\n");
		logger.info("ns = " + numSellers);
		logger.info("nb = " + numBuyers);
		logger.info("cs = " + sellerCapacity);
		logger.info("cb = " + buyerCapacity + "\n");
		logger.info("stats = " + stats + "\n");
		logger.info("randomizer = " + randomizer + "\n");
		logger.info("buyer strategy = " + buyerStrategies[0] + "\n");
		logger.info("seller strategy = " + sellerStrategies[0] + "\n");
	}

	protected void recordVariables(double auctioneerK) {
		dataFile.newData(auctioneerK);
		for (int i = 0; i < variables.length; i++) {
			dataFile.newData(variables[i].getMean());
			dataFile.newData(variables[i].getStdDev());
			dataFile.newData(variables[i].getMin());
			dataFile.newData(variables[i].getMax());
		}
	}

	protected void resetVariables() {
		for (int i = 0; i < variables.length; i++) {
			variables[i].reset();
		}
	}

	protected void writeDataFileHeadings() throws IOException {
		CSVWriter headings = new CSVWriter(new FileOutputStream(outputDir + "/"
		    + "headings-" + paramSummary + ".csv"), DATAFILE_NUM_COLUMNS);

		headings.newData("k");
		for (int i = 0; i < variables.length; i++) {
			String name = variables[i].getName();
			headings.newData(name + "_mean");
			headings.newData(name + "_stdev");
			headings.newData(name + "_min");
			headings.newData(name + "_max");
		}
		headings.close();
	}

	protected void reportSummary(double auctioneerK) {
		logger.info("\n*** Summary results for: k = " + auctioneerK + "\n");
		for (int i = 0; i < variables.length; i++) {
			logger.info(variables[i]);
		}
	}

	protected void dumpIterResults() {
		if (collectIterData) {
			iterResults.newData(stats.getEA());
			iterResults.newData(stats.getMPB());
			iterResults.newData(stats.getMPS());
			iterResults.newData(stats.getSMPB());
			iterResults.newData(stats.getSMPS());
			iterResults.newData(marketData.getTransPriceStats().getMean());
			iterResults.newData(marketData.getAskPriceStats().getMean());
			iterResults.newData(marketData.getBidPriceStats().getMean());
			iterResults.newData(stats.calculateEquilibriumPrice());
		}
	}

	protected void dumpStrategyData() {
		if (collectStrategyData) {
			Iterator i = auction.getTraderIterator();
			while (i.hasNext()) {
				ElectricityTrader t = (ElectricityTrader) i.next();
				Strategy s = t.getStrategy();
				if (s instanceof AdaptiveStrategy) {
					Learner l = ((AdaptiveStrategy) s).getLearner();
					l.dumpState(strategyData);
				}
			}
		}
	}

	protected void initIterResults(String filename) throws FileNotFoundException {
		if (collectIterData) {
			FileOutputStream iterOut = new FileOutputStream(filename);
			iterResults = new CSVWriter(iterOut, ITERRESULTS_NUM_COLUMNS);
		}
	}

	protected void initStrategyData(String filename) throws FileNotFoundException {
		AdaptiveStrategy s = (AdaptiveStrategy) buyerStrategies[0];
		DiscreteLearner l = (DiscreteLearner) s.getLearner();
		int numColumns = l.getNumberOfActions();
		FileOutputStream strategyDataOut = new FileOutputStream(filename);
		strategyData = new CSVWriter(strategyDataOut, numColumns);
	}

	public double mpNormalise(double marketPower) {
		return 1 / (1 + Math.abs(marketPower));
	}

}
