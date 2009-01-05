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

public class MooreNeighborhooder extends AbstractNeighborhooder {
	
    public MooreNeighborhooder(Discrete2DSpace space) {
        super(space);
        torus = space instanceof Torus;
    }

    public Vector getNeighbors(int x, int y, int[] extents, boolean returnNulls) {
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
}
