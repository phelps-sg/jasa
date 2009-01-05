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
package uchicago.src.sim.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import uchicago.src.sim.util.SimUtilities;
import cern.colt.list.IntArrayList;


/**
 * Class for constructing networks from text files in Pajek's *.net Arc/Edge
 * list file format. Will not read Pajek Arclist/Edgelist format, or Pajek
 * matrix format.  Requires that if "*Arcs" and "*Edges" both exist in file,
 * "*Arcs" must be before "*Edges" (this is Pajek's default).
 *<BR><BR>
 * Pajek is Windows-based freeware, written by Vladimir Batagelj and
 * Andrej Mrvar,University of Ljubljana,Slovenia downloadable from:
 * http://vlado.fmf.uni-lj.si/pub/networks/pajek/
 * <BR><BR>
 * currently does not read colors, edge widths, node sizes, etc..
 *
 * @version $Revision$ $Date$
 * @author Skye Bender-deMoll e-mail skyebend@santafe.edu
 */
public class PajekNetReader extends Object {
  private BufferedReader reader;
  private ArrayList nodeList = new ArrayList ();
  //hold coords for parsed drawable nodes
  private IntArrayList xCoordList = new IntArrayList ();
  private IntArrayList yCoordList = new IntArrayList ();

  public PajekNetReader (String fileAndPath) {
    try {
      reader = new BufferedReader (new FileReader (fileAndPath));
    } catch (IOException ex) {
      try {
        if (reader != null) reader.close ();
      } catch (IOException ex1) {
      }
      //handle error
      SimUtilities.showError ("Error reading network file: " + fileAndPath, ex);
      System.exit (0);
    }
  }

  /**
   * Returns a list of nodes of type nodeClass forming network corresponding
   * to the *.net file with edges of class edgeClass and with strengths
   * corresponding to the arcs and edges in the file
   * @param nodeClass the class to construct nodes from. Must implement Node
   * and have no-argument constructor
   * @param edgeClass the class to construct nodes from. Must implement Edge
   * and have no-argument constructor
   * @throws IOException
   */
  public List getNetwork (Class nodeClass, Class edgeClass)
          throws IOException {
    int numNodes = 0;
    nodeList.clear ();
    xCoordList.clear ();
    yCoordList.clear ();
    //parse header (1st line)
    numNodes = parseHeader (reader.readLine ());

    //next numNodes lines should be entries for nodes
    for (int n = 1; n <= numNodes; n++) {
      //parse node and add it to list
      parseNode (nodeClass, reader.readLine ().trim (), n);
    }

    //rest of lines should be arcs or edges until arcs or edges or null
    String subHead = reader.readLine ().trim ();
    String line = "";
    if (subHead.equals ("*Arcs")) {
      line = reader.readLine ().trim ();
      while ((line != null) && !line.equals ("*Edges") && !line.equals ("")) {
        //add Arcs (one way edge) to nodes
        parseArc (edgeClass, line.trim ());
        line = reader.readLine ();
      }
    }
    if (subHead.equals ("*Edges") || ((line != null) && line.equals ("*Edges"))) {
      line = reader.readLine ().trim ();
      while (line != null && !line.equals ("")) {
        //add Edges (edge in each direction) to nodes
        parseEdge (edgeClass, line.trim ());
        line = reader.readLine ();
      }
    }

    return nodeList;
  }

  /**
   * Returns a list of nodes of type nodeClass forming network corresponding
   * to the *.net file with edges of class edgeClass and strength corresponding
   * to the
   * arcs and edges in the file.  Coordinates of nodes are parsed from file,
   * and can be accessed via getXY(Node node) when constructing wrapper classes
   * spaceWidth and spaceHeight are needed to rescale the Pajek coordinates to
   * values used by repast.
   * @param nodeClass the class to construct nodes from. Must implement Node
   * and have no-argument constructor
   * @param edgeClass the class to construct nodes from. Must implement Edge
   * and have no-argument constructor
   * @param spaceWidth the horizontal dimension of the display in pixels
   * @param spaceHeight the vertical dimension of the display in pixels
   * @throws IOException
   */
  public List getDrawableNetwork (Class nodeClass, Class edgeClass,
                                  int spaceWidth, int spaceHeight)
          throws IOException {
    int numNodes = 0;
    nodeList.clear ();
    xCoordList.clear ();
    yCoordList.clear ();
    //parse header (1st line)
    numNodes = parseHeader (reader.readLine ());

    //next numNodes lines should be entries for nodes
    for (int n = 1; n <= numNodes; n++) {
      //parse node and add it to list
      parseDrawableNode (nodeClass, spaceWidth, spaceHeight,
                         reader.readLine ().trim (), n);
    }

    //rest of lines should be arcs or edges until arcs or edges or null
    String subHead = reader.readLine ().trim ();
    String line = "";
    if (subHead.equals ("*Arcs")) {
      line = reader.readLine ().trim ();
      while ((line != null) && !line.equals ("*Edges")) {
        //add Arcs (one way edge) to nodes
        parseArc (edgeClass, line.trim ());
        line = reader.readLine ();
      }
    }
    if (subHead.equals ("*Edges") || ((line != null) && line.equals ("*Edges"))) {
      line = reader.readLine ().trim ();
      while (line != null && !line.equals ("")) {
        //add Edges (edge in each direction) to nodes
        parseEdge (edgeClass, line.trim ());
        line = reader.readLine ();
      }
    }

    return nodeList;
  }


