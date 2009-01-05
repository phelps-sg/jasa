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
package uchicago.src.sim.topology.demo;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.topology.Context;
import uchicago.src.sim.topology.space2.GridAgent;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

public class Player implements Drawable, GridAgent {
  final int C = 1;
  final int D = 0;
  final String[] actionToString = {"D", "C"};

  public final int I_PARAMS[] = {C, C, D, D};
  public final int P_PARAMS[] = {C, C, D, D};
  public final int Q_PARAMS[] = {C, D, C, D};

  private int x, y;
  private int playerID;
  private Player other;
  private int type;
  private int newType;
  private int[][] prefs = { {1,5},
                    {0,3} };
  private int action;
  private int memory;
  private int cumulPayoff;
  private int numPlays;
  private double pAdapt;
  private int maxIter;

  private ArrayList otherList;

  private Context world;
  private String relationshipType;

  public Player (int i, int t, Model m) {
    playerID = i;
    type = t;
    pAdapt = m.getPAdapt();
    maxIter = m.getMaxIter();
    otherList = new ArrayList();
  }

  public void reset() {
    numPlays = 0;
    cumulPayoff = 0;
    otherList.clear();
  }

  public void remember() {
    memory = other.action;
  }

  public void interact(){
    List neighbors = world.getRelated(this, relationshipType, 1);
    //System.out.println("neighbors.size() = " + neighbors.size());
    // Loop through them
    Iterator it = neighbors.iterator();
    while (it.hasNext()) {
      // Pick the next neighbor...
      Player otherPlayer = (Player) it.next();
      // ... and play a game against it.
      game(otherPlayer);
    }
  }

  public void game(Player other){
    this.setOther(other);
    other.setOther(this);


    // Here the iterated game unfolds in maxIter rounds as in SimpleIPD
    for (int i=1; i<=maxIter; i++) {    // Note that 'time' starts at 1.
      play(i);
      other.play(i);
      remember();
      other.remember();
      addPayoff();
      other.addPayoff();
    }
  }

  public Context getWorld() {
    return world;
  }

  public void setWorld(Context world) {
    this.world = world;
  }

  public String getRelationshipType(){
    return relationshipType;
  }

  public void setRelationshipType(String rel){
    relationshipType = rel;
  }

  public void play(int time) {
    numPlays++;
    if (time == 1)
      action = I_PARAMS[type];
    else
      if (memory == C)
        action = P_PARAMS[type];
      else
        action = Q_PARAMS[type];
  }

  public void addPayoff() {
    cumulPayoff = cumulPayoff + prefs[action][other.action];
  }

  public void adapt() {

    newType = type;

    if (Random.uniform.nextDoubleFromTo(0,1) < pAdapt) {

      SimUtilities.shuffle(otherList);

      double bestPayoff = getAveragePayoff();
      Iterator i = otherList.iterator();
      while (i.hasNext()) {
        Player aPlayer = (Player)i.next();
        double payoff = aPlayer.getAveragePayoff();
        if (payoff > bestPayoff) {
          bestPayoff = payoff;
          newType = aPlayer.type;
        }
      }
    }
  }

  public void updateType() {
    type = newType;
  }

  public double getAveragePayoff() {
    if (numPlays == 0)
      return -1.0;              // Extreme value as an 'error message'
    else
      return (double)cumulPayoff/(double)numPlays;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getType () {
    return type;
  }

  public void setType (int v) {
    type = v;
  }

  public Player getOther() {
    return other;
  }

  public void setOther(Player other) {
    this.other = other;
    otherList.add(other);
  }

  public int getPlayerID() {
    return playerID;
  }

  public void setPlayerID(int playerID) {
    this.playerID = playerID;
  }

  public String toString(){
    return Integer.toString(playerID);
  }

  final Color COLOR[] = {Color.red, Color.blue, Color.green, Color.white};

  public void draw(SimGraphics g) {
    g.setDrawingParameters( DisplayConstants.CELL_WIDTH * 2 / 3,
                            DisplayConstants.CELL_HEIGHT * 2 / 3,
                            DisplayConstants.CELL_DEPTH * 2 / 3 );
    g.drawFastRoundRect(COLOR[type]);
  }
}