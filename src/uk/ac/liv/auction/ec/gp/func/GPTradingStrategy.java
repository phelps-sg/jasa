package uk.ac.liv.auction.ec.gp.func;

import ec.gp.*;
import ec.*;

import uk.ac.liv.auction.agent.*;
import uk.ac.liv.auction.core.*;

import uk.ac.liv.ec.gp.func.*;
import uk.ac.liv.ec.gp.*;

import uk.ac.liv.util.GenericNumber;

/**
 * <p>Title: JASA</p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p> </p>
 * @author Steve Phelps
 *
 */

public class GPTradingStrategy extends GPIndividualCtx implements Strategy, QuoteProvider {

  AbstractTraderAgent agent = null;

  Shout currentShout = null;

  int quantity = 1;

  Auction currentAuction = null;


  public void setAgent( AbstractTraderAgent agent ) {
    this.agent = agent;
  }

  public void setQuantity( int quantity ) {
    this.quantity = quantity;
  }

  protected double getPrivateValue() {
    return agent.getPrivateValue();
  }

  public Auction getAuction() {
    return currentAuction;
  }

  public MarketQuote getQuote() {
    return currentAuction.getQuote();
  }

  public void modifyShout( Shout shout, Auction auction ) {
    currentShout = shout;
    currentAuction = auction;
    GPNumberData input = new GPNumberData();
    try {
      evaluateTree(1, input);
    } catch ( ArithmeticException e ) {
      System.out.println("Caught: " + e);
      //e.printStackTrace();
    }
    shout.setPrice( ((GenericNumber) input.data).doubleValue() );
    shout.setQuantity( quantity );
  }

}

