package uk.ac.liv.auction.agent.jade;

import jade.content.Predicate;

public class RemoveShoutAction implements Predicate {

  ACLShout shout;

  public RemoveShoutAction() {
  }

  public ACLShout getShout() {
    return shout;
  }

  public void setShout( ACLShout shout ) {
    this.shout = shout;
  }
}