package uk.ac.liv.auction.ec.gp;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.electricity.ElectricityTrader;

class GPElectricityTrader extends ElectricityTrader {

  public GPElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller ) {
    super(capacity, privateValue, fixedCosts, isSeller);
  }

  public void informOfSeller( Auction auction, Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {

    if ( price <= valuer.determineValue(auction) ) {

      GPElectricityTrader trader = (GPElectricityTrader) seller;
      trader.informOfBuyer(auction, this, price, quantity);
    }
  }

  public void trade( Auction auction, double price, int quantity ) {
    lastProfit = quantity * (valuer.determineValue(auction) - price);
    profits += lastProfit;
  }

  public void informOfBuyer( Auction auction, GPElectricityTrader buyer,
                              double price, int quantity ) {

    double privValue = valuer.determineValue(auction);

    if ( price >= privValue ) {

      buyer.trade(auction, price, quantity);

      lastProfit = quantity * (price - privValue);
      profits += lastProfit;
    }
  }



}
