package uk.ac.liv.auction.agent.jade;

import jade.core.Agent;
import jade.core.AID;

public class JASAProxy {

  AID targetJadeID;
  Agent sender;

  public JASAProxy( AID targetJadeID, Agent sender ) {
    this.targetJadeID = targetJadeID;
    this.sender = sender;
  }

  public AID getSenderAID() {
    return sender.getAID();
  }

}