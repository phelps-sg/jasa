package uk.ac.liv.auction.ec;

import ec.*;

import java.util.Vector;

/**
 * @author Steve Phelps
 */

public abstract class CoEvolutionaryProblem extends Problem {


  public abstract void evaluate( EvolutionState state,
				  Vector[] group,  // the individuals to evaluate together
				  int threadnum);


  public Object protoClone() throws CloneNotSupportedException {
    return this.clone();
  }

  public Object protoCloneSimple() {
    try {
      return protoClone();
    }
    catch( CloneNotSupportedException e ) {
      return null;  // never happens
    }
  }

}