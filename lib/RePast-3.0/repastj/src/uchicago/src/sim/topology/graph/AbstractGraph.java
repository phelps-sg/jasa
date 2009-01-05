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
package uchicago.src.sim.topology.graph;

import java.io.Serializable;
import java.util.List;


/**
 * @author Tom Howe
 * @version $Revision$
 * @serial 4862542571588171769L
 */
public abstract class AbstractGraph implements Graph, Serializable{
  protected String type = "";
  static final long serialVersionUID = 4862542571588171769L;

  /**
   * Gets all of the Objects within a given range.
   * @param element
   * @param range
   * @return
   */
  public List getRelations(Object element, double range) {
    return this.getAdjacentNodes(element, range, EdgeType.ALL);
  }

  public List getRelations(Object element){
    return this.getAdjacentNodes(element, EdgeType.ALL);
  }

  /**
   * Add a relationship in this Topology between the two elements with the given distance.
   * @param element1
   * @param element2
   * @param distance
   */
  public void addRelation(Object element1, Object element2, double distance) {
    insertEdge(element1, element2, distance);
  }

  /**
   * Remove the relationship between the two elements.
   * @param element1
   * @param element2
   */
  public void removeRelation(Object element1, Object element2) {
    removeEdge(element1, element2);
  }

  /**
   * Get the type of relationship/topology that is represented by this
   * Class.  For example, if this represents a VonNeumann topology, this
   * should return the String "VON_NEUMANN".
   *
   * @return
   */
  public String getRelationType() {
    return type;
  }

  /**
   * Set the name for this type of relation.
   * @param type
   */
  public void setRelationType(String type) {
    this.type = type;
  }
  
  
  
	public int hashCode() {
		final int PRIME = 1000003;
		int result = 0;
		if (type != null) {
			result = PRIME * result + type.hashCode();
		}

		return result;
	}

	public boolean equals(Object oth) {
		if (this == oth) {
			return true;
		}

		if (oth == null) {
			return false;
		}

		if (oth.getClass() != getClass()) {
			return false;
		}

		AbstractGraph other = (AbstractGraph) oth;
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else {
			if (!this.type.equals(other.type)) {
				return false;
			}
		}

		return true;
	}

}
