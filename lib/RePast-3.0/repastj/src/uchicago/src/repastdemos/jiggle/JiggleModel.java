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
package uchicago.src.repastdemos.jiggle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Network2DDisplay;
import uchicago.src.sim.gui.OvalNetworkItem;
import uchicago.src.sim.gui.RectNetworkItem;
import uchicago.src.sim.network.NetworkFactory;
import uchicago.src.sim.network.NetworkRecorder;
import uchicago.src.sim.network.Node;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

/**
 * A simple example model that illustrates how to create and display a
 * non-grid network.<p>
 *
 * The JiggleModel creates two networks of oval and rectangular agents where
 * each agent is linked to every other node in its network. These
 * agents then randomly pick another member of the network and if the two
 * agents do not overlap, the first agent moves towards the second. If they
 * do overlap the first moves away.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class JiggleModel extends SimModelImpl {

  // model variables
  private int numRects = 7;
  private int numOvals = 9;
  private ArrayList agentList = new ArrayList (16);
  private int worldXSize = 400;
  private int worldYSize = 400;
  private boolean loadFromFile = false;

  // implementation variables
  private DisplaySurface surface;
  private Schedule schedule;

  private NetworkRecorder nRecorder;

  // get and set accessor methods

  public void setLoadFromFile (boolean load) {
    loadFromFile = load;
  }

  public boolean isLoadFromFile () {
    return loadFromFile;
  }

  public void setNumRects (int n) {
    numRects = n;
  }

  public int getNumRects () {
    return numRects;
  }

  public void setNumOvals (int n) {
    numOvals = n;
  }

  public int getNumOvals () {
    return numOvals;
  }

  public int getWorldXSize () {
    return worldXSize;
  }

  public void setWorldXSize (int size) {
    worldXSize = size;
  }

  public int getWorldYSize () {
    return worldYSize;
  }

  public void setWorldYSize (int size) {
    worldYSize = size;
  }

  // creates an Edge between every agent in the list and every other
  // agent in the list. The Edges will be of the specified color.
  private ArrayList makeLinks (ArrayList list, Color color) {
    for (int i = 0; i < list.size (); i++) {
      Node node = (Node) list.get (i);
      for (int j = 0; j < list.size (); j++) {
        Node otherNode = (Node) list.get (j);
        if (otherNode != node) {
          JiggleEdge edge = new JiggleEdge (node, otherNode, color);
          node.addOutEdge (edge);
        }
      }
    }

    return list;
  }

  // builds the model either from a network description in a dl file,
  // or from scratch.
  public void buildModel () {

    if (loadFromFile) {
      buildModelFromFile ();
    } else {
      buildModelFromScratch ();
    }
  }

  // builds the Rect and OvalJigglers from a dl format file
  private void buildModelFromFile () {

    // if we didn't need to get the jiggle.dl from the the jiggle.jar
    // file we could just pass the file name to NetworkFactor.getNetwork(...)
    // However, we want to make this portable by including the jiggle.dl
    // file inside the jar.

    // by getting the file as a resource in this way, we can retrieve
    // it from the jiggle.jar archive.
    java.io.InputStream stream = getClass().getResourceAsStream ("/uchicago/src/repastdemos/jiggle/jiggle.dl");
    
    if (stream == null) {
      SimUtilities.showMessage("Unable to load network from file.");
      return;
    }

    // gets a list of JiggleNodes and JiggleEdges that
    // are constructed according to the matrix described in the
    // jiggle.dl file above.
    List list = NetworkFactory.getDLNetworkFromStream (stream,
                                                       JiggleNode.class,
                                                       JiggleEdge.class,
                                                       NetworkFactory.BINARY);
    agentList.clear ();
    
    for (int i = 0; i < list.size (); i++) {
      JiggleNode node = (JiggleNode) list.get (i);

      if (node.getNodeLabel ().startsWith ("Rect")) {
        // if the node label starts with Rect we ..
        int x = Random.uniform.nextIntFromTo (0, worldXSize - 1);
        int y = Random.uniform.nextIntFromTo (0, worldYSize - 1);

        // draw it as a rectangle.
        RectNetworkItem drawable = new RectNetworkItem (x, y);
        // we need to initialize it with the drawable and the world
        // size. Normally we would do this in the constructor, but
        // we have no access to the constructor when the JiggleNodes are
        // created from a file.
        node.init (worldXSize, worldYSize, drawable);
        node.setNodeLabel (node.getNodeLabel ());
        node.setBorderColor (Color.red);
        node.setBorderWidth (4);

        for (int j = 0; j < node.getOutEdges ().size (); j++) {
          // Rect nodes' edge color is green.
          JiggleEdge edge = (JiggleEdge) node.getOutEdges ().get (j);
          edge.setColor (Color.green);
        }
        agentList.add (node);

      } else {

        // if not a Rect node then its an OvalNode.
        int x = Random.uniform.nextIntFromTo (0, worldXSize - 1);
        int y = Random.uniform.nextIntFromTo (0, worldYSize - 1);

        // so we draw it as an oval.
        OvalNetworkItem drawable = new OvalNetworkItem (x, y);
        node.init (worldXSize, worldYSize, drawable);
        node.setNodeLabel (node.getNodeLabel ());
        node.setBorderColor (Color.orange);
        node.setBorderWidth (4);

        for (int j = 0; j < node.getOutEdges ().size (); j++) {
          // Oval node's edge color is red.
          JiggleEdge edge = (JiggleEdge) node.getOutEdges ().get (j);
          edge.setColor (Color.red);
        }
        agentList.add (node);
      }
    }
  }


  private void buildModelFromScratch () {
    ArrayList list = new ArrayList (numRects);
    for (int i = 0; i < numOvals; i++) {
      // create the Oval nodes.
      int x = Random.uniform.nextIntFromTo (0, worldXSize - 1);
      int y = Random.uniform.nextIntFromTo (0, worldYSize - 1);
      OvalNetworkItem drawable = new OvalNetworkItem (x, y);

      // we can use the constructor because we are creating the nodes
      // here.
      JiggleNode node = new JiggleNode (worldXSize, worldYSize, drawable);
      node.setNodeLabel ("Oval - " + i);
      node.setBorderColor (Color.orange);
      node.setBorderWidth (4);

      list.add (node);
    }

    list = makeLinks (list, Color.red);
    agentList.addAll (list);

    list.clear ();

    for (int i = 0; i < numRects; i++) {
      // create the Rect nodes
      int x = Random.uniform.nextIntFromTo (0, worldXSize - 1);
      int y = Random.uniform.nextIntFromTo (0, worldYSize - 1);

      RectNetworkItem drawable = new RectNetworkItem (x, y);
      JiggleNode node = new JiggleNode (worldXSize, worldYSize, drawable);
      node.setNodeLabel ("Rect - " + i);
      node.setBorderColor (Color.red);
      node.setBorderWidth (4);

      list.add (node);
    }

    list = makeLinks (list, Color.green);
    agentList.addAll (list);
  }

  /*
   * Builds the display. Creating the Network2DDisplay and then adding
   * it to the DisplaySurface. In this case we also add the Network2DDisplay
   * as a Zoomable.
   */
  public void buildDisplay () {
    Network2DDisplay display = new Network2DDisplay (agentList, worldXSize,
                                                     worldYSize);

    surface.addDisplayableProbeable (display, "Jiggle View");
    surface.addZoomable (display);
    surface.setBackground (java.awt.Color.white);
    addSimEventListener (surface);
  }

  private void buildSchedule () {

    // Every tick we call the jiggle method on every agent (JiggleNode).
    schedule.scheduleActionBeginning (0, new BasicAction () {
      public void execute () {
        for (int i = 0; i < agentList.size (); i++) {
          JiggleNode j = (JiggleNode) agentList.get (i);
          j.jiggle ();
        }
        surface.updateDisplay ();
      }
    });


    // At the 10th tick, we record the network to a file, and we
    // choose the first node in the list, get the its "first" edge,
    // and change that edge type. Changing the edge means that this
    // edges and its nodes will be treated as different network when
    // saved to a file. We also change the node's label as well.
    schedule.scheduleActionAt (10, new BasicAction () {
      public void execute () {
        nRecorder.record (agentList, "tick: " + getTickCount (),
                          NetworkRecorder.BINARY);
        Node node = (Node) agentList.get (0);
        JiggleEdge edge = (JiggleEdge) node.getOutEdges ().get (0);
        edge.setType ("Test");
        node.setNodeLabel ("tag");
        surface.updateDisplay ();
      }
    });


    // record the network to a file at the end of the simluation.
    schedule.scheduleActionAtEnd (new BasicAction () {
      public void execute () {
        nRecorder.record (agentList, "tick: " + getTickCount (),
                          NetworkRecorder.BINARY);
        nRecorder.write ();
      }
    });
  }

  public void begin () {
    buildModel ();
    buildDisplay ();
    buildSchedule ();
    surface.display ();
  }

  public void setup () {
    Random.createUniform ();

    if (surface != null)
      surface.dispose ();
    surface = null;
    schedule = null;

    System.gc ();

    surface = new DisplaySurface (this, "Jiggle");
    registerDisplaySurface ("Jiggle", surface);
    schedule = new Schedule (1);

    numRects = 7;
    numOvals = 9;
    agentList = new ArrayList (16);
    nRecorder = new NetworkRecorder (NetworkRecorder.DL,
                                     "./jiggle_matrix.dl", this);
    worldXSize = 400;
    worldYSize = 400;
  }

  public String[] getInitParam () {
    String[] params = {"numOvals", "numRects", "worldXSize", "worldYSize",
                       "loadFromFile"};
    return params;
  }

  public Schedule getSchedule () {
    return schedule;
  }

  public String getName () {
    return "Jiggle";
  }

  public static void main (String[] args) {
    uchicago.src.sim.engine.SimInit init = new uchicago.src.sim.engine.SimInit ();
    JiggleModel model = new JiggleModel ();
    init.loadModel (model, null, false);
  }
}
