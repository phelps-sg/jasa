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

/**
 * An event fired by changes to a model's properties/parameters. A
 * PropertyEvent identifies the property associated with the PropertyEvent
 * and the current value of that Property. The property identifier should be
 * some simulation specific constant.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class PropertyEvent {

  private Object value;
  private int propertyId;

  /**
   * Creates a new PropertyEvent using the specified paramters.
   *
   * @param propertyId the integer identifier of the property associated with
   * this event. This should be some simulation specific constant.
   *
   * @param propertyValue the value of the property.
   */
  public PropertyEvent(int propertyId, Object propertyValue) {
    value = propertyValue;
    this.propertyId = propertyId;
  }

  /**
   * Gets the property identifier for this event.
   */
  public int getPropertyId() {
    return propertyId;
  }

  /**
   * Gets the value of this property.
   */
  public Object getPropertyValue() {
    return value;
  }
}