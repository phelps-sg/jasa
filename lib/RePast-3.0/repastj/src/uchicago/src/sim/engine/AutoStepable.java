/*$$
 * Copyright (c) 2004, Repast Organization for Architecture and Design (ROAD)
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
 * Neither the name of the ROAD nor the names of its
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
package uchicago.src.sim.engine;

/**
 * This interface can be used for a more advanced <i>stepping</i> pattern for
 * agents.  This steps agents in 3 increments, first in the preStep (normally 
 * for setting an agent up for the next step, next in the step (normally for 
 * doing the bulk of the agent's actions), and finally in the postStep 
 * (normally for cleaning up after a run, swapping buffers and so forth).<br/>
 * 
 * Each method can throw @{link #Exception}s, allowing for stopping of the model
 * when an agent can't complete an action.
 *
 * @author Jerry Vos
 * @version $Revision$ $Date$
 */
public interface AutoStepable {
	 /**
	 * normally the method for setting up an agent for the next step
	 * @throws Exception when there is an error 
	 */
	public void preStep() throws Exception;

	/**
	 * normally the method that performs the actual actions of a step
	 * @throws Exception when there is an error 
	 */
	public void step() throws Exception;

	/**
	 * normally the method for swapping in new public data and otherwise 
	 * handling the results/cleanup of a step
	 * @throws Exception when there is an error 
	 */
	public void postStep() throws Exception;
}
