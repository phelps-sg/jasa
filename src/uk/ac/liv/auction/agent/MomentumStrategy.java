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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

import uk.ac.liv.auction.event.AgentPolledEvent;
import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.event.AuctionOpenEvent;
import uk.ac.liv.auction.event.ShoutPlacedEvent;
import uk.ac.liv.auction.event.TransactionExecutedEvent;

import uk.ac.liv.ai.learning.MimicryLearner;
import uk.ac.liv.ai.learning.Learner;

import uk.ac.liv.util.Parameterizable;

import uk.ac.liv.prng.GlobalPRNG;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

// import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.io.Serializable;

import org.apache.log4j.Logger;

import cern.jet.random.AbstractContinousDistribution;
import cern.jet.random.Uniform;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class MomentumStrategy extends AdaptiveStrategyImpl implements
    Serializable {

  protected MimicryLearner learner;

  protected double currentPrice;

  protected Shout lastShout;

  /**
   * A parameter used to scale the randomly drawn price adjustment perturbation
   * values.
   */
  protected double scaling = 0.01;

  protected boolean lastShoutAccepted;

  protected double trPrice, trBidPrice, trAskPrice;

  protected AbstractContinousDistribution initialMarginDistribution = new Uniform(
      0.05, 0.35, GlobalPRNG.getInstance());

  protected AbstractContinousDistribution relativePerterbationDistribution;
  protected AbstractContinousDistribution absolutePerterbationDistribution;

  public static final String P_DEF_BASE = "momentumstrategy";

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
    
    Parameter defBase = new Parameter(P_DEF_BASE);
    
    scaling = parameters.getDoubleWithDefault(base.push(P_SCALING), 
    		defBase.push(P_SCALING), scaling);

    learner = (MimicryLearner) parameters.getInstanceForParameter(
    		base.push(P_LEARNER), defBase.push(P_LEARNER), 
        MimicryLearner.class);
    if ( learner instanceof Parameterizable ) {
      ((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
    }

    initialise();


    logger.debug("Initialised with scaling = " + scaling + " and learner = "
        + learner);

  }

  public void initialise() {
    super.initialise();
    relativePerterbationDistribution = new Uniform(0, scaling, GlobalPRNG.getInstance());
    absolutePerterbationDistribution = new Uniform(0, 0.05, GlobalPRNG.getInstance());
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    shout.setPrice(currentPrice);
    return super.modifyShout(shout);
  }

  public void eventOccurred( AuctionEvent event ) {
    super.eventOccurred(event);
    if ( event instanceof TransactionExecutedEvent ) {
      transactionExecuted((TransactionExecutedEvent) event);
    } else if ( event instanceof ShoutPlacedEvent ) {
      shoutPlaced((ShoutPlacedEvent) event);
    } else if ( event instanceof AgentPolledEvent ) {
      agentPolled((AgentPolledEvent) event);
    } else if ( event instanceof AuctionOpenEvent ) {
      if ( agent.isSeller(auction) ) {
      	setMargin(initialMarginDistribution.nextDouble());
      } else {
      	setMargin(-initialMarginDistribution.nextDouble());
      }
      updateCurrentPrice();
    }
  }

  protected void agentPolled( AgentPolledEvent event ) {
    auction = event.getAuction();
    if ( event.getAgent() != agent ) {
      adjustMargin();
    }
  }

  protected void shoutPlaced( ShoutPlacedEvent event ) {
    lastShout = event.getShout();
    lastShoutAccepted = false;
  }

  protected void transactionExecuted( TransactionExecutedEvent event ) {
    lastShoutAccepted = lastShout.isAsk() && event.getAsk().equals(lastShout)
        || lastShout.isBid() && event.getBid().equals(lastShout);
    if ( lastShoutAccepted ) {
      trPrice = event.getPrice();
      trBidPrice = event.getBid().getPrice();
      trAskPrice = event.getAsk().getPrice();
    }
  }

  public void endOfRound( Auction auction ) {

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

  public double getCurrentPrice() {
    return currentPrice;
  }

  public Shout getLastShout() {
    return lastShout;
  }

  public boolean isLastShoutAccepted() {
    return lastShoutAccepted;
  }
  
  public void setScaling( double scaling ) {
  	assert scaling >= 0 && scaling <= 1;
  	this.scaling = scaling;
  }

  public double getScaling() {
    return scaling;
  }

  public double getTrAskPrice() {
    return trAskPrice;
  }

  public double getTrBidPrice() {
    return trBidPrice;
  }

  public double getTrPrice() {
    return trPrice;
  }

  private void updateCurrentPrice() {
  	currentPrice = calculatePrice(learner.act());
  	assert currentPrice > 0;
  }

  protected double calculatePrice( double margin ) {
  	if ( (agent.isBuyer(auction) && margin <= 0.0 && margin > -1.0 )
  			|| (agent.isSeller(auction) && margin >= 0.0) ) {
  		return agent.getValuation(auction) * (1 + margin);
  	} else {
  		return currentPrice;
  	}
  }

  protected double targetMargin( double targetPrice ) {
    double privValue = agent.getValuation(auction);
    double targetMargin = 0;
    targetMargin = (targetPrice - privValue) / privValue;
    
    return targetMargin;
  }

  protected void adjustMargin( double targetMargin ) {
    learner.train(targetMargin);
    updateCurrentPrice();
  }

  protected double perterb( double price ) {
    double relative = relativePerterbationDistribution.nextDouble();
    double absolute = absolutePerterbationDistribution.nextDouble();
    return relative * price + absolute;
  }

  protected abstract void adjustMargin();
}