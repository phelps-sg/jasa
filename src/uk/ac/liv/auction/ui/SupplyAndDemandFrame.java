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

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.stats.SupplyAndDemandStats;

import uchicago.src.sim.analysis.plot.*;

import uk.ac.liv.util.io.DataSeriesWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.log4j.Logger;


/**
 * A frame containing a graph of the supply and demand curves for the
 * specified auction.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class SupplyAndDemandFrame extends JFrame {

  protected RoundRobinAuction auction;

  protected JButton updateButton;

  protected RepastPlot graph;

  public static final String TITLE = "Supply and Demand Graph";

  static Logger logger = Logger.getLogger(SupplyAndDemandFrame.class);

  public SupplyAndDemandFrame( RoundRobinAuction auction ) {

    this.auction = auction;
    Container contentPane = getContentPane();
    BorderLayout layout = new BorderLayout();
    contentPane.setLayout(layout);

    graph = new RepastPlot(null);
    plotSupplyAndDemand();
    
    contentPane.add(graph, BorderLayout.CENTER);

    updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateGraph();
      }
    }
    );
    contentPane.add(updateButton, BorderLayout.SOUTH);

    updateTitle();

    pack();
  }

  public void updateGraph() {
    graph.clearPoints();
    plotSupplyAndDemand();
    updateTitle();
  }

  public void updateTitle() {
    setTitle(TITLE + " for " + auction.getName() + " at time " +
              auction.getRound());
  }

  protected void plotSupplyAndDemand() {
    DataSeriesWriter supplyCurve = new DataSeriesWriter();
    DataSeriesWriter demandCurve = new DataSeriesWriter();
    SupplyAndDemandStats stats =
        new SupplyAndDemandStats(auction, supplyCurve, demandCurve);
    stats.calculate();
    stats.generateReport();
    plotCurve(0, supplyCurve);
    plotCurve(1, demandCurve);
  }

  protected void plotCurve( int seriesIndex, DataSeriesWriter curve ) {
    for( int i=0; i<curve.length(); i++ ) {
      graph.addPoint(seriesIndex, curve.getXCoord(i), curve.getYCoord(i), true);
    }
  }
}
