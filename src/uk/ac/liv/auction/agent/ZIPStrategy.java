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

import uk.ac.liv.ai.learning.MimicryLearner;
import uk.ac.liv.ai.learning.Learner;

import uk.ac.liv.util.Seedable;
import uk.ac.liv.util.Parameterizable;

import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

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
 * Note that this class is currently untested.
 *
 * @author Steve Phelps
 */

public class ZIPStrategy extends FixedQuantityStrategyImpl
    implements Seedable, Serializable, Parameterizable {

  protected double currentMargin;

  protected double currentPrice;

  protected double lastPrice;

  protected MimicryLearner learner;

  protected MersenneTwisterFast randGenerator = new MersenneTwisterFast();

  protected double scaling;

  public static final String P_SCALING = "scaling";
  public static final String P_LEARNER = "learner";

  public ZIPStrategy() {
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {

    super.setup(parameters, base);

    scaling = parameters.getDouble(base.push(P_SCALING), null, 0);

    learner = (MimicryLearner)
        parameters.getInstanceForParameter(base.push(P_LEARNER), null,
                                                   MimicryLearner.class);
    if ( learner instanceof Parameterizable ) {
      ((Parameterizable) learner).setup(parameters, base.push(P_LEARNER));
    }

  }

  public void modifyShout( Shout shout, Auction auction ) {
    super.modifyShout(shout, auction);
    Shout lastShout = auction.getLastShout();
    if ( agent.isSeller() ) {
      sellerStrategy(lastShout);
      } else {
      buyerStrategy(lastShout);
    }
    currentMargin = learner.act();
    if ( agent.isBuyer() ) {
      currentPrice = agent.getPrivateValue() * (1 - currentMargin);
    } else {
      currentPrice = agent.getPrivateValue() * (1 + currentMargin);
    }
    if ( currentPrice > 0 ) {
      shout.setPrice(currentPrice);
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
    double lastPrice = lastShout.getPrice();
    if ( lastShout.accepted() ) {
      if ( currentPrice <= lastPrice ) {
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

  protected void buyerStrategy( Shout lastShout ) {
    double lastPrice = lastShout.getPrice();
    if ( lastShout.accepted() ) {
      if ( currentPrice >= lastPrice ) {
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

  protected double targetPrice( double price, double absolute, double relative ) {
    return relative * price + absolute;
  }

  protected void raiseMargin( double price ) {
    if ( agent.active() ) {
      // Only active agents raise margins
      double relative = randGenerator.nextDouble() + 1;
      double absolute = randGenerator.nextDouble() * scaling;
      learner.train(targetPrice(price, absolute, relative));
    }
  }

  protected void lowerMargin( double price ) {
    double relative = randGenerator.nextDouble();
    double absolute = randGenerator.nextDouble() * -scaling;
    learner.train(targetPrice(price, absolute, relative));
  }

}