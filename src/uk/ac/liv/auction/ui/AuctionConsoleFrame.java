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
import uk.ac.liv.auction.agent.AbstractTradingAgent;

import uk.ac.liv.util.Resetable;

import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;

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
  protected JLabel dayLabel;
  protected JLabel numTradersLabel;
  
  protected JMenuBar menuBar;
 
  protected JButton resetAgentsButton;
  protected JButton closeAuctionButton;
  protected float graphXExtrema = 0f;
 
  protected Font decimalFont = new Font("Monospaced", Font.TRUETYPE_FONT, 10);

  protected DecimalFormat currencyFormatter =
      new DecimalFormat("+000000.00;-000000.00");

  protected DecimalFormat decimalFormatter =
      new DecimalFormat(" #########;-#########");

  protected GridBagLayout gridBag;

  protected int currentRound = 0;

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
    bidLabel.setFont(decimalFont);
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
    askLabel.setFont(decimalFont);
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
    lastShoutLabel.setFont(decimalFont);
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
    numTradersLabel.setFont(decimalFont);
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
    roundLabel.setFont(decimalFont);
    contentPane.add(roundLabel);

    JLabel dayTextLabel = new JLabel("Day: ");
    c.gridx = 2;
    c.gridy = 4;
    c.weightx = 0;
    gridBag.setConstraints(dayTextLabel, c);
    contentPane.add(dayTextLabel);

    dayLabel = new JLabel();
    c.gridx = 3;
    c.gridy = 4;
    c.weightx = 0;
    gridBag.setConstraints(dayLabel, c);
    dayLabel.setFont(decimalFont);
    contentPane.add(dayLabel);


    JButton logAuctionStatusButton = new JButton("Dump");
    logAuctionStatusButton.setToolTipText("Display the current state of the auction");
    c.gridx = 2;
    c.gridy = 6;
    c.weightx = 0;
    gridBag.setConstraints(logAuctionStatusButton, c);
    contentPane.add(logAuctionStatusButton);
    logAuctionStatusButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          logAuctionStatus();
        }
    });

    JButton resetAgentsButton = new JButton("Reset");
    resetAgentsButton.setToolTipText("Reset all agents");
    c.gridx = 3;
    c.gridy = 6;
    gridBag.setConstraints(resetAgentsButton, c);
    contentPane.add(resetAgentsButton);
    resetAgentsButton.addActionListener(new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        resetAgents();
      }
    });

    closeAuctionButton = new JButton("Close");
    closeAuctionButton.setToolTipText("Close the auction");
    c.gridx = 4;
    c.gridy = 6;
    gridBag.setConstraints(closeAuctionButton, c);
    contentPane.add(closeAuctionButton);
    closeAuctionButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          closeAuction();
        }
    });
    
    setAuctionName(name);
    setJMenuBar(menuBar = new AuctionConsoleMenu());
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
    logger.debug("round = " + auction.getRound());
    MarketQuote quote = auction.getQuote();
    currencyFormatter.setMaximumIntegerDigits(6);
    if ( quote != null ) {
      bidLabel.setText(currencyFormatter.format(((double) quote.getBid())/100));
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
    roundLabel.setText(decimalFormatter.format(auction.getRound()));
    dayLabel.setText(decimalFormatter.format(auction.getDay()));
    numTradersLabel.setText(decimalFormatter.format(auction.getNumberOfTraders()));

    logger.debug("update() complete");
  }


  
  public void resetAgents() {
    new Thread() {
      public void run() {
        Iterator i = auction.getTraderIterator();
        while (i.hasNext()) {
          AbstractTradingAgent agent = (AbstractTradingAgent) i.next();
          agent.reset();
        }
      }
    }.start();
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


  public void generateReport() {
    new Thread() {
      public void run() {
        auction.generateReport();
      }
    }.start();
  }

  public void reset() {
    //TODO
  }

  
  class AuctionConsoleMenu extends JMenuBar {
    
    protected JCheckBoxMenuItem viewTrueSupplyAndDemand;    
    protected JCheckBoxMenuItem viewAuctionState;    
    protected JCheckBoxMenuItem viewReportedSupplyAndDemand;
    
    protected TrueSupplyAndDemandFrame trueSupDemGraph = null;
    protected ReportedSupplyAndDemandFrame reportedSupDemGraph = null;
    protected AuctionStateFrame auctionStateGraph = null;
    
    public AuctionConsoleMenu() {
      JMenu viewMenu = new JMenu("View");
      
      viewTrueSupplyAndDemand = new JCheckBoxMenuItem("True Supply and Demand");
      ActionListener viewListener = new ActionListener() {
        public void actionPerformed( ActionEvent event ) {
          toggleTrueSupplyAndDemand();
        }
      };
      viewTrueSupplyAndDemand.addActionListener(viewListener);
      viewMenu.add(viewTrueSupplyAndDemand);
      
      viewReportedSupplyAndDemand = new JCheckBoxMenuItem("Reported Supply and Demand");
      viewListener = new ActionListener() {
        public void actionPerformed( ActionEvent event ) {
          toggleReportedSupplyAndDemand();
        }
      };
      viewReportedSupplyAndDemand.addActionListener(viewListener);
      viewMenu.add(viewReportedSupplyAndDemand);
      
      viewAuctionState = new JCheckBoxMenuItem("Auction State");
      viewListener = new ActionListener() {
        public void actionPerformed( ActionEvent event ) {
          toggleAuctionState();
        }
      };
      viewAuctionState.addActionListener(viewListener);
      viewMenu.add(viewAuctionState);
      
      add(viewMenu);
    }
    
    
    
    public void toggleTrueSupplyAndDemand() {
      if ( trueSupDemGraph == null ) {
        trueSupDemGraph = new TrueSupplyAndDemandFrame(
            (RoundRobinAuction) auction);
        
        ComponentListener listener = new ComponentListener() {

          public void componentHidden( ComponentEvent e ) {
            viewTrueSupplyAndDemand.setSelected(false);
            trueSupDemGraph = null;
          }
          
          public void componentMoved( ComponentEvent e ) {
          }
          
          public void componentResized( ComponentEvent e ) {
          }
          
          public void componentShown( ComponentEvent e ) {
          }
        };
        trueSupDemGraph.addComponentListener(listener);
        trueSupDemGraph.open();
        viewTrueSupplyAndDemand.setSelected(true);
      } else {
        trueSupDemGraph.close();
      }
    }
    
    public void toggleAuctionState() {
      if ( auctionStateGraph == null ) {
        auctionStateGraph = new AuctionStateFrame(
            (RoundRobinAuction) auction);
        
        ComponentListener listener = new ComponentListener() {

          public void componentHidden( ComponentEvent e ) {
            viewAuctionState.setSelected(false);
            auctionStateGraph = null;
          }
          
          public void componentMoved( ComponentEvent e ) {
          }
          
          public void componentResized( ComponentEvent e ) {
          }
          
          public void componentShown( ComponentEvent e ) {
          }
        };
        
        auctionStateGraph.addComponentListener(listener);
        auctionStateGraph.open();
        viewAuctionState.setSelected(true);
      } else {
        auctionStateGraph.close();
      }
    }

    public void toggleReportedSupplyAndDemand() {
      if ( reportedSupDemGraph == null ) {
        reportedSupDemGraph = new ReportedSupplyAndDemandFrame(
            (RoundRobinAuction) auction);
        
        ComponentListener listener = new ComponentListener() {

          public void componentHidden( ComponentEvent e ) {
            viewReportedSupplyAndDemand.setSelected(false);
            reportedSupDemGraph = null;
          }
          
          public void componentMoved( ComponentEvent e ) {
          }
          
          public void componentResized( ComponentEvent e ) {
          }
          
          public void componentShown( ComponentEvent e ) {
          }
        };
        reportedSupDemGraph.addComponentListener(listener);
        reportedSupDemGraph.open();
        viewTrueSupplyAndDemand.setSelected(true);
      } else {
        reportedSupDemGraph.close();
      }
    }
    
    
  }
}
