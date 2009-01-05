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

import java.util.Collection;
import java.util.Iterator;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Torus;
import uchicago.src.sim.topology.space.Agent;
import uchicago.src.sim.topology.space.Location;
import uchicago.src.sim.topology.space.Space;

/**
 * Base class for grids whose cells can hold one or more occupants. The
 * actual object held in the grid cell is a Object2DLocation object. 
 *
 * @version $Revision$ $Date$
 */

public abstract class AbstractObject2DSpace
    extends AbstractObject2DTopology
    implements Object2DSpace {

    protected BaseMatrix matrix = null;

    public AbstractObject2DSpace(String type) {
        super(type);
    }

    /**
     * Clears the contents the specified location.
     *
     * @param x the x coordinate of the cell to clear
     * @param y the y coordinate of the cell to clear
     */
    public void removeLocation(Location location) {
        if(location instanceof Object2DLocation){
            Object2DLocation loc2d = (Object2DLocation)location;
            Location c = (Location) matrix.remove(xnorm(loc2d.getX()), ynorm(loc2d.getY()));
            if (c != null)
               c.clear();
        }
    }

    /**
     * Clears the contents the specified cell.
     *
     * @param x the x coordinate of the cell to clear
     * @param y the y coordinate of the cell to clear
     */
    public void removeLocation(int x, int y) {
        Location c = (Location) matrix.remove(xnorm(x), ynorm(y));
        if (c != null)
            c.clear();
    }

    /**
     * Gets the Object2DLocation object at the specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @throws IndexOutOfBoundsException if the given coordinates are out of
     * range (x < 0 || x >= xSize || y < 0 || y >= ySize)
     */
    public abstract Location getLocation(int x, int y);

    public Location getLocation(Object obj) {
        return (Location) this.locations.get(obj);
    }

    /**
     * Returns the matrix collection object associated with this 2d grid
     */
    public BaseMatrix getMatrix() {
        return matrix;
    }


    /* (non-Javadoc)
     * @see uchicago.src.sim.space.Space#getSize()
     */
    public double[] getSize() {
        return new double[]{ 
            getMatrix().getNumCols(),
            getMatrix().getNumRows()};
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.space.Space#getSizeX()
     */
    public int getSizeX() {
        return getMatrix().getNumCols();
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.space.Space#getSizeY()
     */
    public int getSizeY() {
        return getMatrix().getNumRows();
    }

    /**
     * Releases any superfluous memory. This is only usefull when
     * working with sparse grids.
     */
    public void trim() {
        for (int i = 0; i < getSizeX(); i++) {
            for (int j = 0; j < getSizeY(); j++) {
                Object2DLocation loc = (Object2DLocation) this.getMatrix().get(i, j);
                if (loc != null && loc.isEmpty()) {
                    getMatrix().remove(i, j);
                }
            }
        }
        getMatrix().trim();
    }

    public int xnorm(int x) {
        if (x < 0 || x >= getSizeX()) {
            if (this instanceof Torus) {
                while (x < 0)
                    x += getSizeX();
                return x % getSizeX();
            } else {
                throw new IndexOutOfBoundsException("x coordinate is out of bounds");
            }
        }
        return x;
    }

    public int ynorm(int y) {
        if (y < 0 || y >= getSizeY()) {
            if (this instanceof Torus) {
                while (y < 0)
                    y += getSizeY();
                return y % getSizeY();
            } else {
                throw new IndexOutOfBoundsException("y coordinate is out of bounds");
            }
        }
        return y;
    }

    /**
     * A data structure holding an object and the x and y coordinates of that
     * object. Note that the instance variables obj, x and y are public and so
     * they may be accessed directly e.g <code><pre>
     *   Abstract2DLocation ol = new Abstract2DLocation(someObj, 3, 3);
     *   Agent a = (Agent)ol.obj;
     *   int xLocl = ol.x;
     *   ...
     * </pre></code>
     *
     * However, changing these x and y coordinates does not effect the actual
     * object coordinates in any way.<p>
     *
     * Abstract2DLocation overrides equals and will return true when the object
     * and the x and y coordinates are equal.
     *
     * @version $Revision$ $Date$
     */
    public class Abstract2DLocation implements Object2DLocation, Drawable {

        /** the underlying collection at this location */
        protected Collection elements;

        /** the capacity of this location */
        protected int capacity = Integer.MAX_VALUE;

        /** The x coordinate of the object. */
        protected int x;

        /** The y coordinate of the object. */
        protected int y;

        /**
         * Creates an Abstract2DLocation from the specified object and coordinates.
         *
         * @param obj the object at this location.
         * @param x the x coordinate
         * @param y the y coordinaate
         */
        protected Abstract2DLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {

            if (!(o instanceof Object2DLocation))
                return false;

            Object2DLocation other = (Object2DLocation) o;

            return (x == other.getX() && y == other.getY())
                && elements.equals(o);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            int result = 17;

            result = 37 * result + x;
            result = 37 * result + y;
            result = 37 * result + elements.hashCode();
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "(x: " + x + " y: " + y + ")";
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.gui.Drawable#getX()
         */
        public int getX() {
            return x;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.gui.Drawable#getY()
         */
        public int getY() {
            return y;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#getCapacity()
         */
        public int getCapacity() {
            return capacity;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#setCapacity(int)
         */
        public void setCapacity(int i) {
            capacity = i;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#isFull()
         */
        public boolean isFull() {
            return size() >= capacity;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#getSpace()
         */
        public Space getSpace() {
            return AbstractObject2DSpace.this;
        }

        /* Object2DLocation interface implmentation */

        /* (non-Javadoc)
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        public boolean addAll(Collection c) {
            boolean result = !c.isEmpty();
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                result = !this.add(iter.next()) ? false : result;
            }
            return result;
        }

        /* (non-Javadoc)
         * @see java.util.Collection#add(java.lang.Object)
         */
        public boolean add(Object o) {

            boolean result = false;

            if (!this.isFull()) {
                result = elements.add(o);
            }

            if (result) {
                if (!this.isEmpty())
                    getMatrix().put(x, y, this);

                if (o instanceof Agent) {
                    ((Agent) o).setLocation(this);
                }
            }

            return result;
        }

        /* (non-Javadoc)
         * @see java.util.Collection#remove(java.lang.Object)
         */
        public boolean remove(Object o) {

            boolean result = elements.remove(o);

            if (result) {
                if (o instanceof Agent) {
                    ((Agent) o).setLocation(null);
                }

                if (this.isEmpty()) {
                    removeLocation(x, y);
                }
            }
            return result;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.space.Object2DLocation#clear()
         */
        public void clear() {
            if (!this.isEmpty()) {
                for (Iterator iter = this.iterator(); iter.hasNext();) {
                    iter.next();
                    iter.remove();
                }
            }
            elements.clear();
        }

        /* (non-Javadoc)
         * @see java.util.Collection#removeAll(java.util.Collection)
         */
        public boolean removeAll(Collection c) {
            boolean result = !c.isEmpty();
            for (Iterator iter = this.iterator(); iter.hasNext();) {
                Object o = iter.next();
                if (c.contains(o)) {
                    result = true;
                    iter.remove();
                }
            }
            return result;
        }

        /* (non-Javadoc)
         * @see java.util.Collection#retainAll(java.util.Collection)
         */
        public boolean retainAll(Collection c) {
            boolean result = c.isEmpty();
            for (Iterator iter = this.iterator(); iter.hasNext();) {
                Object o = iter.next();
                if (!c.contains(o)) {
                    result = true;
                    iter.remove();
                }
            }
            return result;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.gui.Drawable#draw(uchicago.src.sim.gui.SimGraphics)
         */
        public void draw(SimGraphics g) {
            g.drawHollowRect(java.awt.Color.lightGray);
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#contains(java.lang.Object)
         */
        public boolean contains(Object o) {
            return elements.contains(o);
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#containsAll(java.util.Collection)
         */
        public boolean containsAll(Collection c) {
            return elements.containsAll(c);
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#isEmpty()
         */
        public boolean isEmpty() {
            return elements.isEmpty();
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#iterator()
         */
        public Iterator iterator() {
            final Iterator iter = elements.iterator();
            return new Iterator() {

                private Object o = null;

                public boolean hasNext() {
                    return iter.hasNext();
                }

                public Object next() {
                    o = iter.next();
                    return o;
                }

                public void remove() {
                    iter.remove();

                    if (o instanceof Agent) {
                        ((Agent) o).setLocation(null);
                    }

                    if (isEmpty()) {
                        removeLocation(x, y);
                    }
                }

            };
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#size()
         */
        public int size() {
            return elements.size();
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#toArray()
         */
        public Object[] toArray() {
            return elements.toArray();
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.Object2DLocation#toArray(java.lang.Object[])
         */
        public Object[] toArray(Object[] a) {
            return elements.toArray(a);
        }

    }
}