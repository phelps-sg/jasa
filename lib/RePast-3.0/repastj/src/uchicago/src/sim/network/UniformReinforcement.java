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
package uchicago.src.sim.network;

import java.util.List;

import uchicago.src.collection.RangeMap;
import cern.colt.function.IntDoubleProcedure;
import cern.colt.map.OpenIntDoubleHashMap;


/**
 * A ProbabiltyRule that implements uniform reinforcement. The idea
 * here is that this UniformReinforcement contains a vector of weights
 * associated with a list of Objects. update(Object o) adds a specified
 * amount to the weight of the specified object. The probablity of each
 * object is that objects weight divided by the sum of all the weights.
 *
 * @version $Revision$ $Date$
 */
public class UniformReinforcement extends AbstractProbabilityRule {

  private float discount = 1f;
  private float amtToUpdate = 1f;

  private OpenIntDoubleHashMap curWeights = new OpenIntDoubleHashMap();

  /**
   * Create this UniformReinforcement using the specified list of objects and
   * the specified startWeight. The amount to update an objects weight defaults
   * to one.
   *
   * @param objs the objects to calculate probabilities for.
   * @param startWeight the initial weight for each object in the weight
   * vector
   */
  public UniformReinforcement(List objs, float startWeight) {
    this(objs, startWeight, 1f);
  }

  /**
   * Create this UniformReinforcement using the specified list of nodes and
   * the specified startWeight, and the specified amtToUpdate.
   *
   * @param objs the objects to calculate probabilities for.
   * @param startWeight the initial weight for each object in the weight
   * vector
   * @amtToUpdate the amount to update an objects weight in update(Object o)
   */
  public UniformReinforcement(List objs, float startWeight,
			      float amtToUpdate)
  {
    super(objs, startWeight);
    this.amtToUpdate = amtToUpdate;
  }

  /**
   * Create this UniformReinforcement using the specified list of objects and
   * the specified startWeight, excluding the specified object. The amount to
   * update an objects weight defaults to one.
   *
   * @param objs the objects to calculate probabilities for.
   * @param startWeight the initial weight for each object in the weight
   * vector
   * @param exclude the object to exclude from the list of objects
   */
  public UniformReinforcement(List objs, float startWeight, Object exclude) {
    this(objs, startWeight, exclude, 1f);
  }

  /**
   * Create this UniformReinforcement using the specified list of nodes,
   * excluding the specified object, the specified startWeight, and the
   * specified amtToUpdate.
   *
   * @param objs the objects to calculate probabilities for.
   * @param startWeight the initial weight for each object in the weight
   * vector
   * @amtToUpdate the amount to update an objects weight in update(Object o)
   * @param exclude the object to exclude from the list of objects
   */
  public UniformReinforcement(List objs, float startWeight, Object exclude,
			      float amtToUpdate)
  {
    super(objs, startWeight, exclude);
    this.amtToUpdate = amtToUpdate;
  }

  public void setPastDiscount(float val) {
    if (val < 0 || val > 1) {
      throw new IllegalArgumentException("Discount must be between 0 and 1");
    }

    discount = val;
  }

  public RangeMap makeProbabilityMap(RangeMap map) {
    if (discount != 1) {
      
      for (int i = 0; i < weights.size(); i++) {
	float f = weights.getQuick(i) * discount;
	weights.setQuick(i, f);
      }

      curWeights.forEachPair(new IntDoubleProcedure() {
	  public boolean apply(int index, double val) {
	    float f = weights.getQuick(index);
	    weights.setQuick(index, (float)(f + val));
	    return true;
	  }
	});
      curWeights.clear();
    }

    return super.makeProbabilityMap(map);
  }

  /**
   * Gets the probability for the specified object. The probabilty is
   * calculated by dividing the current weight for this object by the
   * sum of all the weights.
   */
  public double getProbability(Object o) {
    return getWeight(o) / sum;
  }

  /**
   * Adds the amount to update (either 1 or specified in the constructor) to
   * the weight of the specified object.
   */
  public void update(Object o) {
    update(o, amtToUpdate);
  }

  public void update(Object o, float amt) {
    if (discount == 1) addToNodeWeight(o, amt);
    else {
      int index = ((Integer)weightMap.get(o)).intValue();
      curWeights.put(index, amt);
    }
  }
}
