package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.ai.learning.NPTRothErevLearner;
import uk.ac.liv.ai.learning.StimuliResponseLearner;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

public class StimuliResponseStrategy extends AbstractStrategy {

  StimuliResponseLearner learner;

  static final String P_LEARNER = "learner";

  public StimuliResponseStrategy( AbstractTraderAgent agent ) {
    super(agent);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    learner = (StimuliResponseLearner) parameters.getInstanceForParameter(base.push(P_LEARNER), null, StimuliResponseLearner.class);
    ((Parameterizable) learner).setup(parameters, base);
  }

  public void modifyShout( Shout shout, Auction auction ) {

    // Reward the learner based on last earnings
    learner.reward(agent.getLastProfit());

    // Generate an action from the learning algorithm
    int action = learner.act();

    Debug.assertTrue("action >= 0", action >= 0);
    // Now turn the action into a price
    double price;
    if ( agent.isSeller() ) {
      price = agent.getPrivateValue() + action;
    } else {
      price = agent.getPrivateValue() - action;
    }
    /* TODO
    if ( price < funds ) {
      price = funds;
    } */
    if ( price < 0 ) {
      price = 0;
    }
    shout.setPrice(price);
    shout.setQuantity(agent.determineQuantity(auction));
  }

  public void reset() {
    super.reset();
    ((Resetable) learner).reset();
  }

}