  private int parseHeader (String firstLine)
          throws IOException {
    int returnInt = 0;
    //first line should be *Vertices N where N is numNodes
    StringTokenizer header = new StringTokenizer (firstLine.trim (), " ");
    if (header.countTokens () == 2) {
      if (header.nextToken ().equals ("*Vertices")) {
        try {
          returnInt = Integer.parseInt (header.nextToken ());
        } catch (NumberFormatException intParseEx) {
          SimUtilities.showError ("Unable to parse number of Vertices: ", intParseEx);
        }
      } else {
        SimUtilities.showError ("File must begin with \"*Vertices:\" ",
                                new IOException ("Unable to Parse .net file"));
      }
    } else {
      SimUtilities.showError ("Wrong number of entries in first line of file",
                              new IOException ("Unable to Parse .net file"));
    }
    return returnInt;
  }


  private void parseNode (Class nodeClass, String line, int lineNumber)
          throws IOException {
    Node node;
    String label = "";
    int nodeNumber = 0;
    //as later this is to account for node names with spaces in them
    StringTokenizer quoteTokenizer = new StringTokenizer (line, "\"");
    if (quoteTokenizer.countTokens () < 2) {
      SimUtilities.showError ("Line " + lineNumber + "is missing entries",
                              new IOException ("Unable to Parse .net file"));
    } else {
      String first = quoteTokenizer.nextToken ();
      label = quoteTokenizer.nextToken ();

      // line should be: 1 "NodeLabel" xCoord yCoord otherParmStrings
      StringTokenizer nodeString = new StringTokenizer (first, " ");
      //make sure line number match correctly
      try {
        nodeNumber = Integer.parseInt (nodeString.nextToken ());
        if (nodeNumber != lineNumber) {
          SimUtilities.showError ("Vertex line numbers must be in sequence: ",
                                  new IOException ("Unable to Parse .net file"));
        }
      } catch (NumberFormatException intParseEx) {
        SimUtilities.showError ("Each vertex must be proceeded by an integer line number: ",
                                intParseEx);
      }
      //parse label
    }
    try {
      node = (Node) nodeClass.newInstance ();
      node.setNodeLabel (label);
      nodeList.add (node);
    } catch (IllegalAccessException e) {
      SimUtilities.showError ("Error instantiating nodes", e);
    } catch (InstantiationException e) {
      SimUtilities.showError ("Error instantiating nodes", e);
    }
  }

  private void parseArc (Class edgeClass, String line)
          throws IOException {
    Edge edge;
    int fromIndex, toIndex;
    double strength = 1;
    // line should be: fromIndex toIndex strength otherParmStrings
    StringTokenizer edgeString = new StringTokenizer (line, " ");
    if (edgeString.countTokens () < 2) {
      SimUtilities.showError ("An Arc is missing entries",
                              new IOException ("Unable to Parse .net file"));
    } else {
      //get from and to node ids
      try {
        fromIndex = Integer.parseInt (edgeString.nextToken ()) - 1;
        toIndex = Integer.parseInt (edgeString.nextToken ()) - 1;
        if (edgeString.hasMoreTokens ()) {
          strength = Double.parseDouble (edgeString.nextToken ());
        }
        // construct the edge
        try {
          Node fromNode = (Node) nodeList.get (fromIndex);
          Node toNode = (Node) nodeList.get (toIndex);
          edge = (Edge) edgeClass.newInstance ();
          edge.setFrom (fromNode);
          edge.setTo (toNode);
          edge.setStrength (strength);
          fromNode.addOutEdge (edge);
          toNode.addInEdge (edge);
        } catch (IllegalAccessException e) {
          SimUtilities.showError ("Error instantiating Edge", e);
        } catch (InstantiationException e) {
          SimUtilities.showError ("Error instantiating Edge", e);
        }
      } catch (NumberFormatException ex) {
        SimUtilities.showError ("Problem with Arc entries", ex);
      }

    }
  }

