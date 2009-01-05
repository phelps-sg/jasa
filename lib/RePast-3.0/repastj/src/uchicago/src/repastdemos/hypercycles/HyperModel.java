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
import java.util.Collections;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.reflector.BooleanPropertyDescriptor;
import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.analysis.ObjectDataRecorder;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DGridDisplay;
import uchicago.src.sim.space.Object2DTorus;
import cern.jet.random.Uniform;

/**
 * An implementation of the simulation described in John Padgett's
 * "The Emergence of Simple Ecologies of Skill: A Hypercycle Approach to
 * Economic Organization" in _The Economy as an Evolving
 * Complex System II_, Eds. Arthur, Durlauf, and Lane. SFI
 * Studies in the Sciences of Complexity, Vol. XXVII, Addison-Wesley, 1997,
 * pp. 199-221. Thanks to John Padgett for allowing us to include it here.
 * jpadgett@midway.uchicago.edu
 *
 *
 * Graphicaly the display shows cells activating each other, and displays
 * existing hypercycles when stopped or paused.
 *
 * @author Nick Collier and John Padgett
 * @version $Revision$ $Date$
 */

public class HyperModel extends SimModelImpl {

  private int reproductionMode = HyperGame.SOURCE;

  // list of individual skill types (i.e. 1, 2, 3, 4)
  private Vector skills = new Vector();

  // a list of the Skill objects
  private ArrayList skillList = new ArrayList();
  private int maxSkillNum = 2;
  private int numSkills = 200;
  private int nType = HyperGame.VON_NEUMANN;

  private Object2DTorus space;
  private HyperGame game;
  private DisplaySurface dsurf;
  private Schedule schedule;
  private Network2DGridDisplay cellDisplay;

  private OpenSequenceGraph graph;
  private ObjectDataRecorder recorder;
  //private int numCycles = 0;

  // Whether to show the actual HyperCycles each tick. This can be
  // toggled on and off using a checkbox in the Custom Actions panel.
  // See setupCustomActions for how this is done.
  private boolean showLinks = false;
  private boolean record = false;
  //private boolean recorded = false;

  // these two are used in searching for actual HyperCycles and
  // clearing them each turn
  private HCycleSearcher hCSearcher = new HCycleSearcher();
  private Vector hyperNodes = new Vector(117);
  private Vector cycles = new Vector(13);

  public HyperModel() {
    skills.add(new Integer(1));
    skills.add(new Integer(2));

    DisplayConstants.CELL_HEIGHT = 28;
    DisplayConstants.CELL_WIDTH = 30;

    Hashtable h1 = new Hashtable();
    h1.put(new Integer(HyperGame.VON_NEUMANN), "VON NEUMANN");
    h1.put(new Integer(HyperGame.MOORE), "MOORE");
    ListPropertyDescriptor pd = new ListPropertyDescriptor("NType", h1);
    descriptors.put("NType", pd);

    Hashtable h = new Hashtable();
    h.put(new Integer(HyperGame.SOURCE), "SOURCE");
    h.put(new Integer(HyperGame.TARGET), "TARGET");
    h.put(new Integer(HyperGame.JOINT), "JOINT");
    ListPropertyDescriptor pd1 = new ListPropertyDescriptor("Mode", h);
    descriptors.put("Mode", pd1);

    BooleanPropertyDescriptor bd = new BooleanPropertyDescriptor("ShowCycles",
      false);
    descriptors.put("ShowCycles", bd);
    BooleanPropertyDescriptor bd1 = new BooleanPropertyDescriptor("RecordCycleData",
      false);
    descriptors.put("RecordCycleData", bd1);
  }

  public void setMode(int i) {
    reproductionMode = i;
  }

  public int getMode() {
    return reproductionMode;
  }

  public boolean isShowCycles() {
    return showLinks;
  }

  public void setShowCycles(boolean show) {
    showLinks = show;
  }

  public boolean isRecordCycleData() {
    return record;
  }

  public void setRecordCycleData(boolean record) {
    this.record = record;
  }

  public void setNType(int type) {
    nType = type;
  }

  public int getNType() {
    return nType;
  }

  public void setSkills(String someSkills) {
    skills.clear();
    StringTokenizer tok = new StringTokenizer(someSkills, ",");
    while (tok.hasMoreTokens()) {
      String num = tok.nextToken();
      num = num.trim();
      try {
       Integer p = new Integer(num);
       if (p.intValue() > maxSkillNum) {
        maxSkillNum = p.intValue();
       }
       skills.add(p);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Skills must be delimited by ','");
      }
    }

    Collections.sort(skills);
    //DebugMethods.printCollection(skills);
  }

  public String getSkills() {
    String retVal = "";
    for (int i = 0; i < skills.size(); i++) {
      Integer j = (Integer)skills.elementAt(i);
      retVal += j.intValue() + ",";
    }

    if (retVal.length() > 0) {
      retVal = retVal.substring(0, retVal.length() - 1);
    }

    return retVal;
  }

  public int getNumberOfSkills() {
    return numSkills;
  }

