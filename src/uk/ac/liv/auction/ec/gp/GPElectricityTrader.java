package uk.ac.liv.auction.ec.gp;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.ElectricityTrader;

class GPElectricityTrader extends ElectricityTrader {

  public GPElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller ) {
    super(capacity, privateValue, fixedCosts, isSeller);
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {

    if ( price < privateValue ) {

      GPElectricityTrader trader = (GPElectricityTrader) seller;
      trader.informOfBuyer(this, price, quantity);
    }
  }

  public void trade( double price, int quantity ) {
    double profit = quantity * (privateValue - price);
    profits += profit;
  }

  public void informOfBuyer( GPElectricityTrader buyer, double price, int quantity ) {

    if ( price > privateValue ) {

      buyer.trade(price, quantity);

      double profit = quantity * (price - privateValue);
      profits += profit;
    }
  }



}
