package uk.ac.liv.ai.learning;

/**
 * @author Steve Phelps
 */

public interface StimuliResponseLearner {

  public int act();

  public void reward( double reward );

}