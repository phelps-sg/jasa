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

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uchicago.src.sim.network.Edge;
import uchicago.src.sim.network.Node;
import uchicago.src.sim.space.VectorSpace;

/**
 * Displays nodes and links that inhabit a VectorSpace, that is, nodes
 * (and their links) that do not inhabit a grid.  This expects to be drawing a
 * VectorSpace of DrawableNonGridNodes or a List of DrawableNonGridNodes.
 * Note that for efficiency this only draws a Node's out edges (which must be
 * some other Node's in edge and so they all get drawn). It is important then
 * that any edge that is being drawn be added to some Nodes list of out edges.
 * In addition, these edges must implement DrawableEdge in order to be
 * drawn.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see DrawableNonGridNode
 * @see DrawableEdge
 */
public class Network2DDisplay implements Displayable, Probeable, Zoomable {

  protected GraphLayout layout;

  // todo move this to Displayable
  private static final int TOGGLE_EDGE_ON_TOP = 100;

  protected ArrayList edges = new ArrayList(113);
  protected Hashtable nodePoint = new Hashtable(113);

  protected boolean viewNodes = true;
  protected boolean viewLinks = true;
  protected ArrayList tmpList = new ArrayList();

  //protected boolean view = true;
  protected int height, width;
  protected HashMap zoomSet = new HashMap();

  private boolean drawEdgesFirst = true;
  
  class NodePoint {
    double x;
    double y;
    double nx, ny;
    int width, height;


    public NodePoint(double nx, double ny, int width, int height) {
      this.nx = nx;
      this.ny = ny;
      this.width = width;
      this.height = height;
      x = nx + width / 2;
      y = ny + height / 2;
    }

    public NodePoint(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }


  interface Drawer {
    public void drawDisplay(SimGraphics g);
  }

  class ZoomDrawer implements Drawer {
    public void drawDisplay(SimGraphics g) {
      if (!viewNodes && !viewLinks) {
        return;
      }

      float xScale = g.getXScale();
      float yScale = g.getYScale();

      if (drawEdgesFirst) {

        synchronized (nodePoint) {
          Iterator iter = zoomSet.keySet().iterator();
          while (iter.hasNext()) {
            DrawableNonGridNode node = (DrawableNonGridNode) iter.next();
            ArrayList outEdges = node.getOutEdges();
            if (outEdges != null) {
              int size = outEdges.size();
              for (int i = 0; i < size; i++) {
                Edge edge = (Edge) outEdges.get(i);
                Node n = edge.getTo();
                if (zoomSet.containsKey(n)) edges.add(edge);

              }
            }

            double x = node.getX() * xScale;
            double y = node.getY() * yScale;
            node.calcSize(g);
            int nodeWidth = (int) (node.getWidth() * xScale);
            int nodeHeight = (int) (node.getHeight() * xScale);
            nodePoint.put(node, new NodePoint(x, y, nodeWidth, nodeHeight));
          }

          if (viewLinks) {
            NodePoint fromP;
            NodePoint toP;
            for (int i = 0; i < edges.size(); i++) {
              DrawableEdge edge = (DrawableEdge) edges.get(i);
              toP = (NodePoint) nodePoint.get(edge.getTo());
              fromP = (NodePoint) nodePoint.get(edge.getFrom());
              edge.draw(g, (int) fromP.x, (int) toP.x, (int) fromP.y, (int) toP.y);
            }
          }

          if (viewNodes) {
            for (Iterator iter1 = nodePoint.entrySet().iterator(); iter1.hasNext();) {
              Map.Entry entry = (Map.Entry) iter1.next();
              DrawableNonGridNode node = (DrawableNonGridNode) entry.getKey();
              NodePoint point = (NodePoint) entry.getValue();

              g.setDrawingParametersNoScale(point.width, point.height, 0);
              g.setDrawingCoordinates((float) point.nx, (float) point.ny, 0);
              node.draw(g);
            }

          }
        }
      } else {

        synchronized (nodePoint) {
          Iterator iter = zoomSet.keySet().iterator();
          while (iter.hasNext()) {
            DrawableNonGridNode node = (DrawableNonGridNode) iter.next();
            ArrayList outEdges = node.getOutEdges();
            if (outEdges != null) {
              int size = outEdges.size();
              for (int i = 0; i < size; i++) {
                Edge edge = (Edge) outEdges.get(i);
                Node n = edge.getTo();
                if (zoomSet.containsKey(n)) edges.add(edge);

              }
            }

            double x = node.getX() * xScale;
            double y = node.getY() * yScale;
            node.calcSize(g);
            int nodeWidth = (int) (node.getWidth() * xScale);
            int nodeHeight = (int) (node.getHeight() * xScale);
            nodePoint.put(node, new NodePoint(x, y, nodeWidth, nodeHeight));

            if (viewNodes) {
              g.setDrawingParametersNoScale(nodeWidth, nodeHeight, 0);
              g.setDrawingCoordinates((float) x, (float) y, 0);
              node.draw(g);
            }
          }

          if (viewLinks) {
            NodePoint fromP;
            NodePoint toP;
            for (int i = 0; i < edges.size(); i++) {
              DrawableEdge edge = (DrawableEdge) edges.get(i);
              toP = (NodePoint) nodePoint.get(edge.getTo());
              fromP = (NodePoint) nodePoint.get(edge.getFrom());
              edge.draw(g, (int) fromP.x, (int) toP.x, (int) fromP.y, (int) toP.y);
            }
          }
        }
      }

      edges.clear();
      nodePoint.clear();
    }
  }

