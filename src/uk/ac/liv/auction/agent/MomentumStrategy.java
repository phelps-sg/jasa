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


package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.ai.learning.MimicryLearner;
import uk.ac.liv.ai.learning.Learner;

import uk.ac.liv.util.Parameterizable;

import uk.ac.liv.prng.GlobalPRNG;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class MomentumStrategy extends AdaptiveStrategyImpl 
		implements Serializable {

  protected MimicryLearner learner;
  
  protected double currentPrice;
  
  /**
   * A parameter used to scale the randomly drawn price adjustment
   * perturbation values.
   */
  protected double scaling = 0.01;

  public static final String P_SCALING = "scaling";
  public static final String P_LEARNER = "learner";

  static Logger logger = Logger.getLogger(MomentumStrategy.class);


  public MomentumStrategy( AbstractTradingAgent agent ) {
    super(agent);
  }
  
  public MomentumStrategy() {
    this(null);
  }
  
  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    scaling =
        parameters.getDoubleWithDefault(base.push(P_SCALING), null, scaling);

    learner = (MimicryLearner)
        parameters.getInstanceForParameter(base.push(P_LEARNER), null,
                                                   MimicryLearner.class);
    if ( learner instanceof Parameterizable ) {
      ((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
    }

    initialise();
    
    setMargin(GlobalPRNG.getInstance().uniform(0.5, 1.0));
    
    logger.debug("Initialised with scaling = " + scaling + " and learner = " +
                  learner);

  }


  public void initialise() {
    super.initialise();
  }


  public boolean modifyShout( Shout.MutableShout shout ) {
    double currentMargin = learner.act();
    if ( currentMargin < 0 ) {
      logger.debug(this + ": clipping negative margin at 0");
      setMargin(currentMargin = 0);
    } else if ( currentMargin > 1 ) {
      logger.debug(this + ": clipping margin at 1.0");
      setMargin(currentMargin = 1.0);
    }
    adjustMargin();    
    if ( agent.isBuyer() ) {
      currentPrice = agent.getValuation(auction) * (1 - currentMargin);
    } else {
      currentPrice = agent.getValuation(auction) * (1 + currentMargin);
    }      
    shout.setPrice(currentPrice);
    return super.modifyShout(shout);
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public void setLearner( Learner learner ) {
    this.learner = (MimicryLearner) learner;
  }

  public Learner getLearner() {
    return learner;
  }

  public void setMargin( double margin ) {
    learner.setOutputLevel(margin);
  }

  
  protected double targetMargin( double targetPrice ) {        
    double privValue = agent.getValuation(auction);
    double targetMargin = 0;
    if ( agent.isBuyer() ) {
      targetMargin = (targetPrice - privValue) / privValue;
    } else {
      targetMargin = (privValue - targetPrice) / privValue;
    }
    if ( targetMargin < 0 ) {      
      targetMargin = 0;
    }    
    return targetMargin;
  }
  
  protected void adjustMargin( double targetMargin ) {
    learner.train(targetMargin);
  }
  
  protected double perterb( double price ) {
    RandomElement prng = GlobalPRNG.getInstance();
    double relative = prng.uniform(0, scaling);
    double absolute = prng.uniform(0, scaling);
    return relative*price + absolute;
  }

  protected abstract void adjustMargin();
}