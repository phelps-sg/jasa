package uk.ac.liv.auction.electricity;


import uk.ac.liv.auction.core.*;

import uk.ac.liv.ai.learning.*;

/**
 * @author Steve Phelps
 */

public class AdaptiveElectricityAuctioneer extends DiscrimPriceCDAAuctioneer {

  StimuliResponseLearner learner;

  static final int K = 100;
  static final double R = 0.2;
  static final double E = 0.2;
  static final double S1 = 100.0;

  static final long MIN_PRICE = 0;
  static final long MAX_PRICE = 200;

  public AdaptiveElectricityAuctioneer( Auction auction ) {
    super(auction, 0.0);
    initialise();
  }

  public void initialise() {
    learner = new NPTRothErevLearner(K, R, E, S1);
  }

  public synchronized void clear() {
    setK( ((double) learner.act()) / (double) K );
    super.clear();
    ElectricityStats stats =
      new ElectricityStats(MIN_PRICE, MAX_PRICE, (RoundRobinAuction) auction);
    if ( !Double.isInfinite(stats.eA) && !Double.isNaN(stats.eA) ) {
      learner.reward(stats.eA);
    } else {
      learner.reward(0);
    }
  }


}