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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.Named;
import uchicago.src.sim.util.SimUtilities;

/**
 * Button property for introspection panels. Clicking the button should
 * bring up a new introspection frame for the wrapped object.
 */

public class PropertyButton extends JButton implements PropertyWidget {

  private String propertyName = null;

  // this shouldn't be used
  private String value = "";
  private Object property = null;
  private boolean actionAdded = false;

  public PropertyButton(Object prop, boolean isEnabled) {
	super();
	String label = prop.getClass().toString();
	int dot = label.lastIndexOf(".");
	if (dot != -1 ) {
	  label = label.substring(dot + 1, label.length());
	}
    super.setText(label);
    super.setEnabled(isEnabled);
    this.property = prop;
  }

  private Action probe = new AbstractAction() {
    public void actionPerformed(ActionEvent evt) {
      final IntrospectFrame spector;
      String objName = "";
      if (property instanceof Named) {
        Named n = (Named)property;
        objName = n.getName();
      }

      if (property instanceof CustomProbeable) {
        CustomProbeable cp = (CustomProbeable)property;
        spector = new IntrospectFrame(property, objName, cp.getProbedProperties());
      } else {
        spector = new IntrospectFrame(property, objName);
      }

      try {
        spector.display();
      } catch (Exception ex) {
        SimUtilities.showError("Probing error", ex);
        ex.printStackTrace();
        System.exit(0);
      }
    }
  };

  // override any other kindo of action listener
  public void addActionListener(ActionListener listener) {
    if (!actionAdded) super.addActionListener(probe);
    actionAdded = true;
  }

  public void setPropertyName(String propName) {
      propertyName = propName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setValue(Object value) {
    if (value == null) setEnabled(false);
    else setEnabled(true);
    property = value;
  }

  public Object getValue() {
    return value.toString();
  }
}
