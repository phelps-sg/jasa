package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.core.Shout;

import jade.content.Concept;

public class ACLShout implements Concept  {

  String agentName;
  Shout jasaShout;

  public ACLShout( Shout jasaShout ) {
    this.jasaShout = jasaShout;
  }


  public void setAgentName( String agentName ) {
    this.agentName = agentName;
  }

  public String getAgentName() {
    return agentName;
  }

  public double getPrice() {
    return jasaShout.getPrice();
  }

  public int getQuantity() {
    return jasaShout.getQuantity();
  }

  public boolean getIsBid() {
    return jasaShout.isBid();
  }

  public int getId() {
    return jasaShout.getId();
  }

  public void setPrice( double price ) {
    jasaShout.setPrice(price);
  }

  public void setQuantity( int quantity ) {
    jasaShout.setQuantity(quantity);
  }


  public void setId(int id) {
    ShoutWithMutableId muted = new ShoutWithMutableId();
    muted.copyFrom(jasaShout);
    muted.setId(id);
    jasaShout = muted;
  }

  public Shout jasaShout() {
    return jasaShout;
  }


}

class ShoutWithMutableId extends Shout {

  public void setId( int id ) {
    this.id = id;
  }

}
