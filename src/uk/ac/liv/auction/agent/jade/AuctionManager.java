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

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.Parameterizable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import jade.core.*;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import jade.content.*;
import jade.content.onto.OntologyException;
import jade.content.lang.Codec;

import jade.proto.*;

import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import jade.lang.acl.*;

import jade.content.lang.sl.*;

import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;

import java.io.*;


/**
 * A JADE agent for starting an auction simulation.
 */

public class AuctionManager extends JADEAbstractAuctionAgent {

  AID auctioneerAID = null;

  PlatformController container;

  static final String P_AUCTION = "auction";
  static final String P_NUM_AGENT_TYPES = "numagenttypes";
  static final String P_NUM_AGENTS = "numagents";
  static final String P_AGENT_TYPE = "agenttype";
  static final String P_AGENTS = "agents";
  static final String P_AUCTIONEER = "auctioneer";

  static final String DEFAULT_PARAMETER_FILE = "examples/jade.params";


  public AuctionManager() {
  }


  protected void setup() {
    super.setup();
    activateUI();
  }


  public void setup( ParameterDatabase parameters, Parameter base ) {

    try {

      System.out.print("Setup.. ");

      container = getContainerController();

      Object[] auctioneerArguments = new Object[2];
      auctioneerArguments[0] = parameters;
      auctioneerArguments[1] = base.push(P_AUCTIONEER);
      AgentController auctioneerController =
          container.createNewAgent("auctioneer",
          JADERandomRobinAuctioneer.class.getName(),
          auctioneerArguments);

      auctioneerController.start();

      auctioneerAID = findAuctioneer();

      int numAgentTypes = parameters.getInt(base.push(P_NUM_AGENT_TYPES), null, 1);

      for( int t=0; t<numAgentTypes; t++ ) {

        Parameter typeParam = base.push(P_AGENT_TYPE).push(""+t);
        Parameter agentParam = typeParam.push(P_AGENTS);

        int numAgents = parameters.getInt(typeParam.push(P_NUM_AGENTS), null, 0);
        for( int i=0; i<numAgents; i++ ) {

          JADETraderAgentAdaptor dummyAgent =
                   (JADETraderAgentAdaptor) parameters.getInstanceForParameter(typeParam, null,
                   JADETraderAgentAdaptor.class);

          try {
            Object[] agentParameters = new Object[2];
            agentParameters[0] = parameters;
            agentParameters[1] = typeParam;
            String agentClassName = dummyAgent.getClass().getName();
            System.out.println("Attempting to start trader agent with class name " + agentClassName);
            AgentController agentController =
                container.createNewAgent("trader-" + i + "-" + t, agentClassName,
                agentParameters);
            agentController.start();
          } catch ( Exception e ) {
            e.printStackTrace();
            throw new Error(e.getMessage());
          }

        }
      }
      System.out.println("done.");
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }


  public void activateUI() {
    System.out.println("auction manager activating UI");
    ManagerUIFrame ui = new ManagerUIFrame(this);
    ui.setSize( 400, 200 );
    ui.setLocation( 400, 400 );
    ui.pack();
    ui.setVisible(true);
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
      JADEAbstractAuctionAgent.sendMessage(this, start, startContent);
    } catch ( Exception e ) {
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
    System.out.println(this + ": setting up... ");
    Object[] arguments = getArguments();
    String parameterFileName = DEFAULT_PARAMETER_FILE;
    if ( arguments != null && arguments.length >= 1 ) {
      parameterFileName = (String) arguments[0];
    }
    System.out.println("Using parameter file " + parameterFileName);
    File paramFile = new File(parameterFileName);
    if ( ! paramFile.canRead() ) {
      System.err.println("Cannot read parameter file " + parameterFileName);
      throw new Error("Cannot read parameter file " + parameterFileName);
    }

    try {
      ParameterDatabase parameters = new ParameterDatabase(paramFile);
      setup(parameters, new Parameter(P_AUCTION));
      System.out.println("done.");
    } catch ( IOException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }

  }

}