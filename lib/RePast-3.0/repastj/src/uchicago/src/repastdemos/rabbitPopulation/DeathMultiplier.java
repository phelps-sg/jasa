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
package uchicago.src.repastdemos.rabbitPopulation;

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.Named;
import uchicago.src.sim.gui.RoundRectNetworkItem;
import uchicago.src.sim.network.DefaultDrawableNode;


/**
 * Creates a death multiplier. The death multiplier inceases the number of deaths depending on
 * the density. Higher density makes a higher death multiplier. 
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class DeathMultiplier extends DefaultDrawableNode implements CustomProbeable, Named {

  private PopulationDensity pDensity;

  public DeathMultiplier() {
    super(new RoundRectNetworkItem(282, 114));
    setNodeLabel("Death Multiplier");
  }

  public void init(PopulationDensity density) {
    this.pDensity = density;
  }

  public float getDeathMultiplier() {
    double density = pDensity.getDensity();
    if (density <= 300) {
      return 1.0f;
    } else if (density <= 400) {
      return (float) (((density - 300) / 100) * .5) + 1;
    } else if (density <= 500) {
      return (float) (((density - 400) / 100) * .5) + 1.5f;
    } else if (density <= 600) {
      return (float) (((density - 500) / 100) * .7) + 2;
    } else if (density <= 700) {
      return (float) (((density - 600) / 100)) + 2.7f;
    } else if (density <= 800) {
      return (float) (((density - 700) / 100)) + 3.7f;
    } else if (density <= 900) {
      return (float) (((density - 800) / 100)) + 4.7f;
    } else if (density <= 1000) {
      return (float) (((density - 900) / 100) * 1.8f) + 5.7f;
    } 
    
    return 7.5f;
  }
  
   // implements Named interface
  public String getName() {
    return "Death Multiplier";
  }

  // implements CustomProbeable interface
  public String[] getProbedProperties() {
    return new String[]{"deathMultiplier"};
  }
}
