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

/**
 * @author Tom Howe
 * @version $Revision$
 */
public class DefaultEdge implements Edge {
	private Object element1;
	private Object element2;
	private double strength = 1;

	public DefaultEdge(Object e1, Object e2) {
		element1 = e1;
		element2 = e2;
	}

	public DefaultEdge() {
		element1 = null;
		element2 = null;
	}

	public DefaultEdge(Object e1, Object e2, double strength) {
		element1 = e1;
		element2 = e2;
		this.strength = strength;
	}

	public Object getElementOne() {
		return element1;
	}

	public Object getElementTwo() {
		return element2;
	}

	public void setElementOne(Object e1) {
		element1 = e1;
	}

	public void setElementTwo(Object e2) {
		element2 = e2;
	}

	public Object getOtherElement(Object e) {
		if (element1.equals(e)) {
			return element2;
		}
		return element1;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double s) {
		strength = s;
	}
	
	public Edge copy(){
		Edge e = new DefaultEdge();
		e.setElementOne(getElementOne());
		e.setElementTwo(getElementTwo());
		e.setStrength(getStrength());
		return e;
	}

}
