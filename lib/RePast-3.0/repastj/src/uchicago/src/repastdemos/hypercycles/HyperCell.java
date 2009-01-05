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

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.RoundRectNetworkItem;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultDrawableNode;

/**
 * A pseudo-agent class for the hypercycles simulation. A Hypercell
 * is essentially a cell on a 2D grid that contains the skills. Has methods
 * for adding and deleting skills. Extends AbstractNode in order to track
 * the link between this HyperCell when it activates or is activated by
 * another HyperCell.
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

public class HyperCell extends DefaultDrawableNode implements CustomProbeable {

  private int x, y;
  Hashtable skillCount = new Hashtable();
  private String label = "";
  private boolean makeNewLabel = true;

  public HyperCell(int x, int y) {
    super();
    RoundRectNetworkItem item = new RoundRectNetworkItem(x, y);
    item.setHollow(true);
    super.setDrawable(item);
    this.setLabelColor(Color.white);
  }

  public String getSkills() {
    String retVal = "";
    Enumeration e = skillCount.keys();
    while (e.hasMoreElements()) {
      Integer key = (Integer)e.nextElement();
      Integer count = (Integer)skillCount.get(key);
      retVal += count + " of " + key + ", ";
    }

    if (retVal.length() > 0)
      retVal = retVal.substring(0, retVal.length() - 2);
    else
      retVal = "none";
    return retVal;
  }

  public Object[] getSkillSet() {
    return skillCount.keySet().toArray();
  }

  public void addSkill(Skill skill) {
    Integer skillVal = new Integer(skill.value);

    // increases the amount of skill values.
    if (skillCount.containsKey(skillVal)) {
      Integer count = (Integer)skillCount.get(skillVal);
      int newCount = count.intValue();
      newCount++;
      skillCount.put(skillVal, new Integer(newCount));
    } else {
      skillCount.put(skillVal, new Integer(1));
    }
  }

  public boolean removeSkill(int skill) {
    Integer skillVal = new Integer(skill);
    if (skillCount.containsKey(skillVal)) {
      Integer count = (Integer)skillCount.get(skillVal);
      int newCount = count.intValue();
      newCount--;
      if (newCount == 0) {
        skillCount.remove(skillVal);
        makeNewLabel = true;
      } else {
        skillCount.put(skillVal, new Integer(newCount));
      }
      return true;
    }

    return false;
  }


  /**
   * Returns the the int value of the activated skill - i.e. 1 activates 2,
   * so return 2 etc. or returns -1 if no activation
   */
  public int activate(int targetSkill) {
    if (targetSkill == HyperGame.MAX_SKILL) {
      targetSkill = 1;
    } else {
      targetSkill++;
    }

    int retVal = targetSkill;

    // no activation through complementary skills here
    if (!skillCount.containsKey(new Integer(targetSkill))) {
      retVal = -1;
    }

    return retVal;
  }

  public String getLocation() {
    return x + ", " + y;
  }


  public void setLocation(float x, float y) {
    setX((int)x);
    setY((int)y);
  }

  private String getLabel() {
    if (makeNewLabel) {
      Enumeration e = skillCount.keys();
      label = "";
      while (e.hasMoreElements()) {
        Integer val = (Integer)e.nextElement();
        label += val.toString() + " ";
      }

      if (label.length() > 0) {
        label = label.substring(0, label.length() - 1);
      }
      makeNewLabel = false;
    }

    return label;
  }

  public boolean isEmpty() {
    return skillCount.isEmpty();
  }

  public boolean contains(Integer skill) {
    return skillCount.containsKey(skill);
  }

  public void draw(SimGraphics g) {
    if (skillCount.size() > 0) super.setColor(Color.yellow);
    else super.setColor(Color.black);
    this.setNodeLabel(getLabel());
    super.draw(g);
  }

  public String[] getProbedProperties() {
    String[] props = {"Skills", "X", "Y"};
    return props;
  }

  public String toString() {
    StringBuffer retVal = new StringBuffer("Cell[" + x + ", " + y + "]: ");
    Enumeration e = skillCount.keys();
    while (e.hasMoreElements()) {
      Integer key = (Integer)e.nextElement();
      Integer count = (Integer)skillCount.get(key);
      if (e.hasMoreElements()) {
        retVal.append(count + " x " + key + ", ");
      } else {
        retVal.append(count + " x " + key);
      }
    }
    return retVal.toString();
  }
}
