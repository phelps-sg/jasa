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


public class AuctionManager extends JADEAbstractAuctionAgent {

  AID auctioneerAID = null;

  public AuctionManager() {
  }

  protected void setup() {
    super.setup();
    activateUI();
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

          PlatformController container = getContainerController();
          AgentController auctioneerController =
              container.createNewAgent("auctioneer", "uk.ac.liv.auction.agent.jade.JADERandomRobinAuctioneer", null);
          AgentController trader1Controller =
              container.createNewAgent("trader1", "uk.ac.liv.auction.agent.jade.JADEElectricityTrader", null);

          auctioneerController.start();
          trader1Controller.start();

          auctioneerAID = findAuctioneer();
/*
          ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
          msg.addReceiver(auctioneerAID);
          StartAuctionAction content = new StartAuctionAction();
          JADEAbstractAuctionAgent.sendMessage(myAgent, msg, content); */
        } catch ( Exception e ) {
          e.printStackTrace();
        }
      }
    } );
  }
}