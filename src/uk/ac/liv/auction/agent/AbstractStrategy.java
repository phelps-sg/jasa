/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;

import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;

/**
 * <p>
 * An abstract implementation of the Strategy interface
 * that provides skeleton functionality for making trading
 * decisions.
 * </p>
 */
public abstract class AbstractStrategy implements
                                        Strategy,
                                        Parameterizable,
                                        Resetable,
                                        Cloneable {

  protected AbstractTraderAgent agent;

  public AbstractStrategy() {
  }

  public AbstractStrategy( AbstractTraderAgent agent ) {
    this.agent = agent;
    initialise();
  }

  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public void reset() {
    initialise();
  }

  public void modifyShout( Shout shout, Auction auction ) {
    shout.setIsBid(agent.isBuyer());
    shout.setQuantity(agent.determineQuantity(auction));
  }

  public void initialise() {
  }

}