package uk.ac.liv.auction.agent.jade;

import jade.content.AgentAction;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class NewShoutAction implements AgentAction {

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