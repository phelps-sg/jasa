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

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.AuctionException;

import uk.ac.liv.util.IdAllocator;

import java.io.Serializable;

/**
 * <p>
 * An abstract class representing a simple agent trading in a round-robin auction.
 * Traders of this type deal in a single commodity for which they have a
 * well-defined private valuation.
 * </p>
 *
 * @see TraderAgent
 * @see uk.ac.liv.auction.core.RoundRobinAuction
 *
 * @author Steve Phelps
 *
 */

public abstract class AbstractTraderAgent implements RoundRobinTrader,
                                                      TraderAgent,
                                                      Serializable {

  /**
   * The number of items of stock this agent posseses.
   */
  protected int stock = 0;

  /**
   * The initial stock of this agent
   */
  protected int initialStock = 0;

  /**
   * The amount of money this agent posseses.
   */
  protected double funds = 0;

  /**
   * The initial amount of money for this agent
   */
  protected double initialFunds = 0;

  /**
   * Used to allocate each agent with a unique id.
   */
  static IdAllocator idAllocator = new IdAllocator();

  /**
   * The private value of the commodity for this trader
   */
  protected double privateValue = 0;

  /**
   * Unique id for this trader.  Its used mainly for debugging purposes.
   */
  protected int id;

  /**
   * Flag indicating whether this trader is a seller or buyer.
   */
  protected boolean isSeller = false;

  /**
   * The bidding strategy for this trader.
   * The default strategy is to bid truthfully for a single unit.
   */
  protected Strategy strategy;

  /**
   * The current shout for this trader.
   */
  Shout currentShout;



  /**
   * Construct a trader with given stock level and funds.
   *
   * @param stock         The quantity of stock for this trader.
   * @param funds         The amount of money for this trader.
   * @param privateValue  The private value of the commodity traded by this trader.
   * @param isSeller      Whether or not this trader is a seller.
   */
  public AbstractTraderAgent( int stock, double funds, double privateValue,
                                boolean isSeller, Strategy strategy ) {
    this();
    initialStock = stock;
    initialFunds = funds;
    this.privateValue = privateValue;
    this.isSeller = isSeller;
    this.strategy = strategy;
    initialise();
  }

  public AbstractTraderAgent( int stock, double funds, double privateValue,
                                boolean isSeller ) {
    this(stock, funds, privateValue, isSeller, null);
    // Set the default strategy- truth.
    setStrategy(new PureSimpleStrategy(this, 0, 1));
  }

  public AbstractTraderAgent( int stock, double funds ) {
    this(stock, funds, 0, false);
  }

  /**
   * Construct a trader with no money and no funds.
   */
  public AbstractTraderAgent() {
    id = idAllocator.nextId();
  }

  /**
   * Default trading behaviour for agents.
   */
  public void requestShout( RoundRobinAuction auction ) {
    try {
      auction.removeShout(currentShout);
      strategy.modifyShout(currentShout, auction);
      auction.newShout(currentShout);
    } catch ( AuctionException e ) {
      e.printStackTrace();
    }
  }

  public Shout getCurrentShout() {
    return currentShout;
  }

  public synchronized void purchaseFrom(AbstractTraderAgent  seller, int quantity, double price) {
    giveFunds(seller, price*quantity);
    stock += seller.deliver(quantity);
  }

  public synchronized void giveFunds( AbstractTraderAgent seller, double amount ) {
    funds -= amount;
    seller.pay(amount);
  }

  /**
   * This method is invoked by a buyer on a seller when it wishes to transfer funds.
   *
   * @param amount The total amount of money to give to the seller
   */
  public synchronized void pay( double amount ) {
    funds += amount;
  }

  /**
   * This method is invoked by a seller on a buyer when it is transfering stock
   *
   * @param quantity The number of items of stock to transfer
   */

  public synchronized int deliver( int quantity ) {
    stock -= quantity;
    return quantity;
  }

  public int getId() {
    return id;
  }

  public double getFunds() {
    return funds;
  }

  public int getStock() {
    return stock;
  }

  protected void initialise() {
    stock = initialStock;
    funds = initialFunds;
    currentShout = new Shout(this);
    currentShout.setIsBid(!isSeller);
  }

  public void reset() {
    initialise();
  }

  public double getPrivateValue() {
    return privateValue;
  }

  public void setPrivateValue( double privateValue ) {
    this.privateValue = privateValue;
  }

  public boolean isSeller() {
    return isSeller;
  }

  public boolean isBuyer() {
    return !isSeller;
  }

  public void setStrategy( Strategy strategy ) {
    this.strategy = strategy;
  }

  public Strategy getStrategy() {
    return strategy;
  }

}