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
package uchicago.src.sim.topology.space2;

import java.util.ArrayList;
import java.util.List;

import uchicago.src.sim.topology.ModifyableTopology;
import uchicago.src.sim.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 17, 2003
 * Time: 4:32:49 PM
 * To change this template use Options | File Templates.
 */
public class OccupationTopology implements ModifyableTopology{
  private Discrete2DSpace space;
  public static String type = "OCCUPATION";

  public OccupationTopology(Discrete2DSpace s){
    space = s;
  }

  public void addRelation(Object element1, Object element2, double distance) {
    Location l = null;
    if(element2 instanceof Location){
      l = (Location) element2;
      space.putObjectAt(l.getX(), l.getY(), element1);
    }else if(element1 instanceof Location){
      l = (Location) element1;
      space.putObjectAt(l.getX(), l.getY(), element2);
    }else{
      throw new IllegalArgumentException("OccupationTopology requires a Location Object");
    }
  }

  public void removeRelation(Object element1, Object element2){
    Location l = null;
    if(element2 instanceof Location){
      l = (Location) element2;
      space.putObjectAt(l.getX(), l.getY(), null);
    }else if(element1 instanceof Location){
      l = (Location) element1;
      space.putObjectAt(l.getX(), l.getY(), null);
    }else{
      throw new IllegalArgumentException("OccupationTopology requires a Location Object");
    }
  }

  public boolean insertElement(Object element) {
    int i = 0;
    int x = Random.uniform.nextIntFromTo(0, space.getSizeX() - 1);
    int y = Random.uniform.nextIntFromTo(0, space.getSizeY() - 1);
    while(space.getObjectAt(x,y) != null){
      x = Random.uniform.nextIntFromTo(0, space.getSizeX() - 1);
      y = Random.uniform.nextIntFromTo(0, space.getSizeY() - 1);
      if(i >= 50){
        return false;
      }
    }
    space.putObjectAt(x, y , element);
    return true;
  }

  public boolean removeElement(Object element) {
    Location l = space.getLocation(element);
    if(l == null){
      return false;
    }
    space.putObjectAt(l.getX(), l.getY(), null);
    return true;
  }

  /**
   * Get all of the relationships that the given element has
   * with other elements.
   * @param element
   * @return
   */
  public List getRelations(Object element) {
    ArrayList out = new ArrayList();
    if(!(element instanceof Location)){
      out.add(space.getLocation(element));
      return out;
    }else{
      Location l = (Location) element;
      out.add(space.getObjectAt(l.getX(), l.getY()));
    }
    return out;
  }

  /**
   * Gets all of the Objects within a given range.
   * @param element
   * @param range
   * @return
   */
  public List getRelations(Object element, double range) {
    return getRelations(element);
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
   * @param element1
   * @param element2
   * @return
   */
  public double distance(Object element1, Object element2) {
    if(space.getLocation(element1).equals(element2) ||
          space.getLocation(element2).equals(element1)){
      return 1;
    }
    return -1;
  }

  public void setRelationType(String type) {
    OccupationTopology.type = type;
  }
}
