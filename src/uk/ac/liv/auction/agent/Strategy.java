package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

/**
 * @author Steve Phelps
 */

public interface Strategy {

  public abstract void modifyShout( Shout shout, Auction auction );

}