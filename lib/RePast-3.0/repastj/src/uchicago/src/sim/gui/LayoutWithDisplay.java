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

import java.util.List;

public abstract class LayoutWithDisplay extends AbstractGraphLayout {

  protected DisplaySurface display; // HACK (cjw): made protected
  protected boolean isEventThread = false;

  /**
   * Constructs a new LayoutWithDisplay.
   *
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */
  public LayoutWithDisplay(int width, int height) {
    super(width, height);
  }

  /**
   * Constructs new LayoutWithDisplay.
   *
   * @param nodes the list of nodes to be optimised
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */
  public LayoutWithDisplay(List nodes, int width, int height) {
    super(nodes, width, height);
  }
  
  /**
   * Constructs new LayoutWithDisplay.
   *
   * @param nodes the list of nodes to be optimised
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   * @param surface the display surface to update
   */
  public LayoutWithDisplay(List nodes, int width, int height,
			   DisplaySurface surface)
  {
    super(nodes, width, height);
    display = surface;
  }

  /**
   * Setting the display surface (or passing it with the constructor)
   * allows the graph layout to update the display to show the
   * algorithm's convergence within the current tick.
   *
   * @param surface the DisplaySurface to update
   */
  public void setDisplay(DisplaySurface surface) {
    display = surface;
  }

  protected void updateDisplay() {
    if (display != null) {
      if (display.isFrameVisible()) {
	if (isEventThread) display.updateDisplayDirect();
	else display.updateDisplay();
      }
    }
  }
}
