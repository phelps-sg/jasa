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

package uk.ac.liv.ai.learning;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.IdAllocator;

/**
 *
 *
 * @author Steve Phelps
 */

public class ReplicatorLearner implements StimuliResponseLearner {
  
  protected static int numAgents = 0;
  
  protected static int learningRate;
  
  protected static int population[];
  
  protected static StimuliResponseLearner[] subLearners;
  
  /**
   * Payoffs for each strategy
   */
  protected static int[] payoffs;
  
  protected static boolean[] rewardReceived;
  
  protected long id;
  
  static IdAllocator idAllocator = new IdAllocator();
  
  static final String P_N = "n";
  
  
  public ReplicatorLearner() {    
    numAgents++;
    id = idAllocator.nextId();
  }
  
  
  public void setup( ParameterDatabase parameters, Parameter base ) {
    int numLearners = parameters.getInt(base.push(P_N), null, 1);
    
    subLearners = new StimuliResponseLearner[numLearners];
    
    for( int i=0; i<numLearners; i++ ) {
      
      StimuliResponseLearner sub = (StimuliResponseLearner)
        parameters.getInstanceForParameter(base.push(i+""),  null, 
                                            StimuliResponseLearner.class);
      
      if ( sub instanceof Parameterizable ) {
        ((Parameterizable) sub).setup(parameters, base.push(i+""));
      }
      
      subLearners[i] = sub;
    }            
  }
  
  public void reward( double reward ) {
    //TODO
  }
  
  public int act() {
    return 0; //TODO
  }
  
  public double getLearningDelta() {
    return 0; //TODO
  }
}
