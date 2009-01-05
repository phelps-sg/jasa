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
package uchicago.src.sim.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.DefaultNode;
import uchicago.src.sim.network.NetUtilities;
import uchicago.src.sim.network.NetworkConstants;
import uchicago.src.sim.network.NetworkFactory;
import uchicago.src.sim.network.Node;
import uchicago.src.sim.util.Random;

/**
 * Tests DefaultNode.
 *
 * @version $Revision$ $Date$
 */
public class DefaultNodeTest extends TestCase {

  private DefaultNode iNode, jNode, kNode, lNode, mNode;
  private DefaultEdge edge1, edge2, edge3, edge4, edge5, edge6, edge7,
  edge8, edge9;

  public DefaultNodeTest(String name) {
    super(name);
    Random.createUniform();
  }

  /*
   * iNode -> lNode, jNode
   * jNode -> lNode, kNode
   * kNode -> iNode, jNode, lNode
   * lNode -> iNode, kNode
   * mNode ->
   */
  public void setUp() {
    Random.createUniform();
    iNode = new DefaultNode("iNode");
    jNode = new DefaultNode("jNode");
    kNode = new DefaultNode("kNode");
    lNode = new DefaultNode("lNode");
    mNode = new DefaultNode("mNode");

    edge1 = new DefaultEdge(iNode, lNode);
    iNode.addOutEdge(edge1);
    lNode.addInEdge(edge1);

    edge2 = new DefaultEdge(iNode, jNode);
    iNode.addOutEdge(edge2);
    jNode.addInEdge(edge2);

    edge3 = new DefaultEdge(jNode, lNode);
    jNode.addOutEdge(edge3);
    lNode.addInEdge(edge3);

    edge4 = new DefaultEdge(jNode, kNode);
    jNode.addOutEdge(edge4);
    kNode.addInEdge(edge4);

    edge5 = new DefaultEdge(kNode, iNode);
    kNode.addOutEdge(edge5);
    iNode.addInEdge(edge5);

    edge6 = new DefaultEdge(kNode, jNode);
    kNode.addOutEdge(edge6);
    jNode.addInEdge(edge6);

    edge7 = new DefaultEdge(kNode, lNode);
    kNode.addOutEdge(edge7);
    lNode.addInEdge(edge7);

    edge8 = new DefaultEdge(lNode, iNode);
    lNode.addOutEdge(edge8);
    iNode.addInEdge(edge8);

    edge9 = new DefaultEdge(lNode, kNode);
    lNode.addOutEdge(edge9);
    kNode.addInEdge(edge9);
  }

  public void testGetInEdges() {
    ArrayList inEdges = iNode.getInEdges();
    assertEquals(2, inEdges.size());
    assertTrue(inEdges.contains(edge5));
    assertTrue(inEdges.contains(edge8));

    inEdges = lNode.getInEdges();
    assertEquals(3, inEdges.size());
    assertTrue(inEdges.contains(edge1));
    assertTrue(inEdges.contains(edge3));
    assertTrue(inEdges.contains(edge7));
  }

  public void testGetInNodes() {
    // iNode <- lNode, kNode
    ArrayList inNodes = iNode.getInNodes();
    assertEquals(2, inNodes.size());
    assertEquals(kNode, inNodes.get(0));
    assertEquals(lNode, inNodes.get(1));

    inNodes = lNode.getInNodes();
    assertEquals(3, inNodes.size());
    assertEquals(iNode, inNodes.get(0));
    assertEquals(jNode, inNodes.get(1));
    assertEquals(kNode, inNodes.get(2));

    kNode.removeEdgesTo(iNode);
    iNode.removeEdgesFrom(kNode);
    inNodes = iNode.getInNodes();
    assertEquals(1, inNodes.size());
    assertEquals(lNode, inNodes.get(0));

    DefaultEdge edge5 = new DefaultEdge(kNode, iNode);
    kNode.addOutEdge(edge5);
    iNode.addInEdge(edge5);
    inNodes = iNode.getInNodes();
    assertEquals(2, inNodes.size());
    assertEquals(lNode, inNodes.get(0));
    assertEquals(kNode, inNodes.get(1));

    iNode.clearInEdges();
    inNodes = iNode.getInNodes();
    assertEquals(0, inNodes.size());
  }

  public void testGetOutEdges() {
    ArrayList outEdges = jNode.getOutEdges();
    assertEquals(2, outEdges.size());
    assertTrue(outEdges.contains(edge3));
    assertTrue(outEdges.contains(edge4));

    outEdges = kNode.getOutEdges();
    assertEquals(3, outEdges.size());
    assertTrue(outEdges.contains(edge5));
    assertTrue(outEdges.contains(edge6));
    assertTrue(outEdges.contains(edge7));
  }

