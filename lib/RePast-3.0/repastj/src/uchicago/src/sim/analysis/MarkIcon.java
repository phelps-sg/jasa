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
package uchicago.src.sim.analysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Icon that represents plot point shape integer constants in
 * the appropriate shape. 
 *
 * @version $Revision$ $Date$
 */

public class MarkIcon implements Icon {

  private int size;
  private int type;

  public MarkIcon(int size) {
    this.size = size;
  }

  public void setType(int type) {
    this.type = type;
  }

  // Icon interface
  public int getIconHeight() {
    return size;
  }

  public int getIconWidth() {
    return size;
  }

  public void paintIcon(Component comp, Graphics graphics, int x, int y) {
    Color c = graphics.getColor();
    graphics.setColor(Color.black);
    int xpoints[], ypoints[];
    int radius = size / 2;

    switch (type) {
    case 0:
      // filled circle
      graphics.fillOval(x, y, size, size);
      break;
    case 1:
      // cross
      graphics.drawLine(x, y, x + size, y + size);
      graphics.drawLine(x, y + size, x + size, y);
      break;
    case 2:
      // square
      graphics.drawRect(x, y, size, size);
      break;
    case 3:
      // filled triangle
      xpoints = new int[4];
      ypoints = new int[4];
      xpoints[0] = x + radius; ypoints[0] = y;
      xpoints[1] = x; ypoints[1] = y + size;
      xpoints[2] = x + size; ypoints[2] = y + size;
      xpoints[3] = x + radius; ypoints[3] = y;
      graphics.fillPolygon(xpoints, ypoints, 4);
      break;
    case 4:
      // diamond
      xpoints = new int[5];
      ypoints = new int[5];
      xpoints[0] = x + radius; ypoints[0] = y;
      xpoints[1] = x; ypoints[1] = y + radius;
      xpoints[2] = x + radius; ypoints[2] = y + size;
      xpoints[3] = x + size; ypoints[3] = y + radius;
      xpoints[4] = x + radius; ypoints[4] = y;
      graphics.drawPolygon(xpoints, ypoints, 5);
      break;
    case 5:
      // circle
      graphics.drawOval(x, y, size, size);
      break;
    case 6:
      // plus sign
      graphics.drawLine(x + radius, y, x + radius, y + size);
      graphics.drawLine(x, y + radius, x + size, y + radius);
      break;
    case 7:
      // filled square
      graphics.fillRect(x, y, size, size);
      break;
    case 8:
      // triangle
      xpoints = new int[4];
      ypoints = new int[4];
      xpoints[0] = x + radius; ypoints[0] = y;
      xpoints[1] = x; ypoints[1] = y + size;
      xpoints[2] = x + size; ypoints[2] = y + size;
      xpoints[3] = x + radius; ypoints[3] = y;
      graphics.drawPolygon(xpoints, ypoints, 4);
      break;
    case 9:
      // filled diamond
      xpoints = new int[5];
      ypoints = new int[5];
      xpoints[0] = x + radius; ypoints[0] = y;
      xpoints[1] = x; ypoints[1] = y + radius;
      xpoints[2] = x + radius; ypoints[2] = y + size;
      xpoints[3] = x + size; ypoints[3] = y + radius;
      xpoints[4] = x + radius; ypoints[4] = y;
      graphics.fillPolygon(xpoints, ypoints, 5);
      break;
    }
    graphics.setColor(c);
  }
}
