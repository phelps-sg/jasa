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

package uk.ac.liv.auction.core;

import JSci.swing.JLineGraph;
import JSci.awt.Graph2DModel;
import JSci.awt.DefaultGraph2DModel;

import uk.ac.liv.auction.stats.GraphMarketDataLogger;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import java.text.DecimalFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.log4j.Logger;


/**
 * A frame for monitoring and controlling the progress of an auction.
 *
 * @author Steve Phelps
 */
public class AuctionConsoleFrame extends JFrame
    implements Observer {

  protected Auction auction;

  protected JLabel bidLabel;
  protected JLabel askLabel;
  protected JLabel lastShoutLabel;
  protected JLabel roundLabel;
  protected JLabel numTradersLabel;
  protected JButton closeAuctionButton;

  protected DecimalFormat currencyFormatter =
      new DecimalFormat("+000000.00;-000000.00");

  protected DecimalFormat decimalFormatter =
      new DecimalFormat(" #########;-#########");

  protected GridBagLayout gridBag;

  protected int currentRound = 0;

  protected GraphMarketDataLogger graphModel;

  static Logger logger = Logger.getLogger(AuctionConsoleFrame.class);

  public AuctionConsoleFrame( Auction auction, String name ) {

    this.auction = auction;

    Container contentPane = getContentPane();
    gridBag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    contentPane.setLayout(gridBag);

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    c.ipady = 20;
    c.ipadx = 80;
    c.insets = new Insets(20,20,20,20);


    JLabel bidTextLabel = new JLabel("Bid: ");
    c.gridx = 0;
    c.gridy = 0;
    gridBag.setConstraints(bidTextLabel, c);
    contentPane.add(bidTextLabel);

    bidLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    gridBag.setConstraints(bidLabel, c);
    contentPane.add(bidLabel);

    JLabel askTextLabel = new JLabel("Ask: ");
    c.gridx = 2;
    c.gridy = 0;
    c.weightx = 0;
    gridBag.setConstraints(askTextLabel, c);
    contentPane.add(askTextLabel);

    askLabel = new JLabel();
    c.gridx = 3;
    c.gridy = 0;
    c.weightx = 1;
    gridBag.setConstraints(askLabel, c);
    contentPane.add(askLabel);

    JLabel lastShoutTextLabel = new JLabel("Last Shout: ");
    c.gridx = 0;
    c.gridy = 1;
    gridBag.setConstraints(lastShoutTextLabel, c);
    contentPane.add(lastShoutTextLabel);

    lastShoutLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    gridBag.setConstraints(lastShoutLabel, c);
    contentPane.add(lastShoutLabel);

    JLabel numTradersTextLabel = new JLabel("Number of traders: ");
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0;
    gridBag.setConstraints(numTradersTextLabel, c);
    contentPane.add(numTradersTextLabel);

    numTradersLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    gridBag.setConstraints(numTradersLabel, c);
    contentPane.add(numTradersLabel);

    JLabel roundTextLabel = new JLabel("Round: ");
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    gridBag.setConstraints(roundTextLabel, c);
    contentPane.add(roundTextLabel);

    roundLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 3;
    c.weightx = 1;
    gridBag.setConstraints(roundLabel, c);
    contentPane.add(roundLabel);

    closeAuctionButton = new JButton("Close Auction");
    c.gridx = 1;
    c.gridy = 4;
    c.ipadx = 0;
    c.ipady = 0;
    c.gridwidth = 1;
    gridBag.setConstraints(closeAuctionButton, c);
    contentPane.add(closeAuctionButton);
    closeAuctionButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          closeAuction();
        }
    });

    JButton logAuctionStatusButton = new JButton("Dump bids");
    c.gridx = 2;
    c.gridy = 4;
    gridBag.setConstraints(logAuctionStatusButton, c);
    contentPane.add(logAuctionStatusButton);
    logAuctionStatusButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          logAuctionStatus();
        }
    });

    if ( (graphModel = GraphMarketDataLogger.getSingletonInstance()) != null ) {
      JLineGraph graph = new JLineGraph(graphModel);
      c.gridx = 1;
      c.gridy = 5;
      gridBag.setConstraints(graph, c);
      contentPane.add(graph);
    }

    setAuctionName(name);
  }

  public void setAuctionName( String name ) {
    setTitle("Auction Console for " + name);
  }

  /**
   *  Close the auction.
   */
  public void closeAuction() {
    auction.close();
  }

  /**
   *  Log the status of the auction.
   */
  public void logAuctionStatus() {
    auction.printState();
  }

  public void update( Observable o, Object arg ) {
    Auction auction = (Auction) o;
    MarketQuote quote = auction.getQuote();
    currencyFormatter.setMaximumIntegerDigits(6);
    if ( quote != null ) {
      bidLabel.setText(currencyFormatter.format(((double)quote.getBid())/100));
      askLabel.setText(currencyFormatter.format(((double) quote.getAsk())/100));
    }
    Shout lastShout = auction.getLastShout();
    if ( lastShout != null ) {
      double lastPrice = lastShout.getPrice();
      if ( !lastShout.isBid() ) {
        lastPrice = -lastPrice;
      }
      lastShoutLabel.setText(currencyFormatter.format(((double)lastPrice)/100));
    }
    roundLabel.setText(decimalFormatter.format(auction.getAge()));
    numTradersLabel.setText(decimalFormatter.format(auction.getNumberOfTraders()));
    if ( graphModel != null ) {
      logger.debug("Notifying model of data change..");
      graphModel.fireDataChanged();
    }
  }

  /**
   *  Activate the frame by popping it up.
   */
  public void activate() {
    pack();
    setVisible(true);
  }

  /**
   *  Close the frame.
   */
  public void deactivate() {
    setVisible(false);
  }


}