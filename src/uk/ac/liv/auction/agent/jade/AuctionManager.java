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

package uk.ac.liv.auction.agent.jade;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import jade.core.*;

import jade.core.behaviours.OneShotBehaviour;


import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import jade.lang.acl.*;

import java.io.*;

import org.apache.log4j.Logger;


/**
 * A JADE agent for starting an auction simulation.
 */

public class AuctionManager extends JADEAbstractAuctionAgent {

  protected AID auctioneerAID = null;

  protected PlatformController container;

  static Logger logger = Logger.getLogger(AuctionManager.class);

  public static final String AGENTNAME_AUCTIONEER = "Auctioneer";
  public static final String AGENTNAME_TRADER = "Trader";

  static final String P_AUCTION = "auction";
  static final String P_NUM_AGENT_TYPES = "n";
  static final String P_NUM_AGENTS = "numagents";
  static final String P_AGENT_TYPE = "agenttype";
  static final String P_AGENTS = "agents";
  static final String P_AUCTIONEER = "auctioneer";

  static final String DEFAULT_PARAMETER_FILE = "examples/jade.params";


  public AuctionManager() {
  }


  protected void setup() {
    super.setup();
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    try {

      logger.info("Setup.. ");

      container = getContainerController();

      int numAgentTypes =
          parameters.getInt(base.push(P_AGENT_TYPE).push("n"), null, 1);

      for( int t=0; t<numAgentTypes; t++ ) {

        Parameter typeParam = base.push(P_AGENT_TYPE).push(""+t);
        Parameter agentParam = typeParam.push(P_AGENTS);

        int numAgents = parameters.getInt(typeParam.push(P_NUM_AGENTS), null, 0);
        for( int i=0; i<numAgents; i++ ) {

          try {
            Object[] agentParameters = new Object[2];
            agentParameters[0] = parameters;
            agentParameters[1] = typeParam;
            String agentClassName = JADETraderAgentAdaptor.class.getName();
            logger.debug("Attempting to start trader agent with class name " +
                           agentClassName);
            AgentController agentController =
                container.createNewAgent(AGENTNAME_TRADER + i + "-" + t,
                                           agentClassName, agentParameters);
            agentController.start();
          } catch ( Exception e ) {
            e.printStackTrace();
            throw new Error(e.getMessage());
          }

        }
      }

      // Create auctioneer
      Object[] auctioneerArguments = new Object[3];
      auctioneerArguments[0] = parameters;
      auctioneerArguments[1] = base;
      auctioneerArguments[2] = this;
      AgentController auctioneerController =
          container.createNewAgent(AGENTNAME_AUCTIONEER,
                                   JADEAuctionAdaptor.class.getName(),
                                   auctioneerArguments);

      auctioneerController.start();

      auctioneerAID = findAuctioneer();

      logger.info("done.");

      ManagerUIFrame.getSingletonInstance().enableStartButton();

    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }


  public String getServiceName() {
    return "AUCTION_MANAGER";
  }


  public void startAuction() {
    try {
      if ( auctioneerAID == null ) {
        auctioneerAID = findAuctioneer();
      }
      ACLMessage start = new ACLMessage(ACLMessage.INFORM);
      start.addReceiver(auctioneerAID);
      StartAuctionAction startContent = new StartAuctionAction();
      logger.debug("Sending " + start + " with content " + startContent + " to start auction.");
      sendMessageAsynch(start, startContent);
    } catch ( Exception e ) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
  }


  public void addBehaviours() {
    addBehaviour( new OneShotBehaviour() {
      public void action() {
        try {
          setupFromParameterFile();
        } catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    } );
  }


  public void setupFromParameterFile() {
    Object[] arguments = getArguments();
    String parameterFileName = DEFAULT_PARAMETER_FILE;
    if ( arguments != null && arguments.length >= 1 ) {
      parameterFileName = (String) arguments[0];
    }

    File paramFile = new File(parameterFileName);
    if ( ! paramFile.canRead() ) {
      System.err.println("Cannot read parameter file " + parameterFileName);
      throw new Error("Cannot read parameter file " + parameterFileName);
    }

    org.apache.log4j.PropertyConfigurator.configure(parameterFileName);
    logger.debug("Using parameter file " + parameterFileName);

    uk.ac.liv.auction.MarketSimulation.gnuMessage();

    try {
      ParameterDatabase parameters = new ParameterDatabase(paramFile);
      setup(parameters, new Parameter(P_AUCTION));
      logger.debug("setup done.");
    } catch ( IOException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }

  }

}