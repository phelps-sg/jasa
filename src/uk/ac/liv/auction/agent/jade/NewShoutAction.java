package uk.ac.liv.auction.agent.jade;

import jade.content.Predicate;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class NewShoutAction implements Predicate {

  ACLShout shout;

  public NewShoutAction() {
  }

  public ACLShout getShout() {
    return shout;
  }

  public void setShout( ACLShout shout ) {
    this.shout = shout;
  }
}