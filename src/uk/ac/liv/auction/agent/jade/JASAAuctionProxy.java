package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.AuctionException;
import uk.ac.liv.auction.agent.TraderAgent;
import uk.ac.liv.auction.core.MarketQuote;

import jade.core.Agent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;

public class JASAAuctionProxy extends JASAProxy implements Auction {

  public JASAAuctionProxy( AID targetJadeID, Agent sender ) {
    super(targetJadeID, sender);
  }

  public void newShout( Shout shout ) throws AuctionException {
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
      NewShoutAction content = new NewShoutAction();
      ACLShout aclShout = new ACLShout(shout);
      aclShout.setAgent(sender.getAID().getName());
      content.setShout(aclShout);
      sender.getContentManager().fillContent(msg, content);
      JADEAbstractAuctionAgent.sendMessage(sender, msg);
    } catch ( Exception e ) {
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


  public void removeShout(Shout shout) {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method removeShout() not yet implemented.");
  }

  public Shout getLastShout() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method getLastShout() not yet implemented.");
  }

  public void printState() {
    /**@todo Implement this uk.ac.liv.auction.core.Auction method*/
    throw new java.lang.UnsupportedOperationException("Method printState() not yet implemented.");
  }

  public void clear(Shout ask, TraderAgent buyer, TraderAgent seller, double price, int quantity) {
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