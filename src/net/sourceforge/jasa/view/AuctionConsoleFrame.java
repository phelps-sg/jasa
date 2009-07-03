/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2009 Steve Phelps
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

package net.sourceforge.jasa.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import net.sourceforge.jasa.market.Market;
import net.sourceforge.jasa.market.MarketQuote;
import net.sourceforge.jasa.market.Order;
import net.sourceforge.jasa.market.MarketFacade;
import net.sourceforge.jasa.market.ShoutsNotVisibleException;
import net.sourceforge.jasa.sim.util.Resetable;

import org.apache.log4j.Logger;


/**
 * A frame for monitoring and controlling the progress of an market.
 * 
 * @author Steve Phelps
 * @version $Revision$
 */
public class AuctionConsoleFrame extends JFrame implements Observer, Resetable {

	protected MarketFacade auction;

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

	protected DecimalFormat currencyFormatter = new DecimalFormat(
	    "+000000.00;-000000.00");

	protected DecimalFormat decimalFormatter = new DecimalFormat(
	    " #########;-#########");

	protected GridBagLayout gridBag;

	protected int currentRound = 0;

	static Logger logger = Logger.getLogger(AuctionConsoleFrame.class);

	public AuctionConsoleFrame(MarketFacade auction, String name) {

		this.auction = auction;
		Container contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());

		JPanel statsPanel = new JPanel();
		gridBag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		statsPanel.setLayout(gridBag);

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.ipady = 0;
		c.ipadx = 0;
		c.insets = new Insets(5, 5, 5, 5);

		JLabel bidTextLabel = new JLabel("Bid: ");
		c.gridx = 0;
		c.gridy = 1;
		gridBag.setConstraints(bidTextLabel, c);
		statsPanel.add(bidTextLabel);

		bidLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		gridBag.setConstraints(bidLabel, c);
		bidLabel.setFont(decimalFont);
		statsPanel.add(bidLabel);

		JLabel askTextLabel = new JLabel("Ask: ");
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0;
		gridBag.setConstraints(askTextLabel, c);
		statsPanel.add(askTextLabel);

		askLabel = new JLabel();
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 1;
		gridBag.setConstraints(askLabel, c);
		askLabel.setFont(decimalFont);
		statsPanel.add(askLabel);

		JLabel lastShoutTextLabel = new JLabel("Last Shout: ");
		c.gridx = 0;
		c.gridy = 2;
		gridBag.setConstraints(lastShoutTextLabel, c);
		statsPanel.add(lastShoutTextLabel);

		lastShoutLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		gridBag.setConstraints(lastShoutLabel, c);
		lastShoutLabel.setFont(decimalFont);
		statsPanel.add(lastShoutLabel);

		JLabel numTradersTextLabel = new JLabel("Number of traders: ");
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		gridBag.setConstraints(numTradersTextLabel, c);
		statsPanel.add(numTradersTextLabel);

		numTradersLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		gridBag.setConstraints(numTradersLabel, c);
		numTradersLabel.setFont(decimalFont);
		statsPanel.add(numTradersLabel);

		JLabel roundTextLabel = new JLabel("Round: ");
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		gridBag.setConstraints(roundTextLabel, c);
		statsPanel.add(roundTextLabel);

		roundLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 1;
		gridBag.setConstraints(roundLabel, c);
		roundLabel.setFont(decimalFont);
		statsPanel.add(roundLabel);

		JLabel dayTextLabel = new JLabel("Day: ");
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 0;
		gridBag.setConstraints(dayTextLabel, c);
		statsPanel.add(dayTextLabel);

		dayLabel = new JLabel();
		c.gridx = 3;
		c.gridy = 4;
		c.weightx = 0;
		gridBag.setConstraints(dayLabel, c);
		dayLabel.setFont(decimalFont);
		statsPanel.add(dayLabel);

