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


package uk.ac.liv.auction.zi;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.*;

import uk.ac.liv.ai.learning.MimicryLearner;
import uk.ac.liv.ai.learning.Learner;

import uk.ac.liv.util.Seedable;
import uk.ac.liv.util.Parameterizable;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

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
    implements Seedable, Serializable, Parameterizable {

  protected double currentMargin;

  protected double currentPrice;

  protected double lastPrice;

  protected MimicryLearner learner;

  /**
   * The PRNG used to draw perturbation values
   */
  protected static MersenneTwisterFast randGenerator = new MersenneTwisterFast();

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

    scaling = parameters.getDouble(base.push(P_SCALING), null, scaling);

    learner = (MimicryLearner)
        parameters.getInstanceForParameter(base.push(P_LEARNER), null,
                                                   MimicryLearner.class);
    if ( learner instanceof Parameterizable ) {
      ((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
    }

    logger.debug("Initialised with scaling = " + scaling + " and learner = " +
                  learner);

  }

  public void modifyShout( Shout.MutableShout shout ) {
    try {
      super.modifyShout(shout);
      Shout lastShout = auction.getLastShout();
      if (agent.isSeller()) {
        sellerStrategy(lastShout);
      } else {
        buyerStrategy(lastShout);
      }
      currentMargin = learner.act();
      logger.debug("Bidding with margin " + currentMargin);
      logger.debug("Agent's private value = " + agent.getPrivateValue(auction));
      if (agent.isBuyer()) {
        currentPrice = agent.getPrivateValue(auction) * (1 - currentMargin);
      }
      else {
        currentPrice = agent.getPrivateValue(auction) * (1 + currentMargin);
      }
      logger.debug("Bidding at " + currentPrice);
      if (currentPrice > 0) {
        shout.setPrice(currentPrice);
      }
    } catch ( ShoutsNotVisibleException e ) {
      logger.error(e.getMessage());
      throw new AuctionError("ZIPStrategy can only be used with auctioneers who permit shout visibility");
    }
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

  public void setSeed( long seed ) {
    randGenerator.setSeed(seed);
  }

  public void setLearner( Learner learner ) {
    this.learner = (MimicryLearner) learner;
  }

  public Learner getLearner() {
    return learner;
  }

  protected void sellerStrategy( Shout lastShout ) {
    logger.debug("sellerStrategy(" + lastShout + ")");
    if ( lastShout == null ) {
      return;
    }
    double lastPrice = lastShout.getPrice();
    if ( auction.shoutAccepted(lastShout) ) {
      logger.debug("last shout was accepted");
      if ( agent.active() && currentPrice <= lastPrice ) {
        logger.debug("agent is active - raising");
        raiseMargin(lastPrice);
      } else if ( lastShout.isBid() ) {
        if ( currentPrice >= lastPrice ) {
          lowerMargin(lastPrice);
        }
      }
    } else {
      logger.debug("last shout not accepted");
      if ( lastShout.isAsk() ) {
        if ( currentPrice >= lastPrice ) {
          lowerMargin(lastPrice);
        }
      }
    }
  }

  protected void buyerStrategy( Shout lastShout ) {
    logger.debug("buyerStrategy(" + lastShout + ")");
    if ( lastShout == null ) {
      return;
    }
    double lastPrice = lastShout.getPrice();
    if ( auction.shoutAccepted(lastShout) ) {
      logger.debug("last shout was accepted");
      if ( agent.active() && currentPrice >= lastPrice ) {
        logger.debug("agent is active - raising");
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
    logger.debug("targetMargin(" + price + ", " + absolute + ", " + relative);
    double targetPrice = relative * price + absolute;
    logger.debug("targetPrice = " + targetPrice);
    double privValue = agent.getPrivateValue(auction);
    double targetMargin = (targetPrice - privValue) / privValue;
    logger.debug("targetMargin = " + targetMargin);
    return targetMargin;
  }

  protected void raiseMargin( double price ) {
    logger.debug("Raising margin towards " + price);
    double relative = randGenerator.nextDouble() + 1;
    double absolute = randGenerator.nextDouble() * scaling;
    learner.train(targetMargin(price, absolute, relative));
  }

  protected void lowerMargin( double price ) {
    logger.debug("Lowering margin towards " + price);
    double relative = randGenerator.nextDouble();
    double absolute = randGenerator.nextDouble() * -scaling;
    learner.train(targetMargin(price, absolute, relative));
  }

}