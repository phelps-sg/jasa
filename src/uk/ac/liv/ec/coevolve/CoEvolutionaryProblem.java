package uk.ac.liv.ec.coevolve;

import ec.*;

import java.util.Vector;

/**
 * @author Steve Phelps
 */

public interface CoEvolutionaryProblem  {


  public void evaluate( EvolutionState state,
				  Vector[] group,  // the individuals to evaluate together
				  int threadnum);


}