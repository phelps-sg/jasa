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

import jade.core.*;

import jade.core.behaviours.*;

import jade.content.*;

import jade.proto.*;

import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;

import jade.lang.acl.*;

import jade.content.lang.sl.*;

import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;

import uk.ac.liv.util.Parameterizable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import org.apache.log4j.Logger;

/**
 * An adaptor that lets a JASA auction pretend to be a JADE agent.
 * This adaptor translates incoming ACL messages into JASA
 * method invocations on the wrapped auction class.
 *
 * @author Steve Phelps
 */

public class JADEAuctionAdaptor extends JADEAbstractAuctionAgent
                                 implements Parameterizable {

  protected JADEAuction auction;

  static Logger logger = Logger.getLogger(JADEAuctionAdaptor.class);

  public static final String SERVICE_AUCTIONEER = "JASAAuctioneer";

  static final String STATE_REGISTRATION = "REGISTRATION";
  static final String STATE_REQUEST_SHOUTS = "REQUEST_SHOUTS";
  static final String STATE_PROCESS_SHOUTS = "PROCESS_SHOUTS";
  static final String STATE_FINALISE_ROUND = "FINALISE_ROUND";
  static final String STATE_END = "END";


  public JADEAuctionAdaptor( JADEAuction auction ) {
    this();
    this.auction = auction;    
  }
  
  public JADEAuctionAdaptor() {
  }


  public void setup( ec.util.ParameterDatabase parameters, ec.util.Parameter base ) {
    auction = 
      (JADEAuction) parameters.getInstanceForParameterEq(base, null,
                                                          JADEAuction.class);
    auction.setup(parameters, base);
  }


  public String getServiceName() {
    return SERVICE_AUCTIONEER;
  }

  
  public void addBehaviours() {
      
    RegistrationBehaviour registrationBehaviour = new RegistrationBehaviour(this);
    RequestShoutsBehaviour requestShoutsBehaviour = new RequestShoutsBehaviour(this);
    ProcessShoutsBehaviour processShoutsBehaviour = new ProcessShoutsBehaviour(this);
    FinaliseRoundBehaviour finaliseRoundBehaviour = new FinaliseRoundBehaviour(this);

    FSMBehaviour auctionFSM = new FSMBehaviour(this);
    auctionFSM.registerFirstState(registrationBehaviour, STATE_REGISTRATION);
    auctionFSM.registerState(requestShoutsBehaviour, STATE_REQUEST_SHOUTS);
    auctionFSM.registerState(processShoutsBehaviour, STATE_PROCESS_SHOUTS);
    auctionFSM.registerState(finaliseRoundBehaviour, STATE_FINALISE_ROUND);
    OneShotBehaviour end = new OneShotBehaviour(this) {
      public void action() {
        auction.generateReport();
      }
    };
    auctionFSM.registerLastState(end, STATE_END);

    auctionFSM.registerDefaultTransition(STATE_REGISTRATION, STATE_REQUEST_SHOUTS);
    auctionFSM.registerDefaultTransition(STATE_REQUEST_SHOUTS, STATE_PROCESS_SHOUTS);
    auctionFSM.registerDefaultTransition(STATE_PROCESS_SHOUTS, STATE_FINALISE_ROUND);
    auctionFSM.registerDefaultTransition(STATE_FINALISE_ROUND, STATE_REQUEST_SHOUTS);
    auctionFSM.registerTransition(STATE_REQUEST_SHOUTS, STATE_END,
                                  RequestShoutsBehaviour.FSM_EVENT_AUCTION_CLOSED);

    addBehaviour(auctionFSM);
  }


/// Inner classes for behaviours

  class RegistrationBehaviour extends SimpleBehaviour {

    boolean finished = false;

    public RegistrationBehaviour( Agent agent ) {
      super(agent);
    }


    public void action() {
      try {
        ACLMessage msg = receive();
        if ( msg != null ) {
          if ( msg.getPerformative() == msg.INFORM ) {
            ContentElement content = getContentManager().extractContent(msg);
            if ( content instanceof RegisterAction ) {
              RegisterAction action = (RegisterAction) content;
              AID traderAID = new AID(action.getAgent(), true);
              auction.register(new JASATraderAgentProxy(traderAID, myAgent));
              block();
            } else if ( content instanceof StartAuctionAction ) {
              logger.info("Starting auction");
              finished = true;
            }
          } else {
            //TODO
          }
        }
      } catch ( Exception e ) {
        e.printStackTrace();
        //TODO
        throw new Error(e.getMessage());
      }
    }

    public boolean done() {
      return finished;
    }

    public int onEnd() {
      return 0;
    }

  }


  class RequestShoutsBehaviour extends OneShotBehaviour {

    boolean auctionClosed = false;

    public static final int FSM_EVENT_AUCTION_CLOSED = 1;

    public RequestShoutsBehaviour( Agent agent ) {
      super(agent);
    }

    public void action() {
      try {
        auction.initiateRound();
      } catch ( AuctionClosedException e ) {
        auctionClosed = true;
      }
    }

    public int onEnd() {
      if ( auctionClosed ) {
        logger.info("Auction closed");
        return FSM_EVENT_AUCTION_CLOSED;
      } else {
        return 0;
      }
    }

  }


  class ProcessShoutsBehaviour extends SimpleBehaviour {


    public ProcessShoutsBehaviour( Agent agent ) {
      super(agent);
    }

    public void action() {
      try {
        ACLMessage msg = receive();
        if ( msg != null ) {
          ContentElement content = getContentManager().extractContent(msg);
          if ( content instanceof NewShoutAction ) {
            ACLShout shout = ((NewShoutAction) content).getShout();
            shout.assignJADEAuctioneer(myAgent);
            auction.newShout(shout.jasaShout());
          } else if ( content instanceof RemoveShoutAction ) {
            ACLShout shout = ((RemoveShoutAction) content).getShout();
            shout.assignJADEAuctioneer(myAgent);
            auction.removeShout(shout.jasaShout());
          }
        }
      } catch ( Exception e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
      //block();
    }

    public boolean done() {
      return auction.roundFinished();
    }

  }


  class FinaliseRoundBehaviour extends OneShotBehaviour {

    public FinaliseRoundBehaviour( Agent agent ) {
      super(agent);
    }

    public void action() {
      auction.finaliseRound();
      logger.debug("End of round " + (auction.getAge()-1));
    }

  }

/// end of inner-classes


}


