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
import JSci.awt.Graph2DModel;
import JSci.awt.DefaultGraph2DModel;
import JSci.swing.JGraphLayout;

import uk.ac.liv.auction.core.*;
import uk.ac.liv.auction.agent.AbstractTraderAgent;

import uk.ac.liv.auction.stats.GraphMarketDataLogger;

import uk.ac.liv.util.Resetable;

import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;
import java.util.LinkedList;

import java.text.DecimalFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.log4j.Logger;


/**
 * A frame for monitoring and controlling the progress of an auction.
 *
 * @author Steve Phelps
 * @version $Revision$
 */
public class AuctionConsoleFrame extends JFrame
    implements Observer, Resetable {

  protected RoundRobinAuction auction;

  protected JLabel bidLabel;
  protected JLabel askLabel;
  protected JLabel lastShoutLabel;
  protected JLabel roundLabel;
  protected JLabel numTradersLabel;

  protected JButton closeAuctionButton;
  protected JButton supplyAndDemandButton;
  protected JButton rerunAuctionButton;
  protected JButton reportButton;
  protected JButton resumeButton;
  protected JButton pauseButton;
  protected JButton resetAgentsButton;

  protected JGraphLayout graphLayout;
  protected JLineGraph graph;
  protected JPanel graphPanel;
  protected float graphXExtrema = 0f;
  protected GraphMarketDataLogger graphModel;

  protected DecimalFormat currencyFormatter =
      new DecimalFormat("+000000.00;-000000.00");

  protected DecimalFormat decimalFormatter =
      new DecimalFormat(" #########;-#########");

  protected GridBagLayout gridBag;

  protected int currentRound = 0;


  protected LinkedList graphs = new LinkedList();

  protected Timer graphUpdateTimer = null;

  private Thread auctionRunner;

  static Logger logger = Logger.getLogger(AuctionConsoleFrame.class);

  public AuctionConsoleFrame( RoundRobinAuction auction, String name ) {

    this.auction = auction;
    Container contentPane = getContentPane();
    gridBag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    contentPane.setLayout(gridBag);

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    c.ipady = 0;
    c.ipadx = 0;
    c.insets = new Insets(5,5,5,5);

    JLabel bidTextLabel = new JLabel("Bid: ");
    c.gridx = 0;
    c.gridy = 1;
    gridBag.setConstraints(bidTextLabel, c);
    contentPane.add(bidTextLabel);

    bidLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    gridBag.setConstraints(bidLabel, c);
    contentPane.add(bidLabel);

    JLabel askTextLabel = new JLabel("Ask: ");
    c.gridx = 2;
    c.gridy = 1;
    c.weightx = 0;
    gridBag.setConstraints(askTextLabel, c);
    contentPane.add(askTextLabel);

    askLabel = new JLabel();
    c.gridx = 3;
    c.gridy = 1;
    c.weightx = 1;
    gridBag.setConstraints(askLabel, c);
    contentPane.add(askLabel);

    JLabel lastShoutTextLabel = new JLabel("Last Shout: ");
    c.gridx = 0;
    c.gridy = 2;
    gridBag.setConstraints(lastShoutTextLabel, c);
    contentPane.add(lastShoutTextLabel);

    lastShoutLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 1;
    gridBag.setConstraints(lastShoutLabel, c);
    contentPane.add(lastShoutLabel);

    JLabel numTradersTextLabel = new JLabel("Number of traders: ");
    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 0;
    gridBag.setConstraints(numTradersTextLabel, c);
    contentPane.add(numTradersTextLabel);

    numTradersLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 3;
    c.weightx = 1;
    gridBag.setConstraints(numTradersLabel, c);
    contentPane.add(numTradersLabel);

    JLabel roundTextLabel = new JLabel("Round: ");
    c.gridx = 0;
    c.gridy = 4;
    c.weightx = 0;
    gridBag.setConstraints(roundTextLabel, c);
    contentPane.add(roundTextLabel);

    roundLabel = new JLabel();
    c.gridx = 1;
    c.gridy = 4;
    c.weightx = 1;
    gridBag.setConstraints(roundLabel, c);
    contentPane.add(roundLabel);

    closeAuctionButton = new JButton("Stop");
    closeAuctionButton.setToolTipText("Close the auction");
    c.gridx = 1;
    c.gridy = 5;
    gridBag.setConstraints(closeAuctionButton, c);
    contentPane.add(closeAuctionButton);
    closeAuctionButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          closeAuction();
        }
    });


    rerunAuctionButton = new JButton("Rerun");
    rerunAuctionButton.setToolTipText("Run the auction from the beginning");
    c.gridx = 2;
    c.gridy = 5;
    c.ipadx = 0;
    c.ipady = 0;
    c.gridwidth = 1;
    gridBag.setConstraints(rerunAuctionButton, c);
    contentPane.add(rerunAuctionButton);
    rerunAuctionButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          rerunAuction();
        }
    });


    JButton logAuctionStatusButton = new JButton("Dump");
    logAuctionStatusButton.setToolTipText("Display the current state of the auction");
    c.gridx = 3;
    c.gridy = 5;
    c.weightx = 0;
    gridBag.setConstraints(logAuctionStatusButton, c);
    contentPane.add(logAuctionStatusButton);
    logAuctionStatusButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          logAuctionStatus();
        }
    });

    JButton reportButton = new JButton("Report");
    reportButton.setToolTipText("Generate a report on the auction");
    c.gridx = 1;
    c.gridy = 6;
    c.weightx = 0;
    gridBag.setConstraints(reportButton, c);
    contentPane.add(reportButton);
    reportButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          generateReport();
        }
    });


    JButton supplyAndDemandButton = new JButton("S/D");
    supplyAndDemandButton.setToolTipText("Draw a graph of supply and demand");
    c.gridx = 2;
    c.gridy = 6;
    c.weightx = 0;
    gridBag.setConstraints(supplyAndDemandButton, c);
    contentPane.add(supplyAndDemandButton);
    supplyAndDemandButton.addActionListener(new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        graphSupplyAndDemand();
      }
    });

   pauseButton = new JButton("Pause");
    c.gridx = 1;
    c.gridy = 7;
    gridBag.setConstraints(pauseButton, c);
    contentPane.add(pauseButton);
    pauseButton.addActionListener(new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        pause();
      }
    });

    resumeButton = new JButton("Resume");
    resumeButton.setToolTipText("Resume the auction");
    resumeButton.setEnabled(false);
    c.gridx = 2;
    c.gridy = 7;
    gridBag.setConstraints(resumeButton, c);
    contentPane.add(resumeButton);
    resumeButton.addActionListener(new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        resume();
      }
    });

    JButton resetAgentsButton = new JButton("Reset");
    resetAgentsButton.setToolTipText("Reset all agents");
    c.gridx = 3;
    c.gridy = 7;
    gridBag.setConstraints(resetAgentsButton, c);
    contentPane.add(resetAgentsButton);
    resetAgentsButton.addActionListener(new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        resetAgents();
      }
    });

    if ( (graphModel = GraphMarketDataLogger.getSingletonInstance()) != null ) {
      graphPanel = new JPanel(graphLayout = new JGraphLayout());
      graph = new JLineGraph(graphModel);
      graphPanel.add(graph, "Graph");
      c.gridx = 0;
      c.gridy = 0;
      c.gridwidth = 8;
      c.gridheight = 1;
      c.weightx = 1;
      c.weighty = 1;
      c.fill = c.BOTH;
      graphPanel.setPreferredSize(new Dimension(600,200));
      gridBag.setConstraints(graphPanel, c);
      contentPane.add(graphPanel);
      graph.setYExtrema(0f, 0f, 0f);
      graph.setXExtrema(0f, 500f, 500f);
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
    logger.debug("closeAuction()");
    auction.close();
  }

  /**
   *  Log the status of the auction.
   */
  public void logAuctionStatus() {
    auction.printState();
  }

  public void update( Observable o, Object arg ) {
    logger.debug("update(" + o + ", " + arg + ")");

    Auction auction = (Auction) o;
    logger.debug("round = " + auction.getAge());
    MarketQuote quote = auction.getQuote();
    currencyFormatter.setMaximumIntegerDigits(6);
    if ( quote != null ) {
      bidLabel.setText(currencyFormatter.format(((double)quote.getBid())/100));
      askLabel.setText(currencyFormatter.format(((double) quote.getAsk())/100));
    }
    Shout lastShout = null;
    try {
      lastShout = auction.getLastShout();
    } catch ( ShoutsNotVisibleException e ) {
      lastShout = null;
    }
    if ( lastShout != null ) {
      double lastPrice = lastShout.getPrice();
      if ( !lastShout.isBid() ) {
        lastPrice = -lastPrice;
      }
      lastShoutLabel.setText(currencyFormatter.format(((double)lastPrice)/100));
    }
    roundLabel.setText(decimalFormatter.format(auction.getAge()));
    numTradersLabel.setText(decimalFormatter.format(auction.getNumberOfTraders()));

    if ( graphModel != null && auction.getAge() != currentRound) {
      currentRound = auction.getAge();
//      if ( currentRound > 1 && currentRound % 500 == 0 ) {
//        graphXExtrema += 500f;
//        graph.setXExtrema(0, graphXExtrema);
//      }
      notifyGraphModelChanged();
    }
    logger.debug("update() complete");
  }

  public void graphSupplyAndDemand() {
    logger.debug("graphSupplyAndDemand()");
    SupplyAndDemandFrame graphFrame =
        new SupplyAndDemandFrame((RoundRobinAuction) auction);
    graphFrame.pack();
    graphFrame.setVisible(true);
    graphs.add(graphFrame);
    logger.debug("exiting GraphSupplyAndDemand()");
  }

  public void pause() {
    logger.debug("pause()");
    try {
      logger.debug("Invoking auction.pause()..");
      auction.pause();
      while (!auction.isPaused()) {
        //wait for auction to be paused
//        logger.debug("waiting for auction.isPaused()");
      }
      pauseButton.setEnabled(false);
      resumeButton.setEnabled(true);
      logger.debug("exiting pause()");
    } catch ( AuctionClosedException e ) {
      logger.warn(e);
    }
  }

  public void resume() {
    auction.resume();
    pauseButton.setEnabled(true);
    resumeButton.setEnabled(false);
  }

  public void resetAgents() {
    pause();
    Iterator i = auction.getTraderIterator();
    while (i.hasNext()) {
      AbstractTraderAgent agent = (AbstractTraderAgent) i.next();
      agent.reset();
    }
    updateSupplyAndDemandGraphs();
  }

  public void updateSupplyAndDemandGraphs() {
    Iterator i = graphs.iterator();
    while ( i.hasNext() ) {
      SupplyAndDemandFrame gFrame = (SupplyAndDemandFrame) i.next();
      gFrame.updateGraph();
    }
  }

  /**
   *  Activate the frame by popping it up.
   */
  public void activate() {
    pack();
    setVisible(true);
    if ( graphModel != null ) {
//      startGraphUpdateTimer();
    }
  }

  protected void startGraphUpdateTimer() {
    ActionListener graphUpdateListener = new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        graphModel.dataUpdated();
      }
    };
    graphUpdateTimer = new Timer(1000, graphUpdateListener);
    graphUpdateTimer.start();
  }

  /**
   *  Close the frame.
   */
  public void deactivate() {
    setVisible(false);
    if ( graphUpdateTimer != null ) {
//      graphUpdateTimer.stop();
    }
  }

  public void rerunAuction() {
    logger.debug("rerunAuction()");
    resume();
    auction.close();
    while ( auction.isRunning() ) {
      // Wait until the auction has finished running
    }
    reset();
    auction.reset();
    updateSupplyAndDemandGraphs();
    auctionRunner = new Thread(auction);
    auctionRunner.start();
  }

  public void generateReport() {
    pause();
    auction.generateReport();
  }

  public void reset() {
    graphModel.clear();
  }

  protected void notifyGraphModelChanged() {
    logger.debug("notifyGraphModelChanged()");
//    try {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          graphModel.dataUpdated();
        }
      });
//      Thread.currentThread().sleep(1);
//    }
//    catch (InterruptedException e) {
//      logger.warn(e);
//      e.printStackTrace();
//    }
//    catch (java.lang.reflect.InvocationTargetException e) {
//      logger.warn(e);
//      e.printStackTrace();
//    }
    logger.debug("exiting notifyGraphModelChanged()");
  }
}
