package uk.ac.liv.auction.agent.jade;

import uk.ac.liv.auction.agent.*;

import uk.ac.liv.auction.electricity.ElectricityTrader;

public class JADEElectricityTrader extends JADETraderAgentAdaptor {

  public JADEElectricityTrader( int capacity, double privateValue, double fixedCosts,
                               boolean isSeller, Strategy strategy ) {

    super(new ElectricityTrader(capacity, privateValue, fixedCosts, isSeller, strategy));
  }

  public JADEElectricityTrader() {
    this(10, 100, 0, true, null);
    jasaTraderAgent.setStrategy(new RandomConstrainedStrategy(jasaTraderAgent, 100));
  }

}