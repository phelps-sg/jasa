package uk.ac.liv.auction.core;

import java.util.Observable;
import java.util.Observer;

import java.text.DecimalFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class AuctionConsoleFrame extends JFrame implements Observer {

  Auction auction;

  JLabel bidLabel;
  JLabel askLabel;
  JLabel lastShoutLabel;
  JLabel roundLabel;
  JLabel numTradersLabel;
  JButton closeAuctionButton;

  DecimalFormat currencyFormatter = new DecimalFormat("+000000.00;-000000.00");
  DecimalFormat decimalFormatter = new DecimalFormat(" #########;-#########");

  public AuctionConsoleFrame( Auction auction, String name ) {

    this.auction = auction;

    Container contentPane = getContentPane();
    GridBagLayout gridBag = new GridBagLayout();
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

    setTitle("Auction Console for " + name);
  }

  public void closeAuction() {
    auction.close();
  }

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
  }

  public void activate() {
    pack();
    setVisible(true);
  }

  public void deactivate() {
    setVisible(false);
  }

}