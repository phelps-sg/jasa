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

import ec.util.ParamClassLoadException;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.config.CaseEnumConfig;
import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.ReportVariable;

import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.prng.PRNGFactory;
import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.Distribution;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.io.CSVWriter;
import uk.ac.liv.util.io.DataWriter;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

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

public class MarketSimulation implements Serializable, Runnable {

  /**
   * The auction used in this simulation.
   * 
   * @uml.property name="auction"
   * @uml.associationEnd
   */
  protected RandomRobinAuction auction;

  /**
   * The number of repeatitions of this experiment to sample.
   * 
   * @uml.property name="iterations"
   */
  protected int iterations = 0;

  /**
   * @uml.property name="verbose"
   */
  protected boolean verbose = true;

  /**
   * If running more than one iteration, then write batch statistics to this
   * DataWriter.
   */
  protected static DataWriter resultsFile = null;

  public static final String P_CASEENUM = "caseenum";

  public static final String P_AUCTION = "auction";

  public static final String P_SIMULATION = "simulation";

  public static final String P_ITERATIONS = "iterations";

  public static final String P_WRITER = "writer";

  public static final String P_VERBOSE = "verbose";

  static Logger logger = Logger.getLogger("JASA");

  public static void main( String[] args ) {

    try {

      gnuMessage();

      if ( args.length < 1 ) {
        fatalError("You must specify a parameter file");
      }

      String fileName = args[0];
      File file = new File(fileName);
      if ( !file.canRead() ) {
        fatalError("Cannot read parameter file " + fileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      ParameterDatabase parameters = new ParameterDatabase(file, args);
      Parameter base = new Parameter(P_SIMULATION);

      try {
        resultsFile = (DataWriter) parameters.getInstanceForParameter(base
            .push(P_WRITER), null, DataWriter.class);
        if ( resultsFile instanceof Parameterizable ) {
          ((Parameterizable) resultsFile)
              .setup(parameters, base.push(P_WRITER));
        }
      } catch ( ParamClassLoadException e ) {
        resultsFile = null;
      }

      CaseEnumConfig caseEnumConfig = new CaseEnumConfig();
      caseEnumConfig.setup(parameters, base.push(P_CASEENUM));

      if ( caseEnumConfig.getCaseEnumNum() == 0 ) {
        runSingleExperimentSet(parameters, base);
      } else {
        runBatchExperimentSet(parameters, base, caseEnumConfig);
      }

      if ( resultsFile != null ) {
        resultsFile.close();
      }

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }

  private static void runSingleExperimentSet( ParameterDatabase parameters,
      Parameter base ) throws Exception {

    MarketSimulation simulation = new MarketSimulation();
    simulation.setup(parameters, base);
    simulation.run();
  }

  private static void runBatchExperimentSet( ParameterDatabase parameters,
      Parameter base, CaseEnumConfig caseEnumConfig ) throws Exception {

    while ( true ) {

      caseEnumConfig.apply(parameters, base.push(P_AUCTION));

      // run simulation under the current combination of cases
      String s = "*   " + caseEnumConfig.getCurrentDesc() + "   *";
      String stars = "";
      for ( int i = 0; i < s.length(); i++ )
        stars += "*";
      logger.info("\n");
      logger.info(stars);
      logger.info(s);
      logger.info(stars);
      logger.info("\n");

      runSingleExperimentSet(parameters, base);

      if ( !caseEnumConfig.next() )
        break;
    }

  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    logger.debug("Setup... ");

    GlobalPRNG.setup(parameters, base);

    auction = (RandomRobinAuction) parameters.getInstanceForParameterEq(base
        .push(P_AUCTION), null, RandomRobinAuction.class);

    auction.setup(parameters, base.push(P_AUCTION));

    iterations = parameters.getIntWithDefault(base.push(P_ITERATIONS), null,
        iterations);

    verbose = parameters.getBoolean(base.push(P_VERBOSE), null, verbose);

    logger.info("prng = " + PRNGFactory.getFactory().getDescription());
    logger.info("seed = " + GlobalPRNG.getSeed() + "\n");

    logger.debug("Setup complete.");
  }

  public void run() {
    if ( iterations <= 0 ) {
      runSingleExperiment();
    } else {
      runBatchExperiment(iterations);
    }
  }

  public void runSingleExperiment() {
    logger.info("Running auction...");

    if ( verbose ) {
    	logger.info(auction.getAuctioneer().toString());
    	logger.info("");
    }
    
    auction.run();
    logger.info("Auction finished.");
    auction.generateReport();
  }

  public void runBatchExperiment( int n ) {
    HashMap resultsStats = new HashMap();
    
    if ( verbose ) {
    	logger.info("auctioneer:");
    	logger.info(auction.getAuctioneer().toString());
    	logger.info("");
    }
    
    for ( int i = 0; i < n; i++ ) {
      if ( verbose ) {
        logger.info("Running experiment " + (i + 1) + " of " + n + "... ");
      }
      auction.reset();
      auction.run();
      recordResults(auction.getResults(), resultsStats);
      if ( verbose ) {
        logger.info("done.\n");
      }
    }

    finalReport(resultsStats);
  }

  public static void gnuMessage() {
    System.out.println(JASAVersion.getGnuMessage());
  }

  protected void finalReport( Map resultsStats ) {
    logger.info("\nResults");
    logger.info("-------");
    ArrayList variableList = new ArrayList(resultsStats.keySet());
    Collections.sort(variableList);
    Iterator i = variableList.iterator();
    while ( i.hasNext() ) {
      ReportVariable var = (ReportVariable) i.next();
      logger.info("");
      Distribution stats = (Distribution) resultsStats.get(var);
      stats.log();
    }
  }

  protected void recordResults( Map results, Map resultsStats ) {
    ArrayList vars = new ArrayList(results.keySet());
    if ( resultsFile != null && resultsFile instanceof CSVWriter ) {
      ((CSVWriter) resultsFile).setNumColumns(results.size());
    }
    Collections.sort(vars);
    Iterator i = vars.iterator();
    while ( i.hasNext() ) {
      ReportVariable var = (ReportVariable) i.next();
      Object value = results.get(var);
      if ( value instanceof Number ) {
        double v = ((Number) value).doubleValue();
        if ( !Double.isNaN(v) ) {
          CummulativeDistribution varStats = (CummulativeDistribution) resultsStats
              .get(var);
          if ( varStats == null ) {
            varStats = new CummulativeDistribution(var.toString());
            resultsStats.put(var, varStats);
          }
          varStats.newData(v);
        }
      }
      if ( resultsFile != null ) {
        resultsFile.newData(value);
      }
    }

    if ( resultsFile != null ) {
      resultsFile.flush();
    }
  }

  protected static void fatalError( String message ) {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }

}
