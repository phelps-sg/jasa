package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.core.Shout;

import jade.content.Concept;

public class ACLShout implements Concept {

  Shout jasaShout;

  String agentName;

  public ACLShout() {
    jasaShout = new Shout();
  }

  public ACLShout( Shout shout ) {
    this.jasaShout = shout;
  }

  public double getPrice() {
    return jasaShout.getPrice();
  }

  public void setPrice( double price ) {
    jasaShout.setPrice(price);
  }

  public int getQuantity() {
    return jasaShout.getQuantity();
  }

  public void setQuantity( int quantity ) {
    jasaShout.setQuantity(quantity);
  }

  public void setIsBid( boolean isBid ) {
    jasaShout.setIsBid(isBid);
  }

  public boolean getIsBid() {
    return jasaShout.isBid();
  }

  public void setAgent( String agentName ) {
    this.agentName = agentName;
  }

  public String getAgent() {
    return agentName;
  }

  public Shout getJASAShout() {
    return jasaShout;
  }

}