  class NormalDrawer implements Drawer {

    public void drawDisplay(SimGraphics g) {
      if (!viewNodes && !viewLinks) {
        return;
      }

      float xScale = g.getXScale();
      float yScale = g.getYScale();

      if (drawEdgesFirst) {
        synchronized (nodePoint) {
          ArrayList mList = layout.getNodeList();
          ArrayList list;
          synchronized (mList) {
            list = (ArrayList) mList.clone();
          }

          for (int i = 0; i < list.size(); i++) {
            DrawableNonGridNode node = (DrawableNonGridNode) list.get(i);
            if (node.getOutEdges() != null) {
              edges.addAll(node.getOutEdges());
            }

            double x = node.getX() * xScale;
            double y = node.getY() * yScale;
            node.calcSize(g);
            int nodeWidth = (int) (node.getWidth() * xScale);
            int nodeHeight = (int) (node.getHeight() * yScale);
            nodePoint.put(node, new NodePoint(x, y, nodeWidth, nodeHeight));
          }

          if (viewLinks) {
            NodePoint fromP;
            NodePoint toP;
            for (int i = 0; i < edges.size(); i++) {
              DrawableEdge edge = (DrawableEdge) edges.get(i);
              toP = (NodePoint) nodePoint.get(edge.getTo());
              fromP = (NodePoint) nodePoint.get(edge.getFrom());
              //if (toP == null || fromP == null) {
              //  System.out.println(toP + ", " + fromP);
              //}
              edge.draw(g, (int) fromP.x, (int) toP.x, (int) fromP.y, (int) toP.y);
            }
          }

          if (viewNodes) {
            for (Iterator iter = nodePoint.entrySet().iterator(); iter.hasNext();) {
              Map.Entry entry = (Map.Entry) iter.next();
              DrawableNonGridNode node = (DrawableNonGridNode) entry.getKey();
              NodePoint point = (NodePoint) entry.getValue();

              g.setDrawingParametersNoScale(point.width, point.height, 0);
              g.setDrawingCoordinates((float) point.nx, (float) point.ny, 0);
              node.draw(g);
            }
          }
        }

      } else {
        synchronized (nodePoint) {
          ArrayList mList = layout.getNodeList();
          ArrayList list;
          synchronized (mList) {
            list = (ArrayList) mList.clone();
          }

          for (int i = 0; i < list.size(); i++) {
            DrawableNonGridNode node = (DrawableNonGridNode) list.get(i);
            if (node.getOutEdges() != null) {
              edges.addAll(node.getOutEdges());
            }

            double x = node.getX() * xScale;
            double y = node.getY() * yScale;
            node.calcSize(g);
            int nodeWidth = (int) (node.getWidth() * xScale);
            int nodeHeight = (int) (node.getHeight() * yScale);
            nodePoint.put(node, new NodePoint(x, y, nodeWidth, nodeHeight));

            if (viewNodes) {
              g.setDrawingParametersNoScale(nodeWidth, nodeHeight, 0);
              g.setDrawingCoordinates((float) x, (float) y, 0);
              node.draw(g);
            }
          }

          if (viewLinks) {
            NodePoint fromP;
            NodePoint toP;
            for (int i = 0; i < edges.size(); i++) {
              DrawableEdge edge = (DrawableEdge) edges.get(i);
              toP = (NodePoint) nodePoint.get(edge.getTo());
              fromP = (NodePoint) nodePoint.get(edge.getFrom());
              //if (toP == null || fromP == null) {
              //  System.out.println(toP + ", " + fromP);
              //}
              edge.draw(g, (int) fromP.x, (int) toP.x, (int) fromP.y, (int) toP.y);
            }
          }
        }
      }

      edges.clear();
      nodePoint.clear();
    }
  }

