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

/**
 * An adaptor that lets a JASA auction pretend to be a JADE agent.
 *
 * @author Steve Phelps
 */

public class JADEAuctionAdaptor extends JADEAbstractAuctionAgent
                                 implements Parameterizable {

  protected JADEAuction auction;

  public static final String SERVICE_AUCTIONEER = "JASAAuctioneer";

  static final String STATE_REGISTRATION = "REGISTRATION";
  static final String STATE_REQUEST_SHOUTS = "REQUEST_SHOUTS";
  static final String STATE_PROCESS_SHOUTS = "PROCESS_SHOUTS";
  static final String STATE_FINALISE_ROUND = "FINALISE_ROUND";
  static final String STATE_END = "END";

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
            System.out.println("Message content = " + msg.getContent());
            ContentElement content = getContentManager().extractContent(msg);
            if ( content instanceof RegisterAction ) {
              RegisterAction action = (RegisterAction) content;
              AID traderAID = new AID(action.getAgent(), true);
              System.out.println("Registering trader " + traderAID);
              auction.register(new JASATraderAgentProxy(traderAID, myAgent));
            } else if ( content instanceof StartAuctionAction ) {
              System.out.println("Starting auction");
              finished = true;
            }
          } else {
            System.out.println("Received non-understood message: " + msg);
          }
        }
      } catch ( Exception e ) {
        e.printStackTrace();
        //TODO
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
        System.out.println("Initiating auction round..");
        auction.initiateRound();
      } catch ( AuctionClosedException e ) {
        auctionClosed = true;
      }
    }

    public int onEnd() {
      if ( auctionClosed ) {
        System.out.println("Auction closed");
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
        System.out.println("auctioneer: processing shouts");
        ACLMessage msg = receive();
        System.out.println("auctioneer: got msg " + msg);
        if ( msg != null ) {
          ContentElement content = getContentManager().extractContent(msg);
          System.out.println("auctioneer: msg content = " + content);
          if ( content instanceof NewShoutAction ) {
            System.out.println("Recieved new shout " + msg);
            ACLShout shout = ((NewShoutAction) content).getShout();
            System.out.println("JASA shout = " + shout.jasaShout());
            auction.newShout(shout.jasaShout());
          } else if ( content instanceof RemoveShoutAction ) {
            System.out.println("Removing shout " + msg);
            ACLShout shout = ((RemoveShoutAction) content).getShout();
            //auction.removeShout(shout.jasaShout());
          }
        }
      } catch ( Exception e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
      //block();
    }

    public boolean done() {
      System.out.println("auctioneer: checking end of auction");
      System.out.println("finished = " + auction.roundFinished());
      return auction.roundFinished();
    }

  }


  class FinaliseRoundBehaviour extends OneShotBehaviour {

    public FinaliseRoundBehaviour( Agent agent ) {
      super(agent);
    }

    public void action() {
      System.out.println("Finalising auction round");
      auction.finaliseRound();
      System.out.println("Auction age = " + auction.getAge());
    }

  }

/// end of inner-classes


  public JADEAuctionAdaptor( JADEAuction auction ) {
    this.auction = auction;
  }


  public void setup( ec.util.ParameterDatabase parameters, ec.util.Parameter base ) {
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



}


