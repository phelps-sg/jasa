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


/**
 * Object to represent a location on a Discrete2DSpace.
 * @author Tom Howe
 * @version 0.1
 */

public class Location {
  private int x, y;
  private static Location[][] locs;

  private Location(int x, int y){
    this.x = x;
    this.y = y;
  }

  protected static void createLocation(Discrete2DSpace grid){
    locs = new Location[grid.getSizeX()][grid.getSizeY()];
  }

  public static Location getLocation(int x, int y){
    if(locs[x][y] == null){
      locs[x][y] = new Location(x,y);
    }
    return locs[x][y];
  }

  /**
   * @return
   */
  public int getX(){
    return x;
  }

  /**
   * @return
   */
  public int getY(){
    return y;
  }

  public boolean equals(Object o){
    if(o instanceof Location){
      Location l = (Location) o;
      return l.getX() == x && l.getY() == y;
    }
    return false;
  }

  public int hashCode(){
    int result = 17;
    result = 37 * result + x;
    result = 37 * result + y;
    return result;
  }

  public String toString(){
    return "Location: x = " + x + ", y = " + y;
  }
}