  private Drawer drawer = new NormalDrawer();


  /**
   * Creates a Network2DDisplay of the specified width and height for
   * displaying the DrawableNonGridNodes and DrawableEdges
   * in the specified space.
   *
   * @param space the space containing the DrawableNonGridNodes to display
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */

  public Network2DDisplay(VectorSpace space, int width, int height) {
    this(space.getMembers(), width, height);

  }

  /**
   * Creates a Network2DDisplay of the specified width and height for
   * displaying the DrawableNonGridNodes in the list.
   *
   * @param list the list containing the DrawableNonGridNodes to display
   * @param width the width of the display in pixels
   * @param height the height of the display in pixels
   */

  public Network2DDisplay(List list, int width, int height) {
    this.height = height;
    this.width = width;

    layout = new DefaultGraphLayout(list, width, height);
  }

  /**
   * Creates a Network2DDisplay using the specified GraphLayout.
   *
   * @param layout the GraphLayout for this Network2DDisplay.
   */
  public Network2DDisplay(GraphLayout layout) {
    width = layout.getWidth();
    height = layout.getHeight();
    this.layout = layout;
  }

  /**
   * Gets whether or not this draws the edges first and then the
   * nodes over them. Default value is false.
   *
   * @return if true this draws the edges first then the nodes over them
   */
  public boolean getDrawEdgesFirst() {
    return drawEdgesFirst;
  }

  /**
   * Sets whether or not this draws the edges first and then the
   * nodes over them. Default value is false.
   *
   * @param drawEdgesFirst whether or not this draws edges first then nodes
   */
  public void setDrawEdgesFirst(boolean drawEdgesFirst) {
    this.drawEdgesFirst = drawEdgesFirst;
  }

  /**
   * Gets the size of this VectorDisplay.
   */
  public Dimension getSize() {
    return new Dimension(layout.getWidth(), layout.getHeight());
  }

  /**
   * Shows or hides links depending on the value of parameter isVisible.
   *
   * @param isVisible if true, shows the links, otherwise hide links
   */
  public void setLinksVisible(boolean isVisible) {
    viewLinks = isVisible;
  }

  /**
   * Shows or hids nodes depending on the value of the parameter isVisible.
   *
   * @param isVisible if true, shows the nodes, otherwise hide nodes
   */
  public void setNodesVisible(boolean isVisible) {
    viewNodes = isVisible;
  }

