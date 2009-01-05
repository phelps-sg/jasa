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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.util.Collections;

import javax.imageio.ImageIO;

import uchicago.src.sim.util.SimUtilities;

/**
 * Used by DisplaySurface to do the actual painting of displayables. A
 * typical user should never use this class directly.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class LocalPainter extends Painter {

  protected boolean firstDraw = true;
  protected BufferedImage origImage = null;
  protected int prevX, prevY, prevWidth, prevHeight;
  protected boolean eraseBox = false;

  /**
   * Creates a LocalPainter. Before this LocalPainter can be used
   * its init method must be called. This will done automatically by
   * the DisplaySurface if this LocalPainter is an argument to a DisplaySurface's
   * constructor.
   */
  public LocalPainter() {
    super();
  }

  /**
   * Creates a LocalPainter using the specified displaySurface, and with
   * the specified width and height.
   *
   * @param s the displaySurface associated with this LocalPainter
   * @param w the width of the painter
   * @param h the height of the painter
   */
  public LocalPainter(DisplaySurface s, int w, int h) {
    super(s, w, h);
  }

  public void eraseRect(final Graphics g) {
    if (eraseBox) {
      createGraphics2D();
      g2.setXORMode(bgColor);
      g2.setColor(surface.getForeground());
      g2.drawRect(prevX, prevY, prevWidth, prevHeight);
      g2.dispose();

      g.drawImage(buffImage, 0, 0, null);
      eraseBox = false;
      toolkit.sync();
    }
  }

  public void drawRect(final Graphics g, int x, int y, int rectWidth,
                       int rectHeight) {
    if (buffImage == null) {
      createBufferedImage();
    }

    createGraphics2D();
    g2.setXORMode(bgColor);
    g2.setColor(surface.getForeground());
    if (eraseBox) g2.drawRect(prevX, prevY, prevWidth, prevHeight);
    g2.drawRect(x, y, rectWidth, rectHeight);
    prevX = x;
    prevY = y;
    prevWidth = rectWidth;
    prevHeight = rectHeight;
    g2.dispose();

    g.drawImage(buffImage, 0, 0, null);
    eraseBox = true;
    toolkit.sync();
  }

  /**
   * Paints all the displayables on the screen in the order they were
   * added to the display surface
   */
  public void paint(final Graphics g) {

    if (buffImage == null) {
      createBufferedImage();
    }

    createGraphics2D();

    // if background is not painted every tick then
    // displaySurface view menu won't update correctly.
    paintBackground();

    simGraphics.setGraphics(g2);
    simGraphics.setDisplaySurface(surface);

    if (orderedDisplayables.size() > 0) {
      Collections.sort(orderedDisplayables);
      for (int j = 0; j < orderedDisplayables.size(); j++) {
        DisplaySurface.DisplayableOrder disO = (DisplaySurface.DisplayableOrder) orderedDisplayables.get(j);
        disO.getDisplayable().drawDisplay(simGraphics);
      }
    }

    for (int i = 0; i < displayables.size(); i++) {
      Displayable dis = (Displayable) displayables.get(i);
      dis.drawDisplay(simGraphics);
    }

    g2.dispose();
    g.drawImage(buffImage, 0, 0, null);
    eraseBox = false;
    toolkit.sync();
  }

  /**
   * Takes a snapshot of the current screen image and writes it to the
   * specified output stream
   *
   * @param os the OutputStream to write the snapshot to
   */
  public void takeSnapshot(DataOutputStream os) {
    try {
      ImageIO.write(buffImage, "png", os);
      //GifEncoder encoder = new GifEncoder(buffImage, os);
      //encoder.encode();
      //encoder = null;
    } catch (java.io.IOException ex) {
      SimUtilities.showError("Unable to write snapshot image to file", ex);
      //ex.printStackTrace();
    }
  }
}
