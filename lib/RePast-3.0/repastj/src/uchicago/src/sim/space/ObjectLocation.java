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
import java.util.List;


/**
 * A data structure holding an object and the x and y coordinates of that
 * object. Note that the instance variables obj, x and y are public and so
 * they may be accessed directly e.g <code><pre>
 *   ObjectLocation ol = new ObjectLocation(someObj, 3, 3);
 *   Agent a = (Agent)ol.obj;
 *   int xLocl = ol.x;
 *   ...
 * </pre></code>
 *
 * However, changing these x and y coordinates does not effect the actual
 * object coordinates in any way.<p>
 *
 * ObjectLocation overrides equals and will return true when the object
 * and the x and y coordinates are equal.
 *
 * @version $Revision$ $Date$
 */
public class ObjectLocation {

    /**
     * The object at this ObjectLocation.
     */
    public Object obj;

    /**
     * The x coordinate of the object.
     */
    public int x;

    /**
     * The y coordinate of the object.
     */
    public int y;

    /**
     * The z (unused and always 0) coordinate of the object.
     */
    public int z = 0;
   
    /**
     * Creates an ObjectLocation from the specified object and coordinates.
     *
     * @param obj the object at this location.
     * @param x the x coordinate
     * @param y the y coordinaate
     */
    public ObjectLocation(Object obj, int x, int y) {
        this.obj = obj;
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a List of ObjectLocation objects from the objects in
     * List l at the coordinates x and y. This will return an empty
     * ArrayList if the List argument in empty.
     * 
     */
    public static ArrayList makeObjectLocations(List l, int x, int y) {
        int size = l.size();
        ArrayList al = new ArrayList();

        if (size == 0) return al;
        for (int i = 0; i < size; i++) {
            al.add(new ObjectLocation(l.get(i), x, y));
        }

        return al;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ObjectLocation)) return false;
        ObjectLocation other = (ObjectLocation) o;
        boolean objEqual = obj == null ? other.obj == null : obj.equals(other.obj);

        if (objEqual) return (x == other.x && y == other.y);
        return false;
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + x;
        result = 37 * result + y;
        int c = obj == null ? 0 : obj.hashCode();

        result = 37 * result + c;
        return result;
    }

    /**
     * Returns true if the x and y coordinates of the specified
     * ObjectLocation are equal to the x and y coordinates of this
     * ObjectLocation. Otherwise, return false;
     */
    public boolean areXYEqual(ObjectLocation loc) {
        return (loc.x == x && loc.y == y);
    }

    public String toString() {
        return "(" + obj + ": x: " + x + " y: " + y + ")";
    }
}
