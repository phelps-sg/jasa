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
package uchicago.src.sim.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import uchicago.src.sim.engine.Controller;


public class Legend {

  public static final int SQUARE = 0;
  public static final int CIRCLE = 1;

  private ArrayList legends = new ArrayList();
  private JFrame frame;
  private String title = "";
  private JPanel p;

  public Legend(String title) {
    this.title = title;
  }

  public void addLegend(String label, int iconType, Color color, boolean hollow) {
    addLegend(label, iconType, color, hollow, 16, 16);
  }

  public void addLegend(String label, int iconType, Color color, boolean hollow,
                        int iconWidth, int iconHeight)
  {
    LegendIcon icon = null;
    switch (iconType) {
      case SQUARE:
        icon = new SquareIcon(iconWidth, iconHeight, color, hollow);
        break;
      case CIRCLE:
        icon = new CircleIcon(iconWidth, iconHeight, color, hollow);
        break;
      default:
        throw new IllegalArgumentException("Unrecognized legend label type");
    }

    JLabel jl = new JLabel(label, icon, SwingConstants.LEFT);
    jl.setForeground(Color.black);
    jl.setIconTextGap(16);

    legends.add(jl);
  }

  private void layout() {
    GridLayout g = new GridLayout(legends.size(), 1);
    g.setVgap(5);
    p = new JPanel(g);
    for (int i = 0; i < legends.size(); i++) {
      p.add((JLabel)legends.get(i));
    }
  }

  public void display() {
    if (frame == null) {
      frame = new JFrame(title);
      frame.setIconImage(new ImageIcon(
            Controller.class.getResource(
            "/uchicago/src/sim/images/RepastSmall.gif")).getImage());
      layout();
      Container c = frame.getContentPane();
      c.setLayout(new BorderLayout());
      c.add(p, BorderLayout.CENTER);
    }

    frame.pack();
    frame.setVisible(true);


  }

  public void dispose() {
    frame.dispose();
  }

  /*
  public static void main(String[] args) {
    Legend l = new Legend("Test");
    l.addLegend("Test", Legend.SQUARE, Color.red, false);
    l.addLegend("Another Test", CIRCLE, Color.blue, true);
    l.display();
  }
  */
}


