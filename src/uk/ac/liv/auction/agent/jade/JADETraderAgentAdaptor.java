package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.*;

import uk.ac.liv.auction.core.*;

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


public class JADETraderAgentAdaptor extends JADEAbstractAuctionAgent {

  AbstractTraderAgent jasaTraderAgent;

  public static final String SERVICE_TRADER = "JASATrader";


  public JADETraderAgentAdaptor( AbstractTraderAgent jasaTraderAgent ) {
    this.jasaTraderAgent = jasaTraderAgent;
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
                jasaTraderAgent.informOfSeller(p.getShout().jasaShout(),
                    new JASATraderAgentProxy(new AID(p.getSeller(), true), myAgent),
                    p.getPrice(), p.getQuantity());

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

    addBehaviour( new OneShotBehaviour(this) {
      public void action() {
        try {
          AID auctioneerAID = findAuctioneer();
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.addReceiver(auctioneerAID);
          RegisterAction content = new RegisterAction();
          content.setAgent(getAID().getName());
          JADEAbstractAuctionAgent.sendMessage(myAgent, msg, content);
        } catch (Exception fe) {
          fe.printStackTrace();
        }
      }
    } );
  }




}

