/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import uk.ac.liv.auction.core.*;

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
 * The main JASA application class.  This application takes as an argument
 * the name of a parameter file describing an auction experiment, and
 * proceeds to run that experiment.
 * </p>
 *
 * <p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.auction</tt><br>
 * <font size=-1>classname inherits uk.ac.liv.auction.core.RoundRobinAuction</font></td>
 * <td valign=top>(the class of auction to use)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>iterations</tt><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of repetitions of this experiment to sample)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>w</tt><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of repetitions of this experiment to sample)</td></tr>
 * 
 * <tr><td valign=top><i>base</i><tt>.writer</tt><br>
 * <font size=-1>classname inherits uk.ac.liv.auction.io.DataWriter</font></td>
 * <td valign=top>(the data writer used to record results if running a batch of experiments)</td></tr>
 * 
 * </table>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class MarketSimulation implements Serializable, Runnable {

  /**
   * The auction used in this simulation.
   */
  protected RoundRobinAuction auction;
  
  /**
   * The number of repeatitions of this experiment to sample.
   */
  protected int iterations = 0;
  
  /**
   * If running more than one iteration, then write batch statistics
   * to this DataWriter.
   */
  protected DataWriter resultsFile = null;

  
  public static final String P_AUCTION = "auction";
  public static final String P_SIMULATION = "simulation";
  public static final String P_ITERATIONS = "iterations";
  public static final String P_WRITER = "writer";

  static Logger logger = Logger.getLogger("JASA");


  public static void main( String[] args ) {

    try {

      gnuMessage();
      
      if ( args.length < 1 ) {
        fatalError("You must specify a parameter file");
      }

      String fileName = args[0];
      File file = new File(fileName);
      if ( ! file.canRead() ) {
        fatalError("Cannot read parameter file " + fileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      ParameterDatabase parameters = new ParameterDatabase(file);
      MarketSimulation simulation = new MarketSimulation();
      simulation.setup(parameters, new Parameter(P_SIMULATION));
      simulation.run();

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    logger.debug("Setup... ");

    GlobalPRNG.setup(parameters, base);

    auction =
      (RoundRobinAuction)
        parameters.getInstanceForParameterEq(base.push(P_AUCTION),
                                              null,
                                              RoundRobinAuction.class);

    auction.setup(parameters, base.push(P_AUCTION));
    
    iterations = 
      parameters.getIntWithDefault(base.push(P_ITERATIONS), null, iterations);
    
    try {
      resultsFile = 
        (DataWriter) 
        	parameters.getInstanceForParameter(base.push(P_WRITER), null, 
        	    								DataWriter.class);
      if ( resultsFile instanceof Parameterizable ) {
        ((Parameterizable) resultsFile).setup(parameters, base.push(P_WRITER));
      }
    } catch ( ParamClassLoadException e ) {
      resultsFile = null;
    }

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
    auction.run();
    logger.info("Auction finished.");
    auction.generateReport();
  }

  
  public void runBatchExperiment( int n ) {
    HashMap resultsStats = new HashMap();
    for( int i=0; i<n; i++ ) {
      logger.info("Running experiment " + (i+1) + " of " + n + "... ");
      auction.reset();
      auction.run();
      recordResults(auction.getResults(), resultsStats);
      logger.info("done.\n");
    }
    finalReport(resultsStats);
  }
  
  
  public static void gnuMessage() {
    System.out.println(JASAConstants.getGnuMessage());
  }

  
  
  protected void finalReport( Map resultsStats ) {
    logger.info("\nResults");
    logger.info("-------");
    ArrayList variableList = new ArrayList(resultsStats.keySet());
    Collections.sort(variableList);
    Iterator i = variableList.iterator();
    while ( i.hasNext() ) {
      String varName = (String) i.next();
      logger.info("");
      Distribution stats = (Distribution) resultsStats.get(varName);
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
      String variableName = (String) i.next();
      Object value = results.get(variableName);
      if ( value instanceof Number ) {
        double v = ((Number) value).doubleValue();
        CummulativeDistribution varStats = 
          (CummulativeDistribution) resultsStats.get(variableName);
        if ( varStats == null ) {
          varStats = new CummulativeDistribution(variableName);
          resultsStats.put(variableName, varStats);
        }
        varStats.newData(v);
      }
      if ( resultsFile != null ) {
        resultsFile.newData(value);
      }
    }
  }

  protected static void fatalError( String message ) {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }



}
