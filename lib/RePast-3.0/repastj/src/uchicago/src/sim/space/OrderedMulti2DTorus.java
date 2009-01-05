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


/**
 * A torus that can hold more than one object in its cells.
 * The cells themselves store their occupants in order of insertion,
 * The list of objects in a cell contains the first object
 * inserted at the beginning of the list and the last object inserted
 * at the end. The object returned by getObject is a
 * <code>OrderedCell</code>.
 *
 * @version $Revision$ $Date$
 */
public class OrderedMulti2DTorus extends AbsMulti2DTorus {

    /**
     * Creates this OrderedMulti2DTorus with the specified dimensions.
     * sparse specifies whether the grid will be sparsely filled or not.
     *
     * @param xSize the number of columns in the grid
     * @param ySize the number of rows in the grid
     * @param sparse whether the grid will be sparsely populated or not
     */
    public OrderedMulti2DTorus(int xSize, int ySize, boolean sparse) {
        super(xSize, ySize, sparse);
    }
  
    /**
     * Puts the specified Object into the cell at the specified coordinates.
     * The contents of the cell are ordered such that the first object
     * inserted will be the first out and the last in the last out.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param object the object to put
     */
    public void putObjectAt(int x, int y, Object object) {
        x = xnorm(x);
        y = ynorm(y);
    
        Cell c = (Cell) matrix.get(x, y);

        if (c == null) {
            c = new OrderedCell();
            matrix.put(x, y, c);
        }
        c.add(object);
    }

    /**
     * Puts the specified Object into the cell at the specified coordinates
     * and index. The contents of the cell are ordered such that the first
     * object inserted will be the first out and the last in the last out.
     * The index parameter can be used to specify where to insert the
     * this object relative to the other objects at this location. The
     * object at that location will be shifted to the right (i.e. have
     * 1 added to index).<p>
     *
     * <b>Note</b> this will throw an exception if the index is invalid.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param index where to insert the specified object relative to
     * the other objects at this location
     * @param object the object to put
     */
    public void putObjectAt(int x, int y, int index, Object object) {
        x = xnorm(x);
        y = ynorm(y);
    
        OrderedCell c = (OrderedCell) matrix.get(x, y);

        if (c == null) {
            c = new OrderedCell();
            matrix.put(x, y, c);
        }
        c.insert(index, object);
    }

    /**
     * Gets the object at the specified location and index. This will return
     * null if there are no objects at the specified location <b>and</b>
     * if the index is out of range (i.e. no object at that index).
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param index the position of the object to get relative to the
     * other objects in this list.
     */
    public Object getObjectAt(int x, int y, int index) {
        OrderedCell c = (OrderedCell) matrix.get(xnorm(x), ynorm(y));

        if (c != null) {
            if (index < c.size()) return c.getObject(index);
        }
    
        return null;
    }

    /**
     * Gets the index of the specified object at the specified location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param obj the object to get the index for
     * @return the index of the object if found at the specified location.
     * If the object is not found for whatever reason, returns -1.
     */
    public int getIndexOf(int x, int y, Object obj) {
        OrderedCell c = (OrderedCell) matrix.get(xnorm(x), ynorm(y));

        if (c != null) {
            return c.getIndexOf(obj);
        }
    
        return -1;
    }

    /**
     * Removes the object at the specified index at the specified location
     * from the grid.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param index the index of the object to remove
     * @return the removed object or null
     */
    public Object removeObjectAt(int x, int y, int index) {
        OrderedCell c = (OrderedCell) matrix.get(xnorm(x), ynorm(y));

        if (c != null) {
            return c.remove(index);
        }
        return null;
    }
}

