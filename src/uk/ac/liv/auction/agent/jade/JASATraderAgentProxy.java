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

import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

import jade.core.*;
import jade.lang.acl.*;

import jade.content.onto.OntologyException;

/**
 * An implementation of the JASA RoundRobinTrader interface that translates
 * JASA method invocations into ACL messages to a JADE trader agent;
 * that is, a JADETraderAgentAdaptor.
 *
 * @see JADETraderAgentAdaptor
 * @see uk.ac.liv.auction.agent.RoundRobinTrader
 *
 * @author Steve Phelps
 */

public class JASATraderAgentProxy extends JASAProxy implements RoundRobinTrader {

  public JASATraderAgentProxy( AID targetJadeID, Agent sender ) {
    super(targetJadeID, sender);
  }

  public void requestShout( Auction auction ) {
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(targetJadeID);
      RequestShoutAction content = new RequestShoutAction();
      JADEAbstractAuctionAgent.sendMessage(sender, msg, content);
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

  public void informOfSeller(Shout winningShout, RoundRobinTrader seller,
                             double price, int quantity) {
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      BidSuccessfulPredicate content = new BidSuccessfulPredicate();
      content.setPrice(price);
      content.setQuantity(quantity);
      content.setSeller(((JASATraderAgentProxy) seller).getSenderAID().getName());
      JADEAbstractAuctionAgent.sendMessage(sender, msg, content);
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

  public AID getSenderAID() {
    return sender.getAID();
  }

  public int getId() {
    //TODO
    return -1;
  }

  public void reset() {
    //TODO
  }

}