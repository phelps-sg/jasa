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

import uk.ac.liv.ai.learning.WidrowHoffLearner;

import uk.ac.liv.util.CummulativeStatCounter;
import uk.ac.liv.util.MathUtil;

public class WidrowHoffLearnerTest extends TestCase {

  WidrowHoffLearner learner1;

  double score;

  static final double LEARNING_RATE = 0.1;

  static final double MOMENTUM = 1.0;

  static final double TARGET_VALUE = 0.12;

  static final int ITERATIONS = 100;

  public WidrowHoffLearnerTest( String name ) {
    super(name);
  }

  public void setUp() {
    learner1 = new WidrowHoffLearner(LEARNING_RATE, MOMENTUM);
  }

  public void testConvergence() {
    train(ITERATIONS);
    assertTrue(MathUtil.approxEqual(learner1.act(), TARGET_VALUE, 0.01));
    assertTrue(MathUtil.approxEqual(learner1.getLearningDelta(), 0, 0.01));
  }

  public void testReset() {
    train(2);
    assertTrue(learner1.getLearningDelta() > 0.01);
    learner1.reset();
    assertTrue(MathUtil.approxEqual(learner1.getLearningDelta(), 0, 0.00001));
  }

  protected void train( int iterations ) {
    for( int i=0; i<iterations; i++ ) {
      System.out.println("Learning delta = " + learner1.getLearningDelta());
      learner1.train(TARGET_VALUE);
    }
  }

  public static Test suite() {
    return new TestSuite(WidrowHoffLearnerTest.class);
  }
}
