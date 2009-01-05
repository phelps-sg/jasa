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


//import java.util.Vector;


/**
 * A discrete 2 dimensional hexagonal torus of objects, accessed
 * by x and y coordinates.<p>
 *
 * The hexagonal cells are referenced by x, y coordinates as follows:
 * <pre>
 *       _
 *   _ / 1 \ _
 * / 0 \ _ / 2 \
 * \ _ / 4 \ _ /
 * / 3 \ _ / 5 \
 * \ _ / 7 \ _ /
 * / 6 \ _ / 8 \
 * \ _ /   \ _ /
 *
 * </pre>
 *
 * Here we have a 3 x 3 hexagonal grid. The first row of cells is 0,
 * 1, 2 such that 0,0 refers to cell 0, and 0,2 refers to cell 2. The
 * next row of cells is 3, 4, 5, so 1,0 refers to cell 3 and so
 * on. The last row of cells is 6, 7, and 8, so 2, 0 refers to cell 6.
 * The ring of neighbors with radius one that surrounds cell 4 is
 * composed of 1, 2, 5, 7, 3, and 0. The grid wraps as a toriod such
 * that cell -1, 0 refers to cell 2 and cell 0, -1 is cell 6.
 * @author Tom Howe
 * @version $Revision$ $Date$
 */

public class Object2DHexagonalTorus extends Object2DHexagonalGrid
    implements Torus {

    /**
     * Creates a new torus of the specified size.
     *
     * @param xSize the size of the x dimension
     * @param ySize the size of the y dimension
     */
    public Object2DHexagonalTorus(int xSize, int ySize) {
        super(xSize, ySize);
    }

    /**
     * Puts the specified object at (x,y), wrapping the coordinates
     * around the torus if necessary.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param object the object to put at (x,y)
     */

    public void putObjectAt(int x, int y, Object object) {
        matrix.put(xnorm(x), ynorm(y), object);
    }

    /**
     * Puts the specified double at (x,y), wrapping the coordinates
     * around the torus if necessary.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param value the value to put at (x,y)
     */
    public void putValueAt(int x, int y, double value) {
        matrix.put(xnorm(x), ynorm(y), new Double(value));
    }

    /**
     * Gets the object at the specified coordinate
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the object at x, y
     */
    public Object getObjectAt(int x, int y) {
        return matrix.get(xnorm(x), ynorm(y));
    }

    /**
     * Gets the value at (x,y)
     */
    public double getValueAt(int x, int y) {
        return super.getValueAt(xnorm(x), ynorm(y));
    }

    public int xnorm(int x) {
        if (x > xSize - 1 || x < 0) {
            while (x < 0) x += xSize;
            return x % xSize;
        }
        return x;
    }

    /**
     * Normalize the y value to the toroidal coordinates
     *
     * @param y the value to normalize
     * @return the normalized value
     */
    public int ynorm(int y) {
        if (y > ySize - 1 || y < 0) {
            while (y < 0) y += ySize;
            return y % ySize;
        }

        return y;
    }

}
