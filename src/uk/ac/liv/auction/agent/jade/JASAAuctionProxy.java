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

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.AuctionException;
import uk.ac.liv.auction.agent.TraderAgent;
import uk.ac.liv.auction.core.MarketQuote;

import jade.core.Agent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;

import org.apache.log4j.Logger;

/**
 * An implementation of the JASA Auction interface that translates
 * JASA method invocations into ACL messages for a JADE auctioneer agent.
 *
 * @see JADEAuctionAdaptor
 * @see Auction
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public class JASAAuctionProxy extends JASAProxy implements Auction {

  static Logger logger = Logger.getLogger(JASAAuctionProxy.class);

  public JASAAuctionProxy( AID targetJadeID, Agent sender ) {
    super(targetJadeID, sender);
  }

  public void newShout( Shout shout ) throws AuctionException {
    ACLShout aclShout = null;
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(targetJadeID);
      NewShoutAction content = new NewShoutAction();
      aclShout = new ACLShout(shout);
      aclShout.setAgentName(sender.getAID().getName());
      content.setShout(aclShout);
      ((JADEAbstractAuctionAgent) sender).sendMessage(msg, content);
    } catch ( Exception e ) {
      logger.error("unable to place shout: aclShout = " + aclShout);
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }


  public boolean closed() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method closed() not yet implemented.");
  }

  public void close() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method close() not yet implemented.");
  }

  public boolean shoutAccepted( Shout shout ) {
    /**@todo Implement shoutAccepted()*/
    throw new java.lang.UnsupportedOperationException("Method shoutAccepted() not yet implemented.");
  }


  public void removeShout( Shout shout ) {
    ACLShout aclShout = null;
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(targetJadeID);
      RemoveShoutAction content = new RemoveShoutAction();
      aclShout = new ACLShout(shout);
      aclShout.setAgentName(sender.getAID().getName());
      content.setShout(aclShout);
      ((JADEAbstractAuctionAgent) sender).sendMessage(msg, content);
    } catch ( Exception e ) {
      logger.error("unable to remove shout: aclShout = " + aclShout);
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
  }

  public Shout getLastShout() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method getLastShout() not yet implemented.");
  }

  public void printState() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method printState() not yet implemented.");
  }

  public void clear( Shout ask, Shout bid, double price ) {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method clear() not yet implemented.");
  }

  public int getAge() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method getAge() not yet implemented.");
  }

  public int getNumberOfTraders() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method getNumberOfTraders() not yet implemented.");
  }

  public MarketQuote getQuote() {
    /**@todo Implement this uk.ac.liv.auction.core.QuoteProvider method*/
    throw new java.lang.UnsupportedOperationException("Method getQuote() not yet implemented.");
  }

}