		contentPane.add(statsPanel, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();

		JButton logAuctionStatusButton = new JButton("Dump");
		logAuctionStatusButton
		    .setToolTipText("Display the current state of the market");
		controlPanel.add(logAuctionStatusButton);
		logAuctionStatusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logAuctionStatus();
			}
		});

		closeAuctionButton = new JButton("Close");
		closeAuctionButton.setToolTipText("Close the market");

		controlPanel.add(closeAuctionButton);
		closeAuctionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeAuction();
			}
		});
		contentPane.add(controlPanel, BorderLayout.SOUTH);

		setAuctionName(name);
		setJMenuBar(menuBar = new AuctionConsoleMenu());

		rootPane.setPreferredSize(new Dimension(480, 180));
	}

	public void setAuctionName(String name) {
		setTitle("Auction Console for " + name);
	}

	/**
	 * Close the market.
	 */
	public void closeAuction() {
		logger.debug("closeAuction()");
		auction.close();
	}

	/**
	 * Log the status of the market.
	 */
	public void logAuctionStatus() {
		auction.printState();
	}

	public void updatePriceLabel(JLabel label, double price) {
		if (Double.isInfinite(price)) {
			label.setText("      OPEN");
		} else {
			label.setText(currencyFormatter.format(price / 100));
		}
	}

	public void update(Observable o, Object arg) {
		logger.debug("update(" + o + ", " + arg + ")");

		Market auction = (Market) o;
		logger.debug("round = " + auction.getRound());
		MarketQuote quote = auction.getQuote();
		currencyFormatter.setMaximumIntegerDigits(6);
		if (quote != null) {
			updatePriceLabel(bidLabel, quote.getBid());
			updatePriceLabel(askLabel, quote.getAsk());
		}
		Order lastShout = null;
		try {
			lastShout = auction.getLastOrder();
		} catch (ShoutsNotVisibleException e) {
			lastShout = null;
		}
		if (lastShout != null) {
			double lastPrice = lastShout.getPrice();
			if (!lastShout.isBid()) {
				lastPrice = -lastPrice;
			}
			lastShoutLabel.setText(currencyFormatter
			    .format(((double) lastPrice) / 100));
		}
		roundLabel.setText(decimalFormatter.format(auction.getRound()));
		dayLabel.setText(decimalFormatter.format(auction.getDay()));
		numTradersLabel.setText(decimalFormatter.format(auction
		    .getNumberOfTraders()));

		logger.debug("update() complete");
	}

	/**
	 * Activate the frame by popping it up.
	 */
	public void activate() {
		pack();
		setVisible(true);
	}

	/**
	 * Close the frame.
	 */
	public void deactivate() {
		setVisible(false);
	}

	public void generateReport() {
		new Thread() {
			public void run() {
//				auction.generateReport();
				//TODO
			}
		}.start();
	}

	public void reset() {
		// TODO
	}

	/**
	 * @author Steve Phelps
	 * @version $Revision$
	 */
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
				public void actionPerformed(ActionEvent event) {
					toggleTrueSupplyAndDemand();
				}
			};
			viewTrueSupplyAndDemand.addActionListener(viewListener);
			viewMenu.add(viewTrueSupplyAndDemand);

			viewReportedSupplyAndDemand = new JCheckBoxMenuItem(
			    "Reported Supply and Demand");
			viewListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					toggleReportedSupplyAndDemand();
				}
			};
			viewReportedSupplyAndDemand.addActionListener(viewListener);
			viewMenu.add(viewReportedSupplyAndDemand);

			viewAuctionState = new JCheckBoxMenuItem("Auction State");
			viewListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					toggleAuctionState();
				}
			};
			viewAuctionState.addActionListener(viewListener);
			viewMenu.add(viewAuctionState);

			add(viewMenu);
		}

		public void toggleTrueSupplyAndDemand() {
			if (trueSupDemGraph == null) {
				trueSupDemGraph = new TrueSupplyAndDemandFrame(
				    (MarketFacade) auction);

				ComponentListener listener = new ComponentListener() {

					public void componentHidden(ComponentEvent e) {
						viewTrueSupplyAndDemand.setSelected(false);
						trueSupDemGraph = null;
					}

					public void componentMoved(ComponentEvent e) {
					}

					public void componentResized(ComponentEvent e) {
					}

					public void componentShown(ComponentEvent e) {
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
			if (auctionStateGraph == null) {
				auctionStateGraph = new AuctionStateFrame((MarketFacade) auction);

				ComponentListener listener = new ComponentListener() {

					public void componentHidden(ComponentEvent e) {
						viewAuctionState.setSelected(false);
						auctionStateGraph = null;
					}

					public void componentMoved(ComponentEvent e) {
					}

					public void componentResized(ComponentEvent e) {
					}

					public void componentShown(ComponentEvent e) {
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
			if (reportedSupDemGraph == null) {
				reportedSupDemGraph = new ReportedSupplyAndDemandFrame(
				    (MarketFacade) auction);

				ComponentListener listener = new ComponentListener() {

					public void componentHidden(ComponentEvent e) {
						viewReportedSupplyAndDemand.setSelected(false);
						reportedSupDemGraph = null;
					}

					public void componentMoved(ComponentEvent e) {
					}

					public void componentResized(ComponentEvent e) {
					}

					public void componentShown(ComponentEvent e) {
					}
				};
				reportedSupDemGraph.addComponentListener(listener);
				reportedSupDemGraph.open();
				viewReportedSupplyAndDemand.setSelected(true);
			} else {
				reportedSupDemGraph.close();
			}
		}

	}
}
