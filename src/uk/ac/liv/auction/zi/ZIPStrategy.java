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


package uk.ac.liv.auction.zi;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;

import uk.ac.liv.ai.learning.MimicryLearner;
import uk.ac.liv.ai.learning.Learner;

import uk.ac.liv.util.Seeder;
import uk.ac.liv.util.Seedable;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Prototypeable;

import uk.ac.liv.prng.PRNGFactory;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import edu.cornell.lassp.houle.RngPack.RandomElement;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * <p>
 * An implementation of the Zero-Intelligence-Plus (ZIP) strategy.
 * See:
 * </p>
 *
 * <p>
 * "Minimal Intelligence Agents for Bargaining Behaviours in
 * Market-based Environments" Dave Cliff 1997.
 * </p>
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class ZIPStrategy extends AdaptiveStrategyImpl
    implements Seedable, Serializable, Prototypeable {

  protected double currentMargin;

  protected double currentPrice;

  protected double lastPrice;

  protected MimicryLearner learner;
  
  public static final String ERROR_SHOUTVISIBILITY = 
    "ZIPStrategy can only be used with auctioneers who permit shout visibility";

  /**
   * The PRNG used to draw perturbation values
   */
  protected static RandomElement randGenerator =
      PRNGFactory.getFactory().create();

  /**
   * A parameter used to scale the randomly drawn price adjustment
   * perturbation values.
   */
  protected double scaling = 0.01;

  public static final String P_SCALING = "scaling";
  public static final String P_LEARNER = "learner";

  static Logger logger = Logger.getLogger(ZIPStrategy.class);


  public ZIPStrategy( AbstractTraderAgent agent ) {
    super(agent);
  }

  public ZIPStrategy() {
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
    
    setMargin(0.5 + randGenerator.raw()/2);
    
    logger.debug("Initialised with scaling = " + scaling + " and learner = " +
                  learner);

  }

  public Object protoClone() {
    ZIPStrategy clone = new ZIPStrategy();
    clone.scaling = this.scaling;
    clone.learner = (MimicryLearner) ((Prototypeable) this.learner).protoClone();
    clone.reset();
    return clone;
  }


  public void initialise() {
    super.initialise();
  }


  public boolean modifyShout( Shout.MutableShout shout ) {
    try {

      currentMargin = learner.act();
      if ( currentMargin < 0 ) {
        logger.debug(this + ": clipping negative margin at 0");
        setMargin(currentMargin = 0);
      }
      Shout lastShout = auction.getLastShout();
      if ( agent.isSeller() ) {
        sellerStrategy(lastShout);
      } else {
        buyerStrategy(lastShout);
      }      
      if ( agent.isBuyer() ) {
        currentPrice = agent.getPrivateValue(auction) * (1 - currentMargin);
      } else {
        currentPrice = agent.getPrivateValue(auction) * (1 + currentMargin);
      }      
      if ( currentPrice > 0 ) {
        shout.setPrice(currentPrice);
      }
      return super.modifyShout(shout);

    } catch ( ShoutsNotVisibleException e ) {
      logger.error(e.getMessage());
      throw new AuctionError(ERROR_SHOUTVISIBILITY);
    }
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public void setSeed( long seed ) {
    randGenerator = PRNGFactory.getFactory().create(seed);
  }

  public void seed( Seeder s ) {
    logger.debug("seed(" + s + ")");
    setSeed(s.nextSeed());
    learner.seed(s);
//    learner.randomInitialise();
    logger.debug("learner = " + learner);
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

  
  protected void sellerStrategy( Shout lastShout ) 
      throws ShoutsNotVisibleException {   
        
    if ( lastShout == null ) {
      return;
    }
    
    double lastPrice = lastShout.getPrice();
    if ( auction.shoutAccepted(lastShout) ) {      
      if ( agent.active() && currentPrice <= lastPrice ) {        
        raiseMargin(lastPrice);
      } else if ( lastShout.isBid() ) {
        if ( currentPrice >= lastPrice ) {
          lowerMargin(lastPrice);
        }
      }
    } else {      
      if ( lastShout.isAsk() ) {
        if ( currentPrice >= lastPrice ) {
          lowerMargin(lastPrice);
        }
      }
    }
  }

  
  protected void buyerStrategy( Shout lastShout ) 
      throws ShoutsNotVisibleException {    
        
    if ( lastShout == null ) {
      return;
    }
    
    double lastPrice = lastShout.getPrice();
    if ( auction.shoutAccepted(lastShout) ) {      
      if ( agent.active() && currentPrice >= lastPrice ) {        
        raiseMargin(lastPrice);
      } else if ( lastShout.isAsk() ) {
        if ( currentPrice <= lastPrice ) {
          lowerMargin(lastPrice);
        }
      }
    } else if ( lastShout.isBid() ) {
      if ( currentPrice <= lastPrice ) {
        lowerMargin(lastPrice);
      }
    }
  }

  protected double targetMargin( double price, double absolute, double relative ) {    
    double targetPrice = relative * price + absolute;    
    double privValue = agent.getPrivateValue(auction);
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

  protected void raiseMargin( double price ) {    
    double relative = 1 + randGenerator.raw() * scaling;
    double absolute = randGenerator.raw() * scaling;
    learner.train(targetMargin(price, absolute, relative));
  }

  protected void lowerMargin( double price ) {    
    double relative = 1 - randGenerator.raw() * scaling;
    double absolute = randGenerator.raw() * -scaling;
    learner.train(targetMargin(price, absolute, relative));
  }

}