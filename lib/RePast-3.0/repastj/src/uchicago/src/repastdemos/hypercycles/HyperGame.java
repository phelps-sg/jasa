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
package uchicago.src.repastdemos.hypercycles;

import java.util.ArrayList;
import java.util.Vector;

import uchicago.src.sim.space.Object2DTorus;
import cern.jet.random.Uniform;

/**
 * Coordinates the activation, and deletion of skills and cells.
 *
 * An implementation of the simulation described in John Padgett's
 * "The Emergence of Simple Ecologies of Skill: A Hypercycle Approach to
 * Economic Organization" in _The Economy as an Evolving
 * Complex System II_, Eds. Arthur, Durlauf, and Lane. SFI
 * Studies in the Sciences of Complexity, Vol. XXVII, Addison-Wesley, 1997,
 * pp. 199-221. Thanks to John Padgett for allowing us to include it here.
 * jpadgett@midway.uchicago.edu
 *
 * @author Nick Collier and John Padgett
 * @version $Revision$ $Date$
 */

public class HyperGame {

  public final static int INACTIVE = -1;

  // selfish
  public final static int SOURCE = 0;

  // altruistic
  public final static int TARGET = 1;

  // joint
  public final static int JOINT = 2;

  // necessary so that can activate when source is max and target is min.
  public static int MAX_SKILL;

  public final static int VON_NEUMANN = 0;
  public final static int MOORE = 1;

  private int deathCount = 0;

  // the lists of the skills (the balls) in the entire simulation
  private ArrayList skillSet;

  private Object2DTorus mySpace;
  private int mode;

  private HyperCell sourceCell;
  private HyperCell targetCell;

  private int nType = MOORE;

  public HyperGame(Object2DTorus space, int reproductionMode, ArrayList skills,
        int nType)
  {
    mySpace = space;
    this.skillSet = skills;
    mode = reproductionMode;
    this.nType = nType;
  }

  public void play() {
    Skill sourceSkill = (Skill)skillSet.get(Uniform.staticNextIntFromTo(0, skillSet.size() - 1));

    Vector v = null;

    if (nType == VON_NEUMANN) {
      v = mySpace.getVonNeumannNeighbors((int)sourceSkill.cell.getX(),
					 (int)sourceSkill.cell.getY(), true);
    } else {
      v = mySpace.getMooreNeighbors((int)sourceSkill.cell.getX(),
				    (int)sourceSkill.cell.getY(), true);
    }


    HyperCell cell = (HyperCell)v.elementAt(Uniform.staticNextIntFromTo(0, v.size() - 1));

    if (cell != null) {
      int activatedValue = cell.activate(sourceSkill.value);


      if (activatedValue != INACTIVE) {
        sourceCell = sourceSkill.cell;
        targetCell = cell;

        // edges will show the direction of activation NOT
        // the direction of reproduction
        HyperLink link = new HyperLink(sourceCell, targetCell);
        sourceCell.addOutEdge(link);
        targetCell.addInEdge(link);

        if (mode == SOURCE) {
          Skill newSkill = new Skill(sourceSkill.value,
                                      sourceSkill.cell);
          sourceSkill.cell.addSkill(newSkill);
          skillSet.add(newSkill);
          deathCount++;

        } else if (mode == TARGET) {
          Skill newSkill = new Skill(activatedValue, cell);
          cell.addSkill(newSkill);
          skillSet.add(newSkill);
          deathCount++;
        } else if (mode == JOINT) {
          Skill newSkill = new Skill(sourceSkill.value,
                                      sourceSkill.cell);
          sourceSkill.cell.addSkill(newSkill);
          skillSet.add(newSkill);

          newSkill = new Skill(activatedValue, cell);
          cell.addSkill(newSkill);
          skillSet.add(newSkill);
          deathCount += 2;
        }
      }

      killSkill();
    }
  }

  public void killSkill() {
    for (int i = 0; i < deathCount; i++) {
      Skill deadSkill = (Skill)skillSet.get(Uniform.staticNextIntFromTo(0, skillSet.size() - 1));
      deadSkill.cell.removeSkill(deadSkill.value);
      skillSet.remove(deadSkill);
    }
    deathCount = 0;
  }

  public void unactivateCells() {
    if (targetCell != null) {
      targetCell.clearInEdges();
      sourceCell.clearOutEdges();
    }
  }
}








