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
package uchicago.src.sim.util;

/**
 * A semantic event that indicates that an object has been
 * probed. ProbeEvents are passed to ProbeListeners'
 * <code>objectProbed</code> and <code>objectUnprobed</code>
 * methods. When used with ProbeListeners ProbeEvent's
 * <code>getPropertyName</code> will return an empty String, and
 * <code>getNewValue</code> will return
 * null. <code>getProbedObject</code> will return the object that has
 * been probed.
 *
 * @version $Revision$ $Date$
 */
public class ProbeEvent {

  private Object probedObj;
  private String propertyName = "";
  private Object newValue = null;

  /**
   * Constructs a ProbeEvent with the specified probed object.
   *
   * @param probedObject the object being probed
   */
  public ProbeEvent(Object probedObject) {
    this(probedObject, "", null);
  }

  /**
   * Constructs a ProbeEvent with the specified probed object, and the
   * specified propertyName.
   *
   * @param probedObject the object being probed
   * @param propertyName the name of the property being read
   */
  public ProbeEvent(Object probedObject, String propertyName) {
    this(probedObject, propertyName, null);
  }

  /**
   * Constructs a ProbeEvent with the specified probed object, the
   * specified propertyName, and the specified new value of the
   * property.
   *
   * @param probedObject the object being probed
   * @param propertyName the name of the property being written
   * @param newVal the new value of the property being written
   */
  public ProbeEvent(Object probedObject, String propertyName,
		    Object newVal)
  {
    this.probedObj = probedObject;
    this.propertyName = propertyName;
    this.newValue = newVal;
  }

  /**
   * Returns the object being probed.
   */
  public Object getProbedObject() {
    return probedObj;
  }

  /**
   * Returns the property name being written or read. This will be an
   * empty string when this ProbeEvent is used with a ProbeListener.
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Returns the new value of the property being written. This will be
   * null when this ProbeEvent is used with a ProbeListener.
   */
  public Object getNewValue() {
    return newValue;
  }
}
