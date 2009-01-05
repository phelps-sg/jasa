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
package uchicago.src.sim.gui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import uchicago.src.collection.BaseMatrix;
import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Torus;

/**
 * Displays a 2D grid of Drawable2DNodes and DrawableEDges. Draws edges from
 * and to the center of nodes.<p> Like all all the DiscreteDisplayables
 * Network2DGridDisplay draws a Discrete2DSpace by getting the objects in each
 * cell in the Grid and sending those objects a draw message. If the
 * space being displayed implements the Torus marker interface, the edges will
 * be displayed as wrapping around the torus by default.
 *
 * Note that for
 * efficiency this only draws a Node's out edges (which must be some other Node's
 * in edge and so they all get drawn). It is important then that any edge
 * that is being drawn be added to some Nodes list of out edges. As a general
 * rule an edge should be added to both its from Node and its to Node.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see Drawable2DNode
 * @see DrawableEdge
 */
public class Network2DGridDisplay extends Object2DDisplay {

  private Hashtable nodePoint = new Hashtable (113);
  private ArrayList edges = new ArrayList (113);

  //private int xPad;
  //private int yPad;
  private int width;
  private int height;

  private int halfHeight;
  private int halfWidth;

  private boolean viewNodes = true;
  private boolean viewLinks = true;
  private boolean wrapLinks = true;
  private boolean isTorus = false;

  private int xSize;
  private int ySize;

  private class Point {
    int x;
    int y;


