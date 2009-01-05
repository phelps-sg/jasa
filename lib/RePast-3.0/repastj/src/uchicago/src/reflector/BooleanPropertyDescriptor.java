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
package uchicago.src.reflector;

/**
 * Descriptor for a model's boolean properties/parameters. Using a
 * BooleanPropertyDescriptor allows a boolean property to be represented
 * as a on/off checkbox in the properties panel.<p/>
 *
 * A BooleanPropertyDescriptor is typically setup in a model's constructor
 * as follows:
 * <code><pre>
 * BooleanPropertyDescriptor bd1 = new BooleanPropertyDescriptor("RecordData",
 *     false);
 * descriptors.put("RecordData", bd1);
 * </pre></code>
 * <p/>
 * This property descriptor describes the RecordData property/parameter of
 * some model, and thus assumes that the model contains a setRecordData
 * and a getRecordData or a isRecordData method. A BooleanPropertyDescriptor's
 * constructor takes a the name of the boolean property, and the default state
 * of that property, in this case false. The BooleanPropertyDescriptor is then
 * placed in the hashtable "descriptors" (an ivar of the SimModelImpl class).
 * When the model parameters are displayed in gui mode, the RecordData will
 * then be displayed as a check box.
 * <p/>
 * 
 * This class's widget is a PropertyCheckBox.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class BooleanPropertyDescriptor extends PropertyDescriptor {

  /**
   * Creates a BooleanPropertyDescriptor for the specified property, and
   * the specified selection state.
   *
   * @param name the name of the property
   * @param isSelected whether the checkbox representing the boolean
   * property should be selected or not by default.
   */
  public BooleanPropertyDescriptor(String name, boolean isSelected) {
    super(name, new PropertyCheckBox(isSelected));
  }
}