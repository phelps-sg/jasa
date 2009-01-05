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
package uchicago.src.repastdemos.life;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Hashtable;

import uchicago.src.reflector.ListPropertyDescriptor;
import uchicago.src.sim.engine.SimpleModel;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Displayable;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.util.ProbeUtilities;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

/**
 * An implementation of Conway's game of Life. This is implemented
 * here on both a bounded toroidal grid and on an "infinite"
 * space. The infinite space is in fact a really large sparse matrix,
 * see InfiniteLifeSpace.java for more.<p>
 *
 * This is an implementation of John Conway's game of life. Life is
 * typically played on an "infinite" square grid. Each cell can be
 * either live or dead. A cell comes to life if it has three
 * neighboring live cells, and will die of "loneliness" if it has less
 * than two neighbors, and will die of overcrowding if it has more
 * than three neighbors.<p>

 * Particular starting patterns of live cells can yield some very
 * interesting results. This implementation provides a rather bland
 * random initialization as well as the R-Pentimino. You can create
 * your own starting pattern by choosing Empty as the initial pattern
 * and stepping one tick into the simulation to show the display. You
 * can then click on the display to add live cells wherever you
 * want. Percent full is used to specify the percent full for the
 * random initial pattern.  This implementation provides an "infinite"
 * (a really large) grid as well as a torus of user-defined size. When
 * using the the infinite space you can scroll the display to see
 * what's happening off-screen. Make sure the display frame has focus,
 * and then press the arrow keys to scroll the display.<p>

 * The web is littered with web pages devoted to Conway's Life. One
 * such is http://www.math.com/students/wonders/life/life.html.<p>
 *
 * This simulation uses SimpleModel as its base class. As such all the
 * behavoir begins with LifeModel's step(), and postStep() method.
 * Every tick each checks if it has 2 or 3 neighbors. If so, it continues
 * to live, else it dies. LifeAgents do not die immediately. They are
 * added to a deathList and then removed from the main list of agents
 * (agentList) in postStep(). After this check is done for every agent,
 * we call step on the space. This if any empty space is surrounded by
 * 3 and only 3 agents. If so a new agent is born. Like death, birth is
 * delayed until postStep().
 */
public class LifeModel extends SimpleModel {

  private ArrayList birthList = new ArrayList();
  private ArrayList deathList = new ArrayList();
  private Space space;
  private int width = 50;
  private int height = 50;
  private boolean infinite = true;
  private DisplaySurface dsurf;
  private float percentFull = .75f;
  private ISetup initialPattern;

  private Displayable display;
  private int viewX, viewY;

  
  /*
   * Setup is very object oriented here. There are three setup classes
   * all implementing the ISetup interface. When we choose the initial
   * patter via the gui, we are choosing one of the setup classes,
   * and we then call setup on this class when building the model.
   */
  interface ISetup {
    public void setup();
  }

  // Do no setup of agents. This provides empty field for user
  // to add agents.
  class EmptySetup implements ISetup {
    public void setup() {
    }


    public boolean equals(Object o) {
      if (o instanceof EmptySetup) return true;
      return false;
    }

    public int hashCode() {
      return "EmptySetup".hashCode();
    }

    public String toString() {
      return "Empty Setup";
    }
  }

  // Setup agents in initial RPentimino pattern.
  class RPentSetup implements ISetup {
    public void setup() {

      int midX = space.getXSize() / 2;
      int midY = space.getYSize() / 2;

      LifeAgent agent1 = new LifeAgent(midX, midY, space);
      LifeAgent agent2 = new LifeAgent(midX, midY - 1, space);
      LifeAgent agent3 = new LifeAgent(midX, midY + 1, space);
      LifeAgent agent4 = new LifeAgent(midX + 1, midY - 1, space);
      LifeAgent agent5 = new LifeAgent(midX - 1, midY, space);
      space.addAgent(agent1);
      agentList.add(agent1);
      space.addAgent(agent2);
      agentList.add(agent2);
      space.addAgent(agent3);
      agentList.add(agent3);
      space.addAgent(agent4);
      agentList.add(agent4);
      space.addAgent(agent5);
      agentList.add(agent5);
    }


    public boolean equals(Object o) {
      if (o instanceof RPentSetup) return true;
      return false;
    }

    public int hashCode() {
      return "RPentSetup".hashCode();
    }


    public String toString() {
      return "RPentimino Setup";
    }
  }

  // Setup with random fill of grid.
  class RandomSetup implements ISetup {
    public void setup() {
      int top, left;
      top = left = 0;
      if (infinite) {
        left = InfiniteLifeSpace.MAX / 2 - width / 2;
        top = InfiniteLifeSpace.MAX / 2 - height / 2;
      }

      int right = left + width - 1;
      int bottom = top + height - 1;

      if (percentFull >= 1.0 || percentFull <= 0) {
        SimUtilities.
                showMessage("Percent Full >= 1.0 or <= 0, defaulting to .75");
        percentFull = .75f;
        ProbeUtilities.updateProbePanel(this);
      }

      int numAgents = (int) (width * height * percentFull);
      for (int i = 0; i < numAgents; i++) {
        int x, y;

        do {
          x = getNextIntFromTo(left, right);
          y = getNextIntFromTo(top, bottom);
        } while (!(space.isEmptyAt(x, y)));

        LifeAgent agent = new LifeAgent(x, y, space);
        space.addAgent(agent);
        agentList.add(agent);
      }
    }


    public boolean equals(Object o) {
      if (o instanceof RandomSetup) return true;
      return false;
    }


    public int hashCode() {
      return "RandomSetup".hashCode();
    }


