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

import java.util.Iterator;
import java.util.List;

import uchicago.src.sim.topology.space2.GridAgent;
import uchicago.src.sim.topology.space2.Location;
import uchicago.src.sim.topology.space2.Object2DGrid;
import uchicago.src.sim.topology.space2.OccupationTopology;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jun 18, 2003
 * Time: 11:13:31 AM
 * To change this template use Options | File Templates.
 */
public class ContextFactory {

  public static Context getFilledGrid(int worldXSize,
                                      int worldYSize, Class elementClass){
    Context context = new Object2DGrid(worldXSize, worldYSize);
    for(int x = 0 ; x < worldXSize ; x++){
      for(int y = 0 ; y < worldYSize ; y++){
        try{
          context.addRelation(elementClass.newInstance(),
                              Location.getLocation(x,y), OccupationTopology.type, 1);
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    }
    return context;
  }

  public static Context getRandomGrid(int worldXSize,
                                      int worldYSize, double percentFilled,
                                      Class elementClass){
    Context context = new Object2DGrid(worldXSize, worldYSize);
    int numAgents = (int) (percentFilled * worldXSize * worldYSize);
    for(int i = 0 ; i < numAgents ; i++){
      int x = Random.uniform.nextIntFromTo(0, worldXSize - 1);
      int y = Random.uniform.nextIntFromTo(0, worldYSize - 1);
      while(context.getRelated(Location.getLocation(x,y), OccupationTopology.type,
                               1) != null){
        x = Random.uniform.nextIntFromTo(0, worldXSize - 1);
        y = Random.uniform.nextIntFromTo(0, worldYSize - 1);
      }
      try{
        context.addRelation(elementClass.newInstance(),
                            Location.getLocation(x,y), OccupationTopology.type, 1);
      }catch(Exception e){
        e.printStackTrace();
      }
    }
    return context;
  }

  public static Context getGridFromList(int worldXSize, int worldYSize,
                                        List elementList, boolean shuffle){
    if(elementList.size() > worldXSize * worldYSize){
      SimUtilities.showError("You have more Agents than spaces in grid", new
            IllegalArgumentException("size of list must not exceed size of grid"));
    }
    Object2DGrid context = new Object2DGrid(worldXSize, worldYSize);
    if(shuffle){
      SimUtilities.shuffle(elementList);
    }
    for (int x = 0;  x < worldXSize; x++)
      for (int y = 0; y < worldYSize; y++) {
        GridAgent element = (GridAgent) elementList.get(x * worldXSize + y);
        //System.out.println("x, y: " + x + ", " + y);
        context.addRelation(element, Location.getLocation(x,y), OccupationTopology.type);
        //context.putObjectAt(x,y,element);
        element.setX(x); // The player has to record its own position
        element.setY(y);
      }
    return context;
  }

  public static Context getSmallWorldFromList(List elementList, int nCols, int nRows,
                                              boolean wrapAround, int radius,
                                              double pRewire){
    Context context = new DefaultContext();
    ModifyableTopology top = new DefaultModifyableTopology();
    top.setRelationType("SMALL_WORLD");
    context.addRelationType(top);
    squareLattice(elementList, wrapAround, nCols, radius, nRows, context, top);
    randomRewire(elementList, context, pRewire, top);
    return context;
  }

  private static void randomRewire(List elementList, Context context, double pRewire,
                                   RelationTopology top){
    Iterator i = context.iterator();
    while(i.hasNext()){
      Object element = i.next();
      List relations = context.getRelated(element, top.getRelationType(),
                                          1);
      Iterator relIt = relations.iterator();
      while(relIt.hasNext()){
        Object currentRelated = relIt.next();
        if(Random.uniform.nextDoubleFromTo(0,1) < pRewire){
          int newJ = Random.uniform.nextIntFromTo(0, elementList.size());
          Object newRelated = elementList.get(newJ);
          if(!newRelated.equals(element) &&
                !context.areRelated(element, newRelated, 1,
                                    top.getRelationType())){
            context.removeRelation(element, currentRelated, top.getRelationType());
            context.addRelation(element, newRelated, top.getRelationType(), 1);
          }
        }
      }
    }
  }

  private static Context squareLattice(List elementList, boolean wrapAround, int nCols,
                                       int radius, int nRows, Context context,
                                       ModifyableTopology top) {
    Iterator i = elementList.iterator();
    while(i.hasNext()){
      context.add(i.next());
    }
    int nNodes = elementList.size();
    if(wrapAround){
      for (int count = 0; count < nNodes; count++)      {
        Object iNode = elementList.get(count);
        int x = count%nCols;          //col "grid" coords for index count
        int y = (int)Math.floor(count/nCols);   //row
        int j; //index of other node
        int xJ;
        int yJ;
        for (int r = 1; r <= (radius); r++)
        {
          //"east" connection ("west" will be set by previous/last node)
          xJ = (x+r)%nCols;
          yJ = y;
          j =yJ*nCols+xJ;
          if (count != j) //traps 1 by n "ring"
          {
            Object jNode = elementList.get(j);
            context.addRelation(iNode, jNode, top.getRelationType(),1);
          }

          //"south" connection ("north" will be set by previous/last node)
          xJ = x;
          yJ = (y+r)%nRows;
          j =yJ*nCols+xJ;
          if (count != j)  //traps 1 by n "ring"
          {
            Object jNode = elementList.get(j);

            context.addRelation(iNode, jNode, top.getRelationType(),1);
          }
        }
      }
    }
    return context;
  }
}
