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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uchicago.src.sim.space.Torus;
import uchicago.src.sim.topology.space.AbstractNeighborhood;
import uchicago.src.sim.topology.space.Agent;
import uchicago.src.sim.topology.space.Location;
import uchicago.src.sim.topology.space.LocationIterator;
import uchicago.src.sim.topology.space.NeighborIterator;
import uchicago.src.sim.topology.space.Neighborhood;
import uchicago.src.sim.topology.space.Space;

public class VonNeumannNeighborhood
    extends AbstractNeighborhood
    implements Neighborhood {

    protected Log log = null;

    protected Object2DSpace space;

    public VonNeumannNeighborhood(Object2DSpace space) {
        super("VonNeumann Neighborhood");
        this.space = space;
        log = LogFactory.getLog(this.getClass());
    }

    public VonNeumannNeighborhood(String type, Object2DSpace space) {
        super(type);
        this.space = space;
        log = LogFactory.getLog(this.getClass());
    }


    protected Space getSpace(){
        return space;
    }
    
    /**
     * 
     */
    public double distance(Object element1, Object element2) {

        double distance = Double.NaN;
        Object2DLocation l1 = null;
        Object2DLocation l2 = null;

        if (element1 instanceof Object2DLocation) {
            l1 = (Object2DLocation) element1;
        } else if (element1 instanceof Agent) {
            l1 = (Object2DLocation) ((Agent) element1).getLocation();
        } else {
            // TODO how do I get hold of a location for a generic object
        }

        if (element2 instanceof Object2DLocation) {
            l2 = (Object2DLocation) element2;
        } else if (element2 instanceof Agent) {
            l2 = (Object2DLocation) ((Agent) element2).getLocation();
        } else {
            // TODO how do I get hold of a location for a generic object
        }

        if (l1 != null && l2 != null) {
            distance =
                Math.abs(l2.getX() - l1.getX())
                    + Math.abs(l2.getY() - l1.getY());
        }

        return distance;
    }

    /**
     * This method actually uses the following calculation to determine the number 
     * of cells included in a neighborhood.
     * 
     * 2n^2 + 2n + 1
     * 
     * RePast calculates neighs using 1 as the first set (not including the origin cell.
     * I deal with this by adding one to the extent to calabrate it with RePast and 
     * subtracting 1 from final calculation to exclude the center cell.
     * 
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#getNeighborhoodSize(int, int)
     * @param extent
     * @return the number of cells in the neghborhood
     */
    public int getNeighborhoodSize(
        Location location,
        int extent,
        boolean includeOrigin) {
        return (int)
            (2 * Math.pow((extent), 2)
                + 2 * (extent)
                + (includeOrigin ? 1 : 0));
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#isFull(int, int)
     */
    public boolean isFull(
        Location location,
        int extent,
        boolean includeOrigin) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#getAgentCount(int, int)
     */
    public int getAgentCount(
        Location location,
        int extent,
        boolean includeOrigin) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#neighborIterator()
     */
    public NeighborIterator neighborIterator(
        Location location,
        int extent,
        boolean includeOrigin) {
        return new NeighborIteratorImpl(location, extent, includeOrigin);
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#emptyLocationsIterator()
     */
    public LocationIterator locationsIterator(
        Location location,
        int extent,
        boolean includeOrigin) {
        return new VonNeumannLocationIteratorImpl(location, extent, includeOrigin);
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#emptyLocationsIterator()
     */
    public LocationIterator emptyLocationsIterator(
        Location location,
        int extent,
        boolean includeOrigin) {
        return new AvailableLocationIterator(location, extent, includeOrigin);
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#occupiedLocationsIterator()
     */
    public LocationIterator occupiedLocationsIterator(
        Location location,
        int extent,
        boolean includeOrigin) {
        return new OccupiedIterator(location, extent, includeOrigin);
    }

    private class VonNeumannLocationIteratorImpl implements LocationIterator {

        protected int currentDirection = 0;
        protected int currentDistance = 0;
        protected Object2DLocation current = null;
        protected Object2DLocation location = null;
        protected int ring = 0;
        protected int extent = 0;
        protected int cursorX = 0;
        protected int cursorY = 0;
        protected int quadrant = 0;
        protected int xMax = 0;
        protected int xMin = 0;
        protected int yMax = 0;
        protected int yMin = 0;

        public VonNeumannLocationIteratorImpl(
            Location loc,
            int extent,
            boolean includeOrigin) {
            this.location = (Object2DLocation) loc;
            this.extent = extent;

            if (includeOrigin) {
                this.ring = 0;
                this.quadrant = -1;
            } else {
                this.ring = 1;
                this.quadrant = 0;
            }

            this.xMax = location.getX() + ring;
            this.xMin = location.getX() - ring;
            this.yMax = location.getY() + ring;
            this.yMin = location.getY() - ring;
            this.cursorX = location.getX();
            this.cursorY = yMax;

        }

        protected void logState() {
            log.debug(" ");
            log.debug("ring=" + ring);
            log.debug("extent=" + extent);
            log.debug("cursorX=" + cursorX);
            log.debug("cursorY=" + cursorY);
            log.debug("octant=" + quadrant);
            log.debug(" ");
        }

        protected boolean testNext(int x, int y) {
            return true;
        }

        public boolean hasNext() {

            while (ring <= extent) {
                if (!(space instanceof Torus)) {
                    if ((cursorX >= 0 && cursorX < space.getSizeX())
                        && (cursorY >= 0 && cursorY < space.getSizeY())) {
                        if (testNext(space.xnorm(cursorX),
                            space.ynorm(cursorY))) {
                            break;
                        } else {
                            incrementCursor();
                        }
                    } else {
                        incrementCursor();
                    }
                } else {
                    if (testNext(space.xnorm(cursorX), space.ynorm(cursorY))) {
                        break;
                    } else {
                        incrementCursor();
                    }
                }
            }

            return ring <= extent;
        }

        public void incrementCursor() {
            switch (this.quadrant) {
                case 0 :
                    {
                        if (cursorX < xMax && cursorY > location.getY()) {
                            cursorX++;
                            cursorY--;
                        }
                        if (cursorX == xMax && cursorY == location.getY()) {
                            quadrant = 1;
                        }
                        break;
                    }
                case 1 :
                    {
                        if (cursorX > location.getX() && cursorY > yMin) {
                            cursorX--;
                            cursorY--;
                        }
                        if (cursorX == location.getX() && cursorY == yMin) {
                            quadrant = 2;
                        }
                        break;
                    }
                case 2 :
                    {
                        if (cursorX > xMin && cursorY < location.getY()) {
                            cursorX--;
                            cursorY++;
                        }
                        if (cursorX == xMin && cursorY == location.getY()) {
                            quadrant = 3;
                        }
                        break;
                    }
                case 3 :
                    {
                        if (cursorX < location.getX() && cursorY < yMax) {
                            cursorX++;
                            cursorY++;
                        }
                        if (cursorX == location.getX() && cursorY == yMax) {
                            ring++;
                            quadrant = 0;
                            this.xMax = location.getX() + ring;
                            this.xMin = location.getX() - ring;
                            this.yMax = location.getY() + ring;
                            this.yMin = location.getY() - ring;
                            this.cursorX = location.getX();
                            this.cursorY = yMax;
                        }
                        break;
                    }
                default :
                    {
                        ring++;
                        quadrant = 0;
                        this.xMax = location.getX() + ring;
                        this.xMin = location.getX() - ring;
                        this.yMax = location.getY() + ring;
                        this.yMin = location.getY() - ring;
                        this.cursorX = location.getX();
                        this.cursorY = yMax;
                    }
            }
        }

        public Object next() {
            return nextLocation();
        }

        public Location nextLocation() {
            try {
                if (ring <= extent) {
                    Object2DLocation next = (Object2DLocation) 
                        space.getLocation(
                            space.xnorm(cursorX),
                            space.ynorm(cursorY));

                    currentDirection = quadrant;
                    currentDistance = ring;

                    logState();
                    incrementCursor();

                    return next;
                } else {
                    return null;
                }

            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            current.clear();
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDistance()
         */
        public int currentDistance() {
            return currentDistance;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDirection()
         */
        public int currentDirection() {
            return currentDirection;
        }
    }

    private class OccupiedIterator extends VonNeumannLocationIteratorImpl {

        public OccupiedIterator(
            Location location,
            int extent,
            boolean includeOrigin) {
            super(location, extent, includeOrigin);
        }

        protected boolean testNext(int x, int y) {
            return space.getLocation(x, y).size() != 0;
        }
    }

    private class AvailableLocationIterator extends VonNeumannLocationIteratorImpl {

        public AvailableLocationIterator(
            Location location,
            int extent,
            boolean includeOrigin) {
            super(location, extent, includeOrigin);
        }

        protected boolean testNext(int x, int y) {
            return space.getLocation(x, y).size() <= 0;
        }
    }

    private class NeighborIteratorImpl implements NeighborIterator {

        protected OccupiedIterator masterIter = null;

        protected Iterator currentIter = null;

        public NeighborIteratorImpl(
            Location location,
            int extent,
            boolean includeOrigin) {
            masterIter = new OccupiedIterator(location, extent, includeOrigin);

            if (masterIter.hasNext()) {
                currentIter = ((Object2DLocation) masterIter.next()).iterator();
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */

        public boolean hasNext() {
            if (currentIter == null) {
                return false;
            }

            while (masterIter.hasNext() && !currentIter.hasNext()) {
                currentIter = ((Object2DLocation) masterIter.next()).iterator();
            }

            return masterIter.hasNext() && currentIter.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (hasNext())
                return currentIter.next();
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            if (currentIter != null)
                currentIter.remove();
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDistance()
         */
        public int currentDistance() {
            // TODO Auto-generated method stub
            return masterIter.currentDistance();
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDirection()
         */
        public int currentDirection() {
            return masterIter.currentDirection();
        }

    }
}