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

import java.util.EventObject;

/**
 * Events used in the BatchEventListener events.
 *
 * @author Jerry Vos
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class BatchEvent extends EventObject {
	public static class BatchEventType {
		int id;
		private static int baseID = 0;
		
		BatchEventType() { 
			this.id = baseID++;
		}
	}
	
	public static final BatchEventType RUN_ENDED		= new BatchEventType();
	public static final BatchEventType BATCH_FINISHED	= new BatchEventType();
	public static final BatchEventType TICK_CHANGED		= new BatchEventType();
	
	private BatchEventType type;
	private double tick;
	
	public BatchEvent(Object source, BatchEventType type) {
		super(source);
		
		this.type = type;
	}
	
	public BatchEvent(Object source, BatchEventType type, double tick) {
		super(source);
		
		this.type = type;
		this.tick = tick;
	}
	
	public BatchEventType getType() { return this.type; }
	public void setType(BatchEventType type) { this.type = type; }

	/**
	 * @return the current tick (only guaranteed to be valid in a TICK_CHANGED event)
	 */
	public double getTick() { return this.tick; }
	public void setTick(double tick) { this.tick = tick; }
}
