package uk.ac.liv.auction.agent.jade;

import jade.content.Predicate;

public class BidSuccessfulPredicate implements Predicate {

  String seller;

  int quantity;

  double price;

  ACLShout shout;

  public BidSuccessfulPredicate() {
  }

  public String getSeller() { return seller; }
  public int getQuantity() { return quantity; }
  public double getPrice() { return price; }
  public ACLShout getShout() { return shout; }

  public void setSeller( String seller ) { this.seller = seller; }
  public void setQuantity( int quantity ) { this.quantity = quantity; }
  public void setPrice( double price ) { this.price = price; }
  public void setShout( ACLShout shout ) { this.shout = shout; }

}