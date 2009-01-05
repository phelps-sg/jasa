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


import java.util.Vector;


public class HexNeighborhooder extends AbstractNeighborhooder {

    private boolean returnNull = false;
    private static final int[] singleExtent = {1};
    private int sizeX, sizeY;

    public HexNeighborhooder(Discrete2DSpace space) {
        super(space);
        torus = space instanceof Torus;
        sizeX = space.getSizeX();
        sizeY = space.getSizeY();
    }

    /**
     * Returns the ring of neighbors with a radius of 1 surrounding the
     * cell at x, y.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @return an array of doubles in clockwise order starting with the
     * north or "12" neighboring cell
     */
    public Vector getNeighbors(int x, int y, boolean returnNull) {
        return getNeighbors(x, y, singleExtent, returnNull);
    }

    /**
     * Returns the rings of neighbors surrounding the cell at x, y. The number
     * of rings is specified by the radius parameter.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param extents the number of neighbor rings to return
     * @return an array of doubles beginning with the outermost ring of
     * neighbors, starting with the north or "12 o'clock" neighboring cell,
     * continuing clockwise and spiraling inwards
     */
    public Vector getNeighbors(int x, int y, int[] extents, boolean returnNull) {
        this.returnNull = returnNull;
        if (extents.length != 1)
            throw new IllegalArgumentException("Hexagonal Neighborhoods take one argument");
        int radius = extents[0];

        if (radius < 1) return new Vector();

        if (radius == 1) return singleExtent(x, y);
        if (radius == 2) return doubleExtent(x, y);
        else return gtTwoExtent(x, y, radius);
    }

    private Vector gtTwoExtent(int x, int y, int extent) {
        Vector v = new Vector(3 * extent * (extent + 1));
        //int destIndex = 0;

        if (x % 2 == 0) {
            for (int radius = extent; radius > 2; radius--) {
                Vector src = getEvenRing(x, y, radius);

                v.addAll(src);
            }
        } else {
            for (int radius = extent; radius > 2; radius--) {
                Vector src = getOddRing(x, y, radius);

                v.addAll(src);
            }
        }

        Vector src = doubleExtent(x, y);

        v.addAll(src);

        return v;
    }

    private Vector getEvenRing(int x, int y, int radius) {
        Vector v = new Vector(radius * 6);
        int yVal = y - radius;

        addXY(v, x, yVal++);

        //int aIndex = 1;
        int limit = x + radius;
        int xVal = x + 1;

        while (xVal <= limit) {
            addXY(v, xVal++, yVal);
            if (xVal > limit) {
                yVal++;
                break;
            }
            addXY(v, xVal++, yVal);
            yVal++;
        }

        xVal = x + radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal++);

        if (xVal % 2 != 0) {
            xVal--;
            yVal--;
            addXY(v, xVal, yVal++);
        }

        xVal--;

        while (xVal > x) {
            addXY(v, xVal--, yVal);
            if (xVal == x) break;
            addXY(v, xVal--, yVal);
            yVal++;
        }

        yVal = y + radius;
        addXY(v, x, yVal);
        addXY(v, x - 1, yVal);

        yVal--;
        xVal = x - 2;
        limit = x - radius;
        while (xVal >= limit) {
            addXY(v, xVal--, yVal);
            if (xVal < limit) {
                yVal--;
                break;
            }
            addXY(v, xVal--, yVal);
            yVal--;
        }

        xVal = x - radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal--);

        xVal++;
        if (xVal % 2 != 0) {
            yVal++;
            addXY(v, xVal++, yVal--);
        }

        while (xVal < x) {
            addXY(v, xVal++, yVal);
            if (xVal == x) break;
            addXY(v, xVal++, yVal);
            yVal--;
        }

        //printArray(rarray);
        return v;
    }

    private Vector getOddRing(int x, int y, int radius) {

        Vector v = new Vector(radius * 6);

        addXY(v, x, y - radius);
        addXY(v, x + 1, y - radius);

        //int aIndex = 2;
        int xVal = x + 2;
        int yVal = (y - radius) + 1;
        int limit = x + radius;

        while (xVal <= limit) {
            addXY(v, xVal++, yVal);
            if (xVal > limit) {
                yVal++;
                break;
            }
            addXY(v, xVal++, yVal);
            yVal++;
        }

        xVal = x + radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal++);

        if (xVal % 2 != 0) {
            yVal--;
            xVal--;
            addXY(v, xVal, yVal++);
        }

        xVal--;
        while (xVal > x) {
            addXY(v, xVal--, yVal);
            addXY(v, xVal--, yVal);
            yVal++;
        }

        addXY(v, x, y + radius);
        yVal = y + radius - 1;
        xVal = x - 1;

        limit = x - radius;
        while (xVal >= limit) {
            addXY(v, xVal--, yVal);
            if (xVal < limit) {
                yVal--;
                break;
            }
            addXY(v, xVal--, yVal);
            yVal--;
        }

        xVal = x - radius;
        for (int i = 0; i < radius; i++)
            addXY(v, xVal, yVal--);

        xVal++;
        if (xVal % 2 != 0) {
            yVal++;
            addXY(v, xVal++, yVal);
            yVal--;
        }

        while (xVal < x) {
            addXY(v, xVal++, yVal);
            if (xVal == x) break;
            addXY(v, xVal++, yVal);
            yVal--;
        }

        return v;
    }

    private Vector doubleExtent(int x, int y) {
        Vector v = new Vector(18);

        if (x % 2 == 0) {
            addXY(v, x, y - 2);
            addXY(v, x + 1, y - 1);
            addXY(v, x + 2, y - 1);
            addXY(v, x + 2, y);
            addXY(v, x + 2, y + 1);
            addXY(v, x + 1, y + 2);
            addXY(v, x, y + 2);
            addXY(v, x - 1, y + 2);
            addXY(v, x - 2, y + 1);
            addXY(v, x - 2, y);
            addXY(v, x - 2, y - 1);
            addXY(v, x - 1, y - 1);

        } else {
            addXY(v, x, y - 2);
            addXY(v, x + 1, y - 2);
            addXY(v, x + 2, y - 1);
            addXY(v, x + 2, y);
            addXY(v, x + 2, y + 1);
            addXY(v, x + 1, y + 1);
            addXY(v, x, y + 2);
            addXY(v, x - 1, y + 1);
            addXY(v, x - 2, y + 1);
            addXY(v, x - 2, y);
            addXY(v, x - 2, y - 1);
            addXY(v, x - 1, y - 2);
        }
        v.addAll(singleExtent(x, y));
        return v;
    }

    private Vector singleExtent(int x, int y) {
        Vector v = new Vector(6);

        if (x % 2 == 0) {
            addXY(v, x, y - 1);
            addXY(v, x + 1, y);
            addXY(v, x + 1, y + 1);
            addXY(v, x, y + 1);
            addXY(v, x - 1, y + 1);
            addXY(v, x - 1, y);
        } else {
            int top = y - 1;

            addXY(v, x, top);
            addXY(v, x + 1, top);
            addXY(v, x + 1, y);
            addXY(v, x, y + 1);
            addXY(v, x - 1, y);
            addXY(v, x - 1, top);
        }

        return v;
    }

    protected void addXY(Vector v, int x, int y) {
        Object o = null;

        if (!torus) {
            if (x >= 0 & x < sizeX & y >= 0 & y < sizeY)
                o = space.getObjectAt(x, y);
        } else
            o = space.getObjectAt(x, y);
        if (returnNull)
            v.add(o);
        else if (o != null)
            v.add(o);
    }
}

