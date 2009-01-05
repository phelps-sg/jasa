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


import java.util.ArrayList;


/**
 * Base class for hexagonal grids whose cells can hold more that one
 * occupant. The actual object held in the grid cell is a Cell object.<p>
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
 *
 * @version $Revision $ $Date$
 */

public abstract class AbsMulti2DHexagonalGrid extends AbsMulti2DGrid {

    private HexMultiNeighborhooder neigh;
    private static final int[] singleExtent = {1};

    /**
     * Creates this AbsMulti2DHexagonalGrid with the specified dimensions.
     * sparse specifies whether the grid will be sparsely filled or not.
     * Specifying sparse can result in substantial memory savings.
     *
     * @param xSize the number of columns in the grid
     * @param ySize the number of rows in the grid
     * @param sparse whether the grid will be sparsely populated or not
     */
    public AbsMulti2DHexagonalGrid(int xSize, int ySize, boolean sparse) {
        super(xSize, ySize, sparse);
        neigh = new HexMultiNeighborhooder(this);
    }

    /**
     * Returns the rings of neighbors surrounding the cell at x, y. The
     * number of rings is specified by the extent parameter.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param extent the number of neighbor rings to return
     * @param returnNulls whether or not the returned list should return
     * null when a neighbor cell is empty
     * @return an ArrayList of Objexts beginning with the outermost ring of
     * neighbors, starting with the north or "12 o'clock" neighboring cell,
     * continuing clockwise and spiraling inwards
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)

     */
    public ArrayList getNeighbors(int x, int y, int extent,
        boolean returnNulls) {
        int[] range = {extent};

        return neigh.getNeighborsList(x, y, range, returnNulls);
    }
  
    /**
     * Returns the ring of neighbors with a radius of 1 surrounding the
     * object at x, y.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param returnNulls whether or not the returned list should return
     * null when a neighbor cell is empty
     * @return an ArrayList of Objects in clockwise order starting with the
     * north or "12 o'clock" neighboring cell
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public ArrayList getNeighbors(int x, int y, boolean returnNulls) {
		
        return neigh.getNeighborsList(x, y, singleExtent, returnNulls);
    }

    /**
     * Returns the rings of neighbors surrounding the cell at x, y. The
     * number of rings is specified by the extent parameter.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param extent the number of neighbor rings to return
     * @param returnNulls whether or not the returned list should return
     * null when a neighbor cell is empty
     * @return an ArrayList of ObjectLocations beginning with the
     * outermost ring of neighbors, starting with the north or "12
     * o'clock" neighboring cell, continuing clockwise and spiraling
     * inwards
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public ArrayList getNeighborsLoc(int x, int y, int extent,
        boolean returnNulls) {
        int[] range = {extent};

        return neigh.getNeighborsLoc(x, y, range, returnNulls);
    }

    /**
     * Returns the ring of neighbors with a radius of 1 surrounding the
     * object at x, y.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param returnNulls whether or not the returned list should return
     * null when a neighbor cell is empty
     * @return an ArrayList of ObjectLocation-s in clockwise order
     * starting with the north or "12 o'clock" neighboring cell
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public ArrayList getNeighborsLoc(int x, int y, boolean returnNulls) {
        return neigh.getNeighborsLoc(x, y, singleExtent, returnNulls);
    }
	
    /**
     * The notion of VonNeumann neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an
     * UnsupportedOperationException.  To get the neighbors in a
     * hexagonal grid, use the * {@link #getNeighborsLoc(int, int, int, boolean) getNeighborsLoc} method.
     *
     * @throws UnsupportedOperationException when called.
     */
    public ArrayList getVNNeighborsLoc(int x, int y, boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }
  
    /**
     * The notion of VonNeumann neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsLoc(int, int, int, boolean) getNeighborsLoc} method.
     *
     * @throws UnsupportedOperationException when called.
     */
    public ArrayList getVNNeighborsLoc(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }

    /**
     * The notion of Moore neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsLoc(int, int, int, boolean) getNeighborsLoc} method.
     *
     * @throws UnsupportedOperationException when called.
     */

    public ArrayList getMooreNeighborsLoc(int x, int y, boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }

    /**
     * The notion of Moore neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsLoc(int, int, int, boolean) getNeighborsLoc} method.
     *
     * @throws UnsupportedOperationException when called.
     *
     */

    public ArrayList getMooreNeighborsLoc(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }

    /**
     * The notion of VonNeumann neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsint, int, int, boolean) getNeighbors} method.
     *
     * @throws UnsupportedOperationException when called.
     *
     */
    public ArrayList getVNNeighbors(int x, int y, boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }
  
    /**
     * The notion of VonNeumann neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsint, int, int, boolean) getNeighbors} method.
     *
     * @throws UnsupportedOperationException when called.
     */
    public ArrayList getVNNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }

    /**
     * The notion of Moore neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsint, int, int, boolean) getNeighbors} method.
     *
     * @throws UnsupportedOperationException when called.
     *
     */

    public ArrayList getMooreNeighbors(int x, int y, boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }

    /**
     * The notion of Moore neighbors is incoherent on a Hexagonal
     * grid. Consequently, this method throws an UnsupportedOperationException.
     * To get the neighbors in a hexagonal grid, use the
     * {@link #getNeighborsint, int, int, boolean) getNeighbors} method.
     *
     * @throws UnsupportedOperationException when called.
     */

    public ArrayList getMooreNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        String m = "Hexagonal grids do not support VonNeumann or Moore neighbor calls";

        throw new UnsupportedOperationException(m);
    }
}