  private void parseEdge (Class edgeClass, String line) {
    Edge edge;
    Edge otherEdge;
    int fromIndex, toIndex;
    double strength = 1;
    // line should be: fromIndex toIndex strength otherParmStrings
    StringTokenizer edgeString = new StringTokenizer (line, " ");
    if (edgeString.countTokens () < 2) {
      SimUtilities.showError ("An Edge is missing entries",
                              new IOException ("Unable to Parse .net file"));
    } else {
      //get from and to node ids
      try {
        fromIndex = Integer.parseInt (edgeString.nextToken ()) - 1;
        toIndex = Integer.parseInt (edgeString.nextToken ()) - 1;
        if (edgeString.hasMoreTokens ()) {
          strength = Double.parseDouble (edgeString.nextToken ());
        }
        // construct the edge
        try {
          Node fromNode = (Node) nodeList.get (fromIndex);
          Node toNode = (Node) nodeList.get (toIndex);
          //from -> to edge
          edge = (Edge) edgeClass.newInstance ();
          edge.setFrom (fromNode);
          edge.setTo (toNode);
          edge.setStrength (strength);
          fromNode.addOutEdge (edge);
          toNode.addInEdge (edge);
          //to -> from edge
          otherEdge = (Edge) edgeClass.newInstance ();
          otherEdge.setFrom (toNode);
          otherEdge.setTo (fromNode);
          otherEdge.setStrength (strength);
          toNode.addOutEdge (otherEdge);
          fromNode.addInEdge (otherEdge);
        } catch (IllegalAccessException e) {
          SimUtilities.showError ("Error instantiating Edge", e);
        } catch (InstantiationException e) {
          SimUtilities.showError ("Error instantiating Edge", e);
        }
      } catch (NumberFormatException ex) {
        SimUtilities.showError ("Problem with Edge entries", ex);
      }

    }
  }

  //NEED TO KNOW THE SIZE OF THE SPACE TO SCALE TO
  //Pajek coords between 0.0 and 1.0
  private void parseDrawableNode (Class nodeClass, int spaceWidth,
                                  int spaceHeight, String line, int lineNumber)
          throws IOException {
    Node node;
    double x = 0;
    double y = 0;
    String label = "";
    int nodeNumber = 0;
    // line should be: 1 "NodeLabel" xCoord yCoord otherParmStrings
    //in order to account for node names with spaces, we first need this tokenizer
    //to strip out the node name
    StringTokenizer quoteTokenizer = new StringTokenizer (line, "\"");
    if (quoteTokenizer.countTokens () < 3) {
      SimUtilities.showError ("Line " + lineNumber + "is missing entries",
                              new IOException ("Unable to Parse .net file"));
    } else {
      //everything before the node name
      String first = quoteTokenizer.nextToken ();
      label = quoteTokenizer.nextToken ();
      //everything after the node name
      String last = quoteTokenizer.nextToken ();

      StringTokenizer nodeString = new StringTokenizer (first, " ");
      try {
        nodeNumber = Integer.parseInt (nodeString.nextToken ());
        if (nodeNumber != lineNumber) {
          SimUtilities.showError ("Vertex line numbers must be in sequence: ",
                                  new IOException ("Unable to Parse .net file"));
        }
      } catch (NumberFormatException intParseEx) {
        SimUtilities.showError ("Each vertex must be proceeded by an integer line number: ",
                                intParseEx);
      }
      //parse coords after the node name
      nodeString = new StringTokenizer (last, " ");
      if (nodeString.countTokens () < 2) {
        SimUtilities.showError ("Line " + lineNumber + "is missing entries",
                                new IOException ("Unable to Parse .net file"));
      } else {
        //make sure line number match correctly

        try {
          x = Double.parseDouble (nodeString.nextToken ());
          y = Double.parseDouble (nodeString.nextToken ());
          //rescale x and y coords to fit in space and round for int cast
          x = Math.round (x * spaceWidth);
          y = Math.round (y * spaceHeight);
        } catch (NumberFormatException doubleParseEx) {
          SimUtilities.showError ("Error reading .net file, unable to parse coordinates: ",
                                  doubleParseEx);
        }
      }
    }
    try {
      node = (Node) nodeClass.newInstance ();
      node.setNodeLabel (label);
      nodeList.add (node);
      xCoordList.add ((int) x);
      yCoordList.add ((int) y);
      //debug
      //System.out.println(label+" "+x+" "+y);
      //similar treatment for shapes/colors?
    } catch (IllegalAccessException e) {
      SimUtilities.showError ("Error instantiating Drawable node", e);
    } catch (InstantiationException e) {
      SimUtilities.showError ("Error instantiating Drawable node", e);
    }
  }

  /**
   * Returns an int[x,y] with the coordinates of the node if they were
   * read from the Pajek *.net file.  Otherwise returns [0,0]
   * @param node the node (must be of class nodeClass) to return x and y for
   */
  public int[] getXY (Node node) {
    int[] coords = {0, 0};
    int index = nodeList.indexOf (node);
    if ((index != -1) && (xCoordList.size () > 0)) {
      coords[0] = xCoordList.get (index);
      coords[1] = yCoordList.get (index);
    }
    return coords;
  }
}
