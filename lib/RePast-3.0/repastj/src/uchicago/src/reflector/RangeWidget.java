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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import uchicago.src.sim.util.SimUtilities;

/**
 * Gui widget for range properties. This is comprised of a slider and
 * a text box. The text box updates to reflect the slider position and
 * vise-versa.
 *
 * @version $Revision$ $Date$
 */

public class RangeWidget extends JPanel implements PropertyWidget {

  private String propertyName;
  private JSlider slider;
  private JTextField field;
  private int min, max;
  private ArrayList listeners = new ArrayList();
  //private boolean noError = true;
  
  public RangeWidget(int min, int max, int tickSpacing) {
    super(new FlowLayout());
    slider = new JSlider(min, max);
    field = new JTextField(String.valueOf(max).length());
    add(slider);
    add(field);
    this.min = min;
    this.max = max;
    slider.setMajorTickSpacing(tickSpacing);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);

    slider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent evt) {
          if (evt.getSource() == field) {
            // change the value to the fields value
            // check for non-integer value.
            setValue(new Integer(field.getText()));
          } else {
            // change the field to the slider value
            field.setText(String.valueOf(slider.getValue()));
            if (slider.getValueIsAdjusting() == false) fireActionPerformed();
          }
        }
      });

    field.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          //if (noError) {
          setSlider();
          setValue(new Integer(field.getText()));
          fireActionPerformed();
            //}
        }
      });

    field.addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent evt) {
          //if (noError) {
          setSlider();
          fireActionPerformed();
            // }
        }
      });
  }

  private void setSlider() {
    try {
      Integer i = new Integer(field.getText());
      setValue(i);
    } catch (NumberFormatException ex) {
      Integer i = (Integer)getValue();
      field.setText(i.toString());
      setValue(i);

      /*
      // we set noError to false here so that when we pop up the error
      // dialog this doesn't recall this setSlider method when the
      // text field loses focus.
      noError = false;
      SimUtilities.showError("Invalid Parameter for " + propertyName +
			     "\nRange properties must be integers", ex);
      noError = true;
      */
    }
  }
    

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String name) {
    propertyName = name;
  }

  public void setValue(Object val) {
    int iVal = ((Integer)val).intValue();
    iVal = iVal < min ? min : iVal > max ? max : iVal;
    field.setText(String.valueOf(iVal));
    slider.setValue(iVal);
  }

  public Object getValue() {
    return new Integer(slider.getValue());
  }

  public void requestFocus() {
    slider.requestFocus();
  }
  
  public void addActionListener(ActionListener l) {
    listeners.add(l);
  }

  private void fireActionPerformed() {
    ArrayList list;
    synchronized (listeners) {
      list = (ArrayList)listeners.clone();
    }

    for (int i = 0; i < list.size(); i++) {
      ActionListener l = (ActionListener)list.get(i);
      l.actionPerformed(new ActionEvent(this, 0, "action"));
    }
  }
  
  public void setEnabled(boolean b) {
    slider.setEnabled(b);
    field.setEnabled(b);
  }
}
