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
package uchicago.src.repastdemos.mousetrap;

import java.awt.Color;
import java.util.Hashtable;

import uchicago.src.reflector.DescriptorContainer;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DTorus;
import uchicago.src.sim.util.Random;
import cern.jet.random.Uniform;

/**
 * The "mousetrap" agent object. A mousetrap contains some n number of balls
 * when triggered, it will throw these balls into the air to land on other
 * mousetraps. This is implemented in the trigger method. There are no
 * actual ball objects but their effect is simulated by finding
 * some n number of neighbors where n is the number of "balls" and scheduling
 * their trigger methods for some time in the "near" future.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.repastdemos.mousetrap.MouseTrapModel
 */

public class MouseTrap implements Drawable, DescriptorContainer {

  private boolean triggered = false;
  private int x, y;
  private MouseTrapModel model;
  private int maxDistance;
  private int maxTriggerTime;
  private Object2DTorus space;

  private Hashtable descriptors = new Hashtable();

  public MouseTrap(int x, int y, MouseTrapModel model, Object2DTorus space) {
    this.x = x;
    this.y = y;
    this.model = model;
    maxDistance = model.getMaxTriggerDistance();
    maxTriggerTime = model.getMaxTriggerTime();
    this.space = space;

  }

  public boolean isTriggered() {
    return triggered;
  }

  public void setTriggered(boolean val) {
    triggered = val;
  }

  /**
   * Triggers this MouseTrap. Triggering simulates the throwing of some number
   * of balls into the air and having them land on some number of neighboring
   * mousetraps. This is accomplished by finding some number of neighboring
   * mousetraps on the torus and scheduling their trigger methods for some
   * time in the near future.
   */

  public void trigger() {

    model.removeOneBall();

    if (!triggered && (model.getTriggerProbability() >= 1.0
                      || Uniform.staticNextDoubleFromTo(0, 1) <
                      model.getTriggerProbability()))
    {

      triggered = true;
      model.addOneTriggered();

      for (int i = 0; i < model.getNumBalls(); i++) {
        model.addOneBall();

        int xTrigger = Uniform.staticNextIntFromTo(-maxDistance, maxDistance);
        int yTrigger = Uniform.staticNextIntFromTo(-maxDistance, maxDistance);

        MouseTrap mt = (MouseTrap)space.getObjectAt(x + xTrigger, y + yTrigger);

        if (mt != null) {
          //System.out.println ("model.getTickCountDouble() = " + model.getTickCountDouble());
          double time = model.getTickCount() +
                  Random.uniform.nextIntFromTo(1, maxTriggerTime);
          model.scheduleTrigger(time, mt);
        }
      }
    }
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void draw(SimGraphics g) {
    if (triggered)
      g.drawFastRect(Color.red);
  }

  // DescriptorContainer
  public Hashtable getParameterDescriptors() {
    return descriptors;
  }
}

