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

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.ShoutFactory;
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
                                        Resetable,
                                        Cloneable {

  protected AbstractTraderAgent agent;

  protected Shout.MutableShout currentShout;

  protected Auction auction;

  public AbstractStrategy() {
    initialise();
  }

  public AbstractStrategy( AbstractTraderAgent agent ) {
    this();
    this.agent = agent;
  }

  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public void reset() {
    initialise();
  }

  public Shout modifyShout( Shout shout, Auction auction ) {
    this.auction = auction;
    modifyShout(currentShout);
    return ShoutFactory.getFactory().create(currentShout.getAgent(),
                                             currentShout.getQuantity(),
                                             currentShout.getPrice(),
                                             currentShout.isBid());
  }

  public void modifyShout( Shout.MutableShout shout ) {
    shout.setIsBid(agent.isBuyer());
    shout.setAgent(agent);
  }

  public void initialise() {
    currentShout = new Shout.MutableShout();
  }

}