  public void testGetOutNodes() {
    ArrayList outNodes = jNode.getOutNodes();
    assertEquals(2, outNodes.size());
    assertEquals(lNode, outNodes.get(0));
    assertEquals(kNode, outNodes.get(1));


    outNodes = kNode.getOutNodes();
    assertEquals(3, outNodes.size());
    assertEquals(iNode, outNodes.get(0));
    assertEquals(jNode, outNodes.get(1));
    assertEquals(lNode, outNodes.get(2));

    kNode.removeEdgesTo(jNode);
    outNodes = kNode.getOutNodes();
    assertEquals(2, outNodes.size());
    assertEquals(iNode, outNodes.get(0));
    assertEquals(lNode, outNodes.get(1));

    DefaultEdge edge5 = new DefaultEdge(kNode, mNode);
    kNode.addOutEdge(edge5);
    mNode.addInEdge(edge5);

    outNodes = kNode.getOutNodes();
    assertEquals(3, outNodes.size());
    assertEquals(iNode, outNodes.get(0));
    assertEquals(lNode, outNodes.get(1));
    assertEquals(mNode, outNodes.get(2));

    kNode.clearOutEdges();
    outNodes = kNode.getOutNodes();
    assertEquals(0, outNodes.size());


  }

  public void testHasEdgeFromOrTo() {
    assertTrue(iNode.hasEdgeToOrFrom(lNode));
    assertTrue(kNode.hasEdgeToOrFrom(jNode));
    assertTrue(!(mNode.hasEdgeToOrFrom(jNode)));
  }

  public void testAddOutEdge() {
    DefaultEdge edge = new DefaultEdge(mNode, iNode);
    mNode.addOutEdge(edge);
    assertEquals(1, mNode.getOutEdges().size());
    assertTrue(mNode.getOutEdges().contains(edge));
    assertTrue(mNode.getOutNodes().contains(iNode));
    assertTrue(mNode.getToNodes().contains(iNode));
  }

  public void testAddInEdge() {
    DefaultEdge edge = new DefaultEdge(mNode, iNode);
    iNode.addInEdge(edge);
    assertEquals(3, iNode.getInEdges().size());
    assertTrue(iNode.getInEdges().contains(edge));
    assertTrue(iNode.getFromNodes().contains(mNode));
  }

  public void testAddOutEdges() {
    ArrayList edges = new ArrayList();
    DefaultEdge edge1 = new DefaultEdge(mNode, iNode);
    DefaultEdge edge2 = new DefaultEdge(mNode, kNode);
    edges.add(edge1);
    edges.add(edge2);

    mNode.addOutEdges(edges);
    assertEquals(2, mNode.getOutEdges().size());
    assertTrue(mNode.getOutEdges().contains(edge1));
    assertTrue(mNode.getToNodes().contains(iNode));
    assertTrue(mNode.getToNodes().contains(kNode));
    assertTrue(!(mNode.getToNodes().contains(jNode)));
  }

  public void testAddInEdges() {
    ArrayList edges = new ArrayList();
    DefaultEdge edge1 = new DefaultEdge(iNode, mNode);
    DefaultEdge edge2 = new DefaultEdge(kNode, mNode);
    edges.add(edge1);
    edges.add(edge2);

    mNode.addInEdges(edges);
    assertEquals(2, mNode.getInEdges().size());
    assertTrue(mNode.getInEdges().contains(edge1));
    assertTrue(mNode.getFromNodes().contains(iNode));
    assertTrue(mNode.getFromNodes().contains(kNode));
    assertTrue(!(mNode.getFromNodes().contains(jNode)));
  }

  public void testClearInEdges() {
    iNode.clearInEdges();
    assertEquals(0, iNode.getInEdges().size());
    assertTrue(!(iNode.getFromNodes().contains(lNode)));
  }

  public void testClearOutEdges() {
    iNode.clearOutEdges();
    assertEquals(0, iNode.getOutEdges().size());
    assertTrue(!(iNode.getToNodes().contains(lNode)));
  }

  public void testRemoveInEdge() {
    kNode.removeInEdge(edge4);
    assertEquals(1, kNode.getInEdges().size());
    assertTrue(!(kNode.getFromNodes().contains(jNode)));
  }

  public void testRemoveOutEdge() {
    kNode.removeOutEdge(edge6);
    assertEquals(2, kNode.getOutEdges().size());
    assertTrue(!(kNode.getToNodes().contains(jNode)));
    assertTrue(jNode.getFromNodes().contains(kNode));
  }

  public void testGetRandomFromNode() {
    assertNull(mNode.getRandomFromNode());
    Node n = jNode.getRandomFromNode();
    assertTrue(jNode.getFromNodes().contains(n));
  }

  public void testGetRandomToNode() {
    assertNull(mNode.getRandomToNode());
    Node n = jNode.getRandomToNode();
    assertTrue(jNode.getToNodes().contains(n));
  }

