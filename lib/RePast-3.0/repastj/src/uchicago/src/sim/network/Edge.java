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
package uchicago.src.sim.network;


/**
 * A interface for all edge objects (as in nodes and edges). Implementing this
 * inteface allows the edge to be displayed correctly.<p>
 *
 * At this time labels are not displayed.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public interface Edge {

    /**
     * Gets the node that this edge comes from.
     */
    public Node getFrom();

    /**
     * Gets the node that this edge goes to
     */
    public Node getTo();

    /**
     *  Sets the from node
     */
    public void setFrom(Node node);

    /**
     *  Sets the to Node
     */
    public void setTo(Node node);

    /**
     * Sets the label for this edge
     *
     * @param label the label for this edge
     */
    public void setLabel(String label);

    /**
     * Gets the label for this edge.
     */
    public String getLabel();

    /**
     * Sets the strength of this edge
     */
    public void setStrength(double val);

    /**
     * Gets the strength of this edge
     */
    public double getStrength();

    /**
     * Gets the type of this edge. This is typically used to track the type
     * of network.
     */
    public String getType();

    /**
     *  Sets the type of this edge. This is typically used to track the type
     * of network (i.e. marriage etc.)
     */
    public void setType(String type);
}
