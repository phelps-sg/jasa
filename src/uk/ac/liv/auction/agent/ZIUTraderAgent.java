package uk.ac.liv.auction.agent;

/**
 * An unconstrained ZI trader agent (ZI-U).
 */

import uk.ac.liv.auction.core.Auction;

public class ZIUTraderAgent extends ZITraderAgent {

  public ZIUTraderAgent( long privateValue, int tradeEntitlement, boolean isSeller ) {
    super(privateValue, tradeEntitlement, isSeller);
  }

  public double determinePrice( Auction auction ) {
    return randomPrice( (int) (MAX_PRICE-1))+1;
  }



}