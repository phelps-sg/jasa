package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.core.Shout;

import jade.content.Concept;

public class ACLShout extends Shout implements Concept  {

  String agentName;

  public ACLShout() {
    super();
  }


  public void setAgentName( String agentName ) {
    this.agentName = agentName;
  }

  public String getAgentName() {
    return agentName;
  }


}