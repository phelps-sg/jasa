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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorChooserPopup extends JPopupMenu {

  //private ColorSelectionModel model;
  ColorSwatchPanel panel;

  public ColorChooserPopup(Color selectedColor) {
    this();
    panel.setSelectedColor(selectedColor);
  }

  public ColorChooserPopup() {
    panel = new ColorSwatchPanel(this);
    add(panel);
    pack();
  }

  public Color getColor() {
    return panel.getSelectedColor();
  }

  public void setColor(Color c) {
    panel.setSelectedColor(c);
  }

  public void addColorChangeListener(ChangeListener listener) {
    panel.addColorChangeListener(listener);
  }


  class PopupListener extends MouseAdapter {
    public void mouseReleased(MouseEvent evt) {
      setVisible(false);
    }
  }

  /*
  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });
    f.getContentPane().setLayout(new BorderLayout());
    f.getContentPane().add(new ColorSwatchPanel(), BorderLayout.CENTER);
    f.pack();
    f.setVisible(true);
  }
  */

}

class ColorSwatchPanel extends JPanel {

  private Color selectedColor = Color.black;
  private Color[] colors;
  private Dimension swatchSize = new Dimension(12, 12);
  private Dimension swatchesDim = new Dimension(6, 6);
  private Dimension gap = new Dimension(1, 1);
  private ColorChooserPopup popup;

  private ArrayList listeners = new ArrayList();

  public ColorSwatchPanel(ColorChooserPopup popup) {
    initColors();
    setBackground(Color.gray);
    setOpaque(true);
    setRequestFocusEnabled(false);
    this.popup = popup;

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent evt) {
        selectedColor = getColor(evt.getX(), evt.getY());
        fireColorChange();
      }
    });
  }

  public boolean isFocusTraversable() {
    return false;
  }

  public Dimension getPreferredSize() {
    int width = swatchesDim.width * (swatchSize.width + gap.width);
    int height = swatchesDim.height * (swatchSize.height + gap.height);
    return new Dimension(width, height);
  }

  public Color getSelectedColor() {
    return selectedColor;
  }

  public void setSelectedColor(Color color) {
    selectedColor = color;
  }

  private void fireColorChange() {
    ArrayList list;
    synchronized (listeners) {
      list = (ArrayList) listeners.clone();
    }

    for (int i = 0; i < list.size(); i++) {
      ChangeListener listener = (ChangeListener) list.get(i);
      listener.stateChanged(new ChangeEvent(popup));
    }
  }

  private Color getColor(int x, int y) {
    int row = y / (swatchSize.height + gap.height);
    int col = x / (swatchSize.width + gap.width);
    return getColorForCell(row, col);
  }

  public void addColorChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }

  public void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    for (int row = 0; row < swatchesDim.height; row++) {
      for (int col = 0; col < swatchesDim.width; col++) {
        g.setColor(getColorForCell(row, col));
        int x = col * (swatchSize.width + gap.width);
        int y = row * (swatchSize.height + gap.height);
        g.fill3DRect(x, y, swatchSize.width, swatchSize.height, true);
      }
    }
  }

  private Color getColorForCell(int row, int col) {
    return colors[row * swatchesDim.width + col];
  }


  private void initColors() {
    int[] rawValues = getRawValues();
    int numColors = rawValues.length / 3;
    colors = new Color[numColors];
    for (int i = 0; i < numColors; i++) {
      colors[i] = new Color(rawValues[i * 3], rawValues[(i * 3) + 1],
                            rawValues[i * 3 + 2]);
    }
  }

  private int[] getRawValues() {
    int[] rawValues = {
      255, 255, 255,
      192, 192, 192,
      128, 128, 128,
      64, 64, 64,
      0, 0, 0,
      255, 0, 0,
      100, 100, 100,
      255, 175, 175,
      255, 200, 0,
      255, 255, 0,
      0, 255, 0,
      255, 0, 255,
      0, 255, 255,
      0, 0, 255,
      47, 79, 79,
      105, 105, 105,
      245, 255, 250,
      240, 248, 255,
      175, 238, 238,
      64, 224, 208,
      0, 100, 0,
      173, 255, 47,
      255, 215, 0,
      188, 143, 143,
      255, 255, 224,
      210, 105, 30,
      255, 165, 0,
      148, 12, 211,
      160, 100, 240,
      255, 239, 219,
      139, 121, 94,
      131, 111, 255,
      238, 106, 80,
      255, 69, 0,
      139, 10, 80,
      205, 181, 205,
    };

    return rawValues;
  }
}
