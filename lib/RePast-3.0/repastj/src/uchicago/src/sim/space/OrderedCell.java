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
import java.util.Iterator;
import java.util.List;


/**
 * A grid cell whose occupants are stored in order of insertion. The first
 * object inserted will be on the bottom of the stack at position 0, and the
 * last will be on the top at position num_objects - 1.
 *
 * @version $Revision$ $Date$
 */
public class OrderedCell implements Cell {

    private ArrayList elements = new ArrayList();

    /**
     * Gets the first (bottom) object in the cell. The first object will be
     * the first object added relative to the other objects in the cell.
     */
    public Object getFirst() {
        return elements.get(0);
    }

    /**
     * Gets the last (top) object in the cell. The last object will be
     * the last object added relative to the other objects in the cell.
     */
    public Object getLast() {
        return elements.get(elements.size() - 1);
    }

    /**
     * Gets the object at the specified index.
     */
    public Object getObject(int index) {
        return elements.get(index);
    }

    /**
     * Gets the number of objects in this cell.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Gets the index of the specified object.
     */
    public int getIndexOf(Object o) {
        return elements.indexOf(o);
    }

    /**
     * Inserts the specified object at the specified index.
     */
    public void insert(int index, Object o) {
        elements.add(index, o);
    }

    /**
     * Removes the object at the specified index.
     *
     * @param index the index of the object to remove
     */
    public Object remove(int index) {
        return elements.remove(index);
    }
    
    // Cell interface.

    /**
     * Clears all the objects from this cell.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * Adds an object to this cell.
     */
    public void add(Object o) {
        elements.add(o);
    }

    /**
     * Returns a List of the objects contained in this cell.
     */
    public List getList() {
        return elements;
    }

    /**
     * Returns an Iterator of the objects contained in this cell.
     */
    public Iterator iterator() {
        return elements.iterator();
    }

    /**
     * Removes the specified object from this cell.
     */
    public void remove(Object o) {
        elements.remove(o);
    }
}
