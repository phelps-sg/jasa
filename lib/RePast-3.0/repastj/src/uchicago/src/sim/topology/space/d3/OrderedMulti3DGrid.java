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
package uchicago.src.sim.topology.space.d3;

import java.util.ArrayList;

import uchicago.src.sim.topology.space.Agent;
import uchicago.src.sim.topology.space.Location;

/**
 * A grid object that can hold more than one object in its cells.
 * The cells themselves store their occupants in order of insertion,
 * The list of objects in a cell contains the first object
 * inserted at the beginning of the list and the last object inserted
 * at the end. The object returned by getObject is a
 * <code>OrderedMulti2DLocation</code>.
 *
 * @version $Revision$ $Date$
 */
public class OrderedMulti3DGrid extends Multi3DGrid {

    /**
     * Creates this OrderedMulti2DGrid with the specified dimensions.
     * sparse specifies whether the grid will be sparsely filled or not.
     *
     * @param xSize the number of columns in the grid
     * @param ySize the number of rows in the grid
     * @param sparse whether the grid will be sparsely populated or not
     */
    public OrderedMulti3DGrid(int xSize, int ySize, int zSize) {
        super("OrderedMulti3DGrid", xSize, ySize, zSize);
    }

    public OrderedMulti3DGrid(String type, int xSize, int ySize, int zSize) {
        super(type, xSize, ySize, zSize);
    }

    /**
     * Gets the Object2DLocation object at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public Location getLocation(int x, int y, int z) {

        Location cell =
            (Location) matrix[xnorm(x)][ynorm(y)][znorm(z)];

        if (cell == null) {
            cell = new OrderedMulti3DLocation(xnorm(x), ynorm(y), znorm(z));
        }

        return cell;
    }

    /**
     * A grid cell whose occupants are stored in order of insertion. The first
     * object inserted will be on the bottom of the stack at position 0, and the
     * last will be on the top at position num_objects - 1.
     *
     * @version $Revision$ $Date$
     */
    public class OrderedMulti3DLocation
        extends Abstract3DLocation
        implements Object3DLocation {

        protected OrderedMulti3DLocation(int x, int y, int z) {
            super(x, y, z);
            elements = new ArrayList();
        }

        /* OrderedLocation Features */

        /**
         * Gets the first (bottom) object in the cell. The first object will be
         * the first object added relative to the other objects in the cell.
         */
        public Object getFirst() {
            return ((ArrayList) elements).get(0);
        }

        /**
         * Gets the last (top) object in the cell. The last object will be
         * the last object added relative to the other objects in the cell.
         */
        public Object getLast() {
            return ((ArrayList) elements).get(elements.size() - 1);
        }

        /**
         * Gets the object at the specified index.
         */
        public Object getObject(int index) {
            return ((ArrayList) elements).get(index);
        }

        /**
         * Gets the index of the specified object.
         */
        public int getIndexOf(Object o) {
            return ((ArrayList) elements).indexOf(o);
        }

        /**
         * Inserts the specified object at the specified index.
         */
        public void insert(int index, Object o) {

            if (elements.isEmpty())
                matrix[x][y][z] = this;

            ((ArrayList) elements).add(index, o);

            if (o instanceof Agent) {
                ((Agent) o).setLocation(this);
            }
        }

        /**
         * Removes the object at the specified index.
         *
         * @param index the index of the object to remove
         */
        public boolean remove(int index) {
            Object o = ((ArrayList) elements).remove(index);

            if (o instanceof Agent) {
                ((Agent) o).setLocation(null);
            }

            if (elements.isEmpty()) {
                matrix[x][y][z] = null;
            }

            return o == null;
        }

    }
}