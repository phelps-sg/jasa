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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;


/**
 * A frame containing a graph of the supply and demand curves for the
 * specified auction.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public abstract class SupplyAndDemandFrame extends JFrame implements AuctionEventListener {

  protected RoundRobinAuction auction;

  protected RepastPlot graph;
  
  protected DataSeriesWriter supplyCurve;
  
  protected DataSeriesWriter demandCurve;
  
  protected JButton updateButton;

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
    updateButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent event ) {
        updateGraph();
      }
    });
    contentPane.add(updateButton, BorderLayout.SOUTH);

    updateTitle();

    pack();
  }
  

  public void updateGraph() {
    graph.clear(0);
    graph.clear(1);
    plotSupplyAndDemand();
    updateTitle();
  }

  public void updateTitle() {
    setTitle(getGraphName() + " for " + auction.getName() + " at time " +
              auction.getRound());
  }
  
  public void open() {
    final SupplyAndDemandFrame thisFrame = this;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        //auction.addAuctionEventListener(thisFrame);
        pack();
        setVisible(true);
      }
    });
  }
  
  public void close() {
    final SupplyAndDemandFrame thisFrame = this;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        //auction.removeAuctionEventListener(thisFrame);
        setVisible(false);
      }
    });
  }
  
  public abstract String getGraphName();
  
  public abstract SupplyAndDemandStats getSupplyAndDemandStats();

  protected void plotSupplyAndDemand() {
    supplyCurve = new DataSeriesWriter();
    demandCurve = new DataSeriesWriter();
    SupplyAndDemandStats stats = getSupplyAndDemandStats();
    stats.calculate();
    stats.produceUserOutput();
    plotCurve(0, supplyCurve);
    plotCurve(1, demandCurve);
  }

  protected void plotCurve( int seriesIndex, DataSeriesWriter curve ) {
    for( int i=0; i<curve.length(); i++ ) {
      graph.addPoint(seriesIndex, curve.getXCoord(i), curve.getYCoord(i), true);
    }
  }
}
