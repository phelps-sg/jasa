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
package uchicago.src.sim.topology.space.d2;

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
public class OrderedMulti2DGrid extends Multi2DGrid {

	/**
	 * Creates this OrderedMulti2DGrid with the specified dimensions.
	 * sparse specifies whether the grid will be sparsely filled or not.
	 *
	 * @param xSize the number of columns in the grid
	 * @param ySize the number of rows in the grid
	 * @param sparse whether the grid will be sparsely populated or not
	 */
	public OrderedMulti2DGrid(int xSize, int ySize, boolean sparse) {
		super("OrderedMulti2DGrid", xSize, ySize, sparse);
	}

    public OrderedMulti2DGrid(String type, int xSize, int ySize, boolean sparse) {
        super(type, xSize, ySize, sparse);
    }
    
	/**
	 * Gets the Object2DLocation object at the specified coordinates.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @throws IndexOutOfBoundsException if the given coordinates are out of
	 * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
	 */
	public Location getLocation(int x, int y) {

		Location cell = (Location) getMatrix().get(xnorm(x), ynorm(y));

		if (cell == null) {
			cell = new OrderedMulti2DLocation(xnorm(x), ynorm(y));
		}

		return cell;
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
		((OrderedMulti2DLocation) this.getLocation(xnorm(x), ynorm(y))).insert(
			index,
			object);
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
		return ((OrderedMulti2DLocation) this.getLocation(
				xnorm(x),
				ynorm(y))).getObject(
			index);
	}

	/**
	 * Removes the object at the specified index at the specified location
	 * from the grid.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param index the index of the object to remove
	 * @return the removed object or null
	 */
	public boolean removeObjectAt(int x, int y, int index) {
        return ((OrderedMulti2DLocation) this.getLocation(
                        xnorm(x),
                        ynorm(y))).remove(index);
	}
    
    /**
     * A grid cell whose occupants are stored in order of insertion. The first
     * object inserted will be on the bottom of the stack at position 0, and the
     * last will be on the top at position num_objects - 1.
     *
     * @version $Revision$ $Date$
     */
    public class OrderedMulti2DLocation extends Abstract2DLocation implements Object2DLocation {

        protected OrderedMulti2DLocation(int x, int y){
            super(x,y);
            elements = new ArrayList();
        }
    
        /* OrderedLocation Features */
    
        /**
         * Gets the first (bottom) object in the cell. The first object will be
         * the first object added relative to the other objects in the cell.
         */
        public Object getFirst() {
            return ((ArrayList)elements).get(0);
        }

        /**
         * Gets the last (top) object in the cell. The last object will be
         * the last object added relative to the other objects in the cell.
         */
        public Object getLast() {
            return ((ArrayList)elements).get(elements.size() - 1);
        }

        /**
         * Gets the object at the specified index.
         */
        public Object getObject(int index) {
            return ((ArrayList)elements).get(index);
        }

        /**
         * Gets the index of the specified object.
         */
        public int getIndexOf(Object o) {
            return ((ArrayList)elements).indexOf(o);
        }

        /**
         * Inserts the specified object at the specified index.
         */
        public void insert(int index, Object o) {
        
            if(elements.isEmpty())
                getMatrix().put(x,y,this);
            
            ((ArrayList)elements).add(index, o);
        
            if(o instanceof Agent){
                ((Agent)o).setLocation(this);
            }
        }

        /**
         * Removes the object at the specified index.
         *
         * @param index the index of the object to remove
         */
        public boolean remove(int index) {
            Object o = ((ArrayList)elements).remove(index);
        
            if(o instanceof Agent){
                ((Agent)o).setLocation(null);
            }
                
            if (elements.isEmpty()) {
                getMatrix().remove(x,y);
            }
                
            return o == null;
        }
    

    }
}