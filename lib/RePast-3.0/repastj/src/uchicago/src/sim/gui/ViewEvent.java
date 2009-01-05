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
package uchicago.src.sim.gui;

import java.util.EventObject;

/**
 * A semantic event that indicates what kind of display event occured. A
 * DisplaySurface queries a displayable for its DisplayInfo and and uses
 * this info to setup the view menu. This menu will send ViewEvents
 * to Displayables with an id indicating what kind of event occured.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ViewEvent extends EventObject {

  private int id;
  private boolean view;

  /**
   * Creates a new ViewEvent with the specified source, id, and whether
   * this event indicates to show the view specified by the id.
   *
   * @param source the source of the event
   * @param id the id of the event
   * @param showView whether this is a show view event
   */
  public ViewEvent(Object source, int id, boolean showView) {
    super(source);
    this.id = id;
    view = showView;
  }

  /**
   * Get the id of this event
   */
  public int getId() {
    return id;
  }

  /**
   * Is this a show view event.
   *
   * @return true if this is a show view event, otherwise false.
   */
  public boolean showView() {
    return view;
  }
}