  /**
   * Draws this display
   */
  public void drawDisplay(SimGraphics g) {
    drawer.drawDisplay(g);
  }

  /**
   * Gets the DisplayableInfo
   *
   * @see DisplayInfo
   */
  public ArrayList getDisplayableInfo() {
    ArrayList list = new ArrayList();
    list.add(new DisplayInfo("Links", TOGGLE_LINKS, this));
    list.add(new DisplayInfo("Nodes", TOGGLE_NODES, this));
    list.add(new DisplayInfo("Update Layout", TOGGLE_UPDATE_LAYOUT, this));
    //list.add(new DisplayInfo("Draw Edges On Top", TOGGLE_EDGE_ON_TOP, this));

    return list;
  }

  /**
   * Gets a list of the objects that contain the specified screen coordinate.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public ArrayList getObjectsAt(int x, int y) {
    ArrayList list = layout.getNodeList();
    Point p = new Point((int) (x / SimGraphics.getInstance().getXScale()),
                        (int) (y / SimGraphics.getInstance().getYScale()));

    ArrayList retList = new ArrayList();
    for (int i = 0; i < list.size(); i++) {
      Object o = list.get(i);
      if (o instanceof NonGridDrawable) {
        NonGridDrawable item = (NonGridDrawable) o;
        if (item.contains(p)) retList.add(o);
      }
    }
    return retList;
  }

  /**
   * Sets the new coordinates for specified moveable. This goes through
   * probeable as some translation between screen pixel coordinates and
   * the simulation coordinates may be necessary.
   *
   * @param moveable the moveable whose coordinates are changed
   * @param x the x coordinate in pixels
   * @param y the y coordinate in pixels
   */
  public void setMoveableXY(Moveable moveable, int x, int y) {
    moveable.setX((int) (x / SimGraphics.getInstance().getXScale()));
    moveable.setY((int) (y / SimGraphics.getInstance().getYScale()));
  }

  public void startZoom(int x, int y, int zWidth, int zHeight) {
    int xLimit = x + zWidth;
    int yLimit = y + zHeight;
    drawer = new ZoomDrawer();
    zoomSet.clear();
    ArrayList list = layout.getNodeList();
    int size = list.size();
    double xScale = width / (double) zWidth;
    double yScale = height / (double) zHeight;

    // current display surface drag scale.
    float xpScale = SimGraphics.getInstance().getXScale();
    float ypScale = SimGraphics.getInstance().getYScale();

    for (int i = 0; i < size; i++) {
      DrawableNonGridNode node = (DrawableNonGridNode) list.get(i);
      double oldX = node.getX();
      double oldY = node.getY();
      double nx = oldX * xpScale;
      double ny = oldY * ypScale;
      if (nx > x && nx < xLimit && ny > y && ny < yLimit) {
        node.setX((nx - x) * xScale);
        node.setY((ny - y) * yScale);
        zoomSet.put(node, new NodePoint(oldX, oldY));
      }
    }
  }

  public void endZoom() {
    drawer = new NormalDrawer();
    Iterator iter = zoomSet.keySet().iterator();
    while (iter.hasNext()) {
      DrawableNonGridNode node = (DrawableNonGridNode) iter.next();
      NodePoint p = (NodePoint) zoomSet.get(node);
      node.setX(p.x);
      node.setY(p.y);
    }
  }

  /**
   * Invoked when a viewEvent for this display is fired by the
   * DisplaySurface.
   */
  public void viewEventPerformed(ViewEvent evt) {
    int id = evt.getId();
    boolean show = evt.showView();
    //System.out.println(id + ": " + show);

    if (id == TOGGLE_NODES) {
      viewNodes = show;
    } else if (id == TOGGLE_LINKS) {
      viewLinks = show;
    } else if (id == TOGGLE_UPDATE_LAYOUT) {
      layout.setUpdate(show);
    } else if (id == TOGGLE_EDGE_ON_TOP) {
      drawEdgesFirst = show;
    }
  }
}

