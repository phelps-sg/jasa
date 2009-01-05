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
package uchicago.src.sim.space;


import java.util.Comparator;
import java.util.Vector;


public abstract class AbstractNeighborhooder implements Neighborhooder {
    protected Comparator comparator;
    protected Discrete2DSpace space;
    protected boolean torus = false;

    public AbstractNeighborhooder(Discrete2DSpace space) {
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

    public Vector findMaximum(int x, int y, int range[], boolean includeOrigin) {
        Vector v;

        v = getNeighbors(x, y, range, false);
        if (includeOrigin)
            v.add(space.getObjectAt(x, y));
        if (comparator == null)
            System.out.print("no comparator");
        return compareMax(v);
    }

    public Vector findMinimum(int x, int y, int range[], boolean includeOrigin) {
        Vector v;
		
        v = getNeighbors(x, y, range, includeOrigin);
        if (includeOrigin)
            v.add(space.getObjectAt(x, y));
        return compareMin(v);
    }

    protected Vector compareMax(Vector v) {
        Vector retVal = new Vector(7);

        if (v.size() != 0) {
            Object max = v.elementAt(0);
            int compResult = 0;

            for (int i = 1; i < v.size(); i++) {
                Object o = v.elementAt(i);

                compResult = comparator.compare(max, o);
                if (compResult == 0) {
                    retVal.add(o);
                } else if (compResult < 0) {
                    retVal.removeAllElements();
                    max = o;
                }
            }
            retVal.add(max);
        }
        return retVal;
    }

    protected Vector compareMin(Vector v) {
        Vector retVal = new Vector(7);

        if (v.size() != 0) {
            Object min = v.elementAt(0);
            int compResult = 0;

            for (int i = 1; i < v.size(); i++) {
                Object o = v.elementAt(i);

                compResult = comparator.compare(min, o);
                if (compResult == 0) {
                    retVal.add(o);
                } else if (compResult > 0) {
                    retVal.removeAllElements();
                    min = o;
                }
            }
            retVal.add(min);
        }
        return retVal;
    }
}

