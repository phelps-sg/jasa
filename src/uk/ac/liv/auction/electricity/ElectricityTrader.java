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

package uk.ac.liv.auction.electricity;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.RoundRobinTrader;
import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.agent.Strategy;

import uk.ac.liv.ai.learning.NPTRothErevLearner;
import uk.ac.liv.ai.learning.RothErevLearner;
import uk.ac.liv.ai.learning.StimuliResponseLearner;

import uk.ac.liv.util.Debug;
import uk.ac.liv.util.IdAllocator;
import uk.ac.liv.util.Resetable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.io.Serializable;


/**
 *
 * <p>
 * An adaptive trader, trading in a simulated Elecitricty market, as described
 * in:
 * </p>
 * <p>
 * "Markert Power and Efficiency in a Computational Electricity Market
 * with Discriminatory Double-Auction Pricing"
 * Nicolaisen, J.; Petrov, V.; and Tesfatsion, L.
 * in IEEE Trans. on Evol. Computation, Vol. 5, No. 5. 2001
 * </p>
 *
 * <p>
 * This code was written by Steve Phelps in an attempt to replicate
 * the results in the above paper.  This work was carried out independently
 * from the original authors.  Any corrections to this code are
 * welcome.
 * </p>
 *
 * @author Steve Phelps
 */

public class ElectricityTrader extends AbstractTraderAgent {

  /**
   * The capacity of this trader in MWh
   */
  protected int capacity;

  /**
   * The fixed costs for this trader.
   */
  protected double fixedCosts;

  /**
   * The total profits of this trader to date.
   */
  protected double profits;

  protected double lastProfit;

  static final String P_CAPACITY = "capacity";
  static final String P_FIXED_COSTS = "fixedcosts";


  public ElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller,
                              Strategy strategy ) {
    super(0, 0, privateValue, isSeller, strategy);
    this.capacity = capacity;
    this.fixedCosts = fixedCosts;
    initialise();
  }

  public ElectricityTrader( int capacity, double privateValue,
                              double fixedCosts, boolean isSeller ) {
    this(capacity, privateValue, fixedCosts, isSeller, null);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    super.setup(parameters, base);
    capacity = parameters.getIntWithDefault(base.push(P_CAPACITY), null, 0);
    fixedCosts = parameters.getDoubleWithDefault(base.push(P_FIXED_COSTS), null, 0);
    initialise();
  }

  public void initialise() {
    super.initialise();
    profits = 0;
    lastProfit = 0;
  }

  public void requestShout( RoundRobinAuction auction ) {
    super.requestShout(auction);
    lastProfit = 0;
  }

  public void transferFunds( double amount, ElectricityTrader other ) {
    this.profits -= amount;
    other.profits += amount;
  }

  public void informOfBuyer( double price, int quantity ) {
    Debug.assertTrue(isSeller);

    // Reward the learning algorithm according to profits made.
    lastProfit = quantity * (price - privateValue);

    //Relax this constraint for GP experiments!
    //Debug.assert(profit >= 0);

    profits += lastProfit;
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {
    Debug.assertTrue(!isSeller);

    // Reward the learning algorithm according to profits made
    lastProfit = quantity * (privateValue - price);

    if ( lastProfit < 0 ) {
      //System.out.println("currentShout = " + getCurrentShout());
      //System.out.println("quantity = " + quantity);
      //System.out.println("privateValue = " + privateValue);
      //System.out.println("price = " + price);
      //System.out.println("winningShout = " + winningShout);
      //Debug.assert(profit >= 0);
      return;
    }


    ElectricityTrader trader = (ElectricityTrader) seller;
    if ( trader.acceptDeal(price, quantity) ) {
      profits += lastProfit;
      trader.informOfBuyer(price,quantity);
    }
  }

  public boolean acceptDeal( double price, int quantity ) {
    Debug.assertTrue(isSeller);
    return price > privateValue;
  }

  public double getProfits() {
    return profits;
  }

  public int getCapacity() {
    return capacity;
  }

  public double getLastProfit() {
    return lastProfit;
  }

  public int determineQuantity( Auction auction ) {
    return capacity;
  }

  public String toString() {
    return "(" + getClass() + " id:" + id + " capacity:" + capacity + " privateValue:" + privateValue + " fixedCosts:" + fixedCosts + " profits:" + profits + " isSeller:" + isSeller + " lastProfit:" + lastProfit + " strategy:" + strategy + ")";
  }

}