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

package uk.ac.liv.auction.ui;

import JSci.swing.JLineGraph;
import JSci.swing.JGraphLayout;
import JSci.awt.Graph2DModel;
import JSci.awt.DefaultGraph2DModel;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.SupplyAndDemandStats;

import uk.ac.liv.util.io.MemoryResidentDataSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.log4j.Logger;


/**
 * A frame containing a graph of the supply and demand curves for the
 * specified auction.
 *
 * @author Steve Phelps
 */
public class SupplyAndDemandFrame extends JFrame {

  protected RoundRobinAuction auction;

  protected JButton updateButton;

  protected JLineGraph graph;

  public static final String TITLE = "Supply and Demand Graph";

  static Logger logger = Logger.getLogger(SupplyAndDemandFrame.class);

  public SupplyAndDemandFrame( RoundRobinAuction auction ) {

    this.auction = auction;
    Container contentPane = getContentPane();
    BorderLayout layout = new BorderLayout();
    contentPane.setLayout(layout);

    Graph2DModel supplyAndDemand = constructSupplyAndDemandModel();
    graph = new JLineGraph(supplyAndDemand);
    JPanel graphPanel = new JPanel(new JGraphLayout());
    graphPanel.add(graph, "Graph");
    graphPanel.add(new JLabel("Quantity"), "X-axis");
    graphPanel.add(new JLabel("Price"), "Y-axis");

    graphPanel.setPreferredSize(new Dimension(400,400));

    contentPane.add(graphPanel, BorderLayout.CENTER);

    updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateGraph();
      }
    }
    );
    contentPane.add(updateButton, BorderLayout.SOUTH);

    updateTitle();
  }

  public void updateGraph() {
    graph.setModel(constructSupplyAndDemandModel());
    updateTitle();
  }

  public void updateTitle() {
    setTitle(TITLE + " for " + auction.getName() + " at time " +
              auction.getAge());
  }

  protected Graph2DModel constructSupplyAndDemandModel() {
    MemoryResidentDataSeries supplyCurve = new MemoryResidentDataSeries();
    MemoryResidentDataSeries demandCurve = new MemoryResidentDataSeries();
    SupplyAndDemandStats stats =
        new SupplyAndDemandStats(auction, supplyCurve, demandCurve);
    stats.calculate();
    stats.generateReport();
    DefaultGraph2DModel model = new DefaultGraph2DModel();
    model.addSeries(supplyCurve);
    model.addSeries(demandCurve);
    return model;
  }

}
