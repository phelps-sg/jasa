package uk.ac.liv.auction.agent.jade;

import jade.content.AgentAction;

public class RegisterAction implements AgentAction {

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