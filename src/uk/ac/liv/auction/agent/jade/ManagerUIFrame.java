package uk.ac.liv.auction.agent.jade;

import java.awt.*;
import javax.swing.*;
import java.beans.*;
import javax.swing.event.*;
import java.awt.event.*;

public class ManagerUIFrame extends JFrame {

  protected AuctionManager manager;

  private JButton start = new JButton();

  public ManagerUIFrame( AuctionManager manager ) throws HeadlessException {
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
    this.getContentPane().add(start, BorderLayout.CENTER);
  }

  void start_actionPerformed(ActionEvent e) {
    manager.startAuction();
  }
}