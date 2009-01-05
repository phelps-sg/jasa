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


import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.collection.NewMatrix;
import uchicago.src.collection.SparseObjectMatrix;


/**
 * Base class for grids whose cells can hold more that one occupant. The
 * actual object held in the grid cell is a Cell object. 
 *
 * @version $Revision$ $Date$
 */

public abstract class AbsMulti2DGrid implements IMulti2DGrid {

    protected BaseMatrix matrix;
    protected int xSize;
    protected int ySize;
    protected List roList = Collections.unmodifiableList(new ArrayList());
    protected Iterator roIter;

    private VNMultiNeighborhooder vnNeigh;
    private MooreMultiNeighborhooder mNeigh;

    /**
     * Creates this AbsMulti2DGrid with the specified dimensions.
     * sparse specifies whether the grid will be sparsely filled or not.
     * Specifying sparse can result in substantial memory savings.
     *
     * @param xSize the number of columns in the grid
     * @param ySize the number of rows in the grid
     * @param sparse whether the grid will be sparsely populated or not
     */
    public AbsMulti2DGrid(int xSize, int ySize, boolean sparse) {
        this.xSize = xSize;
        this.ySize = ySize;
        if (sparse) matrix = new SparseObjectMatrix(xSize, ySize);
        else matrix = new NewMatrix(xSize, ySize);
        roIter = roList.iterator();

        vnNeigh = new VNMultiNeighborhooder(this);
        mNeigh = new MooreMultiNeighborhooder(this);
    }

