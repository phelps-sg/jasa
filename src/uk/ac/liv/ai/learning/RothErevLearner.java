/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
 *
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ai.learning;

import ec.util.MersenneTwisterFast;  // Fast random number generator

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.Resetable;

/**
 * <p>
 * A class implementing the Roth-Erev learning algorithm.  This learning
 * algorithm is designed to produce aggregate human-like behaviour in
 * simple trading games.  See:
 * </p>
 * <br>
 * <p>
 * A.E.Roth and I. Erev "Learning in extensive form games: experimental data
 * and simple dynamic models in the intermediate term" Games Econom. Beh., vol.8
 * </p>
 * @author Steve Phelps
 *
 */

public class RothErevLearner implements Resetable, StimuliResponseLearner {

  /**
   * The number of choices available to make at each iteration.
   */
  int k;

  /**
   * The recency parameter.
   */
  double r;

  /**
   * The experimentation parameter.
   */
  double e;

  /**
   * The scaling parameter.
   */
  double s1;

  /**
   * Propensity for each possible action.
   */
  double q[];

  /**
   * Probabilities for each possible action.
   */
  double p[];

  /**
   * The current iteration.
   */
  int iteration;

  /**
   * Random number generator.
   */
  MersenneTwisterFast randGenerator = new MersenneTwisterFast();


  /**
   * The last action chosen.
   */
  int lastAction;


  /**
   * Construct a new learner.
   *
   * @param k The no. of possible actions.
   * @param r The recency parameter.
   * @param e The experimentation parameter.
   */
  public RothErevLearner( int k, double r, double e, double s1 ) {
    this.k = k;
    this.r = r;
    this.e = e;
    this.s1 = s1;
    validateParams();
    initialise();
  }

  public RothErevLearner( int k, double r, double e, double s1, long seed ) {
    this(k, r, e, s1);
    randGenerator.setSeed(seed);
  }

  protected void validateParams() {
    if ( ! (k > 0) ) {
      throw new IllegalArgumentException("k must be positive");
    }
    if ( ! (r >= 0 && r <= 1) ) {
      throw new IllegalArgumentException("r must range [0..1]");
    }
    if ( ! (e >= 0 && e <= 1) ) {
      throw new IllegalArgumentException("e must range [0..1]");
    }
  }

  /**
   * Generate the next action for this learner.
   *
   * @return An int in the range 0..k representing the choice
   * made by the learner.
   */
  public int act() {
    int action = choose();
    lastAction = action;
    iteration++;
    return action;
  }

  /**
   * Reward the last action taken by the learner according to some payoff.
   *
   * @param reward  The payoff for the last action taken by the learner.
   */
  public void reward( double reward ) {
  //  if ( reward < 0 ) {
  //    reward = 0; // TODO: Is this really the correct thing to do?
  //  }
    updatePropensities(lastAction, reward);
    updateProbabilities();
  }

  /**
   * Choose a random number according to the probability distribution defined by
   * p[].
   *
   * @return one of 0..k according to the probabilities p[0..k].
   */
  public int choose() {
    double rand = randGenerator.nextDouble();
    double cummProb = 0;
    for( int i=0; i<k; i++ ) {
      cummProb += p[i];
      if ( rand <= cummProb ) {
        return i;
      }
    }
   // return randGenerator.nextInt(k);
/*    if ( cummProb == 0 ) {
      return randGenerator.nextInt(k);
    } */
    //System.out.println("WARNING:");
    //System.out.println(this);
    //System.out.println("cummProb = " + cummProb);
    //System.out.println("Probabilities do not sum to 1!");
    reset();
    return randGenerator.nextInt(k);
  }

  /**
   * Update the propensities for each possible action.
   *
   * @param action The last action chosen by the learner
   */
  protected void updatePropensities( int action, double reward ) {
    for( int i=0; i<k; i++ ) {
      q[i] = (1-r) * q[i] + experience(i,action,reward);
    }
  }

  /**
   * Update the probabilities.
   */
  protected void updateProbabilities() {
    double sigmaQ = 0;
    for( int i=0; i<k; i++ ) {
//      if ( q[i] > 0 ) {
        sigmaQ += q[i];
//      }
    }
    for( int i=0; i<k; i++ ) {
 //     if ( q[i] <= 0 ) {
 //       p[i] = 0;
 //     } else {
        p[i] = q[i] / sigmaQ;
 //     }
    }
  }

  /**
   * The experience function
   *
   * @param i       The action under consideration
   *
   * @param action  The last action chosen
   */
  public double experience( int i, int action, double reward ) {
    if ( i == action ) {
      return reward * (1 - e);
    } else {
      return reward * (e / (k - 1));
    }
  }

  /**
   * Replace the current propensities with the supplied propensity array.
   *
   * @param q The new propensity array to use.
   */
  public void setPropensities( double q[] ) {
    this.q = q;
    updateProbabilities();
  }

  public void initialise() {
    q = new double[k];
    p = new double[k];
    for( int i=0; i<k; i++ ) {
      p[i] = 1.0/k;
      q[i] = s1/k;
    }
    iteration = 0;
  }

  public void reset() {
    initialise();
  }

  public void setRecency( double r ) {
    this.r = r;
    validateParams();
  }

  public void setExperimentation( double e ) {
    this.e = e;
    validateParams();
  }

  public int countPeaks() {
    int peaks = 0;
    double lastValue = 0;
    double lastDelta = 0;
    double delta = 0;
    for( int i=0; i<k; i++ ) {
      delta = q[i] - lastValue;
      if ( Math.abs(delta) < 1.0/(k*100) ) {
        delta = 0;
      }
      if ( delta<0 && sign(delta) != sign(lastDelta) ) {
        peaks++;
      }
      lastDelta = delta;
      lastValue = q[i];
    }
    return peaks;
  }

  private static int sign( double value ) {
    return (new Double(value)).compareTo(new Double(0));
  }

  public void setSeed( long seed ) {
    randGenerator.setSeed(seed);
  }

  public String toString() {
    StringBuffer str = new StringBuffer("(" + this.getClass() + " ");
    str.append("k:" + k + " r:" + r + " e:" + e + " ");
    str.append("iteration:" + iteration + " lastAction:" + lastAction + " ");
    for( int i=0; i<k; i++ ) {
      str.append(" p[" + i + "]:" + p[i]);
    }
    for( int i=0; i<k; i++ ) {
      str.append(" q[" + i + "]:" + q[i]);
    }
    str.append("\n)");
    return str.toString();
  }

}