package uk.ac.liv.auction.ec.ga;

import ec.vector.BitVectorIndividual;

import uk.ac.liv.auction.core.*;

/**
 * @author Steve Phelps
 */

public class GAAuctioneerContainer extends BitVectorIndividual {

  ContinuousDoubleAuctioneer auctioneer = null;

  /**
   * Compute the value of auction parameter K from the genome bit-vector.
   */
  protected double computeK() {
    boolean[] bits = (boolean[]) getGenome();
    int exp = 1;
    long k = 0;
    for( int i=0; i<bits.length; i++ ) {
      if ( bits[i] ) {
        k += exp;
      }
      exp *= 2;
    }
    return ((double) k)/128;
  }

  /**
   * Return the auctioneer associated with this individual.
   * The auctioneer will either be freshly constructed, or reinitialised,
   * from the current genome.
   */
  public ContinuousDoubleAuctioneer getAuctioneer() {
    if ( auctioneer == null ) {
      auctioneer = new ContinuousDoubleAuctioneer(computeK());
    } else {
      auctioneer.reset();
    }
    auctioneer.setK(computeK());
    return auctioneer;
  }


}