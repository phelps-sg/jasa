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
package uchicago.src.sim.engine;

import java.util.EventObject;

/**
 * A semantic event that indicates that the simulation run has paused or
 * stopped. The event is passed to SimEventListeners.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see SimEventListener
 */
public class SimEvent extends EventObject {

  public static final int STOP_EVENT = 0;
  public static final int PAUSE_EVENT = 1;
  public static final int END_EVENT = 2;
  public static final int RNG_SEED_EVENT = 3;
  public static final int START_EVENT = 4;
  private int id;

  /**
   * Constructs a SimEvent with the specified source, and the specified id.
   * The id or type can STOP_EVENT, START_EVENT, PAUSE_EVENT, END_EVENT, or
   * RNG_SEED_EVENT.
   *
   * @param source the source of this event
   * @param id or type of the event
   */
  public SimEvent(Object source, int id) {
    super(source);
    this.id = id;
  }

  /**
   * Gets the id (type) of this SimEvent. The id or type can STOP_EVENT,
   * START_EVENT, PAUSE_EVENT, END_EVENT, or RNG_SEED_EVENT.
   */
  public int getId() {
    return id;
  }
}
