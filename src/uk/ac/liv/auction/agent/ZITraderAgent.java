package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.*;

import java.util.Random;


/**
 * Abstract superclass for "Zero Intelligence" (ZI) trader agents.
 * See "Minimal Intelligence Agents for Bargaining Behaviours in
 * Market-based Environments" Dave Cliff 1997.
 *
 * @author Steve Phelps
 */

public abstract class ZITraderAgent extends AbstractTraderAgent {

  /**
   * The maximum price of any shout for all ZI agents.
   */
  public static final double MAX_PRICE = 200;

  /**
   * The number of units this agent is entitlted to trade in this trading period.
   */
  protected int tradeEntitlement;

  /**
   * The initial value of tradeEntitlement
   */
  protected int initialTradeEntitlement;

  /**
   * Flag indicating whether the last shout resulted in a transaction.
   */
  protected boolean lastShoutSuccessful;

  /**
   * The current shout for this agent.
   */
  protected Shout shout;

  /**
   * Static helper class for generating random numbers.
   */
  protected static Random randGenerator;

  /**
   * The number of units traded to date
   */
  protected int quantityTraded = 0;


  public ZITraderAgent( int stock, long funds, long privateValue,
                          int tradeEntitlement, boolean isSeller ) {
    super(stock, funds, privateValue, isSeller);
    randGenerator = new Random();
    this.initialTradeEntitlement = tradeEntitlement;
    initialise();
  }

  protected void initialise() {
    super.initialise();
    lastShoutSuccessful = false;
    tradeEntitlement = initialTradeEntitlement;
    quantityTraded = 0;
    shout = null;
  }

  public ZITraderAgent( long privateValue, int tradeEntitlement, boolean isSeller ) {
    this(0, 0, privateValue, tradeEntitlement, isSeller);
  }

  /**
   * This method is a hook for subclasses to determine the pricing behaviour of
   * an agent.
   */
  protected abstract double determinePrice( Auction auction );

  /**
   * This method is a hook for subclasses to determine the quantity of a trade.
   * The default quantity is 1.
   */
  protected int determineQuantity( Auction auction ) {
    return 1;
  }

  protected long randomPrice( int n ) {
    if ( n == 0 ) {
      return 0;
    }
    synchronized(randGenerator) {
      return (long) randGenerator.nextInt(n);
    }
  }

  protected long randomPrice( long n ) {
    return randomPrice((int) n);
  }

  public void requestShout( RoundRobinAuction auction ) {

    if ( tradeEntitlement == 0 ) {
      // Drop out of trading once my entitlement is out.
      auction.remove(this);
      return;
    }

    double price = determinePrice(auction);
    int qty = determineQuantity(auction);

    if ( shout == null ) {
      shout = new Shout(this, qty, price, !isSeller);
    } else {
      auction.removeShout(shout);
      shout.setPrice(price);
      shout.setQuantity(qty);
    }

    lastShoutSuccessful = false;

    try {
      auction.newShout(shout);
    } catch ( AuctionException e ) {
      e.printStackTrace();
    }
  }

  /**
   * Default behaviour for winning ZI bidders is to purchase unconditionally.
   */
  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                                  double price, int quantity) {
    //System.out.println(this + ": buying " + winningShout.getQuantity() + " from " + seller);
    AbstractTraderAgent agent = (AbstractTraderAgent) seller;
    purchaseFrom(agent, quantity, price);
    lastShoutSuccessful = true;
  }

  public void purchaseFrom( AbstractTraderAgent seller, int quantity, double price ) {
    tradeEntitlement--;
    quantityTraded += quantity;
    AbstractTraderAgent agent = (AbstractTraderAgent) seller;
    super.purchaseFrom(agent, quantity, price);
    //sellUnits(quantity);
  }

  public int deliver( int quantity ) {
    //System.out.println(this + ": selling " + quantity);
    lastShoutSuccessful = true;
    tradeEntitlement--;
    quantityTraded += quantity;
    return super.deliver(quantity);
  }

  public void sellUnits( int numUnits ) {
    stock -= numUnits;
    funds += numUnits * privateValue;
  }

  public int getQuantityTraded() {
    return quantityTraded;
  }


}