  public void setNumberOfSkills(int skills) {
    numSkills = skills;
  }

  private void addSkill(Integer skillVal) {
    // get a random coordinate
    int x = Uniform.staticNextIntFromTo(0, 9);
    int y = Uniform.staticNextIntFromTo(0, 9);

    HyperCell cell = (HyperCell)space.getObjectAt(x, y);
    if (cell == null) {
      cell = new HyperCell(x, y);
      space.putObjectAt(x, y, cell);
    }

    Skill skill = new Skill(skillVal.intValue(), cell);
    cell.addSkill(skill);

    skillList.add(skill);
  }

  public void checkForHyperCycle() {
    BaseMatrix m = space.getMatrix();
    int xSize = m.getNumCols();
    int ySize = m.getNumRows();
    for (int x = 0; x < xSize; x++) {
      for (int y = 0; y < ySize; y++) {
        HyperCell cell = (HyperCell)space.getObjectAt(x, y);
        if (cell != null) {
          if (!cell.isEmpty()) {
            hCSearcher.search(cell);
          }
        }
      }
    }
  }

  public void clearLinks() {
    game.unactivateCells();
    for (int i = 0; i < hyperNodes.size(); i++) {
      HyperCell cell = (HyperCell)hyperNodes.get(i);
      cell.clearOutEdges();
      cell.clearInEdges();
    }
    hyperNodes.clear();
    cycles.clear();
  }

  public void buildModel() {
    space = new Object2DTorus(10, 10);

    /**
     * Only populate the space with cells where a skill happens to fall,
     * otherwise leave empty.
     */
    int amtSkills = skills.size();
    int numSkillsEach = (int) (numSkills / amtSkills);
    int extra = numSkills - (numSkillsEach * amtSkills);

    for (int j = 0; j < numSkillsEach; j++) {
      for (int i = 0; i < amtSkills; i++) {
        Integer skillVal = (Integer)skills.get(i);
        addSkill(skillVal);
      }
    }

    // add the extra amount randomly
    for (int i = 0; i < extra; i++) {
      int index = Uniform.staticNextIntFromTo(0, amtSkills - 1);
      addSkill((Integer)skills.get(index));
    }

    game = new HyperGame(space, reproductionMode, skillList, nType);
    HyperGame.MAX_SKILL = maxSkillNum;

    recorder = new ObjectDataRecorder("cycles.txt", this);
  }

  private StringBuffer hyperCyclesToString() {
    StringBuffer b = new StringBuffer();
    b.append(cycles.size());
    b.append(" Hypercycles\n\n");
    for (int i = 0; i < cycles.size(); i++) {
      HyperCycle cycle = (HyperCycle)cycles.get(i);
      b.append(cycle.toString());
    }
    return b;
  }

  private void buildDisplay() {
    cellDisplay = new Network2DGridDisplay(space);

    dsurf.addDisplayableProbeable(cellDisplay, "Hypercycles");
    dsurf.setBackground(java.awt.Color.black);
    addSimEventListener(dsurf);


    graph.addSequence("Num. Hypercycles", new Sequence() {
      public double getSValue() {
        return (double)hCSearcher.cycleCount;
      }
    });

    graph.setXRange(0, 200);
    graph.setYRange(0, 200);
    graph.setAxisTitles("time", "HyperCycles");

  }


  private void buildSchedule() {

    class HyperRunner extends BasicAction {
      public void execute() {
        clearLinks();
        game.play();
        hCSearcher.cycleCount = 0;
        checkForHyperCycle();
        graph.step();
        dsurf.updateDisplay();

        if (record) {
          //recorded = true;
          recorder.record(hyperCyclesToString(), "tick: " + getTickCount());

        }
      }
    };

    schedule.scheduleActionBeginning(0, new HyperRunner());
    schedule.scheduleActionAtPause(recorder, "write");
    schedule.scheduleActionAtEnd(recorder, "write");
    schedule.scheduleActionAtEnd(this, "checkForHyperCycle");
  }

  public void begin() {
    buildModel();
    buildDisplay();
    buildSchedule();

    dsurf.display();
    graph.display();
  }

  private void setupCustomActions() {
     /*
    modelManipulator.init();

    CheckBoxListener showHyper = new CheckBoxListener() {
      public void execute() {
        showLinks = isSelected;
      }
    };

    CheckBoxListener recordCycles = new CheckBoxListener() {
      public void execute() {
        record = isSelected;
      }
    };

    modelManipulator.addCheckBox("Display HyperCycles", showHyper, false);
    modelManipulator.addCheckBox("Record Cycle Details", recordCycles, false);
    */
  }

  public void setup() {
    if (dsurf != null)
      dsurf.dispose();

    if (graph != null) {
      graph.dispose();
    }

    cellDisplay = null;
    graph = null;
    dsurf = null;
    schedule = null;
    recorder = null;

    System.gc();

    dsurf = new DisplaySurface(this, "Hypercycles Display");
    registerDisplaySurface("Hypercycles display", dsurf);
    graph = new OpenSequenceGraph("Data", this);
    registerMediaProducer("Graph", graph);
    schedule = new Schedule(1);

    reproductionMode = HyperGame.SOURCE;
    skills = new Vector();
    skills.add(new Integer(1));
    skills.add(new Integer(2));

    maxSkillNum = 2;
    numSkills = 200;
    nType = HyperGame.VON_NEUMANN;
    skillList = new ArrayList();

    //HyperGame game = null;
    hCSearcher = new HCycleSearcher();
    hyperNodes = new Vector(117);
    setupCustomActions();

    System.gc();
  }

