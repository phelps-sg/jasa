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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A Display class that can be used to display lines of text on a
 * DisplaySurface. The text is optionally displayed enclosed in a
 * rectangle. The text can remain static or can be changed by calling
 * clearLines or clearLine and then adding new lines.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class TextDisplay extends Display implements Probeable {

  protected Color textColor;
  protected boolean drawBox = true;
  protected ArrayList text = new ArrayList();
  protected String header = null;
  protected Font curFont = new Font("monospace", Font.PLAIN, 12);
  protected Box box;

  private Moveable mbox = new Moveable() {
    public void setX(int x) {
      TextDisplay.this.setX(x);
    }

    public void setY(int y) {
      TextDisplay.this.setY(y);
    }
  };

  public static class Box {
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;

    public boolean contains(int xc, int yc) {
      if (xc >= x && xc <= x + width) {
        if (yc >= y && yc <= y + height) {
          return true;
        }
      }

      return false;
    }
  }

  /**
   * Constructs a TextDisplay to display text at the specified
   * coordinates in the specified color. If the text is to be
   * drawing inside a box (see <code>setBoxVisible()</code>, x and y
   * mark the top left corner of this box. If the text is not drawn
   * inside a box then x and y mark the position of the baseline of
   * left-most character in the first string to be drawn.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param textColor the color to draw the text and box
   */
  public TextDisplay(int x, int y, Color textColor) {
    this(0, 0, x, y, textColor);
  }

  /**
   * Constructs a TextDisplay to display text at the specified
   * coordinates in the specified color. If the TextDisplay is
   * the first Display added to a DisplaySurface then width and
   * height must be specified. If the text is to be
   * drawing inside a box (see <code>setBoxVisible()</code>, x and y
   * mark the top left corner of this box. If the text is not drawn
   * inside a box then x and y mark the position of the baseline of
   * left-most character in the first string to be drawn.
   *
   * @param width the width of the display
   * @param height the height of the display
   * @param x the x coordinate
   * @param y the y coordinate
   * @param textColor the color to draw the text and box
   */
  public TextDisplay(int width, int height, int x, int y, Color textColor) {
    super(width, height);
    this.textColor = textColor;
    box = new Box();
    box.x = x;
    box.y = y;
  }

  /**
   * Sets whether the text should be drawn enclosed in a retangle.
   */
  public void setBoxVisible(boolean val) {
    drawBox = val;
  }

  /**
   * Sets an optional header for the displayed text.
   *
   * @param header the header
   */
  public void setHeader(String header) {
    this.header = header;
  }

  /**
   * Adds a line of text to be displayed. Lines will be displayed in
   * the order they are added. First added will be displayed first.
   * Multiple lines can be added by separating the lines with the "\n"
   * character.
   *
   * @param val the line to add
   */
  public void addLine(String val) {
    if (val.indexOf("\n") > 0) {
      StringTokenizer t = new StringTokenizer(val, "\n");
      while (t.hasMoreTokens()) {
        text.add(t.nextToken());
      }
    } else {
      text.add(val);
    }
  }

  /**
   * Adds a line of text at the specified index (relative to the other
   * lines. Lines will be displayed in the order they are added. First
   * added will be displayed first. Multiple lines can be added by separating
   * the lines with the "\n" character.
   *
   *
   * @param val the line to add
   * @param index where to add the line relative to other lines
   */
  public void addLine(String val, int index) {
    int i = 0;
    if (val.indexOf("\n") > 0) {
      StringTokenizer t = new StringTokenizer(val, "\n");
      while (t.hasMoreTokens()) {
        text.add(index + i, t.nextToken());
        i++;
      }
    } else {
      text.add(index, val);
    }
  }

  /**
   * Clears the list of lines to be displayed.
   */
  public void clearLines() {
    text.clear();
  }

  /**
   * Removes the line at the specified index.
   *
   * @param index the index of the line to remove
   */
  public void clearLine(int index) {
    text.remove(index);
  }

  /**
   * Sets the color of the text and box.
   *
   * @param c the color of the text and box
   */
  public void setColor(Color c) {
    textColor = c;
  }

  /**
   * Sets the font size of the text.
   *
   * @param val the font size
   */
  public void setFontSize(int val) {
    curFont = new Font("monospace", Font.PLAIN, val);
  }

  /**
   * Gets the current font size.
   */
  public int getFontSize() {
    return curFont.getSize();
  }

  /**
   * Sets the coordinates for displaying the lines of text.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void setTextCoordinates(int x, int y) {
    box.x = x;
    box.y = y;
  }

  /**
   * Gets the x coordinate for displaying text.
   */
  public int getX() {
    return box.x;
  }

  /**
   * Sets the x coordinate for displaying text.
   *
   * @param x the x coordinate
   */
  public void setX(int x) {
    this.box.x = x;
  }

  /**
   * Gets the y coordinate for displaying text.
   */
  public int getY() {
    return box.y;
  }

  /**
   * Sets the Y coordinate for displaying text.
   *
   * @param y the y coordinate
   */
  public void setY(int y) {
    this.box.y = y;
  }

  /**
   * Returns the Box that bounds the displayed text in this
   * TextDisplay.
   *
   * @return
   */
  public Box getBoundingBox() {
    return box;
  }


  /**
   * Does the actual drawing using the SimGraphics parameter.
   *
   * @param g the graphics context with which to draw
   */
  public void drawDisplay(SimGraphics g) {
    if (view) {
      Graphics2D graphics = g.getGraphics();
      Font oldFont = graphics.getFont();
      Color oldColor = graphics.getColor();
      graphics.setFont(curFont);
      graphics.setColor(textColor);
      FontMetrics fMetrics = graphics.getFontMetrics();

      int maxwidth = 0;
      int ypos, xpos;
      int spacing = fMetrics.getHeight();
      if (drawBox) {
        xpos = box.x + 6;
        ypos = box.y + spacing;
      } else {
        xpos = box.x;
        ypos = box.y;
      }

      int height = 0;

      if (header != null) {
        int width = fMetrics.stringWidth(header);
        maxwidth = width;
        graphics.drawString(header, xpos, ypos);
        ypos += spacing + 4;
        height = spacing + 4;
      }

      for (int i = 0; i < text.size(); i++) {
        String s = (String) text.get(i);
        int width = fMetrics.stringWidth(s);
        if (width > maxwidth) maxwidth = width;
        graphics.drawString(s, xpos, ypos);
        ypos += spacing;
        height += spacing;
      }

      if (drawBox) {
        graphics.drawRect(box.x, box.y, maxwidth + 12, height + 6);
        box.width = maxwidth + 12;
        box.height = height + 6;
      } else {
        box.width = maxwidth;
        box.height = height;
      }

      graphics.setFont(oldFont);
      graphics.setColor(oldColor);
    }
  }


  /**
   * Gets a list of the DisplayInfo object associated with this Display.
   */
  public ArrayList getDisplayableInfo() {
    ArrayList list = new ArrayList();
    list.add(new DisplayInfo("", TOGGLE_VIEW, this));
    return list;
  }

  /**
   * Invoked when a viewEvent for this display is fired by the
   * DisplaySurface.
   */
  public void viewEventPerformed(ViewEvent evt) {
    view = evt.showView();
  }

  // Probeable interface
  public ArrayList getObjectsAt(int x, int y) {
    ArrayList l = new ArrayList();
    if (box.contains(x, y)) {
      //System.out.println("Box contains");
      l.add(mbox);
    }
    return l;
  }

  /**
   * Sets the new coordinates for specified moveable. This goes through
   * probeable as some translation between screen pixel coordinates and
   * the simulation coordinates may be necessary.
   *
   * @param moveable the moveable whose coordinates are changed
   * @param x the x coordinate in pixels
   * @param y the y coordinate in pixels
   */
  public void setMoveableXY(Moveable moveable, int x, int y) {
    moveable.setX(x);
    moveable.setY(y);
  }
}
