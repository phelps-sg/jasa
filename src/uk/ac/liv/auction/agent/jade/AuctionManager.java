package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.*;

import uk.ac.liv.auction.core.*;

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

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @versio
 */

public class AuctionManager extends JADEAbstractAuctionAgent {

  public AuctionManager() {
  }

  public String getServiceName() {
    return "AUCTION_MANAGER";
  }

  public void addBehaviours() {
    addBehaviour( new OneShotBehaviour() {
      public void action() {
        try {

          PlatformController container = getContainerController();
          AgentController auctioneerController =
              container.createNewAgent("auctioneer", "uk.ac.liv.auction.agent.jade.JADERandomRobinAuctioneer", null);
          AgentController trader1Controller =
              container.createNewAgent("trader1", "uk.ac.liv.auction.agent.jade.JADEElectricityTrader", null);

          auctioneerController.start();
          trader1Controller.start();

          AID auctioneerAID = findAuctioneer();

          ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
          msg.addReceiver(auctioneerAID);
          StartAuctionAction content = new StartAuctionAction();
          JADEAbstractAuctionAgent.sendMessage(myAgent, msg, content);
        } catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    } );
  }
}