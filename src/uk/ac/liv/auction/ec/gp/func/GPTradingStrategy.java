package uk.ac.liv.auction.ec.gp.func;

import java.io.Serializable;

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

public class GPTradingStrategy extends GPIndividualCtx implements Strategy,
                                                                  QuoteProvider,
                                                                  Serializable {

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
    GPGenericData input = new GPGenericData();
    try {
      evaluateTree(0, input);
    } catch ( ArithmeticException e ) {
      System.out.println("Caught: " + e);
      //e.printStackTrace();
    }
    double price = ((GenericNumber) input.data).doubleValue();
    if ( price > 0 ) {
      shout.setPrice(price);
    } else {
      shout.setPrice(0);
    }
    shout.setQuantity( quantity ); //TODO: set depending on capacity
    shout.setIsBid( agent.isBuyer() );
  }

}

