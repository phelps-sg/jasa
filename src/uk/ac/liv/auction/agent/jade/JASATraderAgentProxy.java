package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

import jade.core.*;
import jade.lang.acl.*;

import jade.content.onto.OntologyException;

public class JASATraderAgentProxy extends JASAProxy implements RoundRobinTrader {

  public JASATraderAgentProxy( AID targetJadeID, Agent sender ) {
    super(targetJadeID, sender);
  }

  public void requestShout( Auction auction ) {
    try {
      ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
      RequestShoutAction content = new RequestShoutAction();
      sender.getContentManager().fillContent(msg, content);
      sender.send(msg);
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e);
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
      sender.getContentManager().fillContent(msg, content);
      sender.send(msg);
    } catch ( Exception e ) {
      e.printStackTrace();
      throw new Error(e);
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