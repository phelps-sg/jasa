/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2002 Steve Phelps
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

package uk.ac.liv.auction.agent;

import uk.ac.liv.auction.core.Shout;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * <p>
 * A dialog for specifying auction shouts.
 * </p>
 *
 * @author Steve Phelps
 */

public class InteractiveTraderDialog extends JDialog {

  /**
   * The trader who owns this auction shout.
   */
  TraderAgent trader;

  /**
   * The price per item for this shout.
   */
  double amount;

  /**
   * The quantity for this shout.
   */
  int quantity;

  /**
   * Whether this shout is a bid (true) or an ask (false).
   */
  boolean isBid = true;

  /**
   * False if the user aborted the shout dialog.
   */
  boolean ok = false;

  JTextField amountField, quantityField;
  JButton okButton, cancelButton;
  JRadioButton bidButton, askButton;


  public InteractiveTraderDialog( TraderAgent trader ) {

    super();

    this.trader = trader;

    Container contentPane = getContentPane();
    GridBagLayout gridBag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    contentPane.setLayout(gridBag);

    bidButton = new JRadioButton("Bid");
    askButton = new JRadioButton("Ask");
    bidButton.setSelected(true);

    ButtonGroup bidAskGroup = new ButtonGroup();
    bidAskGroup.add(bidButton);
    bidAskGroup.add(askButton);

    amountField = new JTextField("");
    amountField.setColumns(10);
    amountField.setHorizontalAlignment(JTextField.RIGHT);

    quantityField = new JTextField("1");
    quantityField.setColumns(5);
    quantityField.setHorizontalAlignment(JTextField.RIGHT);

    JLabel amountLabel = new JLabel("Bid Amount");
    JLabel quantityLabel = new JLabel("Quantity");
    JLabel questionLabel = new JLabel(trader + ": Shout?");

    okButton = new JButton("OK");
    cancelButton = new JButton("Cancel");

    quantityField.setNextFocusableComponent(bidButton);
    amountField.setNextFocusableComponent(quantityField);
    bidButton.setNextFocusableComponent(askButton);
    askButton.setNextFocusableComponent(okButton);
    okButton.setNextFocusableComponent(cancelButton);

    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.EAST;
    c.insets = new Insets(20,20,20,20);

    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    gridBag.setConstraints(questionLabel, c);
    contentPane.add(questionLabel);

    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 1;
    gridBag.setConstraints(amountLabel, c);
    contentPane.add(amountLabel);

    c.gridx = 1;
    c.gridy = 1;
    gridBag.setConstraints(amountField, c);
    contentPane.add(amountField);

    c.gridx = 0;
    c.gridy = 2;
    gridBag.setConstraints(quantityLabel, c);
    contentPane.add(quantityLabel);

    c.gridx = 1;
    c.gridy = 2;
    gridBag.setConstraints(quantityField, c);
    contentPane.add(quantityField);

    c.gridx = 1;
    c.gridy = 3;
    gridBag.setConstraints(bidButton, c);
    contentPane.add(bidButton);

    c.gridx = 2;
    c.gridy = 3;
    gridBag.setConstraints(askButton, c);
    contentPane.add(askButton);

    c.gridx = 1;
    c.gridy = 4;
    gridBag.setConstraints(okButton, c);
    contentPane.add(okButton);

    c.gridx = 2;
    c.gridy = 4;
    gridBag.setConstraints(cancelButton, c);
    contentPane.add(cancelButton);

    setTitle(trader.toString());
    pack();

    okButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          handleInput(true);
        }
    });

    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          handleInput(false);
        }
    });

    addWindowListener( new WindowAdapter() {
        public void windowClosing( WindowEvent e ) {
          handleInput(false);
        }

    });

    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    okButton.setDefaultCapable(true);

    this.getRootPane().setDefaultButton(okButton);
    amountField.requestFocus();


  }

  public synchronized boolean waitForInput() {
    setVisible(true);
    try {
      wait();
    } catch ( InterruptedException e ) {
      e.printStackTrace();
    }
    return ok;
  }

  public Shout getShout() {
    return new Shout(trader, quantity, amount, isBid);
  }

  public synchronized void handleInput( boolean ok) {
    try {
      if ( ok ) {
        amount = new Double(amountField.getText()).doubleValue();
        quantity = new Integer(quantityField.getText()).intValue();
        this.ok = ok;
        isBid = bidButton.isSelected();
      }
      setVisible(false);

      notifyAll();
    } catch ( NumberFormatException e ) {
    }
  }





}

