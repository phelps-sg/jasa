package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

/**
 * A constrained ZI trader agent (ZI-C).
 * Agents of this type always trade at or above the margin.
 *
 * @author Steve Phelps
 */

public class ZICTraderAgent extends ZITraderAgent {

  public ZICTraderAgent( long privateValue, int tradeEntitlement, boolean isSeller ) {
    super(privateValue, tradeEntitlement, isSeller);
  }

  public double determinePrice( Auction auction ) {
    if ( isSeller ) {
      return privateValue + randomPrice( (int) (MAX_PRICE-privateValue));
    } else {
      return randomPrice( (int) (privateValue-1))+1;
    }
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                                long price, int quantity ) {
    if ( price <= privateValue ) {
      //purchaseFrom(seller, winningShout.getQuantity(), price);
      AbstractTraderAgent sellerAgent = (AbstractTraderAgent) seller;
      purchaseFrom(sellerAgent, quantity, price);
      // System.out.println("Accepting offer from " + seller + " at price " + price + " my priv value = " + limitPrice);
    } else {
      //System.out.println("Rejecting offer from " + seller + " at price " + price);
    }
  }


}