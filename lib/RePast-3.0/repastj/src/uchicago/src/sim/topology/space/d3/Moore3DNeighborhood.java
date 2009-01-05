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

public class Moore3DNeighborhood
    extends AbstractNeighborhood
    implements Neighborhood {

    protected Object3DSpace space;

    protected Log log = null;

    public Moore3DNeighborhood(String type, Object3DSpace space) {
        super(type);
        log = LogFactory.getLog(this.getClass());
        this.space = space;
    }

    public Moore3DNeighborhood(Object3DSpace space) {
        super("Moore Neighborhood");
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

        Object3DLocation l1 = null;
        Object3DLocation l2 = null;

        if (element1 instanceof Object3DLocation) {
            l1 = (Object3DLocation) element1;
        } else if (element1 instanceof Agent) {
            l1 = (Object3DLocation) ((Agent) element1).getLocation();
        } else {
            // TODO how do I get hold of a location for a generic object
        }

        if (element2 instanceof Object3DLocation) {
            l2 = (Object3DLocation) element2;
        } else if (element2 instanceof Agent) {
            l2 = (Object3DLocation) ((Agent) element2).getLocation();
        } else {
            // TODO how do I get hold of a location for a generic object
        }

        if (l1 != null && l2 != null) {
            distance =
                Math.min(
                    Math.abs(l2.getX() - l1.getX()),
                    Math.abs(l2.getY() - l1.getY()));
        }

        return distance;
    }

    /**
     * This method actually uses the following calculation to determine the number 
     * of cells included in a neighborhood.
     * 
     * (2n + 1)^2
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
        return (int) Math.pow((2 * extent + 1), 2) - (includeOrigin ? 0 : 1);
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
        return new MooreLocationIteratorImpl(location, extent, includeOrigin);
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

    private class MooreLocationIteratorImpl implements LocationIterator {

        protected int currentDirection = 0;
        protected int currentDistance = 0;

        protected Object3DLocation location = null;
        protected Object3DLocation current = null;

        protected int ring = 0;
        protected int extent = 0;
        protected int cursorX = 0;
        protected int cursorY = 0;
        protected int cursorZ = 0;

        protected int zone = -1;

        protected int xMax = 0;
        protected int xMin = 0;
        protected int yMax = 0;
        protected int yMin = 0;
        protected int zMax = 0;
        protected int zMin = 0;

        protected boolean includeOrigin = false;
        
        public MooreLocationIteratorImpl(
            Location loc,
            int extent,
            boolean incOrigin) {
                
            this.location = (Object3DLocation) loc;
            this.extent = extent;
            this.includeOrigin = incOrigin;

            if (includeOrigin) {
                this.ring = 0;
                this.zone = -1;
            } else {
                this.ring = 1;
                this.zone = 0;
            }

            this.xMax = location.getX() + ring;
            this.xMin = location.getX() - ring;
            this.yMax = location.getY() + ring;
            this.yMin = location.getY() - ring;
            this.zMax = location.getZ() + ring;
            this.zMin = location.getZ() - ring;

            this.cursorX = xMin;
            this.cursorY = yMax;
            this.cursorZ = zMin;

        }

        protected void logState() {
            log.debug(" ");
            log.debug("ring=" + ring);
            log.debug("extent=" + extent);
            log.debug("cursorX=" + cursorX);
            log.debug("cursorY=" + cursorY);
            log.debug("cursorZ=" + cursorZ);
            log.debug("octant=" + zone);
            log.debug(" ");
        }

        protected boolean testNext(int x, int y, int z) {
            return true;
        }

        public boolean hasNext() {

            while (ring <= extent) {
                if (!(space instanceof Torus)) {
                    if ((cursorX >= 0 && cursorX < space.getSizeX())
                        && (cursorY >= 0 && cursorY < space.getSizeY())
                        && (cursorZ >= 0 && cursorZ < space.getSizeZ())) {
                        if (testNext(space.xnorm(cursorX),
                            space.ynorm(cursorY),
                            space.znorm(cursorZ))) {
                            break;
                        } else {
                            incrementCursor();
                        }
                    } else {
                        incrementCursor();
                    }
                } else {
                    if (testNext(space.xnorm(cursorX),
                        space.ynorm(cursorY),
                        space.znorm(cursorZ))) {
                        break;
                    } else {
                        incrementCursor();
                    }
                }
            }

            return ring <= extent;
        }

        private void incrementCursor() {
            switch (this.zone) {
                /* in 3D we are going to break the various 
                 * directions of the neighborhood into "zones"
                 */
                case 0 :
                    {
                        if (cursorX < xMax) {
                            cursorX++;
                        }
                        if (cursorX == xMax) {
                            zone = 1;
                        }
                        break;
                    }
                case 1 :
                    {
                        if (cursorY > yMin) {
                            cursorY--;
                        }
                        if (cursorY == yMin) {
                            zone = 2;
                        }
                        break;
                    }
                case 2 :
                    {
                        if (cursorX > xMin) {
                            cursorX--;
                        }
                        if (cursorX == xMin) {
                            zone = 3;
                        }
                        break;
                    }             
                case 3 :
                    {
                        if (cursorY < yMax) {
                            cursorY++;
                        }
                        if (cursorY == yMax) {
                            
                            zone = 0;
                            
                            if(cursorZ == zMax){
                                ring++;
                                this.xMax = location.getX() + ring;
                                this.xMin = location.getX() - ring;
                                this.yMax = location.getY() + ring;
                                this.yMin = location.getY() - ring;
                                this.zMax = location.getZ() + ring;
                                this.zMin = location.getZ() - ring;
                                this.cursorX = xMin;
                                this.cursorY = yMax;
                                this.cursorZ = zMin;                          
                            }else{
                                /* then its time to increment the z index */
                                this.cursorZ++;
                            }
                        }
                        break;
                    }
                default :
                    {
                    
                        ring++;
                        zone = 0;
                        this.xMax = location.getX() + ring;
                        this.xMin = location.getX() - ring;
                        this.yMax = location.getY() + ring;
                        this.yMin = location.getY() - ring;
                        this.zMax = location.getZ() + ring;
                        this.zMin = location.getZ() - ring;
                        this.cursorX = xMin;
                        this.cursorY = yMax;
                        this.cursorZ = zMax;
                    }
            }
        }

        public Object next() {
            return nextLocation();
        }

        public Location nextLocation() {
            try {
                if (ring <= extent) {
                    current =
                        (Object3DLocation) space.getLocation(
                            space.xnorm(cursorX),
                            space.ynorm(cursorY),
                            space.znorm(cursorZ));

                    currentDirection = zone;
                    currentDistance = ring;

                    logState();
                    incrementCursor();

                    return current;
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
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDirection()
         */
        public int currentDirection() {
            return currentDirection;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDistance()
         */
        public int currentDistance() {
            return currentDistance;
        }

    }

    private class OccupiedIterator extends MooreLocationIteratorImpl {

        public OccupiedIterator(
            Location location,
            int extent,
            boolean includeOrigin) {
            super(location, extent, includeOrigin);
        }

        protected boolean testNext(int x, int y, int z) {
            return space.getLocation(x, y, z).size() != 0;
        }
    }

    private class AvailableLocationIterator extends MooreLocationIteratorImpl {

        public AvailableLocationIterator(
            Location location,
            int extent,
            boolean includeOrigin) {
            super(location, extent, includeOrigin);
        }

        protected boolean testNext(int x, int y, int z) {
            return space.getLocation(x, y, z).size() == 0;
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
                currentIter = ((Object3DLocation) masterIter.next()).iterator();
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
                currentIter = ((Object3DLocation) masterIter.next()).iterator();
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
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDirection()
         */
        public int currentDirection() {
            return masterIter.currentDirection;
        }

        /* (non-Javadoc)
         * @see uchicago.src.sim.topology.space.neighborhood.LocationIterator#currentDistance()
         */
        public int currentDistance() {
            return masterIter.currentDistance;
        }

    }

}