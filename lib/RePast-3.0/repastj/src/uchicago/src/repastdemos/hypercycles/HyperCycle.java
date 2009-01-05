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
package uchicago.src.repastdemos.hypercycles;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A Hypercycle. Stores the cells and their location with respect to
 * one another.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class HyperCycle {

  Vector cells = new Vector(7);
  static final String NORTH = "N";
  static final String SOUTH = "S";
  static final String EAST = "E";
  static final String WEST = "W";
  Hashtable totals = new Hashtable();

  public HyperCycle() { }

  public HyperCycle(Vector cycle) {
    makeCycle(cycle);
  }

  private String getRelation(HyperCell cell, HyperCell nextCell) {
    int nextX = (int)nextCell.getX();
    int nextY = (int)nextCell.getY();
    int x = (int)cell.getX();
    int y = (int)cell.getY();

    if (nextX == x) {
      if (y > nextY) {
        return NORTH;
      } else {
        return SOUTH;
      }
    } else {
      // y's are the same
      if (x > nextX) {
        return WEST;
      } else {
        return EAST;
      }
    }
  }

  public void makeCycle(Vector cycle) {
    HyperCell cell = (HyperCell)cycle.get(0);
    cells.add(cell);
    for (int i = 1; i < cycle.size(); i++) {
      HyperCell nextCell = (HyperCell)cycle.get(i);
      cells.add(getRelation(cell, nextCell));
      cells.add(nextCell);
      cell = nextCell;
    }
  }

  public String toString() {
    totals.clear();
    StringBuffer b = new StringBuffer("HyperCycle:\n");
    for (int i = 0; i < cells.size(); i++) {
      Object o = cells.get(i);

      if (o instanceof HyperCell) {
        HyperCell cell = (HyperCell)o;
        if (i == 0) {
          b.append("\t" + cell + "\n");
        } else {
          b.append(cell + "\n");
        }
        Hashtable details = cell.skillCount;

        Enumeration e = details.keys();
        while (e.hasMoreElements()) {
          Object key = e.nextElement();
          Integer val = (Integer)details.get(key);
          if (totals.containsKey(key)) {
            Integer totVal = (Integer)totals.get(key);
            totals.put(key, new Integer(totVal.intValue() + val.intValue()));
          } else {
            totals.put(key, val);
          }
        }
      } else {
        b.append("\t" + o + " -> ");
      }
    }

    b.append("Totals: ");
    Enumeration e = totals.keys();
    while (e.hasMoreElements()) {
      Object key = e.nextElement();
      if (e.hasMoreElements()) {
        b.append(totals.get(key) + " x " + key + ", ");
      } else {
        b.append(totals.get(key) + " x " + key);
      }
    }
    b.append("\n\n");
    return b.toString();
  }

}






