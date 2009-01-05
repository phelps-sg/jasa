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


//import java.awt.Dimension;
//import java.io.*;
//import java.util.StringTokenizer;
import java.util.Vector;
//import java.util.Comparator;

//import uchicago.src.collection.*;
//import uchicago.src.sim.space.Discrete2DSpace;
//import uchicago.src.sim.util.SimUtilities;


/**
 * A discrete hexagonal grid of objects accessed by x and y coordinates.
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
 * that cell -1, 0 refers to cell 2 and cell 0, -1 is cell 6.<p>
 */
public class Object2DHexagonalGrid extends Object2DGrid {

    private Neighborhooder neigh;

    /**
     * Constructs a grid with the specified size.
     * @param xSize the size of the lattice in the x dimension.
     * @param ySize the size of the lattice in the y dimension.
     */
    public Object2DHexagonalGrid(int xSize, int ySize) {
        super(xSize, ySize);
        neigh = new HexNeighborhooder(this);
    }

    /**
     * Constructs a grid from a file. Only ASCII PGM files are supported
     * at this time. Code adapted from Nelson Minar's implementation of
     * SugarScape with Swarm.
     */
    public Object2DHexagonalGrid(String fileName, int type) {
        super(fileName, type);
        neigh = new HexNeighborhooder(this);
    }

    /**
     * Gets the neighbors of the object at x, y. Objects are returned in
     * clockwise order starting at 12 o'clock. The object at x, y is not
     * returned.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls whether nulls (nothing at x,y) should be returned
     * @return a vector of objects (and possibly nulls) in in clockwise
     * order starting at 12 o'clock.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector getNeighbors(int x, int y, boolean returnNulls) {
        return getNeighbors(x, y, 1, returnNulls);
    }

    /**
     * Gets the rings of neighbors of the object at x, y. The extent
     * parameters specfies the radius of the rings to get and thus the
     * number of neighbor rings. That is, an extent of 2 will return the
     * ring radius 2 away from the specified point as well as the innermost
     * ring of radius 1. Objects are returned in clockwise order starting
     * with the object at 12 o'clock and at the outermost ring.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param extent the extension of the neighborhood
     * @param returnNulls whether nulls should be returned
     * @return a vector of objects (and possibly nulls) in clockwise
     * order starting with the object at 12 o'clock and at the outermost
     * ring.
     * @throws IndexOutOfBoundsException if the given coordinates are
     * out of range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector getNeighbors(int x, int y, int extent, boolean returnNulls) {
        int[] range = {extent};

        return neigh.getNeighbors(x, y, range, returnNulls);
    }

    /**
     * The notion of a vonNeumann neighborhood is incoherent for a
     * hexagonal grid.  Consequently this throws an UnsupportedOperation
     * if called.  To get the neighbors in a hexagonal grid, use the
     * {@link uchicago.src.sim.space.Object2DHexagonalGrid#getNeighbors
     * getNeighbors} method.
     **/
    public Vector getVonNeumannNeighbors(int x, int y, boolean returnNulls) throws UnsupportedOperationException {
        return getVonNeumannNeighbors(x, y, 1, 1, returnNulls);
    }

    /**
     * The notion of a vonNeumann neighborhood is incoherent for a
     * hexagonal grid.  Consequently this throws an UnsupportedOperation
     * if called.  To get the neighbors in a hexagonal grid, use the
     * {@link uchicago.src.sim.space.Object2DHexagonalGrid#getNeighbors
     * getNeighbors} method.
     **/
    public Vector getVonNeumannNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Hexagonal spaces do not support Von Neumann neighborhoods");
    }

    /**
     * The notion of a Moore neighborhood is incoherent for a
     * hexagonal grid.  Consequently this throws an UnsupportedOperation
     * if called.  To get the neighbors in a hexagonal grid, use the
     * {@link uchicago.src.sim.space.Object2DHexagonalGrid#getNeighbors
     * getNeighbors} method.
     **/
    public Vector getMooreNeighbors(int x, int y, boolean returnNulls) throws UnsupportedOperationException {
        return getMooreNeighbors(x, y, 1, 1, returnNulls);
    }

    /**
     * The notion of a Moore neighborhood is incoherent for a
     * hexagonal grid.  Consequently this throws an UnsupportedOperation
     * if called.  To get the neighbors in a hexagonal grid, use the
     * {@link uchicago.src.sim.space.Object2DHexagonalGrid#getNeighbors
     * getNeighbors} method.
     **/

    public Vector getMooreNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Hexagonal spaces do not support Moore Neighborhoods");
    }

    /**
     * Finds the maximum grid cell occupant within a specified range from
     * the specified origin coordinate. Maximum is determined by the default
     * or user supplied comparator class. The default comparator compares
     * objects using the >, <, and = operators on the hashcode of the objects.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param range the range out from the coordinate to search
     * @param includeOrigin include object at origin in search
     * @return the Objects determined to be the maximum.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector findMaximum(int x, int y, int range, boolean includeOrigin) {
        int[] extent = {range};

        return neigh.findMaximum(x, y, extent, includeOrigin);
    }

    /**
     * Finds the minimum grid cell occupant within a specified range from
     * the specified origin coordinate. Minimum is determined by the default
     * or user supplied comparator class. The default comparator compares
     * objects using the >, <, and = operators on the hashcode of the objects.
     *
     * @param x the x origin coordinate
     * @param y the y origin coordinate
     * @param range the range out from the coordinate to search
     * @param includeOrigin include object at origin in search
     * @return the Objects determined to be the maximum.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Vector findMinimum(int x, int y, int range, boolean includeOrigin) {
        int[] extent = {range};

        return neigh.findMinimum(x, y, extent, includeOrigin);
    }
}

