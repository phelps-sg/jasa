/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

package test.uk.ac.liv.ai.learning;

import junit.framework.*;

import uk.ac.liv.ai.learning.*;

import uk.ac.liv.util.CummulativeStatCounter;

public class QLearnerTest extends TestCase {

  QLearner learner1;

  static final double EPSILON = 0.05;
  static final double LEARNING_RATE = 0.8;
  static final double DISCOUNT_RATE = 0.9;

  static final int NUM_ACTIONS = 10;
  static final int CORRECT_ACTION = 2;
  static final int NUM_TRIALS = 1000;


  public QLearnerTest( String name ) {
    super(name);
  }

  public void setUp() {
    learner1 = new QLearner(1, NUM_ACTIONS, EPSILON, LEARNING_RATE, DISCOUNT_RATE);
  }

  public void testBestAction() {
    learner1.setEpsilon(0.0);
    System.out.println("testBestAction()");
    CummulativeStatCounter stats = new CummulativeStatCounter("action");
    int correctActions = 0;
    for( int i=0; i<NUM_TRIALS; i++ ) {
      int action = learner1.act();
      assertTrue(action == learner1.bestAction(0));
      stats.newData(action);
      if ( action == CORRECT_ACTION ) {
        learner1.newState(1.0, 0);
        correctActions++;
      } else {
        learner1.newState(0.0, 0);
      }
    }
    System.out.println("final state of learner1 = " + learner1);
    System.out.println("learner1 score = " + score(correctActions) + "%");
    System.out.println(stats);
  }

  public void testMinimumScore() {
    System.out.println("testMinimumScore()");
    CummulativeStatCounter stats = new CummulativeStatCounter("action");
    int correctActions = 0;
    for( int i=0; i<NUM_TRIALS; i++ ) {
      int action = learner1.act();
      assertTrue(action == learner1.getLastActionChosen());
      stats.newData(action);
      if ( action == CORRECT_ACTION ) {
        learner1.newState(1.0, 0);
        correctActions++;
      } else {
        learner1.newState(0.0, 0);
      }
    }
    System.out.println("final state of learner1 = " + learner1);
    double score = score(correctActions);
    System.out.println("learner1 score = " + score + "%");
    System.out.println(stats);
    assertTrue(score > 80);
  }

  public double score( int numCorrect ) {
    return ((double) numCorrect / (double) NUM_TRIALS) * 100;
  }



  public static void main( String[] args ) {
    junit.textui.TestRunner.run(suite());
  }

  public static Test suite() {
    return new TestSuite(QLearnerTest.class);
  }
}