    protected void rangeCheck(int x, int y) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize)
            throw new IndexOutOfBoundsException
                ("x or y coordinate is out of bounds");
    }

    /**
     * Clears the contents the specified cell.
     *
     * @param x the x coordinate of the cell to clear
     * @param y the y coordinate of the cell to clear
     */
    public void clear(int x, int y) {
        matrix.remove(x, y);
    }

    /**
     * Gets the List of objects at the specified coordinates. An ordered
     * grid will return the first object inserted at the beginning of the
     * list and the last object inserted at the end of the list. The
     * list order is undetermined for an unordered object.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the list of objects at the specified location. If no objects
     * are at the location the list will be empty (and unmodifable).
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public List getObjectsAt(int x, int y) {
        rangeCheck(x, y);
        Cell c = (Cell) matrix.get(x, y);

        if (c != null) return c.getList();
        return roList;
    }

    /**
     * Gets the iterator for the collection of objects at the specified
     * coordinates. For an ordered grid the order of iteration will be first
     * object inserted, first returned and so on. For an unordered grid,
     * order is undefined.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return an iterator for the objects at the specified location. If no
     * objects are at the location, the iterator will be empty.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Iterator getIteratorAt(int x, int y) {
        rangeCheck(x, y);
        Cell c = (Cell) matrix.get(x, y);

        if (c != null) return c.iterator();
        return roIter;
    }
      
    /**
     * Gets the Cell object at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Cell getCellAt(int x, int y) {
        rangeCheck(x, y);
        return (Cell) matrix.get(x, y);
    }
  
    /**
     * Gets the size (number of occupants) of the cell at
     * the specified location.
     *
     * @param x the x coordinate
     * @param y the y coordinate*
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public int getCellSizeAt(int x, int y) {
        rangeCheck(x, y);
        Cell c = (Cell) matrix.get(x, y);

        if (c != null) return c.size();
        return 0;
    }

    /**
     * Removes the specified object from the specified location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param obj the object to remove
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public void removeObjectAt(int x, int y, Object obj) {
        rangeCheck(x, y);
        Cell c = (Cell) matrix.get(x, y);

        if (c != null) c.remove(obj);
    }

    /**
     * Gets the von Neumann neighbors of the object(s) at x, y. Objects
     * are returned in west, east, north, south order. The returned ArrayList
     * contains <code>ObjectLocation</code> objects that can be used to
     * determined the exact location of the object returned. The objects at x, y
     * are not returned.<p>
     *
     * If the neighboring objects contain their location information,
     * <code>getVNNeighbors(...)</code> should be used as it is faster.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls whether nulls (nothing at x,y) should be returned
     * @return an ArrayList of ObjectLocation objects. The object contained
     * by the ObjectLocation object may be null.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     *
     * @see ObjectLocation
     */
    public ArrayList getVNNeighborsLoc(int x, int y, boolean returnNulls) {
        return getVNNeighborsLoc(x, y, 1, 1, returnNulls);
    }
  
    /**
     * Gets the extended von Neumann neighbors of the objects(s) at x, y. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * Objects are returned in west, east, north, south order. The
     * most distant objects are returned first, that is, all the objects to the
     * west starting with the most distant, then those to the east and so on.
     * The returned ArrayList contains <code>ObjectLocation</code> objects
     * that can be used to determined the exact location of the object returned.
     * The objects at x,y are not returned.<p>
     *
     * If the neighboring objects contain their location information,
     * <code>getVNNeighbors(...)</code> should be used as it is faster.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @param returnNulls whether nulls should be returned
     * @return an ArrayList of ObjectLocation objects. The object contained
     * by the ObjectLocation object may be null.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     *
     * @see ObjectLocation
     */
    public ArrayList getVNNeighborsLoc(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        int[] extent = {xExtent, yExtent};

        return vnNeigh.getNeighborsLoc(x, y, extent, returnNulls);
    }

    /**
     * Gets the Moore neighbors of the object(s) at x, y. The returned ArrayList
     * contains <code>ObjectLocation</code> objects that can be used to
     * determined the exact location of the object returned. The objects at x, y
     * are not returned. Objects are returned by row starting with the
     * "NW corner" and ending with the "SE corner."<p>
     *
     * If the neighboring objects contain their location information,
     * <code>getMooreNeighbors(...)</code> should be used as it is faster.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls should the returned Vector contain null objects
     * @return an ArrayList of ObjectLocation objects. The object contained
     * by the ObjectLocation object may be null.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     * @see ObjectLocation
     */

    public ArrayList getMooreNeighborsLoc(int x, int y, boolean returnNulls) {
        return getMooreNeighborsLoc(x, y, 1, 1, returnNulls);
    }

    /**
     * Gets the extended Moore neighbors of the object(s) at x, y. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * The returned ArrayList contains <code>ObjectLocation</code> objects
     * that can be used to determined the exact location of the object returned.
     * The objects at x, y are not returned. Objects are returned by
     * row starting with the "NW corner" and ending with the "SE corner."<p>
     *
     * If the neighboring objects contain their location information,
     * <code>getMooreNeighbors(...)</code> should be used as it is faster.
     *
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @param returnNulls should the returned ArrayList contain null objects
     * @return an ArrayList of ObjectLocation objects. The object contained
     * by the ObjectLocation object may be null.
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     *
     * @see ObjectLocation
     */

    public ArrayList getMooreNeighborsLoc(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        int[] extent = {xExtent, yExtent};

        return mNeigh.getNeighborsLoc(x, y, extent, returnNulls);
    }

    /**
     * Gets the von Neumann neighbors of the object(s) at x, y. Objects
     * are returned in west, east, north, south order. The objects at x, y
     * are not returned.<p>
     *
     * If the neighboring objects do not contain their location information,
     * <code>getVNNeighborsLoc(...)</code> can be used, although it is
     * slower.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls whether nulls (nothing at x,y) should be returned
     * @return an ArrayList of objects (and possibly nulls).
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     *
     */
    public ArrayList getVNNeighbors(int x, int y, boolean returnNulls) {
        return getVNNeighbors(x, y, 1, 1, returnNulls);
    }
  
    /**
     * Gets the extended von Neumann neighbors of the objects(s) at x, y. The
     * extension in the x and y direction are specified by xExtent and yExtent.
     * Objects are returned in west, east, north, south order. The
     * most distant objects are returned first, that is, all the objects to the
     * west starting with the most distant, then those to the east and so on.
     * The objects at x,y are not returned.<p>
     *
     * If the neighboring objects do not contain their location information,
     * <code>getVNNeighborsLoc(...)</code> can be used, although it is
     * slower.
     *
     * 
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @param returnNulls whether nulls should be returned
     * @return an ArrayList of objects (and possibly nulls).
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public ArrayList getVNNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        int[] extent = {xExtent, yExtent};

        return vnNeigh.getNeighborsList(x, y, extent, returnNulls);
    }

    /**
     * Gets the Moore neighbors of the object(s) at x, y. The objects at x, y
     * are not returned. Objects are returned by row starting with the
     * "NW corner" and ending with the "SE corner."<p>
     *
     * If the neighboring objects do not contain their location information,
     * <code>getMooreNeighborsLoc(...)</code> can be used although it is
     * slower.
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param returnNulls should the returned ArrayList contain null objects
     * @return an ArrayList of objects (and possibly nulls) ordered by row
     * starting with the "NW corner" and ending with the "SE corner."
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     *
     */

    public ArrayList getMooreNeighbors(int x, int y, boolean returnNulls) {
        return getMooreNeighbors(x, y, 1, 1, returnNulls);
    }

    /**
     * Gets the extended Moore neighbors of the object(s) at x, y. 
     * The objects at x, y are not returned. Objects are returned by
     * row starting with the "NW corner" and ending with the "SE corner."<p>
     *
     * If the neighboring objects do not contain their location information,
     * <code>getMooreNeighborsLoc(...)</code> can be used although it is
     * slower.
     *
     *
     * @param x the x coordinate of the object
     * @param y the y coordinate of the object
     * @param xExtent the extension of the neighborhood in the x direction
     * @param yExtent the extension of the neighborhood in the y direction
     * @param returnNulls should the returned ArrayList contain null objects
     * @return an ArrayList of objects (and possibly nulls) ordered by
     * row starting with the "NW corner" and ending with the "SE corner."
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     *
     * @see ObjectLocation
     */

    public ArrayList getMooreNeighbors(int x, int y, int xExtent, int yExtent,
        boolean returnNulls) {
        int[] extent = {xExtent, yExtent};

        return mNeigh.getNeighborsList(x, y, extent, returnNulls);
    }
  
    // Discrete2DSpace implementation

    /**
     * Gets the size of the x dimension
     */
    public int getSizeX() {
        return xSize;
    }

    /**
     * Gets the size of the y dimension
     */
    public int getSizeY() {
        return ySize;
    }

    /**
     * Gets the dimension of the space
     */
    public Dimension getSize() {
        return new Dimension(xSize, ySize);
    }

    /**
     * Gets the Object at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the Cell at x,y
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Object getObjectAt(int x, int y) {
        rangeCheck(x, y);
        return matrix.get(x, y);
    }

    /**
     * Gets the value at the specified coordinate if appropriate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the value at x, y
     */
    public double getValueAt(int x, int y) {
        throw new UnsupportedOperationException();
    }

    /**
     * Puts the specified Object at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param object the object to put
     */
    public abstract void putObjectAt(int x, int y, Object object);

    /**
     * Puts the specified value at the specified coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param value the value to put at x,y
     */
    public void putValueAt(int x, int y, double value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the matrix collection class that contains all the values
     */
    public BaseMatrix getMatrix() {
        return matrix;
    }

    /**
     * Releases any superfluous memory. This is only usefull when
     * working with sparse grids.
     */
    public void trim() {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                int count = getCellSizeAt(i, j);

                if (count == 0) matrix.remove(i, j);
            }
        }

        matrix.trim();
    } 
}
