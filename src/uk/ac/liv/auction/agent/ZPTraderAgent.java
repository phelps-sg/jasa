package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

/**
 * A trader which tries to reach a desired stock level
 * by bidding/asking the shorfall/excess quantity
 * at zero price (!).
 */

public class ZPTraderAgent extends AbstractTraderAgent {

  static int desiredStock = 50;
  Shout shout;


  public ZPTraderAgent( int stock ) {
    super(stock, 0L);
  }

  public void requestShout( RoundRobinAuction auction ) {
    if ( stock == desiredStock ) {
      // quit the auction when we've reached desired stock levels
      auction.remove(this);
      return;
    }

    // retract my previous shout, if any
    if ( shout != null ) {
      auction.removeShout(shout);
    } else {
      shout = new Shout(this);
    }

    // The excess/shortfall
    shout.setQuantity(Math.abs(desiredStock-stock));

    // whether to buy or sell
    shout.setIsBid(stock < desiredStock);

    // always bid at zero price!
    shout.setPrice(0);

    try {
      auction.newShout(shout);
    } catch ( AuctionException e ) {
      e.printStackTrace();
    }
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                                double price, int quantity ) {
    AbstractTraderAgent sellerAgent = (AbstractTraderAgent) seller;
    purchaseFrom(sellerAgent, quantity, 0);
  }


}