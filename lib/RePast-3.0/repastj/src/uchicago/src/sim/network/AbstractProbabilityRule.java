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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import uchicago.src.collection.RangeMap;
import cern.colt.list.FloatArrayList;

/**
 * Implements some generic functionality for ProbabilityRule-s. The idea
 * here is that this AbstractProbabilityRule contains a vector of weights
 * associated with a list of Objects. Sub-classes can use this vector
 * of weights in their update and getProbability methods. For example,
 * update may add or substract some amount from an objects associated
 * weight and recalculate the probability accordingly.
 *
 * @version $Revision$ $Date$
 */

public abstract class AbstractProbabilityRule implements ProbabilityRule {

  protected FloatArrayList weights;
  protected Hashtable weightMap;
  protected double sum = 0;

  /**
   * Creates this AbstractProbabilityRule to operate on the specified
   * list of Objects, excluding the specified node, and assinging the
   * Objects the specified starting weight.
   *
   * @param objs the list Objects to calculate probabilities for.
   * @param startWeight the initial weight in the vector of weights for
   * each node
   * @param exclude the object to exclude when working with the Objects list
   */
  public AbstractProbabilityRule(List objs, float startWeight,
				 Object exclude)
  {
    int size = objs.size();
    weights = new FloatArrayList();
    weightMap = new Hashtable();
    int j = 0;
    for (int i = 0; i < size; i++) {
      Object o = objs.get(i);
      if (o != exclude) {
	weightMap.put(objs.get(i), new Integer(j++));
	weights.add(startWeight);
      }
    }
  }

  /**
   * Creates this AbstractProbabilityRule to operate on the specified
   * list of Objects,  and assinging the Objects the specified starting weight.
   *
   * @param objs the list Objects to calculate probabilities for.
   * @param startWeight the initial weight in the vector of weights for
   * each node
   */
  public AbstractProbabilityRule(List objs, float startWeight) {
    int size = objs.size();
    weights = new FloatArrayList(size);
    weightMap = new Hashtable();
    for (int i = 0; i < size; i++) {
      //Object o = objs.get(i);
      weightMap.put(objs.get(i), new Integer(i));
      weights.add(startWeight);
    }
  }

  /**
   * Adds the specified value to the current weight for the
   * specified node.
   *
   * @param node the node whose weight is increased (or decreased).
   * @param val the value to increase or decrease the node weight.
   */ 
  public float addToNodeWeight(Object node, float val) {
    int index = ((Integer)weightMap.get(node)).intValue();
    float f = weights.getQuick(index) + val;
    weights.setQuick(index, f);
    return f;
  } 

  /**
   * Adds a node with the specified weight.
   */
  public void addNode(Object node, float startWeight) {
    weightMap.put(node, new Integer(weights.size()));
    weights.add(startWeight);
  }

  /**
   * Gets the weight of the specified node.
   */
  protected float getWeight(Object o) {
    int index = ((Integer)weightMap.get(o)).intValue();
    return weights.getQuick(index);
  }

  /**
   * Returns the index position in the list of weights for the
   * specified object. The returned value is the index for the weight
   * value of the specified object.
   */
  public int getWeightListIndex(Object o) {
    return ((Integer)weightMap.get(o)).intValue();
  }

  /**
   * Recreate the probability map for the list of Objects
   * contained by this AbstractProbabilityRule using the specified
   * RangeMap.
   */
  public RangeMap makeProbabilityMap(RangeMap map) {
    calcSum();
    map.clear();
    Enumeration enumer = weightMap.keys();
    //BigDecimal lowerBound = new BigDecimal(0);
    float lowerBound = 0;
    while (enumer.hasMoreElements()) {
      Object o = enumer.nextElement();
      //DefaultNode node = (DefaultNode)o;
      float prob = (float)getProbability(o);
      if (prob + lowerBound != lowerBound) {
	try {
	  map.put(lowerBound, o);
	} catch (IllegalArgumentException ex) {
	  System.out.println("lowerBound: " + lowerBound);
	  System.out.println("prob: " + prob);
	}
	lowerBound += prob;
      }
    }
    return map;
  }

  /**
   * Calculates the sum of the vector of weights.
   */
  protected void calcSum() {
    int size = weights.size();
    sum = 0;
    for (int i = 0; i < size; i++) {
      sum += weights.getQuick(i);
    }
  }
  
  public abstract double getProbability(Object o);
  public abstract void update(Object o);
  public abstract void update(Object o, float amt);
}

  
      
      
  
