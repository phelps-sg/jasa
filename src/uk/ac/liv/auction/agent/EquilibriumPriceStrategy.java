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

import uk.ac.liv.auction.core.RandomRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.stats.EquilibriumReport;
import uk.ac.liv.util.Prototypeable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;

/**
 * A strategy which will bid at the true equilibrium price, if profitable, or
 * bid truthfully otherwise. Although this is not a realistic strategy, it can
 * be useful for testing and control experiments.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */

public class EquilibriumPriceStrategy extends FixedQuantityStrategyImpl
    implements Serializable, Prototypeable {

  public EquilibriumPriceStrategy( AbstractTradingAgent agent, double price,
      int quantity ) {
    super(agent);

    this.quantity = quantity;
  }

  public EquilibriumPriceStrategy() {
    super(null);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
  }

  public Object protoClone() {
    Object clonedStrategy;
    try {
      clonedStrategy = this.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new Error(e);
    }
    return clonedStrategy;
  }

  public boolean modifyShout( Shout.MutableShout shout ) {
    EquilibriumReport eqReport = new EquilibriumReport(
        (RandomRobinAuction) auction);
    eqReport.calculate();
    double price = eqReport.calculateMidEquilibriumPrice();
    if ( agent.isBuyer(auction) && price <= agent.getValuation(auction)
        || agent.isSeller(auction) && price >= agent.getValuation(auction) ) {
      shout.setPrice(price);
    } else {
      shout.setPrice(agent.getValuation(auction));
    }
    return super.modifyShout(shout);
  }

  public void endOfRound( Auction auction ) {
    // Do nothing
  }

}