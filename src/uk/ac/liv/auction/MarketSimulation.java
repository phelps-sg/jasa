/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;

import uk.ac.liv.ai.learning.Learner;
import uk.ac.liv.ai.learning.StochasticLearner;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Seedable;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
 * <tr><td valign=top><i>base</i><tt>.agenttype.</tt><i>n</i><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of different agent types)</td></tr>
 *
 * <tr><td valign=top><i>base</i><tt>.agenttype.</tt><i>i</i><br>
 * <font size=-1>classname, inherits uk.ac.liv.auction.agent.RoundRobinTrader</font></td>
 * <td valign=top>(the class for agent type #<i>i</i>)</td></tr>
 *
 * </table>
 *
 * @author Steve Phelps
 */

public class MarketSimulation implements Parameterizable, Runnable,
                                          Serializable {

  protected RoundRobinAuction auction;

  protected long prngSeed;

  public static final String P_AUCTION = "auction";
  public static final String P_NUM_AGENT_TYPES = "n";
  public static final String P_NUM_AGENTS = "numagents";
  public static final String P_AGENT_TYPE = "agenttype";
  public static final String P_AGENTS = "agents";
  public static final String P_CONSOLE = "console";
  public static final String P_SIMULATION = "simulation";
  public static final String P_SEED = "seed";


  public static final String VERSION = "0.21";

  public static final String GNU_MESSAGE =
    "\n" +
    "JASA v" + VERSION + " - (C) 2001-2003 Steve Phelps\n" +
    "JASA comes with ABSOLUTELY NO WARRANTY. This is free software,\n" +
    "and you are welcome to redistribute it under certain conditions;\n" +
    "see the GNU General Public license for more details.\n\n" +
    "This is alpha test software.  Please report any bugs, issues\n" +
    "or suggestions to sphelps@csc.liv.ac.uk.\n";

  static Logger logger = Logger.getLogger("JASA");



  public static void main( String[] args ) {

    try {

      if ( args.length < 1 ) {
        fatalError("Must specify a parameter file");
      }

      String fileName = args[0];
      File file = new File(fileName);
      if ( ! file.canRead() ) {
        fatalError("Cannot read parameter file " + fileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(fileName);

      gnuMessage();

      ParameterDatabase parameters = new ParameterDatabase(file);
      MarketSimulation simulation = new MarketSimulation();
      simulation.setup(parameters, new Parameter(P_SIMULATION));
      simulation.run();
      simulation.report();

    } catch ( Exception e ) {
      logger.error(e);
      e.printStackTrace();
    }
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {
    logger.info("Setup.. ");

    prngSeed =
        parameters.getLongWithDefault(base.push(P_SEED), null,
                                       System.currentTimeMillis());

    auction =
      (RoundRobinAuction)
        parameters.getInstanceForParameterEq(base.push(P_AUCTION),
                                              null,
                                              RoundRobinAuction.class);

    auction.setup(parameters, base.push(P_AUCTION));

    if ( parameters.getBoolean(base.push(P_CONSOLE), null, false) ) {
      auction.activateGUIConsole();
    }

    Parameter typeParam = base.push(P_AGENT_TYPE);

    int numAgentTypes = parameters.getInt(typeParam.push("n"), null, 1);

    for( int t=0; t<numAgentTypes; t++ ) {

      Parameter typeParamT = typeParam.push(""+t);
      Parameter agentParam = typeParamT.push(P_AGENTS);

      int numAgents = parameters.getInt(typeParamT.push(P_NUM_AGENTS), null, 0);

      for( int i=0; i<numAgents; i++ ) {

      	RoundRobinTrader agent =
          (RoundRobinTrader)
            parameters.getInstanceForParameter(typeParamT, null,
                                                RoundRobinTrader.class);
        ((Parameterizable) agent).setup(parameters, typeParamT);
        auction.register(agent);

      }
    }

    seedAgents();

    logger.info("done.");
  }


  public void run() {
    auction.run();
  }


  public void report() {
    auction.generateReport();
  }


  public static void gnuMessage() {
    logger.info(GNU_MESSAGE);
  }


  protected static void fatalError( String message ) {
    System.err.println(message);
    System.exit(1);
  }


  protected void seedAgents() {
    logger.info("seed = " + prngSeed);
    MersenneTwisterFast prng = new MersenneTwisterFast(prngSeed);
    Iterator i = auction.getTraderIterator();
    while ( i.hasNext() ) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      Strategy s = agent.getStrategy();
      if ( s instanceof Seedable ) {
        ((Seedable) s).setSeed(prng.nextLong());
      }
      if ( s instanceof AdaptiveStrategy ) {
        Learner l = ((AdaptiveStrategy) s).getLearner();
        if ( l instanceof StochasticLearner ) {
          ((StochasticLearner) l).setSeed(prng.nextLong());
        }
      }
      agent.reset();
    }
  }

}
