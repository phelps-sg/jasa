package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.TraderAgent;

import java.util.Iterator;
import java.util.List;

/**
 *  Auctioneer for standard multi-unit english ascending auction.
 */

public class AscendingAuctioneer extends AbstractAuctioneer {

  protected double reservePrice;
  protected TraderAgent seller;
  int quantity;


  public AscendingAuctioneer( Auction auction, TraderAgent seller,
                                  int quantity, double reservePrice ) {
    super(auction);
    try {
      newAsk( new Shout(seller, quantity, 0, false) );
    } catch ( DuplicateShoutException e ) {
      throw new AuctionError("Fatal error: invalid auction state on initialisation!");
    }
    this.reservePrice = reservePrice;
    this.quantity = quantity;
    this.seller = seller;
  }

  public void endOfRoundProcessing() {
    generateQuote();
  }

  public void endOfAuctionProcessing() {
    clear();
  }

  public void clear() {

    List winners = shoutEngine.getMatchedShouts();
    Iterator i = winners.iterator();
    double maxPrice = 0;
    // disassemble bids from auction, calculating max price
    // and saving bids for clearing op
    while ( i.hasNext() ) {
      Shout winningBid = (Shout) i.next();
      Shout winningAsk = (Shout) i.next();
      if ( winningBid.getPrice() > maxPrice ) {
        maxPrice = winningBid.getPrice();
      }
    }

    // now clear all bids at the maximum price
    i = winners.iterator();
    while ( i.hasNext() ) {
      Shout winningBid = (Shout) i.next();
      Shout winningAsk = (Shout) i.next();

      auction.clear(winningBid, winningBid.getAgent(), seller, maxPrice, quantity);
    }
  }

  public void generateQuote() {
    currentQuote = new MarketQuote(null, shoutEngine.getLowestMatchedBid());
  }

  public void newShout( Shout shout) throws IllegalShoutException {
    if ( shout.isAsk() ) {
      throw new IllegalShoutException("asks are not allowed in an ascending auction");
    }
    // TODO: Additional logic to enforce bid amounts at round nos and/or
    // beat existing bids by certain amount?
    super.newShout(shout);
  }

}