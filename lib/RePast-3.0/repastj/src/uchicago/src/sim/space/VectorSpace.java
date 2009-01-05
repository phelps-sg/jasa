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


//import java.util.List;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A VectorSpace is a list-like container containing the objects "inhabiting"
 * that space. Unlike the Discrete2D spaces, a VectorSpace does not imply any
 * physical arrangement of the objects within the VectorSpace in relation to
 * the other objects within that space. A VectorSpace is used as the space for
 * those object whose relationship to other objects in the space is non-
 * celluar, non-discrete, and so forth. Network nodes are the primary
 * example here.<p>
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class VectorSpace {

    private ArrayList members = new ArrayList(113);

    /**
     * Creates a new VectorSpace.
     */

    public VectorSpace() {}

    /**
     * Creates a vector space using the specified Collection.
     *
     * @param c the collection to make this VectorSpace out of.
     */
    public VectorSpace(Collection c) {
        members.addAll(c);
    }

    /**
     * Add the specified object to this VectorSpace.
     *
     * @param o the object to add
     */
    public void addMember(Object o) {
        members.add(o);
    }

    /**
     * Remove the specified object from this VectorSpace.
     *
     * @param o the object to remove
     */
    public void removeMember(Object o) {
        members.remove(o);
    }

    /**
     * Remove the object at the specified index from this VectorSpace.
     *
     * @param index the index of the object to remove
     */
    public void removeMember(int index) {
        members.remove(index);
    }

    // It would be nice to do this with a Vistor like pattern to hide the
    // ArrayList.

    /**
     * Gets the list of members of this VectorSpace.
     */
    public ArrayList getMembers() {
        return members;
    }
}
