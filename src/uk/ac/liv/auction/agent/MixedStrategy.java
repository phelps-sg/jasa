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


package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.util.DiscreteProbabilityDistribution;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.util.*;

/**
 * A class representing a mixed strategy.
 * A mixed strategy is a strategy in which we play a number of pure strategies
 * with different probabilities on each auction round.
 *
 * <p><b>Parameters</b><br>
 *
 * <table>
 *
 * <tr><td valign=top><i>base.</i><tt>n</tt><br>
 * <font size=-1>int &gt;= 1</font></td>
 * <td valign=top>(the number of pure strategies)</td></tr>
 *
 * <tr><td valign=top><i>base.<i>.i</i><br>
 * <font size=-1>classname, inherits uk.ac.liv.auction.agent.Strategy</font></td>
 * <td valign=top>(the class for pure strategy #<i>i</i>)</td></tr>
 *
 * </table>
 *
 * @author Steve Phelps
 */

public class MixedStrategy extends AbstractStrategy implements Parameterizable,
                                                                Resetable {

  /**
   *  The probabilities for playing each strategy
   */
  protected DiscreteProbabilityDistribution probabilities;

  /**
   *  The pure strategy components
   */
  protected Strategy pureStrategies[];
  
  /**
   *  The strategy currently being played
   */
  protected Strategy currentStrategy;
  
  static final String P_N = "n";


  public MixedStrategy( DiscreteProbabilityDistribution probabilities,
                          Strategy[] pureStrategies ) {
    pureStrategies = pureStrategies;
    this.probabilities = probabilities;
    currentStrategy = null;
  }
  
  public void setup( ParameterDatabase parameters, Parameter base ) {
    
    int numStrategies = parameters.getInt(base.push(P_N), null, 1);
    pureStrategies = new Strategy[numStrategies];
    
    for( int i=0; i<numStrategies; i++ ) {      
      Strategy s = (Strategy)
        parameters.getInstanceForParameter(base.push(i+""), null, 
                                            Strategy.class);
      if ( s instanceof Parameterizable ) {
        ((Parameterizable) s).setup(parameters, base.push(i+""));
      }
      pureStrategies[i] = s;
    }    
    
  }

  public void addPureStrategies( Collection pureStrategies ) {
    pureStrategies.addAll(pureStrategies);
  }


  public void setProbabilityDistribution( DiscreteProbabilityDistribution probabilities ) {
    this.probabilities = probabilities;
  }


  public void modifyShout( Shout shout, Auction auction ) {

    currentStrategy = pureStrategies[probabilities.generateRandomEvent()];

    currentStrategy.modifyShout(shout, auction);
  }


  public void endOfRound( Auction auction ) {
    currentStrategy.endOfRound(auction);
  }

  public Strategy getCurrentStrategy() {
    return currentStrategy;
  }
  
  public void reset() {
    probabilities.reset();
    for( int i=0; i<pureStrategies.length; i++ ) {
      ((Resetable) pureStrategies[i]).reset();
    }
  }

}


