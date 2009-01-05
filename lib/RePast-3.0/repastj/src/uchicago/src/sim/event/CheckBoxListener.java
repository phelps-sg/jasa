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
package uchicago.src.sim.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

/**
 * Abstract implementation of a actionListener customized for a JCheckBox.
 * When the checkbox is clicked the execute() method will be called. A
 * modeler will implement this execute() method to provide the appropriate
 * functionality.
 * The following values are available to sub-classes.
 * <ul>
 * <li> isSelected - a boolean value indicating whether the checkbox is
 * selected.</li>
 * <li> actionEvent - the java.awt.event.ActionEvent fired by the checkbox.
 * </li> 
 * </ul>
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public abstract class CheckBoxListener implements ActionListener {

  protected boolean isSelected = true;
  protected ActionEvent actionEvent;

  /**
   * Reponds to ActionEvent fired by a JCheckBox. This method sets
   * this isSelected ivar and the actionEvent and then calls the user
   * implemented execute() method.
   */
  public void actionPerformed(ActionEvent evt) {
    JCheckBox box = (JCheckBox)evt.getSource();
    isSelected = box.isSelected();
    actionEvent = evt;
    execute();
  }

  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  /**
   * Called whenever the JCheckBox for which this is a listener is
   * clicked. A user can check for selection with the isSelected ivar.
   */
  public abstract void execute();

}