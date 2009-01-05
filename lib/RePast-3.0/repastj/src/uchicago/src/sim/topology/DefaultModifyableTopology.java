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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 18, 2003
 * Time: 3:49:55 PM
 * To change this template use Options | File Templates.
 */
public class DefaultModifyableTopology implements ModifyableTopology{

  private Map relationMap;
  public static String type = "DEFAULT_TOPOLOGY";
  private DijkstraSimple dijkstra;

  public DefaultModifyableTopology(){
    relationMap = new LinkedHashMap();
    dijkstra = new DijkstraSimple();
  }

  public void addRelation(Object element1, Object element2, double distance) {
    Set e1Relations = (Set) relationMap.get(element1);
    Set e2Relations = (Set) relationMap.get(element2);
    e1Relations.add(element2);
    e2Relations.add(element1);
  }

  public void addRelation(Object element1, Object element2){
    addRelation(element1, element1, 1);
  }

  public void removeRelation(Object element1, Object element2) {
    Set e1Relations = (Set) relationMap.get(element1);
    Set e2Relations = (Set) relationMap.get(element2);
    e1Relations.remove(element2);
    e2Relations.remove(element1);
  }

  public boolean insertElement(Object element) {
    return relationMap.put(element, new LinkedHashSet()) != null;
  }

  public boolean removeElement(Object element) {
    Set relations = (Set) relationMap.get(element);
    Iterator i = relations.iterator();
    while(i.hasNext()){
      Object other = i.next();
      Set otherRelations = (Set) relationMap.get(other);
      otherRelations.remove(element);
    }
    return relationMap.remove(element) == null;
  }

  /**
   * Get all of the relationships that the given element has
   * with other elements.
   * @param element
   * @return
   */
  public List getRelations(Object element) {
    return new ArrayList((Set)relationMap.get(element));
  }

  /**
   * Gets all of the Objects within a given range.
   * @param element
   * @param range
   * @return
   */
  public List getRelations(Object element, double range) {
    int rng = (int) Math.floor(range);
    if(rng == 1){
      return getRelations(element);
    }
    ArrayList list = new ArrayList();
    Set currentSet = new LinkedHashSet();
    Set newSet =  new LinkedHashSet();
    currentSet.add(element);
    for(int i = 0 ; i < rng ; i++){
      newSet.clear();
      Iterator it = currentSet.iterator();
      while(it.hasNext()){
        Object o = it.next();
        list.add(o);
        newSet.addAll(getRelations(o));
      }
      currentSet.clear();
      currentSet.addAll(newSet);
    }
    return list;
  }

  /**
   * Get the type of relationship/topology that is represented by this
   * Class.  For example, if this represents a VonNeumann topology, this
   * should return the String "VON_NEUMANN".
   *
   * @return
   */
  public String getRelationType() {
    return type;
  }

  /**
   * Gets the distance between two objects in this topology.  This could be
   * either spatial distance, network distance or any other kind of well defined
   * metric distance.
   *
   * This implementation uses Dijkstra's shortest path algorithm to determine distance.
   * it should achieve performance of O(n log m).
   * @param element1
   * @param element2
   * @return
   */
  public double distance(Object element1, Object element2) {
    if(areAdjacent(element1, element2)){
      return 1;
    }
    return dijkstra.dijkstra(element1, element2);
  }


  public boolean areAdjacent(Object element1, Object element2){
    Set relations = (Set) relationMap.get(element1);
    if(relations.contains(element2)){
      return true;
    }
    return false;
  }

  /**
   * Set the name for this type of relation.
   * @param type
   */
  public void setRelationType(String type) {
    DefaultModifyableTopology.type = type;
  }

  private class DijkstraSimple{

    /*
    * This is an implementation of Dihjkstra's shortest path algorithm
    */
    private final Set settled = new HashSet();
    private final Map shortestDistances = new HashMap();
    private final Map predecessors = new HashMap();
    private final SortedSet unsettled = new TreeSet(new Comparator(){
      public int compare(Object element1, Object element2){
        Integer d = (Integer) shortestDistances.get(element1);
        int dist1 = (d == null) ? Integer.MAX_VALUE : d.intValue();
        d = (Integer) shortestDistances.get(element2);
        int dist2 = d.intValue();
        if(dist1 > dist2){
          return 1;
        }else if(dist2 > dist1){
          return -1;
        }else{
          return 0;
        }
      }
    });

    public Object getPredecessor(Object element){
      return predecessors.get(element);
    }

   public double dijkstra(Object element1, Object element2){
      settled.clear();
      unsettled.clear();

      shortestDistances.clear();
      predecessors.clear();

      // add source
      shortestDistances.put(element1, new Integer(0));
      unsettled.add(element1);

      Object u;

      // extract the node with the shortest distance
      Object element = unsettled.first();
      unsettled.remove(element);
      while ((u = element) != null)
      {
        if(!settled.contains(element)){

          // destination reached, stop
          if (u == element2) break;

          settled.add(u);

          relaxNeighbors(u);
        }
      }
      Integer d = (Integer) shortestDistances.get(element);
      return (d == null) ? Integer.MAX_VALUE : d.intValue();
    }

    private void relaxNeighbors(Object u){
      for (Iterator i = getRelations(u).iterator(); i.hasNext(); )
      {
        Object v = i.next();

        // skip node already settled
        if (settled.contains(v)) continue;
        Integer d = (Integer) shortestDistances.get(v);
        int distV = (d == null) ? Integer.MAX_VALUE : d.intValue();
        d = (Integer) shortestDistances.get(u);
        int distU = (d == null) ? Integer.MAX_VALUE : d.intValue();

        if (distV > distU + 1){
          // assign new shortest distance and mark unsettled
          d = (Integer) shortestDistances.get(u);
          int iU = (d == null) ? Integer.MAX_VALUE : d.intValue();

          shortestDistances.put(v, new Integer(iU + 1));

          // assign predecessor in shortest path
          predecessors.put(v, u);
        }
      }
    }
  }
}
