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

  public void initialise() {
    super.initialise();
    profits = 0;
  }

  public void transferFunds( double amount, ElectricityTrader other ) {
    this.profits -= amount;
    other.profits += amount;
  }



  public void informOfBuyer( double price, int quantity ) {
    Debug.assert(isSeller);

    // Reward the learning algorithm according to profits made.
    double profit = quantity * (price - privateValue);

    //Relax this constraint for GP experiments!
    //Debug.assert(profit >= 0);

    profits += profit;
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                               double price, int quantity) {
    Debug.assert(!isSeller);

    // Reward the learning algorithm according to profits made
    double profit = quantity * (privateValue - price);
    profits += profit;

    if ( profit < 0 ) {
      //System.out.println("currentShout = " + getCurrentShout());
      //System.out.println("quantity = " + quantity);
      //System.out.println("privateValue = " + privateValue);
      //System.out.println("price = " + price);
      //System.out.println("winningShout = " + winningShout);
      //Debug.assert(profit >= 0);
    }

    ElectricityTrader trader = (ElectricityTrader) seller;
    trader.informOfBuyer(price,quantity);
  }

  public double getProfits() {
    return profits;
  }

  public int getCapacity() {
    return capacity;
  }


  public String toString() {
    return "(" + getClass() + " id:" + id + " capacity:" + capacity + " privateValue:" + privateValue + " fixedCosts:" + fixedCosts + " profits:" + profits + " isSeller:" + isSeller + ")";
  }

}