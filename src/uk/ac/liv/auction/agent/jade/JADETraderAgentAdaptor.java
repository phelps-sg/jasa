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
            if ( msg.getPerformative() == msg.REQUEST ) {
              ContentElement content = getContentManager().extractContent(msg);
              if ( content instanceof RequestShoutAction ) {
                jasaTraderAgent.requestShout(
                    new JASAAuctionProxy(msg.getSender(), myAgent));
              }
            } else if ( msg.getPerformative() == msg.INFORM ) {
              ContentElement content = getContentManager().extractContent(msg);
              if ( content instanceof BidSuccessfulPredicate ) {
                BidSuccessfulPredicate p = (BidSuccessfulPredicate) content;
                jasaTraderAgent.informOfSeller(p.getShout().getJASAShout(),
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
          AID auctioneerAID = null;
          DFAgentDescription dfd = new DFAgentDescription();
          ServiceDescription sd = new ServiceDescription();
          sd.setType(JADEAuctionAdaptor.SERVICE_AUCTIONEER);
          dfd.addServices(sd);
          while (true) {
            System.out.println(getLocalName()+ " waiting for a JASAAuctioneer registering with the DF");
            SearchConstraints c = new SearchConstraints();
            c.setMaxDepth(new Long(3));
            DFAgentDescription[] result = DFService.search(myAgent,dfd,c);
            if ((result != null) && (result.length > 0)) {
              dfd = result[0];
              auctioneerAID = dfd.getName();
              break;
            }
            Thread.sleep(10000);
          }
          ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
          RegisterAction content = new RegisterAction();
          content.setAgent(getAID().getName());
          getContentManager().fillContent(msg, content);
          JADEAbstractAuctionAgent.sendMessage(myAgent, msg);
        } catch (Exception fe) {
          fe.printStackTrace();
        }
      }
    } );
  }




}