  public String[] getInitParam() {
    String[] params = {"mode", "skills", "numberOfSkills", "NType", "ShowCycles",
                      "RecordCycleData"};
    return params;
  }

  public Schedule getSchedule() {
    return schedule;
  }

  public String getName() {
    return "Hypercycles";
  }

  class HCycleSearcher {

    int cycleCount = 0;
    Vector cycleList = new Vector(7);

    Vector addList = new Vector(7);

    private Vector getCycleNeighbors(Vector neighs, Integer skill) {
      Vector tempVector = new Vector(5);
      for (int j = 0; j < neighs.size(); j++) {
        HyperCell nCell = (HyperCell)neighs.get(j);
        if (nCell.contains(skill)) {
          tempVector.add(nCell);
        }
      }
      return tempVector;
    }

    private void getNeighbor(Integer skill) {
      addList.clear();
      for (int i = 0; i < cycleList.size(); i++) {
        Vector list = (Vector)cycleList.get(i);
        HyperCell cell = (HyperCell)list.elementAt(list.size() - 1);

        // !!!!Hypercyle check only in VonN neighbors!!!!
        Vector neighs = space.getVonNeumannNeighbors((int)cell.getX(),
						     (int)cell.getY(), false);
        Vector cycleNeighs = getCycleNeighbors(neighs, skill);

        if (!cycleNeighs.isEmpty()) {
          for (int k = 0; k < cycleNeighs.size(); k++) {
            Vector newV = new Vector(5);
            newV.addAll(list);
            newV.add(cycleNeighs.get(k));
            addList.add(newV);
          }
        }
      }

      cycleList.clear();
      cycleList.addAll(addList);
    }

    private void getFinalSkill(Integer skill) {
      addList.clear();
      for (int i = 0; i < cycleList.size(); i++) {
        Vector list = (Vector)cycleList.get(i);
        HyperCell cell = (HyperCell)list.elementAt(list.size() - 1);
        Vector neighs = space.getVonNeumannNeighbors((int)cell.getX(),
						     (int)cell.getY(), false);
        Vector cycleNeighs = getCycleNeighbors(neighs, skill);
        if (!cycleNeighs.isEmpty()) {
          // check if is a true cycle - i.e. last meets first somewhere
          // else in the cycle

          for (int j = 0; j < cycleNeighs.size(); j++) {
            HyperCell lastCell = (HyperCell)cycleNeighs.get(j);

            if (list.indexOf(lastCell) != -1) {
              Vector newV = new Vector(5);
              newV.addAll(list);
              newV.add(lastCell);
              addList.add(newV);
            }

            /*
            int curIndex = 0;
            int index;
            while ((index = list.indexOf(lastCell, curIndex)) != -1) {
              HyperCell inCell = (HyperCell)list.get(index);
              Vector newV = new Vector(5);
              newV.addAll(list);
              newV.add(inCell);
              addList.add(newV);
              System.out.println("while count:" + ++whileCount);

              curIndex = ++index;
            }
            */
          }
        }
      }

      cycleList.clear();
      cycleList.addAll(addList);
      cycleCount += cycleList.size();
      if (record) {
        for (int i = 0; i < cycleList.size(); i++) {
          Vector v = (Vector)cycleList.get(i);
          HyperCycle cycle = new HyperCycle(v);
          cycles.add(cycle);
        }
      }
    }

    private void makeLinks() {
      for (int i = 0; i < cycleList.size(); i++) {
        Vector list = (Vector)cycleList.get(i);

        for (int j = 0; j < list.size() - 1; j++) {
          HyperCell cell = (HyperCell)list.get(j);
          HyperCell toCell = (HyperCell)list.get(j + 1);
          HyperCycleLink link = new HyperCycleLink(cell, toCell);
          cell.addOutEdge(link);
          toCell.addInEdge(link);
          hyperNodes.add(cell);
          hyperNodes.add(toCell);
        }
      }
    }

    public void search(HyperCell cell) {
      cycleList.clear();
      Integer skill = (Integer)skills.get(0);

      // Does this cell have the first skill (int)
      if (cell.contains(skill)) {
        Vector v = new Vector(7);
        v.add(cell);
        cycleList.add(v);

        for (int i = 1; i < skills.size(); i++) {
          skill = (Integer)skills.get(i);
          getNeighbor(skill);
        }

        getFinalSkill((Integer)skills.get(0));
        if (showLinks)
          makeLinks();
      }
    }
  };

  public static void main(String[] args) {
    SimInit init = new SimInit();
    HyperModel model = new HyperModel();
    init.loadModel(model, "", false);
  }
}
