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
import java.util.HashMap;
import java.util.List;

import uchicago.src.sim.topology.ModifyableTopology;
import uchicago.src.sim.topology.space.Agent;
import uchicago.src.sim.topology.space.Location;

/**
 * @author Mark Diggory
 *
 * The Abstract2DTopology class provides an underlying implementation for
 * Object2DSpaces to act as Topologies. It implements all the methods required
 * to get, add, remove and "Relations" between objects and the locations in the 
 * Grid.
 */
public abstract class AbstractObject3DTopology implements ModifyableTopology {

    protected HashMap locations;

    private String type;

    public AbstractObject3DTopology(String type) {
        this.type = type;
    }
        
    /** inserts an object into a location in the Object2DSpace 
     * extending this support class
     * @see uchicago.src.sim.topology.ModifyableTopology#addRelation(java.lang.Object, java.lang.Object, double)
     */
    public void addRelation(
        Object element1,
        Object element2,
        double distance) {
        Location l = null;
        if (element2 instanceof Location) {
            l = (Location) element2;
            l.add(element1);
        } else if (element1 instanceof Location) {
            l = (Location) element1;
            l.add(element2);
        } else {
            throw new IllegalArgumentException("OccupationTopology requires a Location Object");
        }
    }

    /** 
     * removes an object from a location in the the Object2DSpace.
     * @see uchicago.src.sim.topology.ModifyableTopology#removeRelation(java.lang.Object, java.lang.Object)
     */
    public void removeRelation(Object element1, Object element2) {
        Location l = null;
        if (element2 instanceof Location) {
            l = (Location) element2;
            l.remove(element1);
        } else if (element1 instanceof Location) {
            l = (Location) element1;
            l.remove(element2);
        } else {
            throw new IllegalArgumentException("OccupationTopology requires a Location Object");
        }
    }

    /**
     * Get all of the relationships that the given element has
     * with other elements.
     * @param element
     * @return
     */
    public List getRelations(Object element) {
        return getRelations(element, 0);
    }

    /**
     * Get all of the relationships that the given element has
     * with other elements in this location. (In this case, get the List of
     * occupants from the location).
     * 
     * In this case, the only range available is 1, this returns the
     * same contents as public List getRelations(Object element).
     * @param element
     * @param range
     * @return List of objects
     */
    public List getRelations(Object element, double range) {
        Location location = (Location) element;

        if (element instanceof Location) {
            location = (Location) element;
        } else if (element instanceof Agent) {
            location = ((Agent) element).getLocation();
        } else {
            location = (Location) locations.get(element);
        }
        
        return new ArrayList(location);

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

    /**
     * Gets the distance between two objects in this topology.  This could be
     * either spatial distance, network distance or any other kind of well defined
     * metric distance.
     * @param element1
     * @param element2
     * @return double representing the distance between to objects in this Topology
     */
    public double distance(Object element1, Object element2) {
        if (locations.get(element1).equals(element2)
            || locations.get(element2).equals(element1)) {
            return 1;
        }
        return -1;
    }

}