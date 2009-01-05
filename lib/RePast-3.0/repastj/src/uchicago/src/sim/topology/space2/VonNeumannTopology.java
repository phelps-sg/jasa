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
 * Time: 1:08:59 PM
 * To change this template use Options | File Templates.
 */
public class VonNeumannTopology extends AbstractDiscrete2DTopology{

  public static String type = "VON_NEUMANN";

  public VonNeumannTopology(Discrete2DSpace g){
    super(g);
    torus = g instanceof Torus;
  }

  public List getRelations(int x, int y, int[] extents,
                           boolean returnNulls) {
    if (extents.length != 2)
      throw new IllegalArgumentException("Von Neumann neighborhoods " +
                                         "require an extents array of 2 integers");
    int xExtent = extents[0];
    int yExtent = extents[1];
    Vector v = new Vector((xExtent * 2) + (yExtent * 2));
    int xLeft = xExtent;
    int xRight = xExtent;

    if (!torus) {
      if (x - xLeft < 0)
        xLeft = x;
      if (x + xRight > space.getSizeX() - 1)
        xRight = space.getSizeX() - 1 - x;
    }
    for (int i = x - xLeft; i < x; i++) {
      Object o = space.getObjectAt(i, y);

      if (returnNulls)
        v.add(o);
      else if (o != null)
        v.add(o);
    }

    for (int i = x + xRight; i > x; i--) {
      Object o = space.getObjectAt(i, y);

      if (returnNulls)
        v.add(o);
      else if (o != null)
        v.add(o);
    }
    int yTop = yExtent;
    int yBottom = yExtent;

    if (!torus) {
      if (y + yBottom > space.getSizeY() - 1)
        yBottom = space.getSizeY() - 1 - y;

      if (y - yTop < 0)
        yTop = y;
    }

    for (int i = y - yTop; i < y; i++) {
      Object o = space.getObjectAt(x, i);

      if (returnNulls)
        v.add(o);
      else if (o != null)
        v.add(o);
    }

    for (int i = y + yBottom; i > y; i--) {
      Object o = space.getObjectAt(x, i);

      if (returnNulls)
        v.add(o);
      else if (o != null)
        v.add(o);
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
    Location loc = null;
    if(!(element instanceof Location)){
      loc = space.getLocation(element);
    }else{
      loc = (Location) element;
    }
    //System.out.println(loc);
    return this.getRelations(loc.getX(), loc.getY(), new int[]{1,1}, false);
  }

  /**
   * Gets all of the Objects within a given range.
   * @param element
   * @param range
   * @return
   */
  public List getRelations(Object element, double range) {
    Location loc = space.getLocation(element);
    return getRelations(loc.getX(), loc.getY(),
                        new int[]{(int)range,(int)range}, false);
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

  /**
   * Gets the distance between two objects in this topology.  This could be
   * either spatial distance, network distance or any other kind of well defined
   * metric distance.
   *
   * This implementation returns distance as number of neighbors separating element1
   * and element2.
   *
   * @param element1
   * @param element2
   * @return
   */
  public double distance(Object element1, Object element2) {
    Location loc1 = space.getLocation(element1);
    Location loc2 = space.getLocation(element2);
    int dx = Math.abs(loc1.getX() - loc2.getX());
    int dy = Math.abs(loc1.getY() - loc2.getY());
    return dx + dy;
  }

  public void setRelationType(String type) {
    VonNeumannTopology.type = type;
  }
}
