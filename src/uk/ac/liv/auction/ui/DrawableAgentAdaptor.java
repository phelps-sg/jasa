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

package uk.ac.liv.auction.ui;

import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.agent.RandomValuer;
import uk.ac.liv.auction.agent.Valuer;
import uk.ac.liv.auction.agent.Strategy;

import uk.ac.liv.auction.core.Auction;
import uk.ac.liv.auction.core.Shout;

/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class DrawableAgentAdaptor implements Drawable {
  
  protected AbstractTraderAgent agent;
  
  protected Auction auction;
  
  public float scale = 200;
  
  public DrawableAgentAdaptor( Auction auction ) {
    this(auction, null);
  }
  
  public DrawableAgentAdaptor( Auction auction, AbstractTraderAgent agent ) {
    this.agent = agent;
    if ( agent != null ) {
      Valuer valuer = agent.getValuer();
      if ( valuer instanceof RandomValuer ) {
        scale = (float) ((RandomValuer) valuer).getMaxValue();
      }
    }
  }
  
  public void draw( SimGraphics g ) {
    int cellHeight = g.getCurHeight();
    int cellWidth = g.getCurWidth();
    float price = 0;
    if ( agent != null ) {
      Shout shout = agent.getCurrentShout();
      if ( shout != null ) {
        price = (float) shout.getPrice();
      }
    }
    int y =  (int) ((price / scale) * 5);
    g.setDrawingParameters(5, y, 1);
    g.drawRect(Color.RED);
   // g.setDrawingParameters(5, 5, 5);
    //g.drawRect(Color.WHITE);
  }
  
  public int getX() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public int getY() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public double getLastProfit() {
    return agent.getLastProfit();
  }
  
  public double getCurrentValuation() {
    return agent.getValuation(auction);
  }
  
  public long getId() {
    return agent.getId();
  }
  
  public String getRole() {
    if ( agent.isSeller() ) {
      return "seller";
    } else {
      return "buyer";
    }
  }
  
  public Strategy getStrategy() {
    return agent.getStrategy();
  }
  
  public boolean getLastShoutAccepted() {
    return agent.lastShoutAccepted();
  }
}


