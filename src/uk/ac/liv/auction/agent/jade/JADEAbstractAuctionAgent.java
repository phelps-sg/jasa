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

package uk.ac.liv.auction.agent.jade;

import jade.core.*;
import jade.core.behaviours.SenderBehaviour;

import jade.content.*;
import jade.content.onto.OntologyException;
import jade.content.lang.Codec;

import jade.lang.acl.*;

import jade.content.lang.sl.*;

import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.Parameterizable;

import org.apache.log4j.Logger;


/**
 * Super-class for all JADE auction agents.
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public abstract class JADEAbstractAuctionAgent extends jade.core.Agent {

  static Logger logger = Logger.getLogger(JADEAbstractAuctionAgent.class);

  /**
   * The time in ms. to sleep in between attempts to contact
   * the auctioneer.
   */
  static final int REGISTER_RETRY_PERIOD = 1000;


  protected void setup() {
    try {

      // Create the agent's description of itself
      DFAgentDescription dfd = new DFAgentDescription();
      dfd.setName(getAID());
      ServiceDescription sd = new ServiceDescription();
      sd.setName(getServiceName());
      sd.setType(getServiceName());
      dfd.addServices(sd);
      DFService.register(this, dfd);

      // Register the codec for the SL0 language
      getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);

      // Register the ontology used by this application
      getContentManager().registerOntology(AuctionOntology.getInstance());

      examineArguments();

      addBehaviours();

    } catch ( Exception e ) {
      e.printStackTrace();
    }

  }


  protected void examineArguments() {
    Object[] args = getArguments();
    // Initialise ourselves from a parameter database if we are given
    // any arguments.
    if ( args != null && args.length > 0 ) {
      ParameterDatabase parameters = (ParameterDatabase) args[0];
      Parameter base = (Parameter) args[1];
      ((Parameterizable) this).setup(parameters, base);
    }
  }


  public void sendMessage( ACLMessage msg, ContentElement content )
                     throws OntologyException, Codec.CodecException {

    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
    msg.setOntology(AuctionOntology.NAME);
    getContentManager().fillContent(msg, content);
    send(msg);
  }
  
  
  public void sendMessageAsynch( ACLMessage msg, ContentElement content ) 
                               throws OntologyException, Codec.CodecException {
    msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
    msg.setOntology(AuctionOntology.NAME);
    getContentManager().fillContent(msg, content);
    addBehaviour(new SenderBehaviour(this, msg));
  }


  public AID findAuctioneer() throws FIPAException, InterruptedException  {
    AID auctioneerAID = null;
    DFAgentDescription dfd = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType(JADEAuctionAdaptor.SERVICE_AUCTIONEER);
    dfd.addServices(sd);
    while (true) {
      SearchConstraints c = new SearchConstraints();
      c.setMaxDepth(new Long(3));
      DFAgentDescription[] result = DFService.search(this,dfd,c);
      if ((result != null) && (result.length > 0)) {
        dfd = result[0];
        auctioneerAID = dfd.getName();
        break;
      }
      Thread.sleep(REGISTER_RETRY_PERIOD);
    }
    return auctioneerAID;
  }


  public abstract void addBehaviours();

  public abstract String getServiceName();


}

