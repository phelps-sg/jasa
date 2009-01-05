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


import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import uchicago.src.sim.topology.RelationTopology;
/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 16, 2003
 * Time: 2:16:14 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractDiscrete2DTopology implements RelationTopology{

  protected Comparator comparator;
  protected Discrete2DSpace space;
  protected boolean torus = false;

  public AbstractDiscrete2DTopology(Discrete2DSpace space) {
    this.space = space;
    comparator = new Comparator() {
      public int compare(Object o1, Object o2) {
        int hc1 = o1.hashCode();
        int hc2 = o2.hashCode();

        return hc1 < hc2 ? -1 : hc1 > hc2 ? 1 : 0;
      }
    };
  }

  public void setComparator(Comparator c) {
    comparator = c;
  }

  public List findMaximum(int x, int y, int range[], boolean includeOrigin) {
    List v;

    v = getRelations(x, y, range, false);
    if (includeOrigin)
      v.add(space.getObjectAt(x, y));
    if (comparator == null)
      System.out.print("no comparator");
    return compareMax(v);
  }

  public List findMinimum(int x, int y, int range[], boolean includeOrigin) {
    List v;

    v = getRelations(x, y, range, includeOrigin);
    if (includeOrigin)
      v.add(space.getObjectAt(x, y));
    return compareMin(v);
  }

  protected List compareMax(List v) {
    List retVal = new Vector(7);

    if (v.size() != 0) {
      Object max = v.get(0);
      int compResult = 0;

      for (int i = 1; i < v.size(); i++) {
        Object o = v.get(i);

        compResult = comparator.compare(max, o);
        if (compResult == 0) {
          retVal.add(o);
        } else if (compResult < 0) {
          retVal.clear();
          //retVal.removeAllElements();
          max = o;
        }
      }
      retVal.add(max);
    }
    return retVal;
  }

  protected List compareMin(List v) {
    List retVal = new Vector(7);

    if (v.size() != 0) {
      Object min = v.get(0);
      int compResult = 0;

      for (int i = 1; i < v.size(); i++) {
        Object o = v.get(i);

        compResult = comparator.compare(min, o);
        if (compResult == 0) {
          retVal.add(o);
        } else if (compResult > 0) {
          retVal.clear();
          min = o;
        }
      }
      retVal.add(min);
    }
    return retVal;
  }

  public abstract List getRelations(int x, int y, int[] ranges,
                                    boolean returnNulls);

}
