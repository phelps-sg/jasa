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

package uk.ac.liv.auction.ec.gp.func;

import java.io.Serializable;

import ec.util.*;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.ai.learning.MimicryLearner;

import uk.ac.liv.ec.gp.*;

import uk.ac.liv.util.*;

/** 
 * @author Steve Phelps
 * @version $Revision$
 *
 */

public class GPTradingStrategy extends FixedQuantityStrategyImpl
    implements GPObject, Cloneable, Serializable, Resetable {

	protected GPGenericIndividual gpIndividual;

	protected MimicryLearner momentumLearner;
  
  protected GPRothErevLearner rlLearner;
	
	protected double currentMargin;
  
  protected double rlScale;
  
  protected int rlLastAction;
	
  private CummulativeDistribution priceStats = 
  	new CummulativeDistribution("priceStats");
  
  public static final String P_MLEARNER = "mlearner";
  public static final String P_RLLEARNER = "rllearner";
  public static final String P_RLSCALE = "rlscale";
  
  public static final int TREE_STRATEGY = 0;
  public static final int TREE_GPINIT = 1;


  public void setup( ParameterDatabase parameters, Parameter base ) {
  	
  	momentumLearner = (MimicryLearner)
  		parameters.getInstanceForParameterEq(base.push(P_MLEARNER), null,
  																					MimicryLearner.class);
  	if ( momentumLearner instanceof Parameterizable ) {
  		((Parameterizable) momentumLearner).setup(parameters, 
  																									base.push(P_MLEARNER));
  	}
    
    rlLearner = (GPRothErevLearner)
      parameters.getInstanceForParameterEq(base.push(P_RLLEARNER), null,
                                            GPRothErevLearner.class);
    rlLearner.setup(parameters, base.push(P_RLLEARNER));
    
    rlScale = parameters.getDouble(base.push(P_RLSCALE), null, 0);
    
    momentumLearner.setOutputLevel(currentMargin=0.5);  		
  }
  
  public void setGPIndividual( GPGenericIndividual individual ) {
  	this.gpIndividual = individual;
  }
  
  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public AbstractTraderAgent getAgent() {
    return agent;
  }


  protected double getPrivateValue() {
    return agent.getValuation(auction);
  }

  public Auction getAuction() {
    return auction;
  }

  public MarketQuote getQuote() {
    return auction.getQuote();
  }

  public boolean modifyShout( Shout.MutableShout shout ) { 
  	super.modifyShout(shout);
    Number result = gpIndividual.evaluateNumberTree(TREE_STRATEGY);
    double price;
    if ( !gpIndividual.misbehaved() ) {
      price = result.doubleValue();
    } else {
      return false;
    }
    if ( Double.isInfinite(price) || Double.isNaN(price)) {      
      gpIndividual.illegalResult();      
      return false;
    }
    if ( price < 0 ) {
      return false;
    }
    shout.setPrice(price);    
    rlLastAction = rlAction(price);
    priceStats.newData(price - agent.getValuation(auction)); 
    return true;
  }

  public void endOfRound( Auction auction ) {
    if ( agent.active() ) {
      rlLearner.reward(rlLastAction, agent.getLastProfit());
    }
  }

  public double getLastProfit() {
    return agent.getLastProfit();
  }
  
  public boolean lastShoutAccepted() {
  	return agent.lastShoutAccepted();
  }

  public CummulativeDistribution getPriceStats() {
    return priceStats;
  }

  public void reset() {
    priceStats.reset();
    ((Resetable) momentumLearner).reset();
    rlLearner.reset();
    gpIndividual.reset();
    momentumLearner.setOutputLevel(currentMargin=0.5);    
    super.reset();
    gpInitialise();
  }
  
  public boolean misbehaved() {
  	return gpIndividual.misbehaved();
  }
  
  public GPGenericIndividual getGPIndividual() {
  	return gpIndividual;
  }

  public Object protoClone() {
    GPTradingStrategy copy = null;
    try {
      copy = (GPTradingStrategy) super.protoClone();
      copy.priceStats = (CummulativeDistribution) priceStats.clone();  
      copy.gpIndividual = (GPGenericIndividual) gpIndividual.shallowClone();
      copy.gpIndividual.setGPObject(copy);
    } catch ( CloneNotSupportedException e ) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }
    return copy;
  }

  public void setMargin( double margin ) {
  	this.currentMargin = margin;  
  }
  
  public double markedUpPrice() {  	
  	double price;
  	if ( agent.isBuyer() ) {
      price = agent.getValuation(auction) * (1 - currentMargin);
    } else {
      price = agent.getValuation(auction) * (1 + currentMargin);
    }    
  	return price;
  }

  public void adjustMargin( double targetMargin ) {
    momentumLearner.train(targetMargin);   
    currentMargin = momentumLearner.act();
  }
  
  public double rlPrice() {
    double markup = rlLearner.act() * rlScale;
    if ( agent.isBuyer() ) {
      return agent.getValuation(auction) - markup;
    } else {
      return agent.getValuation(auction) + markup;
    }
  }
  
  public int rlAction( double price ) {
    return (int) (Math.abs(agent.getValuation(auction) - price) / rlScale);
  }
  
  public GPRothErevLearner getRlLearner() {
    return rlLearner;
  }
  
  public MimicryLearner getMomentumLearner() {
    return momentumLearner;
  }
  
  public void gpInitialise() {
    if ( gpIndividual.getGPContext() != null && 
          gpIndividual.trees[TREE_GPINIT].child != null ) {
      gpIndividual.evaluateNumberTree(TREE_GPINIT);
    }
  }
  
}

