/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

import junit.framework.*;

import uk.ac.liv.PRNGTestSeeds;

import uk.ac.liv.prng.GlobalPRNG;

import uk.ac.liv.util.CummulativeDistribution;

public class RothErevLearnerTest extends TestCase {

  static final int CORRECT_ACTION = 2;

  /**
   * @uml.property name="learner1"
   * @uml.associationEnd
   */
  NPTRothErevLearner learner1;

  public RothErevLearnerTest( String name ) {
    super(name);
  }

  public void setUp() {
    GlobalPRNG.initialiseWithSeed(PRNGTestSeeds.UNIT_TEST_SEED);
    learner1 = new NPTRothErevLearner(10, 0.2, 0.2, 100.0);
  }

  public void testBasic() {
    learner1.setExperimentation(0.99);
    System.out.println("testBasic()");
    CummulativeDistribution stats = new CummulativeDistribution("action");
    int correctActions = 0;
    for ( int i = 0; i < 100; i++ ) {
      int action = learner1.act();
      stats.newData(action);
      if ( action == CORRECT_ACTION ) {
        learner1.reward(1.0);
        correctActions++;
      } else {
        learner1.reward(0);
      }
      checkProbabilities(learner1);
    }
    System.out.println("final state of learner1 = " + learner1);
    System.out.println("learner1 score = " + correctActions + "%");
    System.out.println("learner1 peaks = " + learner1.countPeaks());
    System.out.println(stats);
  }

  public void testPeaks() {
    System.out.println("\ntestPeaks()");
    double q[] = { 12, 15, 12, 10, 16, 17, 0, 0, 0, 0 };
    learner1.setPropensities(q);
    int peaks = learner1.countPeaks();
    System.out.println(learner1);
    System.out.println("Number of peaks = " + peaks);
    assertTrue(peaks == 2);
  }

  public void testDistribution() {
    System.out.println("\ntestDistribution()");
    double q[] = { 55, 5, 5, 5, 5, 5, 5, 5, 5, 5 };
    CummulativeDistribution action1Data = new CummulativeDistribution("action1");
    for ( int r = 0; r < 10000; r++ ) {
      learner1 = new NPTRothErevLearner(10, 0.2, 0.2, 1);
      learner1.setPropensities(q);
      CummulativeDistribution choiceData = new CummulativeDistribution("choice");
      int action1Chosen = 0;
      for ( int i = 0; i < 100; i++ ) {
        int choice = learner1.act();
        choiceData.newData(choice);
        action1Chosen = 0;
        if ( choice == 0 ) {
          action1Chosen = 1;
        }
        action1Data.newData(action1Chosen);
      }
    }
    System.out.println(action1Data);
    assertTrue(action1Data.getMean() <= 0.57 && action1Data.getMean() >= 0.53);
  }

  public static void checkProbabilities( RothErevLearner l ) {
    double prob = 0;
    for ( int i = 0; i < l.getK(); i++ ) {
      prob += l.getProbability(i);
    }
    if ( prob > 1.001 || prob < 0.999 ) {
      throw new Error("Probabilities should sum to 1");
    }
  }

  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(RothErevLearnerTest.class);
  }
}