    Point (int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  /**
   * Creates a new Network2DGridDisplay for the specified grid.
   *
   * @param grid the grid to display
   */
  public Network2DGridDisplay (Discrete2DSpace grid) {
    super (grid);

    if (grid instanceof Torus) {
      isTorus = true;
    }

    int cellSize = DisplayConstants.CELL_WIDTH;

    if (cellSize < 5) {
      width = cellSize - 2;
    } else if (cellSize < 10) {
      width = cellSize - 4;
    } else if (cellSize < 15) {
      width = cellSize - 6;
    } else if (cellSize < 20) {
      width = cellSize - 8;
    } else {
      width = cellSize - 12;
    }

    cellSize = DisplayConstants.CELL_HEIGHT;

    if (cellSize < 5) {
      height = cellSize - 2;
    } else if (cellSize < 10) {
      height = cellSize - 4;
    } else if (cellSize < 15) {
      height = cellSize - 6;
    } else if (cellSize < 20) {
      height = cellSize - 8;
    } else {
      height = cellSize - 12;
    }

    halfWidth = width / 2;
    halfHeight = height / 2;

    //xPad = (DisplayConstants.CELL_WIDTH / 2) - halfWidth;
    //yPad = (DisplayConstants.CELL_HEIGHT / 2) - halfHeight;

    xSize = size.width;
    ySize = size.height;


  }

  /**
   * Shows or hides links depending on the value of parameter isVisible.
   *
   * @param isVisible if true, shows the links, otherwise hide links
   */
  public void setLinksVisible (boolean isVisible) {
    viewLinks = isVisible;
  }

  /**
   * Shows or hids nodes depending on the value of the parameter isVisible.
   *
   * @param isVisible if true, shows the nodes, otherwise hide nodes
   */

  public void setNodesVisible (boolean isVisible) {
    viewNodes = isVisible;
  }

  /**
   * Wraps the drawn links around the edges of the display, assuming the
   * space to be drawn is a Torus, depending on the value of wrap parameter.
   *
   * @param wrap if true, links will be drawn wrapped around the display,
   * otherwise not.
   */
  public void wrapLinks (boolean wrap) {
    wrapLinks = wrap;
  }


  // Displayable interface
  /**
   * Draws the grid.
   */
  public void drawDisplay (SimGraphics g) {

    if (!viewNodes && !viewLinks) {
      return;
    }

    int oldWidth = g.getCurWidth();
    int oldHeight = g.getCurHeight();

    int cellXSize = g.getCellWidthScale();
    int cellYSize = g.getCellHeightScale();
    int curWidth = cellXSize - 4;
    if (curWidth < 1) curWidth = cellXSize - 1;
    int curHeight = cellYSize - 4;
    if (curHeight < 1) curHeight = cellYSize - 1;
    halfWidth = curWidth / 2;
    halfHeight = curHeight / 2;

    synchronized (nodePoint) {

      g.setDrawingParametersNoScale (curWidth, curHeight, 0);
      Drawable2DGridNode d;
      if (objsToDraw == null) {
        BaseMatrix matrix = grid.getMatrix ();
        synchronized (matrix) {
          for (int i = 0; i < grid.getSizeX (); i++) {
            for (int j = 0; j < grid.getSizeY (); j++) {
              d = (Drawable2DGridNode) matrix.get (i, j);
              if (d != null) {
                ArrayList nodeEdges = d.getOutEdges ();
                if (nodeEdges != null) {
                  synchronized (nodeEdges) {
                    edges.addAll (nodeEdges);
                  }
                }

                // draw the node
                int x = i * cellXSize + 2;
                int y = j * cellYSize + 2;

                //System.out.println("X: " + i + ", Y: " + j);
                nodePoint.put (d, new Point (x + halfWidth, y + halfHeight));
                if (viewNodes) {
                  g.setDrawingCoordinates (x, y, 0);
                  d.draw (g);
                }  // if (viewNodes)
              } // if d != null
            } // for j ...
          } // for i ...
        } // synchronized matrix
      } else {
        ArrayList t;
        synchronized (objsToDraw) {
          t = new ArrayList(objsToDraw);
        }
        ListIterator li = t.listIterator ();
        while (li.hasNext ()) {
          d = (Drawable2DGridNode) li.next ();
          ArrayList nodeEdges = d.getOutEdges ();
          if (nodeEdges != null) {
            edges.addAll (nodeEdges);
          }

          // draw the node
          int x = (int) d.getX () * cellXSize + 2;
          int y = (int) d.getY () * cellYSize + 2;

          nodePoint.put (d, new Point (x + halfWidth, y + halfHeight));

          if (viewNodes) {
            g.setDrawingCoordinates (x, y, 0);
            d.draw (g);
          } // if viewNodes
        } // while li.hasNext
      } // if (objList == null)

      if (viewLinks) {
        drawEdges (g);
      }
    } // synchronized nodePoint
    edges.clear ();
    nodePoint.clear ();
    g.setDrawingParametersNoScale(oldWidth, oldHeight, 0);
  }

  private void drawEdges (SimGraphics g) {
    for (int i = 0; i < edges.size (); i++) {
      DrawableEdge edge = (DrawableEdge) edges.get (i);
      Point to = (Point) nodePoint.get (edge.getTo ());
      Point from = (Point) nodePoint.get (edge.getFrom ());

      if (wrapLinks && isTorus) {
        if (Math.abs (to.x - from.x) == xSize - DisplayConstants.CELL_WIDTH ||
                Math.abs (to.y - from.y) == ySize - DisplayConstants.CELL_HEIGHT) {
          int x1to, x2to, y1to, y2to;
          if (to.x == from.x) {
            x1to = to.x;
            x2to = to.x;
          } else if (to.x < from.x) {
            x1to = from.x - xSize;
            x2to = to.x + xSize;
          } else {
            x1to = to.x + xSize;
            x2to = from.x - xSize;
          }

          if (to.y == from.y) {
            y1to = to.y;
            y2to = to.y;
          } else if (to.y < from.y) {
            y1to = from.y - ySize;
            y2to = to.y + ySize;
          } else {
            y1to = to.y + ySize;
            y2to = from.y - ySize;
          }

          edge.draw (g, from.x, x2to, from.y, y2to);
          edge.draw (g, to.x, x1to, to.y, y1to);

        } else {
          edge.draw (g, from.x, to.x, from.y, to.y);
        }
      } else {
        edge.draw (g, from.x, to.x, from.y, to.y);
      }
    }
  }

  /**
   * Gets the DisplayableInfo
   *
   * @see DisplayInfo
   */
  public ArrayList getDisplayableInfo () {
    ArrayList list = new ArrayList ();
    list.add (new DisplayInfo ("Links", TOGGLE_LINKS, this));
    list.add (new DisplayInfo ("Nodes", TOGGLE_NODES, this));
    if (isTorus) {
      list.add (new DisplayInfo ("Wrap Links", TOGGLE_WRAP, this));
    }
    return list;
  }

  /**
   * Invoked when a viewEvent for this display is fired by the
   * DisplaySurface.
   */
  public void viewEventPerformed (ViewEvent evt) {
    int id = evt.getId ();

    boolean show = evt.showView ();

    if (id == TOGGLE_NODES) {
      viewNodes = show;
    } else if (id == TOGGLE_LINKS) {
      viewLinks = show;
    } else if (id == TOGGLE_WRAP) {
      wrapLinks = show;
    }
  }
}
