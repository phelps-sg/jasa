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


public class JADEAuctionAdaptor extends JADEAbstractAuctionAgent {

  protected JADEAuction auction;

  public static final String SERVICE_AUCTIONEER = "JASAAuctioneer";

  static final String STATE_REGISTRATION = "REGISTRATION";
  static final String STATE_REQUEST_SHOUTS = "REQUEST_SHOUTS";
  static final String STATE_PROCESS_SHOUTS = "PROCESS_SHOUTS";
  static final String STATE_FINALISE_ROUND = "FINALISE_ROUND";
  static final String STATE_END = "END";

  class RegistrationBehaviour extends SimpleBehaviour {

    boolean finished = false;

    public RegistrationBehaviour( Agent agent ) {
      super(agent);
    }

    public void action() {
      try {
        ACLMessage msg = receive();
        if ( msg != null ) {
          if ( msg.getPerformative() == msg.REQUEST ) {
            ContentElement content = getContentManager().extractContent(msg);
            if ( content instanceof RegisterAction ) {
              RegisterAction action = (RegisterAction) content;
              AID traderAID = new AID(action.getAgent(), true);
              auction.register(new JASATraderAgentProxy(traderAID, myAgent));
            } else if ( content instanceof StartAuctionAction ) {
              finished = true;
            }
          }
        }
        block();
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
        auction.initiateRound();
      } catch ( AuctionClosedException e ) {
        auctionClosed = true;
      }
    }

    public int onEnd() {
      if ( auctionClosed ) {
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
            auction.newShout(shout.getJASAShout());
          }
        }
      } catch ( Exception e ) {
        e.printStackTrace();
        throw new Error(e.getMessage());
      }
      block();
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
    }

  }


  public JADEAuctionAdaptor( JADEAuction auction ) {
    this.auction = auction;
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


