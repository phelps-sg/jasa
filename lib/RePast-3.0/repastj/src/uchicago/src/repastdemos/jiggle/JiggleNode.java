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
package uchicago.src.repastdemos.jiggle;

import java.util.ArrayList;

import uchicago.src.sim.gui.NetworkDrawable;
import uchicago.src.sim.network.DefaultDrawableNode;
import uchicago.src.sim.util.Random;

/**
 * The agent class for this model. This agent does not have any
 * particularly interesting behavoir. It just jiggles (see below).
 * This JiggleNode extends DefaultDrawable meaning that it is
 * both a DefaultNode and can be Drawable in a Network2DDisplay.
 */
public class JiggleNode extends DefaultDrawableNode {
  
  private int xSize, ySize;

  /**
   * No argument constructor so that this JiggleNode can be loaded from a
   * file. Because of this we also need the init statement.
   */
  public JiggleNode() {}

  /**
   * This is used when we create the JiggleNodes in the model rather than
   * from a file.
   */
  public JiggleNode(int xSize, int ySize, NetworkDrawable drawable) {
    super(drawable);
    this.xSize = xSize;
    this.ySize = ySize;
  }

  /**
   * This initializes the JiggleNode when we have create it from a file
   * using the no arg constructor, but still need to initialize it. This
   * sort of initialization would normally be done in the cosntructor
   * but sometimes it can be.
   */
  public void init(int xSize, int ySize, NetworkDrawable drawable) {
    super.setDrawable(drawable);
    this.xSize = xSize;
    this.ySize = ySize;
  }

  /**
   * This defines the actual behavoir of the JiggleNode, the Jiggle.
   */
  public void jiggle() {
    ArrayList outEdges = getOutEdges();
    double x = getX();
    double y = getY();
    if (outEdges.size() != 0) {
      int index = Random.uniform.nextIntFromTo(0, outEdges.size() - 1);
      JiggleEdge edge = (JiggleEdge)outEdges.get(index);
      JiggleNode agent = (JiggleNode)edge.getTo();
      double otherX = agent.getX();
      double otherY = agent.getY();

      // do containment from center of wrapper not top left corner
      double cX = x + getWidth() / 2;
      double cY = y + getHeight() / 2;


      if (!agent.contains(new java.awt.Point((int)cX, (int)cY))) {
        // move closer to other agent
        if (otherX > x) {
          x += 2;
        } else if (otherX < x) {
          x -= 2;
        }

        if (x < 0 || x > xSize) {
          x = xSize / 2;
          y = ySize / 2;
        }

        if (otherY > y) {
          y += 2;
        } else if (otherY < y) {
          y -= 2;
        }

        if (y < 0 || y > ySize) {
          y = ySize / 2;
          x = xSize / 2;
        }

      } else {
        int width = getWidth();
        int height = getHeight();
        int p = Random.uniform.nextIntFromTo(1, 2);
        x += (p == 1 ? -width : width);
        p = Random.uniform.nextIntFromTo(1, 2);
        y += (p == 1 ? -height : height);
	
      }

      setX(x);
      setY(y);
    }
  }
}
