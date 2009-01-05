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
package uchicago.src.sim.topology;

import java.util.List;
import java.util.Set;

/**
 * This is an interface for a set of agents who all participate in the same
 * collection of relationships {@link RelationTopology}.  So this interface serves two purposes:
 * 1) It is a collection of agents, and 2) it acts as a mediator for
 * one or more semantically connected relationships.
 *
 * Towards the first purpose, this is implemented as a set.  Meaning, an object
 * should only exist in this collection once.  It can participate in as many
 * relationships as you want, however.
 *
 * Towards the second purpose, the relationships that are contained by this
 * Context should be related (and possibly affect one another).  So, for example,
 * it would make sense to have several space relationships and a network relationship
 * that is affected by spatial distance included in on Context, but it wouldn't
 * necessarily make sense to have a boss-employee relationship in the same Context
 * as a residential neighborhood relationship.  But fundamentally, this is up to the
 * modeller.
 *
 * @author Tom Howe
 * @version $Revision$
 * @see RelationTopology
 */
public interface Context extends Set{

  /**
   * Return a list Strings for the all of the relationship types used by this class.
   * @return
   */
  public String[] getRelationTypes();

  /**
   * Get all of the elements related to the parameter that share the given
   * relationship within the distance.
   * @param element The central element (whose neighbors we want)
   * @param relationType Which relationship we are interested in querying
   * @param distance The range for the query.
   * @return
   */
  public List getRelated(Object element, String relationType, double distance);

  /**
   * Add a Relationship of the given type between the two elements.  Issues of
   * directionality should be dealt with by the RelationTopology.  The
   * relationship should have the given strength.  Both of the elements
   * must be in the Context.
   *
   * @param element1
   * @param element2
   * @param relationType
   * @param distance
   */
  public void addRelation(Object element1, Object element2, String relationType,
                          double distance);

  /**
   * Remove the relationship between the two elements of the given relationship type.
   * Both of the elements must be included in the Context.
   *
   * @param element1
   * @param element2
   * @param relationType
   */
  public void removeRelation(Object element1, Object element2, String relationType);

  /**
   * This method should allow the entire context to be altered in some way.  With
   * this method the user should be able to make an alteration to some or all of the
   * contained {@link RelationTopology}.
   */
  public void update();

  /**
   * Add a new type of {@link RelationTopology} to this context.
   * @param top
   */
  public void addRelationType(RelationTopology top);

  /**
   * Determine if the two elements share a relationship in the {@link RelationTopology}
   * within the specified distance.
   *
   * @param element1
   * @param element2
   * @param distance
   * @param relationType
   * @return
   */
  public boolean areRelated(Object element1, Object element2, double distance,
                            String relationType);

  /**
   * Get the topological distance between the two elements in the given {@link
   * RelationTopology}.
   *
   * @param element1
   * @param element2
   * @param relationType)
   * @return
   */
  public double distance(Object element1, Object element2, String relationType);

  /**
   * Get the actual topology defined by the relationType.
   *
   * @param relationType
   * @return
   */
  public RelationTopology getRelationTopology(String relationType);
}
