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

package uk.ac.liv.auction.stats;

import uk.ac.liv.auction.core.Shout;
import uk.ac.liv.auction.core.MarketQuote;

import uk.ac.liv.util.Resetable;


public interface MarketDataLogger extends Resetable {

  public void updateQuoteLog( int time, MarketQuote quote );

  public void updateTransPriceLog( int time, Shout ask, double price );

  public void updateShoutLog( int time, Shout shout );

  public void finalReport();

}