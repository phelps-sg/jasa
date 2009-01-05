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

import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.math.CEquationFactory;
import uchicago.src.sim.network.EdgeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example model demonstrating how to use the CEquation and CEquationFactory classes 
 * to create system dynamics type models. This particular simple model of a rabbit population is based on that
 * described in http://sysdyn.clexchange.org/sdep/Roadmaps/RM5/D-4432-2.pdf.
 * 
 * The general idea here is to link the CEquations together so that the variables in one equation are 
 * the results of another. For example, population = population + (births - deaths) * dt. When this equation is
 * evaluated the value of births and deaths are the results of a previous evalutation of the births and deaths 
 * equations.
 * 
 * This model encapsulates the equations in classes (psuedo-agents) and associates them with one another via the
 * classes' init methods. The classes and the equations they encapsulate are listed below:
 * 
 * <ul>
 * <li>RabbitPopulation : population + (births - deaths) * dt
 * <li>Births : population * birthRate
 * <li>Deaths : (population / lifeTime) * deathMultiplier
 * <li>PopulationDensity : population / area
 * <li>DeathMultiplier : graph-type table lookup (0.00, 1.00), (100, 1.00), (200, 1.00), (300, 1.00), (400, 1.50), 
 * (500, 2.00), (600, 2.70), (700, 3.70), (800, 4.70), (900, 5.70), (1000, 7.50)
 * </ul>
 * 
 * The simulation proceeds by first evaluating and assigning the births, deaths, and population density equations. 
 * In the process of the evaluation these objects will query the population for its current value. The 
 * population equation is then evaluated and assigned. In doing so, the population equation will query the births
 * and deaths objects for the result of their evaluation. So, in short, we first calculate the number of births and
 * deaths based on the previous years population, and then use that data to calculate the new population.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class RabbitPopulationModel extends SimModelImpl {

  private DisplaySurface surface;
  private Schedule schedule;
  private CEquationFactory equationFactory;
  private RabbitPopulation population;
  private OpenSequenceGraph graph;
  private double initialPopulation = 2;
  private List agents = new ArrayList();

  public RabbitPopulationModel() {
  }

  private void buildModel() {
    equationFactory = new CEquationFactory(schedule);
    
    // RabbitPopulation contains its own CEquation as we need it to update and
    // assign after births and deaths have updated and assigned. Otherwise, if it
    // is evaluated before births and deaths have evaluated then it will use the older
    // non-evaluated versions of births and deaths.
    population = new RabbitPopulation(schedule);
    population.setPopulation(initialPopulation);
    agents.add(population);

    // Births etc. encapsulate CEquations created by a CEquationFactory.
    // The evaluation and assignment of CEquations created in this way is implicitly scheduled.
    Births births = new Births(equationFactory);
    births.addOutEdge(EdgeFactory.createDrawableEdge(births, population));
    agents.add(births);
    Deaths deaths = new Deaths(equationFactory);
    deaths.addOutEdge(EdgeFactory.createDrawableEdge(deaths, population));
    agents.add(deaths);
    PopulationDensity density = new PopulationDensity(equationFactory);
    agents.add(density);
    density.addOutEdge(EdgeFactory.createDrawableEdge(density, deaths));
    DeathMultiplier deathMult = new DeathMultiplier();
    agents.add(deathMult);
    deathMult.addOutEdge(EdgeFactory.createDrawableEdge(deathMult, deaths));

    // These init methods are used to associate the classes with one another. This allows
    // a CEquation to assign the result of one equation to the variable of another.
    population.init(births, deaths);
    births.init(population);
    deaths.init(population, deathMult);
    density.init(population);
    deathMult.init(density);
  }

  // builds the display
  private void buildDisplay() {
    // creates a sequence graph to plot the size of the population vs. tick count.
    graph = new OpenSequenceGraph("Population vs. Time", this);
    this.registerMediaProducer("Mouse Graph", graph);
    graph.addSequence("Population Size", new Sequence() {
      public double getSValue() {
        return population.getPopulation();
      }
    });

    graph.setAxisTitles("Time", "Population Size");
    graph.setXRange(0, 20);
    graph.setYRange(0, 300);

    // the equation agents are drawn as network nodes so we use a network 2d display here.
    Network2DDisplay display = new Network2DDisplay(agents, 400, 200);
    display.setDrawEdgesFirst(true);

    surface.addDisplayableProbeable(display, "Jiggle View");
    surface.addZoomable(display);
    surface.setBackground(java.awt.Color.white);
    addSimEventListener(surface);
  }


  private void buildSchedule() {
    // by creating all our CEquations except the one encapsulated by population
    // with CEquationFactory, the evaluation of those equations is implicitly scheduled. 
    // So, we only need to schedule the step method of RabbitPopulation.
    BasicAction everyTickAction = new BasicAction() {
      public void execute() {
        population.step();
        graph.step();
      }
    };

    // we shedule this last to ensure that the evaluation and assignment of 
    // our other equation agents takes place prior to the evaluation and assignment of
    // population.
    schedule.scheduleActionAtInterval(1, everyTickAction, Schedule.LAST);
  }

  public void begin() {
    buildModel();
    buildDisplay();
    buildSchedule();
    surface.display ();
    graph.display();
  }

  public void setup() {
    agents.clear();

    if (surface != null)
      surface.dispose();
    
    surface = null;
    if (graph != null) {
      graph.dispose();
      graph = null;
    }
    schedule = null;
    System.gc();

    surface = new DisplaySurface(this, "Rabbit Population");
    registerDisplaySurface("Rabbit Population", surface);
    schedule = new Schedule(1);
  }

  public String[] getInitParam() {
    String[] params = {"initialPopulation"};
    return params;
  }

  public double getInitialPopulation() {
    return initialPopulation;
  }

  public void setInitialPopulation(double initialPopulation) {
    this.initialPopulation = initialPopulation;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "Rabbit Population";
  }

  public static void main(String[] args) {
    uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit();
    RabbitPopulationModel model = new RabbitPopulationModel();
    if (args.length > 0)
      init.loadModel(model, args[0], false);
    else
      init.loadModel(model, null, false);
  }
}
