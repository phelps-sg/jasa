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
package uchicago.src.sim.topology.space2;

import java.util.List;
import java.util.Vector;

import uchicago.src.sim.space.Torus;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 16, 2003
 * Time: 4:41:53 PM
 * To change this template use Options | File Templates.
 */
public class MooreTopology extends AbstractDiscrete2DTopology{
  public static String type = "MOORE";


  public MooreTopology(Discrete2DSpace space) {
    super(space);
    torus = space instanceof Torus;
  }

  public List getRelations(int x, int y, int[] extents,
                           boolean returnNulls) {
    if (extents.length != 2)
      throw new IllegalArgumentException("Moore neighborhoods require an extents array of 2 integers");
    int xExtent = extents[0];
    int yExtent = extents[1];
    Vector v = new Vector(xExtent * yExtent * 4 + (xExtent * 2) + (yExtent * 2));
    int xLeft = xExtent;
    int xRight = xExtent;

    if (!torus) {
      if (x + xRight > space.getSizeX() - 1)
        xRight = space.getSizeX() - 1 - x;
      if (x - xLeft < 0)
        xLeft = x;
    }
    int yTop = yExtent;
    int yBottom = yExtent;

    if (!torus) {
      if (y + yBottom > space.getSizeY() - 1)
        yBottom = space.getSizeY() - 1 - y;

      if (y - yTop < 0)
        yTop = y;
    }
    for (int j = y - yTop; j <= y + yBottom; j++) {
      for (int i = x - xLeft; i <= x + xRight; i++) {
        if (!(j == y && i == x)) {
          Object o = space.getObjectAt(i, j);

          if (returnNulls)
            v.add(o);
          else if (o != null) {
            v.add(o);
          }
        }
      }
    }
    return v;


  }

  /**
   * Get all of the relationships that the given element has
   * with other elements.
   * @param element
   * @return
   */
  public List getRelations(Object element) {
    Location loc = space.getLocation(element);
    return getRelations(loc.getX(), loc.getY(), new int[]{1,1}, false);
  }

  /**
   * Get the type of relationship/topology that is represented by this
   * Class.  For example, if this represents a VonNeumann topology, this
   * should return the String "VON_NEUMANN".
   *
   * @return
   */
  public String getRelationType() {
    return type;
  }

  public void setRelationType(String type) {
    MooreTopology.type = type;
  }

  /**
   * Gets all of the Objects within a given range.
   * @param element
   * @param range
   * @return
   */
  public List getRelations(Object element, double range) {
    Location loc = space.getLocation(element);
    return getRelations(loc.getX(), loc.getY(), new int[]{(int) range,(int) range},false);
  }

  /**
   * Gets the distance between two objects in this topology.  This could be
   * either spatial distance, network distance or any other kind of well defined
   * metric distance.
   *
   * @param element1
   * @param element2
   * @return
   */
  public double distance(Object element1, Object element2) {
    Location loc1 = space.getLocation(element1);
    Location loc2 = space.getLocation(element2);
    int x1 = loc1.getX();
    int x2 = loc2.getX();
    int y1 = loc1.getY();
    int y2 = loc2.getY();
    return lineBresenham(x1, y1, x2, y2);
  }

  private int lineBresenham(int x0, int y0, int x1, int y1){
    int dist = 0;
    int dy = y1 - y0;
    int dx = x1 - x0;
    int stepx, stepy;

    if (dy < 0) { dy = -dy;  stepy = -1; } else { stepy = 1; }
    if (dx < 0) { dx = -dx;  stepx = -1; } else { stepx = 1; }
    dy <<= 1;                                                  // dy is now 2*dy
    dx <<= 1;                                                  // dx is now 2*dx


    if (dx > dy) {
      int fraction = dy - (dx >> 1);                         // same as 2*dy - dx
      while (x0 != x1) {
        if (fraction >= 0) {
          y0 += stepy;
          fraction -= dx;                                // same as fraction -= 2*dx
        }
        x0 += stepx;
        fraction += dy;                                    // same as fraction -= 2*dy
        dist++;
      }
    } else {
      int fraction = dx - (dy >> 1);
      while (y0 != y1) {
        if (fraction >= 0) {
          x0 += stepx;
          fraction -= dy;
        }
        y0 += stepy;
        fraction += dx;
        dist++;
      }
    }
    return dist;
  }
}
