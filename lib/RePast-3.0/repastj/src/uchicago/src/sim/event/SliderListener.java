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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Abstract implementation of a ChangeListener. Will call the execute
 * method whenever the JSlider being listened to fires a ChangeEvent, that is,
 * whenever the slider is moved the execute() method will be called.
 * A modeler will implement this execute() method. Two variables are
 * available for use in the execute method.<ul>
 * <li> value - the current value of the slider
 * <li> isAdjusting - a boolean indicating whether the slider is in the
 * process of being adjusted. False indicates that the slider is no longer
 * being moved.
 * <li> isSlidingLeft - a boolean indicating whether the slider is being
 * adjusted to the left.
 * </ul>
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public abstract class SliderListener implements ChangeListener {

  protected int prevValue;
  protected int value;
  protected boolean isAdjusting;
  protected boolean isSlidingLeft;


  public void stateChanged(ChangeEvent evt) {
    JSlider slider = (JSlider)evt.getSource();
    prevValue = value;
    value = slider.getValue();
    isAdjusting = slider.getValueIsAdjusting();
    if (isAdjusting) {
      isSlidingLeft = value < prevValue;
    }
    execute();
  }

  public void setFirstVal(int val) {
    prevValue = val;
    value = val;
  }

  /**
   * Called whenever the JSlider for which this is a listener is moved.
   */
  public abstract void execute();

}
