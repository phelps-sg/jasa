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
    implements Seedable, Serializable, Parameterizable {

  protected double currentMargin;

  protected double currentPrice;

  protected double lastPrice;

  protected MimicryLearner learner;

  /**
   * The PRNG used to draw perturbation values
   */
  protected static RandomElement randGenerator =
      PRNGFactory.getFactory().create();

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

    logger.debug("Initialised with scaling = " + scaling + " and learner = " +
                  learner);

  }

  public void initialise() {
    super.initialise();
    currentMargin = 0.5 + randGenerator.raw()/2;
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    try {
      Shout lastShout = auction.getLastShout();
      if (agent.isSeller()) {
        sellerStrategy(lastShout);
      } else {
        buyerStrategy(lastShout);
      }
      logger.debug(this + ": Bidding with margin " + currentMargin);
      logger.debug(this + ": Agent's private value = " + agent.getPrivateValue(auction));
      if (agent.isBuyer()) {
        currentPrice = agent.getPrivateValue(auction) * (1 - currentMargin);
      }
      else {
        currentPrice = agent.getPrivateValue(auction) * (1 + currentMargin);
      }
      logger.debug(this + ": Bidding at " + currentPrice);
      if (currentPrice > 0) {
        shout.setPrice(currentPrice);
      }
      currentMargin = learner.act();
      return super.modifyShout(shout);
    } catch ( ShoutsNotVisibleException e ) {
      logger.error(e.getMessage());
      throw new AuctionError("ZIPStrategy can only be used with auctioneers who permit shout visibility");
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
    ((Seedable) learner).seed(s);
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
    ((MimicryLearner) learner).setOutputLevel(margin);
  }

  protected void sellerStrategy( Shout lastShout ) {
    logger.debug("sellerStrategy(" + lastShout + ")");
    if ( lastShout == null ) {
      return;
    }
    double lastPrice = lastShout.getPrice();
    if ( auction.shoutAccepted(lastShout) ) {
      logger.debug(this + ": last shout was accepted");
      if ( agent.active() && currentPrice <= lastPrice ) {
        logger.debug(this + ": agent is active - raising");
        raiseMargin(lastPrice);
      } else if ( lastShout.isBid() ) {
        if ( currentPrice >= lastPrice ) {
          lowerMargin(lastPrice);
        }
      }
    } else {
      logger.debug(this + ": last shout not accepted");
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
      logger.debug(this + ": last shout was accepted");
      if ( agent.active() && currentPrice >= lastPrice ) {
        logger.debug(this + ": agent is active - raising");
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
    double targetMargin = 0;
    if ( agent.isBuyer() ) {
      targetMargin = (targetPrice - privValue) / privValue;
    } else {
      targetMargin = (privValue - targetPrice) / privValue;
    }
    if ( targetMargin < 0 ) {
      logger.debug(this + ": clipping margin at 0");
      targetMargin = 0;
    }
    logger.debug(this + ": targetMargin = " + targetMargin);
    return targetMargin;
  }

  protected void raiseMargin( double price ) {
    logger.debug(this + ": Raising margin towards " + price);
    double relative = 1 + randGenerator.raw() * scaling;
    double absolute = randGenerator.raw() * scaling;
    learner.train(targetMargin(price, absolute, relative));
  }

  protected void lowerMargin( double price ) {
    logger.debug(this + ": Lowering margin towards " + price);
    double relative = 1 - randGenerator.raw() * scaling;
    double absolute = randGenerator.raw() * -scaling;
    learner.train(targetMargin(price, absolute, relative));
  }

}