    public String toString() {
      return "Random Setup";
    }
  }

  public LifeModel() {
    name = "Conway's Life";
    params = new String[]{"Width", "Height", "PercentFull", "InitialPattern",
                          "Infinite"};


    Hashtable h1 = new Hashtable();
    h1.put(new RandomSetup(), "RANDOM");
    h1.put(new RPentSetup(), "R-PENTIMINO");
    h1.put(new EmptySetup(), "EMPTY");
    ListPropertyDescriptor pd = new ListPropertyDescriptor("InitialPattern",
                                                           h1);
    descriptors.put("InitialPattern", pd);
  }

  // Property / Parameters accessors
  public ISetup getInitialPattern() {
    return initialPattern;
  }


  public void setInitialPattern(ISetup val) {
    initialPattern = val;
  }


  public int getWidth() {
    return width;
  }

  public void setWidth(int val) {
    width = val;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int val) {
    height = val;
  }

  public float getPercentFull() {
    return percentFull;
  }

  public void setPercentFull(float val) {
    percentFull = val;
  }

  public boolean getInfinite() {
    return infinite;
  }


  public void setInfinite(boolean val) {
    infinite = val;
  }

  // build the model based on the current parameters.
  public void buildModel() {
    if (infinite)
      space = new InfiniteLifeSpace(this, width, height);
    else
      space = new LifeSpace(width, height, true, this);

    initialPattern.setup();
    display = space.getDisplay();
    dsurf.addDisplayableProbeable(display, "Life Display");

    if (infinite) {
      viewX = ((InfiniteSpaceDisplay) display).getViewX();
      viewY = ((InfiniteSpaceDisplay) display).getViewY();
    } else
      ((Object2DDisplay) display).setObjectList(agentList);

    // these add the ability to add agents via mouse clicks and drags.
    dsurf.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        mouseAddAgent(evt);
      }
    });

    dsurf.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent evt) {
        mouseAddAgent(evt);
      }
    });

    addSimEventListener(dsurf);
    dsurf.display();
  }

  // Add an agent at the specified mouse coordinate. Note that
  // the mouse coordinate will be in pixels and has an origin of
  // 0,0. We need to translate using the view window origin and
  // the number of pixels per grid cell.
  private void mouseAddAgent(MouseEvent evt) {
    int xCoord = viewX + evt.getX() / SimGraphics.getInstance().getCellWidthScale();
    int yCoord = viewY + evt.getY() / SimGraphics.getInstance().getCellHeightScale();
    if (space.isEmptyAt(xCoord, yCoord)) {
      LifeAgent agent = new LifeAgent(xCoord, yCoord, space);
      space.addAgent(agent);
      agentList.add(agent);
      dsurf.updateDisplayDirect();
    }
  }


  // moves the view window allowing the window to scroll over the
  // infinite space.
  private void moveViewWindow() {
    InfiniteSpaceDisplay isd = (InfiniteSpaceDisplay) display;
    isd.setViewWindow(viewX, viewY, width, height);
    dsurf.updateDisplayDirect();
  }

  public void setup() {
    super.setup();
    width = 50;
    height = 50;
    percentFull = .75f;
    infinite = true;
    initialPattern = new RPentSetup();
    viewX = viewY = 0;
    if (dsurf != null) dsurf.dispose();
    System.gc();
    dsurf = new DisplaySurface(this, "Life Display");
    registerDisplaySurface("Main", dsurf);
    generateNewSeed();
    Random.createUniform();

    dsurf.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent evt) {
        if (infinite) {
          switch (evt.getKeyCode()) {
            case KeyEvent.VK_DOWN:
              if (viewY < InfiniteLifeSpace.MAX - 10) viewY += 10;
              break;

            case KeyEvent.VK_UP:
              if (viewY > 9) viewY -= 10;
              break;

            case KeyEvent.VK_LEFT:
              if (viewX > 9) viewX -= 10;
              break;

            case KeyEvent.VK_RIGHT:
              if (viewY < InfiniteLifeSpace.MAX - 10) viewX += 10;
              break;

            default:

          }
          moveViewWindow();
        }

      }

    });
  }

  // removing an agent is equivalent to adding it to the deathList.
  // We then remove all the agents in this deathList from the main agent
  // list (agentList) in postStep().
  public void removeAgent(LifeAgent agent) {
    deathList.add(agent);
  }

  // adding an agent is equivalent to adding it to the birthList.
  // We add all the members of this birthList to the the main agent
  // list (agentList) in postStep().
  public void addAgent(LifeAgent agent) {
    birthList.add(agent);
  }

  // this is executed every tick.
  public void step() {
    int size = agentList.size();
    for (int i = 0; i < size; i++) {
      LifeAgent agent = (LifeAgent) agentList.get(i);
      agent.step();
    }

    space.step(agentList);
  }

  // this is executed every tick immediately after step() above.
  public void postStep() {
    agentList.removeAll(deathList);
    agentList.addAll(birthList);

    int size = deathList.size();
    for (int i = 0; i < size; i++) {
      LifeAgent agent = (LifeAgent) deathList.get(i);
      space.removeAgentAt(agent.getX(), agent.getY());
    }

    deathList.clear();

    size = birthList.size();
    for (int i = 0; i < size; i++) {
      LifeAgent agent = (LifeAgent) birthList.get(i);
      space.addAgent(agent);
    }

    birthList.clear();
    dsurf.updateDisplay();
  }

  public static void main(String[] args) {
    uchicago.src.sim.engine.SimInit init =
            new uchicago.src.sim.engine.SimInit();
    LifeModel model = new LifeModel();
    init.loadModel(model, null, false);
  }
}
