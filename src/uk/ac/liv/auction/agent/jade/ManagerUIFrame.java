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

public class ManagerUIFrame extends JFrame {

  protected AuctionManager manager;

  private JButton start = new JButton();
  private JPanel jPanel1 = new JPanel();
  private FlowLayout flowLayout1 = new FlowLayout();

  public ManagerUIFrame( AuctionManager manager )  {
    this.manager = manager;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void jbInit() throws Exception {
    start.setActionCommand("startAuction");
    start.setText("Start");
    start.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        start_actionPerformed(e);
      }
    });
    jPanel1.setLayout(flowLayout1);
    this.setTitle("AuctionManager console");
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.setPreferredSize(new Dimension(300, 200));
    jPanel1.add(start, null);
  }

  void start_actionPerformed(ActionEvent e) {
    manager.startAuction();
  }
}