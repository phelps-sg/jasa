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

package uk.ac.liv.auction.agent.jade;

import java.awt.*;
import javax.swing.*;
import java.beans.*;
import javax.swing.event.*;
import java.awt.event.*;

import uk.ac.liv.auction.ui.AuctionConsoleFrame;
import uk.ac.liv.auction.core.RoundRobinAuction;

import org.apache.log4j.Logger;


/**
 *  An AuctionConsoleFrame extended with a button to start the auction.
 *
 *  @author Steve Phelps
 */
public class ManagerUIFrame extends AuctionConsoleFrame {

  protected AuctionManager manager;

  protected JButton startButton;

  static Logger logger = Logger.getLogger(ManagerUIFrame.class);


  public ManagerUIFrame( AuctionManager manager, RoundRobinAuction auction )  {
    super(auction, "JADE auction");
    this.manager = manager;

    GridBagConstraints c = new GridBagConstraints();
    Container contentPane = getContentPane();

    startButton = new JButton("Start");
    c.gridx = 0;
    c.gridy = 5;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    c.ipadx = 0;
    c.ipady = 0;
    c.gridwidth = 1;
    c.insets = new Insets(20,20,20,20);
    gridBag.setConstraints(startButton, c);

    contentPane.add(startButton);
    startButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          startAuction();
        }
    });

  }

  void startAuction() {
    logger.debug("Starting auction..");
    manager.startAuction();
  }

}