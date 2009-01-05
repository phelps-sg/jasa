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
package uchicago.src.sim.engine.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Panel for Repast Actions tab.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class RepastActionPanel extends JPanel {

  private int gridx = 0;
  private int gridy = 0;
  private GridBagConstraints c;
  private JPanel subPanel = new JPanel(new GridBagLayout());

  public RepastActionPanel() {
    super(new BorderLayout());
    guiInit();
  }

  private void guiInit() {
    add(subPanel, BorderLayout.NORTH);
    c = new GridBagConstraints();
    c.gridx = gridx;
    c.gridy = gridy;
    c.weightx = 1.0;
    c.weighty = 0.1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridheight = 1;
    c.anchor = GridBagConstraints.NORTH;
  }

  public void addButton(String label, ActionListener l) {
    JButton button = new JButton(label);
    button.addActionListener(l);
    c.gridy = gridy++;
    subPanel.add(button, c);
  }

  public void addButton(Action action) {
    JButton button = new JButton(action);
    c.gridy = gridy++;
    subPanel.add(button, c);
  }

  public void addCheckBox(String label, ActionListener l, boolean selected) {
    JCheckBox box = new JCheckBox(label, selected);
    box.addActionListener(l);
    c.gridy = gridy++;
    subPanel.add(box, c);
  }

 // public void repaint() {
  //  super.repaint();
 //   if (subPanel
 //   subPanel.revalidate();
  //}

  public void clear() {
    this.removeAll();
    gridx = 0;
    gridy = 0;
    c = null;
    guiInit();
  }
}
