package uk.ac.liv.auction.agent.jade;

import jade.content.Predicate;

public class RegisterAction implements Predicate {

  String agent;

  public RegisterAction() {
  }

  public void setAgent( String agent ) {
    this.agent = agent;
  }

  public String getAgent() {
    return agent;
  }

}