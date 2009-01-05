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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A rectangular shaped DrawableItem.
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see DrawableItem
 */

public abstract class RectItem extends DrawableItem {

  // FIX (cjw): If the item has a border, the original calcSize method
  // would adjust its size on every recalculation.  If allowResizing is
  // false, this would result in the item ballooning larger with every
  // update.  This fix just adds a new state variable so we only adjust
  // for the border size once.
  protected boolean recalcBorder = true;

  /**
   * Calculates the true size of this object including the border width.
   */
  public void calcSize(SimGraphics g) {
    super.calcSize(g);

    // FIX (cjw): see above
    if (borderWidth != 0 && recalc && recalcBorder) {
      Rectangle2D e = new Rectangle2D.Float((int)getX(), (int)getY(),
					    width, height);
      Shape s = stroke.createStrokedShape(e);
      Rectangle r = s.getBounds();
      width = (int)r.getWidth();
      height = (int)r.getHeight();

      // FIX (cjw): see above
      recalcBorder = false;
    }
  }

  /**
   * Draws this item using the specified context.
   */
  public void draw(SimGraphics g) {
    calcSize (g);
    if (hollow) {
      if (label != null) {
        g.setFont (font);
        g.drawStringInHollowRect (color, labelColor, label);
      } else g.drawHollowFastRect (color);

    } else {
      if (label != null) {
        g.setFont (font);
        g.drawStringInRect (color, labelColor, label);
      } else g.drawFastRect (color);
    }

    if (borderWidth != 0 && !borderColor.equals(color)) {
      g.drawRectBorder(stroke, borderColor);
    }
  }

  /**
   * Does this RectItem contain Point p?
   *
   * @param p the point to test for
   * @return true if this does contain p, false otherwise
   */
  public boolean contains(Point p) {
    Rectangle2D rect = new Rectangle((int)getX(), (int)getY(), width, height);
    return rect.contains(p);
  }
}
