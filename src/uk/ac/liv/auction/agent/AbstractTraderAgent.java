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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.RoundRobinAuction;
import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.AuctionException;
import uk.ac.liv.auction.core.AuctionClosedException;
import uk.ac.liv.auction.core.NotAnImprovementOverQuoteException;

import uk.ac.liv.util.IdAllocator;
import uk.ac.liv.util.Parameterizable;
import uk.ac.liv.util.Resetable;
import uk.ac.liv.util.Prototypeable;

import ec.util.ParameterDatabase;
import ec.util.Parameter;

import java.io.Serializable;

import org.apache.log4j.Logger;

/** <p>
 * An abstract class representing a simple agent trading in a round-robin auction.
 * Traders of this type deal in a single commodity for which they have a
 * well-defined private valuation.
 *
 * </p><p><b>Parameters</b><br>
 * <table>
 * 
 * <tr><td valign=top><i>base</i><tt>.isseller</tt><br>
 * <font size=-1>boolean</font></td>
 * <td valign=top>(is this agent a seller)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.strategy</tt><br>
 * <font size=-1>class</font></td>
 * <td valign=top>(the trading strategy to use)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.initialstock</tt><br>
 * <font size=-1>int &gt;= 0</font></td>
 * <td valign=top>(the initial quantity of the commoditiy possessed by this agent)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.initialfunds</tt><br>
 * <font size=-1>double</font></td>
 * <td valign=top>(the initial funds)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.valuer</tt><br>
 * <font size=-1>class, inherits uk.ac.liv.auction.agent.Valuer</td>
 * <td valign=top>(the valuation policy to use)</td><tr>
 *
 * </table>
 *
 * @see uk.ac.liv.auction.core.RoundRobinAuction
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class AbstractTraderAgent implements RoundRobinTrader,
                                                      Serializable,
                                                      Parameterizable,
                                                      Prototypeable,
                                                      Cloneable {

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
   * The valuer for this agent
   */
  protected Valuer valuer;

  /**
   * Unique id for this trader.  Its used mainly for debugging purposes.
   */
  protected long id;

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
   * The profit made in the last round.
   */
  protected double lastProfit = 0;

  /**
   * The total profits to date
   */
  protected double profits = 0;

  protected boolean lastShoutAccepted = false;

  /**
   * The current shout for this trader.
   */
  protected Shout currentShout;

  static Logger logger = Logger.getLogger(AbstractTraderAgent.class);

  /**
   * Parameter names used when initialising from parameter db
   */
  public static final String P_IS_SELLER = "isseller";
  public static final String P_STRATEGY = "strategy";
  public static final String P_INITIAL_STOCK = "initialstock";
  public static final String P_INITIAL_FUNDS = "initialfunds";  
  public static final String P_VALUER = "valuer";
  public static final String P_DEFAULT_STRATEGY =
                                   "uk.ac.liv.auction.core.PureSimpleStrategy";

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
    this.valuer = new FixedValuer(privateValue);
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


  public void setup( ParameterDatabase parameters, Parameter base ) {

    initialStock = parameters.getIntWithDefault(base.push(P_INITIAL_STOCK),
                                                null, 0);

    initialFunds = parameters.getDoubleWithDefault(base.push(P_INITIAL_FUNDS),
                                                   null, 0);

    isSeller = parameters.getBoolean(base.push(P_IS_SELLER), null, false);

    valuer =
        (Valuer) parameters.getInstanceForParameter(base.push(P_VALUER), null,
                                                     Valuer.class);
    valuer.setup(parameters, base.push(P_VALUER));

    strategy =
        (AbstractStrategy)
         parameters.getInstanceForParameter(base.push(P_STRATEGY),
                                            null, AbstractStrategy.class);
    ((AbstractStrategy) strategy).setAgent(this);
    ((Parameterizable) strategy).setup(parameters, base.push(P_STRATEGY));
    
    initialise();
  }


  /**
   * Place a shout in the auction as determined by our currently configured
   * strategy.
   */
  public void requestShout( Auction auction ) {
    try {
      if ( currentShout != null ) {
        auction.removeShout(currentShout);        
      }
      currentShout = strategy.modifyShout(currentShout, auction);
      if ( active() && currentShout != null ) {
        auction.newShout(currentShout);
      } else {
        logger.debug(this + ": inactive");
      }
    } catch ( AuctionClosedException e ) {
      logger.debug("requestShout(): Received AuctionClosedException");
      // do nothing
    } catch ( NotAnImprovementOverQuoteException e ) {
      logger.debug("requestShout(): Received NotAnImprovementOverQuoteException");
      // do nothing
    } catch ( AuctionException e ) {
      logger.warn(e.getMessage());
      e.printStackTrace();
    }
  }

  public void auctionOpen( Auction auction ) {
  }

  public void auctionClosed( Auction auction ) {
    ((RoundRobinAuction) auction).remove(this);
  }

  public void roundClosed( Auction auction ) {
    //if ( currentShout != null ) {
      //auction.removeShout(currentShout);
      //ShoutPool.release(currentShout);
      //currentShout = null;
    //}
    strategy.endOfRound(auction);
  }

  public Shout getCurrentShout() {
    return currentShout;
  }

  public void purchaseFrom( Auction auction, AbstractTraderAgent  seller,
                                          int quantity, double price) {
    seller.informOfBuyer(auction, this, price, quantity);
    giveFunds(seller, price*quantity);
    stock += seller.deliver(auction, quantity, price);
    lastProfit = quantity * (valuer.determineValue(auction)-price);
    //assert lastProfit >= 0;
    profits += lastProfit;
    valuer.consumeUnit(auction);
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
  public int deliver( Auction auction, int quantity, double price ) {
    stock -= quantity;
    lastProfit = quantity * (price-valuer.determineValue(auction));
    if ( lastProfit < 0 ) {
      logger.debug("Negative profit for seller trading at price " + price);
    }
    profits += lastProfit;
    valuer.consumeUnit(auction);
    return quantity;
  }

  public long getId() {
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
    lastProfit = 0;
    profits = 0;
    lastShoutAccepted = false;
    currentShout = null;    
  }

  public void reset() {
    initialise();
    if ( strategy != null ) {
      ((Resetable) strategy).reset();
    }
    if ( valuer != null ) {
      valuer.reset();
    }
  }

  public double getPrivateValue( Auction auction ) {
    return valuer.determineValue(auction);
  }

  public void setPrivateValue( double privateValue ) {
    ((FixedValuer) valuer).setValue(privateValue);
  }


  public boolean isSeller() {
    return isSeller;
  }

  public boolean isBuyer() {
    return !isSeller;
  }

  public void setStrategy( Strategy strategy ) {
    this.strategy = strategy;
    strategy.setAgent(this);
  }

  public void setIsSeller( boolean isSeller ) {
    this.isSeller = isSeller;
  }

  public Strategy getStrategy() {
    return strategy;
  }

  /**
   * Return the profit made in the most recent auction round.
   * This can be used as, e.g. input to a re-inforcement learning
   * algorithm.
   */
  public double getLastProfit() {
    return lastProfit;
  }

  public double getProfits() {
    return profits;
  }

  public int determineQuantity( Auction auction ) {
    return strategy.determineQuantity(auction);
  }

  public Object protoClone() {
    AbstractTraderAgent copy = null;
    try {
      copy = (AbstractTraderAgent) clone();      
      copy.id = idAllocator.nextId();     
      copy.strategy = (Strategy) ((Prototypeable) strategy).protoClone();
      copy.reset();
    } catch ( CloneNotSupportedException e ) {
    }
    return copy;
  }
  

  public void informOfSeller( Auction auction, Shout winningShout,
                               RoundRobinTrader seller,
                               double price,  int quantity ) {
     lastShoutAccepted = true;
  }

  public void informOfBuyer( Auction auction, RoundRobinTrader buyer,
                             double price, int quantity ) {
    lastShoutAccepted = true;
  }

  public boolean lastShoutAccepted() {
    return lastShoutAccepted;
  }

  public Valuer getValuer() {
    return valuer;
  }

  public void setValuer( Valuer valuer ) {
    this.valuer = valuer;
  }


  /**
   * Calculate the hypothetical surplus this agent will receive if the
   * market had cleared uniformly at the specified equilibrium price
   * and quantity.
   */
  public abstract double equilibriumProfits( Auction auction,
                                              double equilibriumPrice,
                                              int quantity );

  /**
   * Determine whether or not this trader is active.
   * Inactive traders do not place shouts in the auction,
   * but do carry on learning through their strategy.
   *
   * @return true if the trader is active.
   */
  public abstract boolean active();


}