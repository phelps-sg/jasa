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
package uchicago.src.sim.topology.space;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import uchicago.src.sim.topology.RelationTopology;

public abstract class AbstractNeighborhood implements Neighborhood, RelationTopology {

    // TODO Implement modCount so we can remove objects from the iterator and the grid...
    protected int modCount;

    protected Comparator comparator;

    private String type;
    
    public AbstractNeighborhood(String type) {
        super();
        this.type = type;

        comparator = new Comparator() {
                    public int compare(Object o1, Object o2) {
                        int hc1 = o1.hashCode();
                        int hc2 = o2.hashCode();

                        return hc1 < hc2 ? -1 : hc1 > hc2 ? 1 : 0;
                    }
                };
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#agents()
     */
    public List neighbors(Location location, int extent, boolean includeOrigin) {
        ArrayList list = new ArrayList();
        for(Iterator iter = this.neighborIterator(location, extent, includeOrigin);iter.hasNext();){
            list.add(iter.next());
        }
        return list;
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#emptyLocations()
     */
    public List locations(Location location, int extent, boolean includeOrigin) {
        ArrayList list = new ArrayList();
        for(Iterator iter = this.locationsIterator(location, extent, includeOrigin);iter.hasNext();){
            list.add(iter.next());
        }
        return list;
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#emptyLocations()
     */
    public List emptyLocations(Location location, int extent, boolean includeOrigin) {
        ArrayList list = new ArrayList();
        for(Iterator iter = this.emptyLocationsIterator(location, extent, includeOrigin);iter.hasNext();){
            list.add(iter.next());
        }
        return list;
    }

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.space.neighborhood.Neighborhood#occupiedLocations()
     */
    public List occupiedLocations(Location location, int extent, boolean includeOrigin) {
        ArrayList list = new ArrayList();
        for(Iterator iter = this.occupiedLocationsIterator(location, extent, includeOrigin);iter.hasNext();){
            list.add(iter.next());
        }
        return list;
    }

    public void setComparator(Comparator c) {
        comparator = c;
    }

    public List findMaximum(Location location, int extent, boolean includeOrigin) {
        List v = neighbors(location, extent, includeOrigin);
        return compareMax(v);
    }

    public List findMinimum(Location location, int extent, boolean includeOrigin) {
        List v = neighbors(location, extent, includeOrigin);
        return compareMin(v);
    }

    protected List compareMax(List v) {
        List retVal = new ArrayList(7);

        if (v.size() != 0) {
            Object max = v.get(0);
            int compResult = 0;

            for (int i = 1; i < v.size(); i++) {
                Object o = v.get(i);

                compResult = comparator.compare(max, o);
                if (compResult == 0) {
                    retVal.add(o);
                } else if (compResult < 0) {
                    retVal.clear();
                    max = o;
                }
            }
            retVal.add(max);
        }
        return retVal;
    }

    protected List compareMin(List v) {
        List retVal = new ArrayList(7);

        if (v.size() != 0) {
            Object min = v.get(0);
            int compResult = 0;

            for (int i = 1; i < v.size(); i++) {
                Object o = v.get(i);

                compResult = comparator.compare(min, o);
                if (compResult == 0) {
                    retVal.add(o);
                } else if (compResult > 0) {
                    retVal.clear();
                    min = o;
                }
            }
            retVal.add(min);
        }
        return retVal;
    }

    protected abstract Space getSpace();
    
    public abstract boolean isFull(Location location, int extent, boolean includeOrigin);
    
    public abstract int getAgentCount(Location location, int extent, boolean includeOrigin);

    public abstract int getNeighborhoodSize(Location location, int extent, boolean includeOrigin);
        
    public abstract NeighborIterator neighborIterator(Location location, int extent, boolean includeOrigin);

    public abstract LocationIterator locationsIterator(Location location, int extent, boolean includeOrigin);

    public abstract LocationIterator emptyLocationsIterator(Location location, int extent, boolean includeOrigin);

    public abstract LocationIterator occupiedLocationsIterator(Location location, int extent, boolean includeOrigin);

    /* (non-Javadoc)
     * @see uchicago.src.sim.topology.RelationTopology#getRelations(java.lang.Object, double)
     */
    public List getRelations(Object element, double range) {
        
        Location location = null;
        
        if(element instanceof Location){
            location = (Location)element;
        }else if (element instanceof Agent){
            location = ((Agent)element).getLocation();
        }else{
            this.getSpace().getLocation(element);
        }
        
        return this.neighbors(location,(int)range,false);
    }

    /**
     * Get the type of relationship/topology that is represented by this
     * Class.  This can be controlled so that two objects of the same class
     * can exist in the context with different type id's.
     * 
     * For example, if this represents a Object3DGrid, this could return
     * "Agent Space" or "Playing Field". 
     *
     * @return the type
     */
    public String getRelationType() {
        return type;
    }

    /** 
     * Set the type of relationship/topology that is represented by this
     * Class.  This can be controlled so that two objects of the same class
     * can exist in the context with different type id's.
     * 
     * For example, if this represents a Object3DGrid, this could return
     * "Agent Space" or "Playing Field". 
     * 
     * @see uchicago.src.sim.topology.RelationTopology#setRelationType(java.lang.String)
     */
    public void setRelationType(String type) {
        //this.type = type;
    }

}