  public void testRemoveEdgesTo() {
    DefaultEdge edge1 = new DefaultEdge(mNode, iNode);
    mNode.addOutEdge(edge1);
    DefaultEdge edge2 = new DefaultEdge(mNode, iNode);
    mNode.addOutEdge(edge2);

    assertEquals(2, mNode.getOutEdges().size());
    mNode.removeEdgesTo(iNode);
    assertEquals(0, mNode.getOutEdges().size());
    assertTrue(!(mNode.hasEdgeToOrFrom(iNode)));
  }

  public void testRemoveEdgesFrom() {
    DefaultEdge edge1 = new DefaultEdge(iNode, mNode);
    mNode.addInEdge(edge1);
    DefaultEdge edge2 = new DefaultEdge(iNode, mNode);
    mNode.addInEdge(edge2);

    assertEquals(2, mNode.getInEdges().size());
    mNode.removeEdgesFrom(iNode);
    assertEquals(0, mNode.getInEdges().size());
    assertTrue(!(mNode.hasEdgeToOrFrom(iNode)));
  }

  public void testNumOutEdges() {
    assertEquals(2, iNode.getNumOutEdges());
  }

  public void testNumInEdges() {
    assertEquals(2, kNode.getNumInEdges());
  }

  public void testHasEdgeTo() {
    assertTrue(iNode.hasEdgeTo(lNode));
    assertTrue(!(iNode.hasEdgeTo(mNode)));
  }

  public void testHasEdgeFrom() {
    assertTrue(iNode.hasEdgeFrom(kNode));
    assertTrue(!(iNode.hasEdgeFrom(mNode)));
  }

  public void testGetEdgesTo() {
    HashSet edgesTo = lNode.getEdgesTo(iNode);
    assertTrue(edgesTo.contains(edge8));
    assertEquals(1, edgesTo.size());

    assertNull(mNode.getEdgesTo(iNode));
  }

  public void testGetEdgesFrom() {
    DefaultEdge edge = new DefaultEdge(jNode, kNode);
    kNode.addInEdge(edge);

    HashSet edgesFrom = kNode.getEdgesFrom(jNode);
    assertEquals(2, edgesFrom.size());

    assertNull(mNode.getEdgesFrom(iNode));
  }

  public void testEdgeUpdates() {
    assertTrue(iNode.hasEdgeTo(lNode));


    // test that by setting the to node of edge1 we
    // 1. automatically add an in edge to mNode.
    // 2. Don't duplicate this edge if we explicitly add it as a
    // inEdge.
    // 3. That iNode no longer has an edge to lNode (lNode's place has been
    // taken by mNode).
    // 4. That iNode now has an edge to mNode.
    // 5. That explicitly adding edge1 to iNode does not increase iNode's
    // outDegree because edge1 has already been added to iNode.
    edge1.setTo(mNode);
    assertEquals(1, mNode.getInDegree());
    mNode.addInEdge(edge1);
    assertEquals(1, mNode.getInDegree());
    assertTrue(!(iNode.hasEdgeTo(lNode)));
    assertTrue(iNode.hasEdgeTo(mNode));
    assertEquals(2, iNode.getOutDegree());
    iNode.addOutEdge(edge1);
    assertEquals(2, iNode.getOutDegree());

    // do the same as above but test working with the "from" side.
    assertTrue(iNode.hasEdgeFrom(lNode));
    edge8.setFrom(mNode);
    assertEquals(1, mNode.getOutDegree());
    mNode.addOutEdge(edge8);
    assertEquals(1, mNode.getOutDegree());
    assertTrue(!(iNode.hasEdgeFrom(lNode)));
    assertTrue(mNode.hasEdgeFrom(iNode));
    assertEquals(2, iNode.getInDegree());
    iNode.addInEdge(edge8);
    assertEquals(2, iNode.getInDegree());
  }


  public void testRandomRewire() {
    // random rewire requires the node update code in DefaultEdge.setTo/setFrom
    // so we test it here. If those are not working correctly, we would get
    // a multiplex network.
    Random.setSeed(1000);
    Random.createUniform();
    List nodeList = NetworkFactory.getNetwork("uchicago/src/sim/test/krackhardt_advice.dl",
                                              NetworkFactory.DL,
                                              DefaultNode.class,
                                              DefaultEdge.class,
                                              NetworkConstants.BINARY);
    assertTrue(!NetUtilities.isMultiplexNet(nodeList));
    NetUtilities.randomRewire(nodeList, .2);
    assertTrue(!NetUtilities.isMultiplexNet(nodeList));
  }
  
  public static junit.framework.Test suite() {
    return new TestSuite(uchicago.src.sim.test.DefaultNodeTest.class);
  }
}
