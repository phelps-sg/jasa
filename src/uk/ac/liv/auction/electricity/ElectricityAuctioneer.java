package uk.ac.liv.auction.electricity;

import uk.ac.liv.auction.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import uk.ac.liv.util.Debug;


/**
 * <p>
 * An auctioneer for an electricity auction as described in: </p>
 *
 * <p>
 * "Markert Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * in IEEE Trans. on Evol. Computation, Vol. 5, No. 5.
 * </p>
 *
 * @author Steve Phelps
 */

public class ElectricityAuctioneer implements Auctioneer, ParameterizablePricing {

  Auction auction;

  TransmissionGrid grid;

  TreeSet bidList;

  TreeSet askList;

  double k = 0.5;

  MarketQuote quote = new MarketQuote(0, 0);

  public ElectricityAuctioneer( TransmissionGrid grid, Auction auction ) {
    this.grid = grid;
    this.auction = auction;
    initialise();
  }

  public ElectricityAuctioneer( TransmissionGrid transmissionGrid ) {
    this(transmissionGrid, null);
  }

  public void initialise() {
    bidList = new TreeSet( new DescendingShoutComparator() );
    askList = new TreeSet( new AscendingShoutComparator() );
  }

  public void setAuction( Auction auction ) {
    this.auction = auction;
  }

  public void setK( double k ) {
    this.k = k;
  }

  public double getK() {
    return k;
  }

  public void reset() {
    initialise();
  }

  public void printState() {
    //TODO
  }

  public void removeShout( Shout s ) {
    if ( s.isBid() ) {
      bidList.remove(s);
    } else {
      askList.remove(s);
    }
  }

  public void newShout( Shout s ) {
    if ( s.isBid() ) {
      bidList.add(s);
    } else {
      askList.add(s);
    }
  }

  public MarketQuote getQuote() {
    //TODO
    return quote;
  }

  public void endOfAuctionProcessing() {
  }

  public void endOfRoundProcessing() {
    clear();
  }

  public synchronized void clear() {
    // Get the matched shouts from the shout engine
    // and arrange them into two linked lists, one for
    // bids and one for asks.

    Iterator asks = askList.iterator();
    while ( asks.hasNext() ) {
      Shout ask = (Shout) asks.next();
      Iterator bids = bidList.iterator();

      while ( bids.hasNext() && ask.getQuantity() > 0 ) {
        Shout bid = (Shout) bids.next();
        ElectricityTrader buyer = (ElectricityTrader) bid.getAgent();
        ElectricityTrader seller = (ElectricityTrader) ask.getAgent();
        int atc = grid.getATC(buyer, seller);
        // TODO: Available transmission capacity should reflect
        // capacity __at this moment in time__.
        if ( atc > 0 && bid.getPrice() > ask.getPrice() ) {
          int trQuantity =
            Math.min(Math.min(atc, bid.getQuantity()), ask.getQuantity());
          long price = (long) (k * (double) bid.getPrice() + (1.0 - k) * (double) ask.getPrice());
//          System.out.println("Matching\n" + bid + "\nwith\n" + ask + "\nat "  + price + " for " + trQuantity);
          auction.clear(ask, bid.getAgent(), ask.getAgent(), price, trQuantity);
          if ( trQuantity >= bid.getQuantity() ) {
            // bidList.remove(bid);
            bids.remove();
          } else {
            bid.split(trQuantity);
          }
          if ( trQuantity >= ask.getQuantity() ) {
            // askList.remove(ask);
            asks.remove();
            break;
          } else {
            ask.split(trQuantity);
          }
        }
      }
    }

    /*
    try {
      i = bidList.iterator();
      while ( i.hasNext() ) {
        Shout bid = (Shout) i.next();
        newBid(bid);
      }

      i = askList.iterator();
      while ( i.hasNext() ) {
        Shout ask = (Shout) i.next();
        newAsk(ask);
      }
    } catch ( IllegalShoutException e ) {
      throw new AuctionError("illegal shout when clearing electricity auction");
    }
*/
  }

}
