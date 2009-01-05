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
package uchicago.src.sim.math;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

import uchicago.src.reflector.PropertyWidget;

public class CEquationPropertyWidget extends JTextField implements PropertyWidget {

  private String propertyName = null;
  private CEquation equation;

  public CEquationPropertyWidget(int cols) {
    super(cols);
    super.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        if (equation != null) {
          equation.setEquation(getText().trim());
        }
      }
    });
  }

  public void setPropertyName(String propName) {
    propertyName = propName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setValue(Object value) {
    if (value == null) {
      setText("");
      setEnabled(false);
    } else {
      equation = (CEquation)value;
      setText(equation.getEquation());
    }
  }

  public Object getValue() {
    return equation == null ? "" : equation.getEquation();
  }

  // we ignore any outside action and only follow ourselves.
  public void addActionListener(ActionListener l) {
    addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent evt) {
        fireActionPerformed();
      }
    });
  }
}
