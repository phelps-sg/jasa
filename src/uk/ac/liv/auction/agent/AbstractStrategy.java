package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

public abstract class AbstractStrategy implements
                                        Strategy,
                                        Parameterizable,
                                        Resetable,
                                        Cloneable {

  protected AbstractTraderAgent agent;

  public AbstractStrategy() {
  }

  public AbstractStrategy( AbstractTraderAgent agent ) {
    this.agent = agent;
    initialise();
  }

  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public void reset() {
    initialise();
  }

  public void initialise() {
  }

}