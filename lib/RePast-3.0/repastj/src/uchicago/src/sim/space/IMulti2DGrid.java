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


import java.util.Iterator;
import java.util.List;


/**
 * Interface for grids and tori whose cells can hold more than one
 * object.
 *
 * @version $Revision$ $Date$
 */
public interface IMulti2DGrid extends Discrete2DSpace {

    /**
     * Gets the List of objects at the specified coordinates. An ordered
     * torus will return the first object inserted at the beginning of the
     * list and the last object inserted at the end of the list. The
     * list order is undetermined for an unordered torus.
     */
    public List getObjectsAt(int x, int y);

    /**
     * Gets the iterator for the collection of objects at the specified
     * coordinates. For an ordered torus the order of iteration will be first
     * object inserted, first returned and so on. For an unordered torus,
     * order is undefined.
     */
    public Iterator getIteratorAt(int x, int y);

    /**
     * Gets the Cell object at the specified coordinates.
     */
    public Cell getCellAt(int x, int y);
  
    /**
     * Gets the size (number of occupants) of the cell at
     * the specified location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public int getCellSizeAt(int x, int y);

    /**
     * Removes the specified object from the specified location.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param obj the object to remove
     */
    public void removeObjectAt(int x, int y, Object obj);
}
  
