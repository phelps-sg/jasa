/*
 * JASA Java Auction Simulator API
 * Copyright (C) Steve Phelps
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

import uk.ac.liv.auction.core.*;

/**
 * <p>
 * Agents are this type are inspired by, but are not identical to ZIP
 * (Zero Intelligence Plus) agents, as described in
 * </p>
 * <p>
 * "Minimal-Intelligence Agents for Bargaining Behaviors in Market-Based
 * Environments", HP Labs Technical Report 970811 by Dave Cliff & Janet Bruten
 * </p>
 * <p>
 * ZIS agents converge towards the public market quote, rather than
 * the last bid placed by other traders.
 */

public class ZISTraderAgent extends ZITraderAgent {

  double momentum;
  double shoutPrice;

  static final double momentumCoefficient = 0.5;
  static final double learningRate = 0.3;


  public ZISTraderAgent( double privateValue, int tradeEntitlement,
                          boolean isSeller ) {
    super(privateValue, tradeEntitlement, isSeller);
  }

  protected void initialise() {
    super.initialise();
    if ( isSeller ) {
      shoutPrice = (double) MAX_PRICE;
    } else {
      shoutPrice = 0;
    }
    momentum = 0;
  }

  protected double determinePrice( Auction auction ) {
    double price;
    MarketQuote quote = auction.getQuote();
    if ( isSeller ) {
      if ( ! lastShoutSuccessful ) {
        if ( quote.getBid() != Double.NEGATIVE_INFINITY ) {
          adjustMargin(normalise(quote.getBid()),-1);
        }
      } else {
        if ( quote.getAsk() != Double.POSITIVE_INFINITY ) {
          adjustMargin(normalise(quote.getAsk()),1);
        }
      }
    } else {
      if ( ! lastShoutSuccessful ) {
        if ( quote.getAsk() != Double.POSITIVE_INFINITY ) {
          adjustMargin(normalise(quote.getAsk()),+1);
        }
      } else {
        if ( quote.getBid() != Double.NEGATIVE_INFINITY ) {
          adjustMargin(normalise(quote.getBid()),-1);
        }
      }
    }
    return shoutPrice;
  }


  public double widrowHoffDelta( double price, int increase ) {
    return learningRate * (targetPrice(price,increase) - shoutPrice);
  }

  public void adjustMomentum( double price, int increase ) {
    momentum = momentum*momentumCoefficient
                + (1 - momentumCoefficient)*widrowHoffDelta(price,increase);
  }

  public void adjustMargin( double price, int increase ) {
    //System.out.println(this + ": adjusting towards " + price);
    shoutPrice += momentum;
    adjustMomentum(price,increase);
  }

  public double normalise( double price ) {
    double tPrice =  price;
    if ( price < 0 ) {
      tPrice = 0.0;
    } else if ( price > MAX_PRICE ) {
      tPrice = (double) MAX_PRICE;
    }

    if ( isSeller ) {
      if ( tPrice < privateValue ) {
        tPrice = privateValue;
      }
    } else {
      if ( tPrice > privateValue ) {
        tPrice = privateValue;
      }
    }
    return tPrice;
  }

  public double targetPrice( double price, int increase ) {

    //double targetCoef = randGenerator.nextDouble();
    //double absAdjustment = randGenerator.nextDouble();


    //if ( increase > 1 ) {
     // targetCoef = targetCoef + 1;
    //}

    // return targetCoef * price + increase*absAdjustment;
    return price;
  }

  public double getMomentum() {
    return momentum;
  }

  public double getShoutPrice() {
    return shoutPrice;
  }

  /**
   * ZISTraders override default winning behaviour; only purchase if the
   * price is right. i.e. if the price is within our private value.
   */
  public void informOfSeller( Shout winningShout, RoundRobinTrader seller,
                                double price, int quantity ) {
    if ( price <= privateValue ) {
      //purchaseFrom(seller, winningShout.getQuantity(), price);
      AbstractTraderAgent agent = (AbstractTraderAgent) seller;
      purchaseFrom(agent, quantity, price);
      // System.out.println("Accepting offer from " + seller + " at price " + price + " my priv value = " + privateValue);
    } else {
      //System.out.println("Rejecting offer from " + seller + " at price " + price);
    }
  }

  public String toString() {
    return "(ZISTraderAgent id:" + id + " isSeller:" + isSeller + " shoutPrice:" + shoutPrice + " privateValue:" + privateValue + " tradeEntitlement:" + tradeEntitlement + " momentum:" + momentum + " quantityTraded:" + quantityTraded + " lastShoutSuccessful:" + lastShoutSuccessful + ")";
  }



}