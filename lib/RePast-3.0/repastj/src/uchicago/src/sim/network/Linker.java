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

import uchicago.src.collection.RangeMap;
import uchicago.src.sim.util.Random;

/**
 * Creates links between nodes based on some <code>ProbabilityRule</code>.
 * The idea here is that a Node will have an associated Linker. It will
 * use Linker to create links with the other Nodes in the network. The
 * appropriate calling sequence, assuming the Linker is already
 * constructed, is:
 * <code><pre>
 * linker.init();
 * linker.makeLink(this, new SomeEdge());
 * </pre><code>
 *
 * where this is the node that contains the linker, and SomeEdge is
 * whatever sort of Edge is being used in the model.
 *
 * @version $Revision$ $Date$
 */

public class Linker {

  private ProbabilityRule probRule;
  private RangeMap map = new RangeMap();

  /**
   * Creates a Linker that uses the specified ProbabilityRule to determine
   * link creation.
   *
   * @param probRule the ProbabilityRule to use in link creation
   */
  public Linker(ProbabilityRule probRule) {
    this.probRule = probRule;
    init();
  }
  
  /**
   * Sets the ProbabilityRule used by this Linker.
   *
   * @param probRule the ProbabilityRule to use in link creation
   */
  public void setProbabilityRule(ProbabilityRule probRule) {
    this.probRule = probRule;
  }

  /**
   * Initializes this Linker for link creation and getting probabilties.
   * This recreates the probability map incorporating any changes that
   * resulted from calls to update. This should be called before any
   * calls to makeLink, getProbabilityFor, or getNodeForLink if the
   * result of those calls is to reflect any updates.
   */
  public void init() {
    map = probRule.makeProbabilityMap(map);
  }

  /**
   * Returns a random object from the probabililty map created by the
   * ProbabilityRule.
   */
  public Object getNodeForLink() {
    double index = Random.uniform.nextDoubleFromTo(0d, 1d);
    return map.get(index);
  }

  /**
   * Returns the probability map.
   */
  public RangeMap getMap() {
    return map;
  }

  /**
   * Updates the probability of the specified object in the
   * probability map. This calls the corresponding update method in
   * the ProbabilityRule passed in the constructor.  <b> The results
   * of this call are not reflected in the probability map until
   * init() is called.<b>
   *
   * @param o the object whose probability is to be updated.
   */
  public void update(Object o) {
    probRule.update(o);
  }


  /**
   * Updates the probability of the specified object in the
   * probability map with the specified amount. This calls the
   * corresponding update method in the ProbabilityRule passed in the
   * constructor.  <b> The results of this call are not reflected in
   * the probability map until init() is called.<b>
   *
   * @param o the object whose probability is to be updated.
   * @param amt the amt to update.
   */
  public void update(Object o, float amt) { 
    probRule.update(o, amt); 
  } 

  /**
   * Gets the probability for the specified object according to the
   * Probability Rule passed in the constructor.
   */
  public double getProbabilityFor(Object o) {
    return probRule.getProbability(o);
  }

  /**
   * Makes a link between the specified Node using the specified Edge
   * and a Node chosen at random from the probability map, and calls
   * this Linker's update method with this random node and a value of
   * 1 as the arguments. <b>Note</b> that this only adds an OutEdge to
   * the from node, and nothing to the toNode.
   *
   * @param from the node to make the link from.
   * @param edge the Edge to make the link out of.
   * @return the random node that is the target of the link.
   */
  public Node makeLink(Node from, Edge edge) {
    return makeLink(from, edge, 1f);
  }

  /**
   * Makes a link between the specified Node using the specified Edge
   * and a Node chosen at random from the probability map and calls
   * this Linker's update method with this random node and the
   * specified value as arguments. <b>Note that this only adds an
   * OutEdge to the from node, and nothing to the toNode.
   *
   * @param from the node to make the link from.
   * @param edge the Edge to make the link out of.
   * @param amtToUpdate the amount to pass to this Linker's update method.
   * @return the random node that is the target of the link.
   */
  public Node makeLink(Node from, Edge edge, float amtToUpdate) {
    Node to = (Node)getNodeForLink();
    edge.setFrom(from);
    edge.setTo(to);
    from.addOutEdge(edge);
    update(to, amtToUpdate);
    return to;
  }
}
