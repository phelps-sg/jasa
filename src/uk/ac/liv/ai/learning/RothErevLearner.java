/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
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

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.prng.DiscreteProbabilityDistribution;

import uk.ac.liv.util.Prototypeable;
import uk.ac.liv.util.CummulativeDistribution;
import uk.ac.liv.util.MathUtil;
import uk.ac.liv.util.io.DataWriter;

import java.io.Serializable;

/**
 * <p>
 * A class implementing the Roth-Erev learning algorithm.  This learning
 * algorithm is designed to mimic human-like behaviour in extensive form games.
 * See:
 * </p>
 * <p>
 * A.E.Roth and I. Erev "Learning in extensive form games: experimental data
 * and simple dynamic models in the intermediate term"
 * Games and Economic Behiour, Volume 8
 * </p>
 *
 *
 * <p><b>Parameters</b><br></p>
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.k</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the number of a possible actions)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.r</tt><br>
 * <font size=-1>double [0,1]</font></td>
 * <td valign=top>(the recency parameter)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.e</tt><br>
 * <font size=-1>double [0,1]</font></td>
 * <td valign=top>(the experimentation parameter)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.s1</tt><br>
 * <font size=-1>int >= 0</font></td>
 * <td valign=top>(the scaling parameter)</td><tr>
 *
 * </table>
 *
 *
 * @author Steve Phelps
 * @version $Revision$
 *
 */

public class RothErevLearner extends AbstractLearner implements
                                Prototypeable,
                                StimuliResponseLearner,                                
                                Serializable {

  /**
   * The number of choices available to make at each iteration.
   */
  protected int k;

  /**
   * The recency parameter.
   */
  protected double r;

  /**
   * The experimentation parameter.
   */
  protected double e;

  /**
   * The scaling parameter.
   */
  protected double s1;

  /**
   * Propensity for each possible action.
   */
  protected double q[];

  /**
   * Probabilities for each possible action.
   */
  protected DiscreteProbabilityDistribution p;

  /**
   * The current iteration.
   */
  protected int iteration;

  /**
   * The last action chosen.
   */
  protected int lastAction;

  /**
   * The total amount of update to the probability vector on the last iteration.
   */
  protected double deltaP;

  static final int    DEFAULT_K   = 100;
  static final double DEFAULT_R   = 0.1;
  static final double DEFAULT_E   = 0.2;
  static final double DEFAULT_S1  = 1.0;


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
    q = new double[k];
    p = new DiscreteProbabilityDistribution(k);
    initialise();
  }


  public RothErevLearner() {
    this(DEFAULT_K, DEFAULT_R, DEFAULT_E, DEFAULT_S1);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    k = parameters.getIntWithDefault(base.push("k"), null, DEFAULT_K);
    r = parameters.getDoubleWithDefault(base.push("r"), null, DEFAULT_R);
    e = parameters.getDoubleWithDefault(base.push("e"), null, DEFAULT_E);
    s1 = parameters.getDoubleWithDefault(base.push("s1"), null, DEFAULT_S1);
    validateParams();
    q = new double[k];
    p = new DiscreteProbabilityDistribution(k);
    initialise();
  }

  public Object protoClone() {
  	RothErevLearner clonedLearner;
  	try {
  		clonedLearner = (RothErevLearner) clone();
  		clonedLearner.p = (DiscreteProbabilityDistribution) p.protoClone();  		
  	} catch ( CloneNotSupportedException e ) {
  		throw new Error(e);
  	}    
    return clonedLearner;
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
   * @return An int in the range [0..k) representing the choice
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
    updatePropensities(lastAction, reward);
    updateProbabilities();
  }

  /**
   * Choose a random number according to the probability distribution defined by
   * p.
   *
   * @return one of [0..k) according to the probabilities p[0..k-1].
   */
  public int choose() {
    return p.generateRandomEvent();
  }

  /**
   * Update the propensities for each possible action.
   *
   * @param action The last action chosen by the learner
   */
  protected void updatePropensities( int action, double reward ) {
    for( int i=0; i<k; i++ ) {
      double q1 = (1-r) * q[i] + experience(i,action,reward);
      q[i] = q1;
    }
  }

  /**
   * Update the probabilities.
   */
  protected void updateProbabilities() {
    double sigmaQ = 0;
    for ( int i = 0; i < k; i++ ) {
      sigmaQ += q[i];
    }
    if ( sigmaQ <= 10E-10 ) {
      resetDistributions();
      return;
    }
    deltaP = 0;
    for ( int i = 0; i < k; i++ ) {
      double p1 = q[i] / sigmaQ;
      deltaP += MathUtil.diffSq(p.getProbability(i), p1);
      p.setProbability(i, p1);
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
  
  public void resetDistributions() {
    double initialPropensity = s1 / k;
    for( int i=0; i<k; i++ ) {
      q[i] = initialPropensity;
    }
    updateProbabilities();
  }

  public void initialise() {
    resetDistributions();
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
  
  public void setScaling( double s1 ) {
    this.s1 = s1;
  }

  /**
   *  Count the number of peaks in the probability distribution.
   *
   *  @return  The number of peaks in the distribution.
   */
  public int countPeaks() {
    int peaks = 0;
    double lastValue = 0;
    double lastDelta = 0;
    double delta = 0;
    for( int i=0; i<k; i++ ) {
      delta = q[i] - lastValue;
      if ( Math.abs(delta) < 1.0/(k*100000) ) {
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

  /**
   *  Compute modes of the probability distribution p.
   */
  public void computeDistributionStats( CummulativeDistribution stats ) {
    p.computeStats(stats);
  }

  private static int sign( double value ) {
    return (new Double(value)).compareTo(new Double(0));
  }

  public void dumpState( DataWriter out ) {
    for( int i=0; i<k; i++ ) {
      out.newData(p.getProbability(i));
    }
  }

  public int getK() {
    return k;
  }

  public int getNumberOfActions() {
    return getK();
  }

  public double getLearningDelta() {
    return deltaP;
  }

  public double getProbability( int i ) {
    return p.getProbability(i);
  }

  public double getE() {
    return e;
  }
  
  public int getIteration() {
    return iteration;
  }
  
  public int getLastAction() {
    return lastAction;
  }

  public double getR() {
    return r;
  }
  
  public double getS1() {
    return s1;
  }
  
  public String toString() {
    return "(" + getClass() + " k:" + k + " r:" + r + " e:" + e + " s1:" + s1 + " learningDelta:" + deltaP + ")";
  }

}
