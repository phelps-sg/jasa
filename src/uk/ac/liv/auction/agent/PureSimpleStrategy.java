package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 * @version 1.0
 */

public class PureSimpleStrategy implements Strategy {

  AbstractTraderAgent agent;

  double delta;

  int quantity;

  public PureSimpleStrategy( AbstractTraderAgent agent, double margin, int quantity ) {
    this.agent = agent;
    if ( agent.isSeller() ) {
      delta = margin;
    } else {
      delta = -margin;
    }
    this.quantity = quantity;
  }

  public void modifyShout( Shout shout ) {
    shout.setIsBid(agent.isBuyer());
    shout.setPrice(agent.getPrivateValue() + delta);
    shout.setQuantity(quantity);
    if ( shout.getPrice() < 0 ) {
      shout.setPrice(0);
    }
  }

}