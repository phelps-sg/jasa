package uk.ac.liv.ai.learning;

import uk.ac.liv.ai.learning.RothErevLearner;

/**
 * <p>
 * A modification of RothErev to address parameter degeneracy,
 * and modified learning with 0-reward.  These modifications are made
 * in the context of using the RE algorithm for trader agents in a double
 * auction.  See:
 * </p>
 * <p>
 * "Market Power and Efficiency in a Computational Electricity Market
 *  with Discriminatory Double-Auction Pricing"
 *  Nicolaisen, Petrov & Tesfatsion<br>
 *  in IEEE Trans. on Evol. Computation Vol. 5, No. 5, p 504.
 *  </p>
 *
 * @author Steve Phelps
 */

public class NPTRothErevLearner extends RothErevLearner {


  public NPTRothErevLearner( int k, double r, double e, double s1 ) {
    super(k, r, e, s1);
  }

  public NPTRothErevLearner( int k, double r, double e, double s1, long seed ) {
    super(k, r, e, s1, seed);
  }


  /**
   * The modified update function.
   */
  public double experience( int i, int action, double reward ) {
    if ( i == action ) {
      return reward * (1 - e);
    } else {
      return q[i] * (e / (double) (k - 1));
    }
  }


}