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
package uchicago.src.sim.topology.space.d2.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.topology.space.LocationIterator;
import uchicago.src.sim.topology.space.Neighborhood;
import uchicago.src.sim.topology.space.d2.HexNeighborhood;
import uchicago.src.sim.topology.space.d2.Object2DLocation;
import uchicago.src.sim.topology.space.d2.Object2DSpace;
import uchicago.src.sim.topology.space.d2.Object2DTorus;

public class HexNeighborhoodTest extends TestCase {

    Object2DSpace space = null;
    Neighborhood hood = null;
    Object2DLocation loc = null;

    public static Test suite() {
        TestSuite suite = new TestSuite(HexNeighborhoodTest.class);
        suite.setName("Neighborhood Tests");
        return suite;
    }
    public HexNeighborhoodTest(String name) {
        super(name);
    }

    public void testSize() {
        space = new Object2DTorus(100, 100);
        loc = (Object2DLocation) space.getLocation(50, 50);
        hood = new HexNeighborhood(space);
        assertEquals("hex 1", hood.getNeighborhoodSize(loc, 1, true), 7);
        assertEquals("hex 1", hood.getNeighborhoodSize(loc, 1, false), 6);
        assertEquals("hex 2", hood.getNeighborhoodSize(loc, 2, true), 19);
        assertEquals("hex 2", hood.getNeighborhoodSize(loc, 2, false), 18);
        assertEquals("hex 3", hood.getNeighborhoodSize(loc, 3, true), 37);
        assertEquals("hex 3", hood.getNeighborhoodSize(loc, 3, false), 36);
    }

    public void testOriginIterators() {

        space = new Object2DTorus(100, 100);
        loc = (Object2DLocation) space.getLocation(50, 50);
        hood = new HexNeighborhood(space);
        LocationIterator iter = hood.locationsIterator(loc, 1, true);

        Object2DLocation location = (Object2DLocation) iter.next();
        assertEquals("x != 50", 50, location.getX());
        assertEquals("y != 50", 50, location.getY());
        assertEquals("dir = -1", -1, iter.currentDirection());
        assertEquals("dis = 0", 0, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x != 49", 49, location.getX());
        assertEquals("y != 51", 51, location.getY());
        assertEquals("dir = 0", 0, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=50", 50, location.getX());
        assertEquals("y!=51", 51, location.getY());
        assertEquals("dir = 1", 1, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=51", 51, location.getX());
        assertEquals("y!=50", 50, location.getY());
        assertEquals("dir = 2", 2, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=50", 50, location.getX());
        assertEquals("y!=49", 49, location.getY());
        assertEquals("dir = 3", 3, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=49", 49, location.getX());
        assertEquals("y!=49", 49, location.getY());
        assertEquals("dir = 4", 4, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=49", 49, location.getX());
        assertEquals("y!=50", 50, location.getY());
        assertEquals("dir = 4", 4, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        assertTrue("iter shouldn't have next", !iter.hasNext());
    }

    public void testNoOriginIterators() {

        space = new Object2DTorus(100, 100);
        loc = (Object2DLocation) space.getLocation(50, 50);
        hood = new HexNeighborhood(space);
        LocationIterator iter = hood.locationsIterator(loc, 1, false);

        Object2DLocation location = (Object2DLocation) iter.nextLocation();
        assertEquals("x != 49", 49, location.getX());
        assertEquals("y != 51", 51, location.getY());
        assertEquals("dir = 0", 0, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=50", 50, location.getX());
        assertEquals("y!=51", 51, location.getY());
        assertEquals("dir = 1", 1, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=51", 51, location.getX());
        assertEquals("y!=50", 50, location.getY());
        assertEquals("dir = 2", 2, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=50", 50, location.getX());
        assertEquals("y!=49", 49, location.getY());
        assertEquals("dir = 3", 3, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=49", 49, location.getX());
        assertEquals("y!=49", 49, location.getY());
        assertEquals("dir = 4", 4, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        location = (Object2DLocation) iter.nextLocation();
        assertEquals("x!=49", 49, location.getX());
        assertEquals("y!=50", 50, location.getY());
        assertEquals("dir = 4", 4, iter.currentDirection());
        assertEquals("dis = 1", 1, iter.currentDistance());

        assertTrue("iter shouldn't have next", !iter.hasNext());
    }
}