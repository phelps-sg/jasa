
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
 *$$*/package uchicago.src.repastdemos.rabbitPopulation;

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.Named;
import uchicago.src.sim.gui.RoundRectNetworkItem;
import uchicago.src.sim.math.CEquation;
import uchicago.src.sim.math.CEquationFactory;
import uchicago.src.sim.network.DefaultDrawableNode;

/**
 * Calculates the number of rabbits born each year based on the current population. This encapsulates the birth 
 * equation in the Rabbit Population model. The equation is population * birthRate.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class Births extends DefaultDrawableNode implements CustomProbeable, Named {
  
  private float birthRate = 1.5f;
  private double births = 0;
  private CEquation equation;
  private RabbitPopulation rabbitPop;

  /**
   * Creates a Births using the specified CEquationFactory. This factory creates a CEquation
   * and schedules that CEquation's assignment and evaluation.
   * 
   * @param factory
   */ 
  public Births(CEquationFactory factory) {
    super(new RoundRectNetworkItem(107, 64));
    equation = factory.createEquation(this, "population * birthRate", "births", 1);
    setNodeLabel("Births");
  }
  
  /**
   * Initialize this PopulationDensity. The RabbitPopulation is queried for the current population when
   * the CEquation encapsulated by this Births instance is evaluated.
   * 
   * @param population
   */ 
  public void init(RabbitPopulation population) {
    rabbitPop = population;
    births = population.getPopulation() * birthRate;
  }

  public float getBirthRate() {
    return birthRate;
  }

  public void setBirthRate(float birthRate) {
    this.birthRate = birthRate;
  }

  public CEquation getEquation() {
    return equation;
  }

  public void setEquation(CEquation equation) {
    this.equation = equation;
  }

  public double getPopulation() {
    return rabbitPop.getPopulation();
  }

  public double getBirths() {
    return births;
  }

  public void setBirths(double births) {
    this.births = births;
  }
  
  // implements Named interface
  public String getName() {
    return "Births";
  }

  // implements CustomProbeable interface
  public String[] getProbedProperties() {
    return new String[]{"equation", "birthRate"};
  }
}
