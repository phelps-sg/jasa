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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;

import uk.ac.liv.auction.stats.GraphMarketDataLogger;

import uk.ac.liv.util.Parameterizable;

import uk.ac.liv.auction.ui.JASAConsoleGraph;

import uk.ac.liv.prng.GlobalPRNG;
import uk.ac.liv.prng.PRNGFactory;

import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import uchicago.src.sim.engine.*;

import uchicago.src.sim.analysis.OpenSequenceGraph;

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
 * @version $Revision$
 */

public class RepastMarketSimulation 
     implements SimModel, Serializable {

  /**
   * The auction used in this simulation.
   */
  protected RoundRobinAuction auction;
  
  protected String parameterFileName;
  
  protected IController controller;
  
  protected ModelManipulator modelManipulator;
  
  protected Hashtable parameterDescriptors;
  
  protected Schedule schedule;
  
  protected OpenSequenceGraph graph;

  public static final String P_AUCTION = "auction";
  public static final String P_NUM_AGENT_TYPES = "n";
  public static final String P_NUM_AGENTS = "numagents";
  public static final String P_AGENT_TYPE = "agenttype";
  public static final String P_AGENTS = "agents";
  public static final String P_CONSOLE = "console";
  public static final String P_SIMULATION = "simulation";


  public static final String VERSION = "0.33";

  public static final String GNU_MESSAGE =
    "\n" +
    "JASA v" + VERSION + " - (C) 2001-2004 Steve Phelps\n" +
    "JASA comes with ABSOLUTELY NO WARRANTY. This is free software,\n" +
    "and you are welcome to redistribute it under certain conditions;\n" +
    "see the GNU General Public license for more details.\n\n" +
    "This is alpha test software.  Please report any bugs, issues\n" +
    "or suggestions to sphelps@csc.liv.ac.uk.\n";

  static Logger logger = Logger.getLogger("JASA");


  public static void main( String[] args ) {
    SimInit init = new SimInit();
    init.loadModel( new RepastMarketSimulation(args[0]), null, false);
  }
  
  public RepastMarketSimulation( String parameterFileName ) {
    this.parameterFileName = parameterFileName;
    modelManipulator = new ModelManipulator();
    parameterDescriptors = new Hashtable();
    schedule = new Schedule();
    schedule.scheduleActionBeginning(1, this, "step");
  }
  
  public RepastMarketSimulation() {
    this(null);
  }

  public void setup() {

    try {

      gnuMessage();
      
      File file = new File(parameterFileName);
      if ( ! file.canRead() ) {
        fatalError("Cannot read parameter file " + parameterFileName);
      }

      org.apache.log4j.PropertyConfigurator.configure(parameterFileName);

      ParameterDatabase parameters = new ParameterDatabase(file);
      setup(parameters, new Parameter(P_SIMULATION));
    
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

    if ( parameters.getBoolean(base.push(P_CONSOLE), null, false) ) {
      auction.activateGUIConsole();
    }

    Parameter typeParam = base.push(P_AGENT_TYPE);

    int numAgentTypes = parameters.getInt(typeParam.push("n"), null, 1);

    for( int t=0; t<numAgentTypes; t++ ) {

      Parameter typeParamT = typeParam.push(""+t);
      Parameter agentParam = typeParamT.push(P_AGENTS);

      int numAgents = parameters.getInt(typeParamT.push(P_NUM_AGENTS), null, 0);

      logger.info("Configuring agent population " + t + ":\n\t" + numAgents +
                   " agents of type " + parameters.getString(typeParamT, null));

      for( int i=0; i<numAgents; i++ ) {

      	RoundRobinTrader agent =
          (RoundRobinTrader)
            parameters.getInstanceForParameter(typeParamT, null,
                                                RoundRobinTrader.class);
        ((Parameterizable) agent).setup(parameters, typeParamT);
        auction.register(agent);

      }

      logger.info("done.\n");
    }
    

    logger.info("prng = " + PRNGFactory.getFactory().getDescription());
    logger.info("seed = " + GlobalPRNG.getSeed() + "\n");
    
    logger.debug("Setup complete.");
  }

  public void begin() {
    GraphMarketDataLogger graphLogger;
    if ( (graphLogger = GraphMarketDataLogger.getSingletonInstance()) != null ) {
      graph = new JASAConsoleGraph(getName(), this, graphLogger);
      graph.display();
    }
    auction.begin();
  }
  
  public void step() {
    try {
      auction.step();
    } catch ( AuctionClosedException e ) {
      controller.stopSim();
    }
    graph.step();
  }
  
  public String getName() {
    return "JASA auction simulation";
  }
  
  public String getParameterFileName () {
    return parameterFileName;
  }
  
  public void setParameterFileName ( String parameterFileName) {
    this.parameterFileName = parameterFileName;
  }
  
  public Schedule getSchedule() {
    return schedule;
  }
  
  public String[] getInitParam() {
    return new String[] {"parameterFileName"};
  }

  public void report() {
    auction.generateReport();
  }


  public static void gnuMessage() {
    System.out.println(GNU_MESSAGE);
  }


  protected static void fatalError( String message ) {
    System.err.println("ERROR: " + message);
    System.exit(1);
  }



  public void clearMediaProducers () {
    // TODO Auto-generated method stub

  }
  public void clearPropertyListeners () {
    // TODO Auto-generated method stub

  }
  public void generateNewSeed () {
    // TODO Auto-generated method stub

  }
  
  public IController getController () {
    return controller;
  }
  
  public Vector getMediaProducers () {
    // TODO Auto-generated method stub
    return null;
  }
  
  public ModelManipulator getModelManipulator () {
    return modelManipulator;
  }
  
  public Hashtable getParameterDescriptors () {
    return parameterDescriptors;
  }
  
  public String getPropertiesValues () {
    return "";
  }
  
  public long getRngSeed () {
    return GlobalPRNG.getSeed();
  }
  
  public double getTickCount () {
    return auction.getAge();
  }
  
  public void setController ( IController controller) {
   this.controller = controller;
  }
  
  public void setRngSeed ( long seed) {
    
    GlobalPRNG.initialiseWithSeed(seed);
  }
  
  public void addSimEventListener ( SimEventListener l) {
    // TODO Auto-generated method stub

  }
  
  public void fireSimEvent ( SimEvent evt) {
    // TODO Auto-generated method stub

  }
  
  public void removeSimEventListener ( SimEventListener l) {
    // TODO Auto-generated method stub

  }
}
