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

import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import uk.ac.liv.auction.agent.AbstractTraderAgent;
import uk.ac.liv.auction.agent.AdaptiveStrategy;
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
  
  protected ColorMap colorMap;
  
  protected float price;
  
  public float scale = 1000;
  
  public DrawableAgentAdaptor( Auction auction ) {
    this(auction, null);
  }
  
  public DrawableAgentAdaptor( Auction auction, AbstractTraderAgent agent ) {
    this(auction, agent, null);
    colorMap = new ColorMap();
    double scale2 = scale * 0.75;
    for( int i=0; i<scale; i++ ) {
      double intensity = 0.25 + i/scale2;
      colorMap.mapColor(i, intensity, 0, intensity);
    }
  }
  
  public DrawableAgentAdaptor( Auction auction, AbstractTraderAgent agent, 
      						ColorMap colorMap ) {
    this.agent = agent;
    this.colorMap = colorMap;
    if ( agent != null ) {
      Valuer valuer = agent.getValuer();
      if ( valuer instanceof RandomValuer ) {
        scale = (float) ((RandomValuer) valuer).getMaxValue() * 2;
      }
    }
  }
  
  public void draw( SimGraphics g ) {
    int cellHeight = g.getCurHeight();
    int cellWidth = g.getCurWidth();
    price = 0;
    if ( agent != null ) {
      Shout shout = agent.getCurrentShout();
      if ( shout != null ) {
        price = (float) shout.getPrice();
      }
    }
    int y =  (int) ((price / scale) * 5);
    g.setDrawingParameters(5, y, 1);
    if ( colorMap == null ) {
      g.drawRect(Color.RED); 
    } else {
      g.drawRect(colorMap.getColor((int) price));
    }
    g.setDrawingParameters(5, 5, 5);
    g.drawHollowRect(Color.WHITE);
  }
  
  public int getX() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public int getY() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public float getLastProfit() {
    return (float) agent.getLastProfit();
  }
  
  public float getCurrentValuation() {
    return (float) agent.getValuation(auction);
  }
  
  public long getId() {
    return agent.getId();
  }
  
  public String getRole() {
    if ( agent.isSeller() ) {
      return "Seller";
    } else {
      return "Buyer";
    }
  }
  
  public Object getAgentType() {
    return agent;
  }
  
  public boolean getLastShoutAccepted() {
    return agent.lastShoutAccepted();
  }
  
  public float getLastShoutPrice() {
    return price;
  }

  public float getLearningDelta() {
    Strategy s = agent.getStrategy();
    if ( s instanceof AdaptiveStrategy ) {
      return (float) ((AdaptiveStrategy) s).getLearner().getLearningDelta();
    } else {
      return -1;
    }
  }
  
}


