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

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import jade.core.*;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import jade.content.*;

import jade.proto.*;

import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import jade.lang.acl.*;

import jade.content.lang.sl.*;

import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;

/**
 * An adaptor that lets a JASA round-robin trader pretend to be
 * a JADE agent.  This adaptor translates incoming ACL messages into
 * JASA method invocations on the target JASA agent.
 *
 * @author Steve Phelps
 * @version $Revision$
 *
 * @see AbstractTraderAgent
 */

public class JADETraderAgentAdaptor extends JADEAbstractAuctionAgent 
                                     implements Parameterizable {

  AbstractTraderAgent jasaTraderAgent;

  public static final String SERVICE_TRADER = "JASATrader";


  public JADETraderAgentAdaptor( AbstractTraderAgent jasaTraderAgent ) {
    this();
    this.jasaTraderAgent = jasaTraderAgent;
  }
  
  public JADETraderAgentAdaptor() {    
  }

 
  public void setup( ParameterDatabase parameters, Parameter base ) {
    jasaTraderAgent = 
      (AbstractTraderAgent) parameters.getInstanceForParameterEq(base, null,
                                                                  AbstractTraderAgent.class);    
    jasaTraderAgent.setup(parameters, base);
  }  

  
  public String getServiceName() {
    return SERVICE_TRADER;
  }

  public void addBehaviours() {
    
    // add a Behaviour to handle messages from auctioneers
    
    addBehaviour( new CyclicBehaviour( this ) {
      public void action() {
        ACLMessage msg = receive();

        try {

          if ( msg != null ) {
            if ( msg.getPerformative() == msg.INFORM ) {
              ContentElement content = getContentManager().extractContent(msg);
              if ( content instanceof RequestShoutAction ) {
                jasaTraderAgent.requestShout(
                    new JASAAuctionProxy(msg.getSender(), myAgent));
              } else if ( content instanceof BidSuccessfulPredicate ) {
                BidSuccessfulPredicate p = (BidSuccessfulPredicate) content;
                logger.debug("Successful bid: " + p);
                //TODO
                //jasaTraderAgent.informOfSeller(p.getShout().jasaShout(),
                //    new JASATraderAgentProxy(new AID(p.getSeller(), true), myAgent),
                //    p.getPrice(), p.getQuantity());

              }
            }
          }

        } catch ( Exception e ) {
          e.printStackTrace();
          //TODO
        }

        block();
      }
    } );
    
    // add initialisation behaviour to find and register with an auctioneer

    addBehaviour( new OneShotBehaviour(this) {
      public void action() {
        try {
          AID auctioneerAID = findAuctioneer();
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.addReceiver(auctioneerAID);
          RegisterAction content = new RegisterAction();
          content.setAgent(getAID().getName());
          ((JADEAbstractAuctionAgent) myAgent).sendMessage(msg, content);
        } catch (Exception fe) {
          fe.printStackTrace();
        }
      }
    } );
  }



}

