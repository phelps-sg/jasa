package uk.ac.liv.auction.core;

import uk.ac.liv.auction.agent.RoundRobinTrader;

import java.util.ArrayList;
import java.util.Iterator;

import ec.util.MersenneTwisterFast;   // Fast random number generator

/**
 * <p>
 * A round-robin auction in which the ordering of traders in
 * randomized for each round.
 * </p>
 *
 * @author Steve Phelps
 */

public class RandomRobinAuction extends RoundRobinAuction {

  MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  public RandomRobinAuction() {
    super();
  }

  public RandomRobinAuction( String name ) {
    super(name);
  }

  public void requestShouts() {
    Object[] candidates = activeTraders.toArray();
    int numCandidates = candidates.length;
    for( int i=0; i<numTraders; i++ ) {
      int choice = randGenerator.nextInt(numCandidates);
      RoundRobinTrader trader = (RoundRobinTrader) candidates[choice];
      candidates[choice] = candidates[numCandidates-1];
      numCandidates--;
      trader.requestShout(this);
    }
  }

}