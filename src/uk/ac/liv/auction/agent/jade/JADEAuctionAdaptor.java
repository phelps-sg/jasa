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


public class JADEAuctionAdaptor extends jade.core.Agent {

  RoundRobinAuction jasaAuction;

  public static final String SERVICE_AUCTIONEER = "JASAAuctioneer";


  public JADEAuctionAdaptor( RoundRobinAuction jasaAuction ) {
    this.jasaAuction = jasaAuction;
  }


  /**
   * Setup the agent.  Registers with the DF, and adds a behaviour to
   * process incoming messages.
   */
  protected void setup() {
    try {
      System.out.println( getLocalName() + " setting up");

      // Create the agent descrption of itself
      DFAgentDescription dfd = new DFAgentDescription();
      dfd.setName(getAID());
      ServiceDescription sd = new ServiceDescription();
      sd.setType(SERVICE_AUCTIONEER);
      dfd.addServices(sd);
      DFService.register(this, dfd);

      // Register the codec for the SL0 language
      getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);

      // Register the ontology used by this application
      getContentManager().registerOntology(AuctionOntology.getInstance());

      addBehaviours();

    } catch ( Exception e ) {
      e.printStackTrace();
    }

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
              if ( content instanceof RegisterAction ) {
                RegisterAction action = (RegisterAction) content;
                AID traderAID = new AID(action.getAgent(), true);
                jasaAuction.register(new JASATraderAgentProxy(traderAID, myAgent));
              }
            } 
          }

        } catch ( Exception e ) {
          e.printStackTrace();
          //TODO
        }

      }
    } );
  }


}

