/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
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

import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


/**
 * An implementation of TraderAgent that makes its bidding decisions entirely by
 * interacting with a human trader.
 *
 * @author Steve Phelps
 */

public class InteractiveTraderAgent extends AbstractTraderAgent {

  BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
  PrintStream out = System.out;

  public InteractiveTraderAgent( int stock, double funds ) {
    super(stock, funds);
  }



  public void requestShout(RoundRobinAuction auction) {
    while (true) {
      try {
        Shout shout = getShout();
        out.println(this + ": shouting " + shout);
        if ( shout != null ) {
          auction.newShout(shout);
          auction.printState();
        }
      } catch ( AuctionException e ) {
        JOptionPane.showMessageDialog(null, e.getMessage(), "Bid Error",
                                    JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
      }
      break;
    }
  }

  public void informOfSeller( Shout winningShout, RoundRobinTrader seller, double price, int quantity ) {
    //TODO: make dialog for this.
    out.println(this + ": I've been informed of seller " + seller + " at price " + price + " and qty " + quantity);
    out.println("Buying");
    AbstractTraderAgent agent = (AbstractTraderAgent) seller;
    purchaseFrom(agent, quantity, price);
  }


  /**
   * Get a shout from the user.
   */
  public Shout getShout() {
    InteractiveTraderDialog dialog = new InteractiveTraderDialog(this);
    boolean ok = dialog.waitForInput();
    if ( ok ) {
      return dialog.getShout();
    } else {
      return null;
    }
  }

  public int determineQuantity( Auction auction ) {
    return 1;
  }

  public void auctionOpen( Auction auction ) {
  }

  public void auctionClosed( Auction auction ) {
  }

  public void roundClosed( Auction auction ) {
  }

  public boolean active() {
    return true;
  }

  public String toString() {
    return "(InteractiveTraderAgent stock: " + stock + " funds: " + funds + " id:" + id + ")";
  }


}
