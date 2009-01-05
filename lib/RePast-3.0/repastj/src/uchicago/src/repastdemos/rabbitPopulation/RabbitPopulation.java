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
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.gui.Named;
import uchicago.src.sim.gui.RoundRectNetworkItem;
import uchicago.src.sim.math.CEquation;
import uchicago.src.sim.network.DefaultDrawableNode;

/**
 * Calculates the current population of rabbits based on the current number of births and eaths. This 
 * encapsulates the population equation in the Rabbit Population model. The equation is
 * population + (births - deaths) * dt
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class RabbitPopulation extends DefaultDrawableNode implements CustomProbeable, Named  {
  
  private double population = 2;
  private CEquation equation;
  private Births births;
  private Deaths deaths;

  /**
   * Creates a rabbit population using the specified schedule.
   * @param schedule
   */ 
  public RabbitPopulation(Schedule schedule) {
    super(new RoundRectNetworkItem(146, 15));
    setNodeLabel("RabbitPopulation");
    equation = new CEquation(this, schedule, "population + (births - deaths) * dt", "population");
  }
  
  /**
   * Initializes this RabbitPopulation with the specified births and deaths. These are queried when
   * the CEquation encapsulated by this RabbitPopulation is evaluated. 
   * 
   * @param births
   * @param deaths
   */ 
  public void init(Births births, Deaths deaths) {
    this.births = births;
    this.deaths = deaths;
  }

  public CEquation getEquation() {
    return equation;
  }

  public void setEquation(CEquation equation) {
    this.equation = equation;
  }

  public double getPopulation() {
    return population;
  }

  public void setPopulation(double population) {
    if (population < 0) population = 0;
    this.population = population;
  }
  
  public double getBirths() {
    return births.getBirths();
  }
  
  public double getDeaths() {
    return deaths.getDeaths();
  }
  
  public void step() {
    equation.evaluateAndAssign();
  }

  // implements Named interface
  public String getName() {
    return "Rabbit Population";
  }

  // implements CustomProbeable interface
  public String[] getProbedProperties() {
    return new String[]{"equation", "population"};
  }
}
