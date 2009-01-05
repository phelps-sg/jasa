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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a default implementation of the Context interface.  It doesn't have any
 * {@link RelationTopology} included at this point, but it is ready to accept them.
 * It, obviously, also hasn't implemented the updated method.  But it should be a good
 * starting point for any Context.  It is backed by a {@link LinkedHashSet}, so the
 * iterator order of the elements should be consistent.
 *
 * @author Tom Howe
 * @version $Revision$
 */
public class DefaultContext implements Context{
  private HashMap relationMap;
  private final Set elements = new LinkedHashSet();

  public DefaultContext(){
    relationMap = new LinkedHashMap();
  }

  /**
   * Get all of the elements related to the parameter that share the given
   * relationship within the distance.
   * @param element The central element (whose neighbors we want)
   * @param relationType Which relationship we are interested in querying
   * @param distance The range for the query.
   * @return
   */
  public List getRelated(Object element, String relationType, double distance) {
    RelationTopology relation = (RelationTopology) relationMap.get(relationType);
    return relation.getRelations(element, distance);
  }

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
                          double distance) {
    ModifyableTopology relation = (ModifyableTopology) relationMap.get(relationType);
    relation.addRelation(element1, element2, distance);
  }

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
                            String relationType) {
    RelationTopology top = (RelationTopology) relationMap.get(relationType);
    if(top.getRelations(element1, distance).contains(element2)){
      return true;
    }
    return false;
  }

  /**
   * Get the topological distance between the two elements in the given {@link
   * RelationTopology}.
   *
   * @param element1
   * @param element2
   * @param relationType)
   * @return
   */
  public double distance(Object element1, Object element2, String relationType) {
    RelationTopology top = (RelationTopology) relationMap.get(relationType);
    return top.distance(element1, element2);
  }

  /**
   * Get the actual topology defined by the relationType.
   *
   * @param relationType
   * @return
   */
  public RelationTopology getRelationTopology(String relationType) {
    RelationTopology top = (RelationTopology) relationMap.get(relationType);
    return top;
  }

  /**
   * Return a list Strings for the all of the relationship types used by this class.
   * @return
   */
  public String[] getRelationTypes() {
    Set keys = relationMap.keySet();
    String[] names = new String[keys.size()];
    Object[] relations = keys.toArray(names);
    return (String[]) relations;
  }

  /**
   * Remove the relationship between the two elements of the given relationship type.
   * Both of the elements must be included in the Context.
   *
   * @param element1
   * @param element2
   * @param relationType
   */
  public void removeRelation(Object element1, Object element2, String relationType) {
    ModifyableTopology relation = (ModifyableTopology) relationMap.get(relationType);
    relation.removeRelation(element1, element2);
  }

  /**
   * This method should allow the entire context to be altered in some way.  With
   * this method the user should be able to make an alteration to some or all of the
   * contained {@link RelationTopology}.
   *
   * This method is not implemented in this class.
   */
  public void update() {}

  /**
   * Add a new type of {@link RelationTopology} to this context.
   * @param top
   */
  public void addRelationType(RelationTopology top) {
    relationMap.put(top.getRelationType(), top);
  }

  /**
   * Returns the number of elements in this set (its cardinality).  If this
   * set contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of elements in this set (its cardinality).
   */
  public int size() {
    return elements.size();
  }

  /**
   * Returns <tt>true</tt> if this set contains no elements.
   *
   * @return <tt>true</tt> if this set contains no elements.
   */
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * Returns <tt>true</tt> if this set contains the specified element.  More
   * formally, returns <tt>true</tt> if and only if this set contains an
   * element <code>e</code> such that <code>(o==null ? e==null :
   * o.equals(e))</code>.
   *
   * @param o element whose presence in this set is to be tested.
   * @return <tt>true</tt> if this set contains the specified element.
   * @throws ClassCastException if the type of the specified element
   * 	       is incompatible with this set (optional).
   * @throws NullPointerException if the specified element is null and this
   *         set does not support null elements (optional).
   */
  public boolean contains(Object o) {
    return elements.contains(o);
  }

  /**
   * Returns an array containing all of the elements in this set.
   * Obeys the general contract of the <tt>Collection.toArray</tt> method.
   *
   * @return an array containing all of the elements in this set.
   */
  public Object[] toArray() {
    return elements.toArray();
  }

  /**
   * Returns an array containing all of the elements in this set; the
   * runtime type of the returned array is that of the specified array.
   * Obeys the general contract of the
   * <tt>Collection.toArray(Object[])</tt> method.
   *
   * @param a the array into which the elements of this set are to
   *		be stored, if it is big enough; otherwise, a new array of the
   * 		same runtime type is allocated for this purpose.
   * @return an array containing the elements of this set.
   * @throws    ArrayStoreException the runtime type of a is not a supertype
   *            of the runtime type of every element in this set.
   * @throws NullPointerException if the specified array is <tt>null</tt>.
   */
  public Object[] toArray(Object a[]) {
    return elements.toArray(a);
  }

  /**
   * Adds the specified element to this set if it is not already present
   * (optional operation).  More formally, adds the specified element,
   * <code>o</code>, to this set if this set contains no element
   * <code>e</code> such that <code>(o==null ? e==null :
   * o.equals(e))</code>.  If this set already contains the specified
   * element, the call leaves this set unchanged and returns <tt>false</tt>.
   * In combination with the restriction on constructors, this ensures that
   * sets never contain duplicate elements.<p>
   *
   * The stipulation above does not imply that sets must accept all
   * elements; sets may refuse to add any particular element, including
   * <tt>null</tt>, and throwing an exception, as described in the
   * specification for <tt>Collection.add</tt>.  Individual set
   * implementations should clearly document any restrictions on the the
   * elements that they may contain.
   *
   * @param o element to be added to this set.
   * @return <tt>true</tt> if this set did not already contain the specified
   *         element.
   *
   * @throws UnsupportedOperationException if the <tt>add</tt> method is not
   * 	       supported by this set.
   * @throws ClassCastException if the class of the specified element
   * 	       prevents it from being added to this set.
   * @throws NullPointerException if the specified element is null and this
   *         set does not support null elements.
   * @throws IllegalArgumentException if some aspect of the specified element
   *         prevents it from being added to this set.
   */
  public boolean add(Object o) {
    return elements.add(o);
  }

  /**
   * Removes the specified element from this set if it is present (optional
   * operation).  More formally, removes an element <code>e</code> such that
   * <code>(o==null ?  e==null : o.equals(e))</code>, if the set contains
   * such an element.  Returns <tt>true</tt> if the set contained the
   * specified element (or equivalently, if the set changed as a result of
   * the call).  (The set will not contain the specified element once the
   * call returns.)
   *
   * @param o object to be removed from this set, if present.
   * @return true if the set contained the specified element.
   * @throws ClassCastException if the type of the specified element
   * 	       is incompatible with this set (optional).
   * @throws NullPointerException if the specified element is null and this
   *         set does not support null elements (optional).
   * @throws UnsupportedOperationException if the <tt>remove</tt> method is
   *         not supported by this set.
   */
  public boolean remove(Object o) {
    return elements.remove(o);
  }

  /**
   * Returns <tt>true</tt> if this set contains all of the elements of the
   * specified collection.  If the specified collection is also a set, this
   * method returns <tt>true</tt> if it is a <i>subset</i> of this set.
   *
   * @param  c collection to be checked for containment in this set.
   * @return <tt>true</tt> if this set contains all of the elements of the
   * 	       specified collection.
   * @throws ClassCastException if the types of one or more elements
   *         in the specified collection are incompatible with this
   *         set (optional).
   * @throws NullPointerException if the specified collection contains one
   *         or more null elements and this set does not support null
   *         elements (optional).
   * @throws NullPointerException if the specified collection is
   *         <tt>null</tt>.
   * @see    #contains(Object)
   */
  public boolean containsAll(Collection c) {
    return elements.containsAll(c);
  }

  /**
   * Adds all of the elements in the specified collection to this set if
   * they're not already present (optional operation).  If the specified
   * collection is also a set, the <tt>addAll</tt> operation effectively
   * modifies this set so that its value is the <i>union</i> of the two
   * sets.  The behavior of this operation is unspecified if the specified
   * collection is modified while the operation is in progress.
   *
   * @param c collection whose elements are to be added to this set.
   * @return <tt>true</tt> if this set changed as a result of the call.
   *
   * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
   * 		  not supported by this set.
   * @throws ClassCastException if the class of some element of the
   * 		  specified collection prevents it from being added to this
   * 		  set.
   * @throws NullPointerException if the specified collection contains one
   *           or more null elements and this set does not support null
   *           elements, or if the specified collection is <tt>null</tt>.
   * @throws IllegalArgumentException if some aspect of some element of the
   *		  specified collection prevents it from being added to this
   *		  set.
   * @see #add(Object)
   */
  public boolean addAll(Collection c) {
    return elements.addAll(c);
  }

  /**
   * Retains only the elements in this set that are contained in the
   * specified collection (optional operation).  In other words, removes
   * from this set all of its elements that are not contained in the
   * specified collection.  If the specified collection is also a set, this
   * operation effectively modifies this set so that its value is the
   * <i>intersection</i> of the two sets.
   *
   * @param c collection that defines which elements this set will retain.
   * @return <tt>true</tt> if this collection changed as a result of the
   *         call.
   * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
   * 		  is not supported by this Collection.
   * @throws ClassCastException if the types of one or more elements in this
   *            set are incompatible with the specified collection
   *            (optional).
   * @throws NullPointerException if this set contains a null element and
   *            the specified collection does not support null elements
   *            (optional).
   * @throws NullPointerException if the specified collection is
   *           <tt>null</tt>.
   * @see #remove(Object)
   */
  public boolean retainAll(Collection c) {
    return elements.removeAll(c);
  }

  /**
   * Removes from this set all of its elements that are contained in the
   * specified collection (optional operation).  If the specified
   * collection is also a set, this operation effectively modifies this
   * set so that its value is the <i>asymmetric set difference</i> of
   * the two sets.
   *
   * @param  c collection that defines which elements will be removed from
   *           this set.
   * @return <tt>true</tt> if this set changed as a result of the call.
   *
   * @throws UnsupportedOperationException if the <tt>removeAll</tt>
   * 		  method is not supported by this Collection.
   * @throws ClassCastException if the types of one or more elements in this
   *            set are incompatible with the specified collection
   *            (optional).
   * @throws NullPointerException if this set contains a null element and
   *            the specified collection does not support null elements
   *            (optional).
   * @throws NullPointerException if the specified collection is
   *           <tt>null</tt>.
   * @see    #remove(Object)
   */
  public boolean removeAll(Collection c) {
    return elements.removeAll(c);
  }

  /**
   * Removes all of the elements from this set (optional operation).
   * This set will be empty after this call returns (unless it throws an
   * exception).
   *
   * @throws UnsupportedOperationException if the <tt>clear</tt> method
   * 		  is not supported by this set.
   */
  public void clear() {
    elements.clear();
  }

  public Iterator iterator(){
    return elements.iterator();
  }
}
