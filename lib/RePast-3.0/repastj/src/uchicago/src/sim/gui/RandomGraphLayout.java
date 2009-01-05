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

import uchicago.src.sim.util.Random;

public class RandomGraphLayout extends AbstractGraphLayout {

  // number of pixels to shrink radius by. Java draws object from top
  // left hand corner and this allows objects drawn on the far right to
  // be visible.
  private int pad = 4;

  public RandomGraphLayout(int width, int height) {
    super(width, height);
  }

  public RandomGraphLayout(List nodes, int width, int height) {
    super(nodes, width, height);
  }

  /**
   * Sets the number of pixels to shrink radius by. Java draws object from top
   * left hand corner and this allows objects drawn on the far right to
   * be visible.
   */
  public void setPad(int p) {
    pad = p;
  }

  public void updateLayout() {
    if (update) {
      int n = nodeList.size();
      for (int i = 0; i < n; i++) {
	DrawableNonGridNode node = (DrawableNonGridNode)nodeList.get(i);
	node.setX(Random.uniform.nextIntFromTo(0, width - pad));
	node.setY(Random.uniform.nextIntFromTo(0, height));
      }
    }
  }
}
