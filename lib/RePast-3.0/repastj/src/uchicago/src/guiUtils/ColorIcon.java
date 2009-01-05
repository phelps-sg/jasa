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
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorIcon implements Icon {

  protected static final int DEFAULT_SIZE = 32;
  protected static final int BORDER_SIZE = 2;
  
  protected Color color, shadowColor;
  protected int width, height, borderSize, fillHeight, fillWidth;

  public ColorIcon(Color color, int width, int height, int borderSize) {
    this.color = color;
    this.width = width;
    this.height = height;
    this.borderSize = borderSize;
    shadowColor = Color.black;
    fillHeight = height - 2 * borderSize;
    fillWidth = width - 2 * borderSize;
  }

  public ColorIcon(Color color, int size) {
    this(color, size, size, BORDER_SIZE);
  }

  public ColorIcon(Color color) {
    this(color, DEFAULT_SIZE, DEFAULT_SIZE, BORDER_SIZE);
  }

  public void setColor(Color c) {
    color = c;
  }

  // Icon interface 
  public int getIconHeight() {
    return height;
  }
  
  public int getIconWidth() {
    return width;
  }
  
  public void paintIcon(Component comp, Graphics g, int x, int y) {
    Color c = g.getColor();
    if (borderSize > 0) {
      g.setColor(shadowColor);
      for (int i = 0; i < borderSize; i++) {
	g.drawRect(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1);
      }
    }

    g.setColor(color);
    g.fillRect(x + borderSize, y + borderSize, fillWidth, fillHeight);
    g.setColor(c);
  }
}
