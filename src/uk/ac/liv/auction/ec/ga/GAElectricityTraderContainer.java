package uk.ac.liv.auction.ec.ga;

import uk.ac.liv.auction.electricity.*;

import uk.ac.liv.ai.learning.RothErevLearner;

import ec.vector.DoubleVectorIndividual;

/**
 * @author Steve Phelps
 */

public class GAElectricityTraderContainer extends DoubleVectorIndividual {

  MREElectricityTrader trader = null;

  public ElectricityTrader getTrader() {
    double propensities[] = (double []) getGenome();
    ((RothErevLearner) trader.getLearner()).setPropensities(propensities);
    trader.reset();
    return trader;
  }

  public void setTrader( MREElectricityTrader trader ) {
    this.trader = trader;
  }

}