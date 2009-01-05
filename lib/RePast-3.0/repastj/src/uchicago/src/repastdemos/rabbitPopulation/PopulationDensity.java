package uchicago.src.repastdemos.rabbitPopulation;

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.Named;
import uchicago.src.sim.gui.RoundRectNetworkItem;
import uchicago.src.sim.math.CEquation;
import uchicago.src.sim.math.CEquationFactory;
import uchicago.src.sim.network.DefaultDrawableNode;

/**
 * Calculates the population density based on the current population of rabbits and the current area. This 
 * encapsulates the population density equation in the Rabbit Population model. The equation is:
 * population / area where population is the current rabbit population.
 * 
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class PopulationDensity extends DefaultDrawableNode implements CustomProbeable, Named {
  
  private float area = 1.0f;
  private double density;
  private CEquation equation;
  private RabbitPopulation rabbitPop;

   /**
   * Creates a PopulationDensity using the specified CEquationFactory. This factory creates a CEquation
   * and schedules that CEquation's assignment and evaluation.
   * 
   * @param factory
   */ 
  public PopulationDensity(CEquationFactory factory) {
    super(new RoundRectNetworkItem(131, 114));
    setNodeLabel("Population Density");
    equation = factory.createEquation(this, "population / area", "density", 1);
  }
  
  /**
   * Initialize this PopulationDensity. The RabbitPopulation is queried for the current population when
   * the CEquation encapsulated by this PopulationDensity instance is evaluated.
   * 
   * @param population
   */ 
  public void init(RabbitPopulation population) {
    rabbitPop = population;
    density = rabbitPop.getPopulation() / 1.0f;
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


  public float getArea() {
    return area;
  }

  public void setArea(float area) {
    this.area = area;
  }

  public double getDensity() {
    return density;
    //return rabbitPop.getPopulation() / area;
  }

  public void setDensity(double density) {
    this.density = density;
  }
  
   // implements Named interface
  public String getName() {
    return "Population Density";
  }

  // implements CustomProbeable interface
  public String[] getProbedProperties() {
    return new String[]{"equation", "area"};
  }
}
