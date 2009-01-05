/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.guiUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A Wizard style dialog with back, next, finish, and cancel buttons.
 */

public abstract class Wizard extends JPanel {

  protected JButton btnNext = new JButton("Next");
  protected JButton btnBack = new JButton("Back");
  protected JButton btnCancel = new JButton("Cancel");
  protected JPanel top;
  protected JDialog dialog;
  protected Dimension mySize = null;
  protected boolean exitOnClose = false;
  protected boolean cancel = false;

  protected Stack panels = new Stack();

  public Wizard() {
    this(false);
  }

  public Wizard(boolean exitOnClose) {
    this.exitOnClose = exitOnClose;
    guiInit();
    setCommands();
  }

  private void setCommands() {
    btnNext.setActionCommand("next");
    btnBack.setActionCommand("back");
    btnCancel.setActionCommand("cancel");
  }

  protected void guiInit() {
    setLayout(new BorderLayout());
    if (top != null)
      add(top, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel left = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    left.add(btnBack);
    left.add(btnNext);
    right.add(btnCancel);
    bottom.add(left);
    bottom.add(right);

    add(bottom, BorderLayout.SOUTH);

    btnNext.setMnemonic('n');
    btnBack.setMnemonic('b');
    btnCancel.setMnemonic('c');
  }

  public void setTopPanel(JPanel topPanel) {
    if (top != null) {
      this.remove(top);
      top = null;
    }
    top = topPanel;
    add(top, BorderLayout.CENTER);
    invalidate();
    validate();
    repaint();
  }

  public void display(JFrame owner, String title, boolean centerOnOwner) {
    if (owner == null) {
      owner = new JFrame();
    }
    dialog = new JDialog(owner, true);
    dialog.setTitle(title);
    java.awt.Container c = dialog.getContentPane();
    c.setLayout(new java.awt.BorderLayout());
    c.add(this, java.awt.BorderLayout.CENTER);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        cancel = true;
        close();
      }
    });

    if (mySize == null) {
      dialog.pack();
      //System.out.println(f.getSize().width + ", " + f.getSize().height);
    } else {
      dialog.setSize(mySize);
    }
    //GuiUtilities.centerComponentOnComponent(dialog, owner);
    if (centerOnOwner) {
      dialog.setLocationRelativeTo(owner);
    } else {
      GuiUtilities.centerComponentOnScreen(dialog);
    }
    dialog.setVisible(true);
  }

  public void display(JDialog owner, String title, boolean centerOnOwner) {
    if (owner == null) {
      owner = new JDialog();
    }
    dialog = new JDialog(owner, true);
    dialog.setTitle(title);
    java.awt.Container c = dialog.getContentPane();
    c.setLayout(new java.awt.BorderLayout());
    c.add(this, java.awt.BorderLayout.CENTER);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        cancel = true;
        close();
      }
    });

    if (mySize == null) {
      dialog.pack();
      //System.out.println(f.getSize().width + ", " + f.getSize().height);
    } else {
      dialog.setSize(mySize);
    }
    //GuiUtilities.centerComponentOnComponent(dialog, owner);
    if (centerOnOwner) {
      dialog.setLocationRelativeTo(owner);
    } else {
      GuiUtilities.centerComponentOnScreen(dialog);
    }
    dialog.setVisible(true);
  }

  public void setSize(int width, int height) {
    mySize = new Dimension(width, height);
  }

  public void close() {
    //System.out.println(dialog.getSize());
    if (dialog != null) {
      dialog.dispose();
    }
    if (exitOnClose) {
      System.exit(0);
    }


  }

   public void setNextEnabled(boolean enabled) {
    btnNext.setEnabled(enabled);
  }

  public void setBackEnabled(boolean enabled) {
    btnBack.setEnabled(enabled);
  }

  public void setNextToFinish() {
    btnNext.setText("Finished");
  }
}