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
package uchicago.src.collection;

/**
 * A Map whose keys are doubles and whose values are Objects. Objects are
 * inserted with an implied range, relative to the other objects inserted.
 * So,<br>
 * <pre><code>
 * map.put(0, obj1);
 * map.put(.5, obj2);
 * map.put(11.2, obj3);
 * </code></pre>
 *
 * associates obj1 with the range [0, .5) (inclusive of 0 and exclusive of .5),
 * and obj2 with the range [.5, 11.2) and obj3 with [11.2, pos_infinity).<p>
 *
 * Getting an object is done with a key as in other types of maps. However,
 * here the object returned is the object within whose range the key falls.
 * So, <code><pre>
 * map.get(.25);
 * </pre></code>
 *
 * will return obj1.<p>
 *
 * The data structure used here is redblack tree implemented without
 * removal. The implementation is largely a port of the C++ code in
 * Mark Allen Weiss, _Algorithms, Data Structures, and Problem Solving
 * with C++_.
 *
 * @version $Revision$ $Date$
 */

public class RangeMap {
  
  public static final int BLACK = 0;
  public static final int RED = 1;

  private BinaryNode header;
  private BinaryNode nullNode;
  private BinaryNode current;
  private BinaryNode parent;
  private BinaryNode grand;
  private BinaryNode great;

  /**
   * Constructs an empty range map.
   */
  public RangeMap() {
    nullNode = new BinaryNode();
    header = new BinaryNode();
    header.key = Double.NEGATIVE_INFINITY;
    nullNode.left = nullNode.right = header.left = header.right = nullNode;
  }

  /**
   * Removes all the entries in the map.
   */
  public void clear() {
    clear(header);
    header.right = nullNode;
    header.left = nullNode;
  }
  
  private void clear(BinaryNode node) {
    if (node != nullNode) {
      clear(node.left);
      clear(node.right);
      node.left = null;
      node.right = null;
    }
  }

  /**
   * Prints a representation of this RangeMap to the console.
   */
  public void print() {
    print(header);
  }

  /**
   * Prints a representation of this RangeMap to the console, beginning
   * with the specified node.
   */
  public void print(BinaryNode node) {
    if (node != nullNode) {
      System.out.println();
      System.out.println("node: " + node.key);
      if (node.left != nullNode)
	System.out.println("\tnode.left: " + node.left.key);
      if (node.right != nullNode)
	System.out.println("\tnode.right: " + node.right.key);
      System.out.println();
      
      print(node.left);
      print(node.right);
    }
  }

  /**
   * Gets the Object within whose range the specified key falls. If the
   * key is not within any objects range, then return null.
   *
   * @param key the number within the range whose associated object is
   * returned
   */
  public Object get(double key) {
    current = header.right;
    BinaryNode next = current;
    BinaryNode lastLessThan = null;

    while (next != nullNode) {
      current = next;
      //System.out.println("current.key: " + current.key);
      //System.out.println("key: " + key);
      
      if (key < current.key) next = current.left;
      else {
	lastLessThan = current;
	next = current.right;
      }
    }
    
    if (current == nullNode) return null;
    if (current.key > key) return (lastLessThan == null ? null :
				   lastLessThan.element);
    return current.element;
      
  }

  /**
   * Puts an object in the map and associates it with a range. The specified
   * key defines the inclusive lower bound of the range. The exclusive upper
   * bound is the next highest lower bound of any other inserted object. If
   * there is no lower bound higher than the specified lower, then the
   * exclusive upper bound is Double.POSITIVE_INFINITY.
   *
   * @param key the inclusive lower bound the range to associate with
   * the inserted object
   * @param obj the object to insert and associate with the range
   */
  public void put(double key, Object obj) {
    current = parent = grand = header;
    nullNode.key = key;
    while (current.key != key) {
      great = grand;
      grand = parent;
      parent = current;
      current = key < current.key ? current.left : current.right;
      if (current.left.color == RED && current.right.color == RED)
	reorient(key);
    }

    if (current != nullNode) {
      System.out.println("key: " + key);
      String mess = "Invalid Key: another object alreay inserted with key";
      throw new IllegalArgumentException(mess);
    }
    
    current = new BinaryNode(key, obj, nullNode, nullNode);

    if (key < parent.key) parent.left = current;
    else parent.right = current;

    reorient(key);
  }

  /**
   * Is this RangeMap empty.
   */
  public boolean isEmpty() {
    return header.right == nullNode;
  }

  private void reorient(double key) {
    current.color = RED;
    current.left.color = BLACK;
    current.right.color = BLACK;

    if (parent.color == RED) {
      grand.color = RED;
      if ((key < grand.key) != (key < parent.key)) parent = rotate(key, grand);

      current = rotate(key, great);
      current.color = BLACK;
      
    }

    header.right.color = BLACK;
  }

  private BinaryNode rotate(double key, BinaryNode node) {
    if (key < node.key) {
      node.left = key < node.left.key ? rotateWithLeftChild(node.left) :
	rotateWithRightChild(node.left);
      return node.left;
    } else {
      node.right = key < node.right.key ? rotateWithLeftChild(node.right) :
	rotateWithRightChild(node.right);
      return node.right;
    }
  }

  private BinaryNode rotateWithLeftChild(BinaryNode k2) {
    BinaryNode k1 = k2.left;
    k2.left = k1.right;
    k1.right = k2;
    return k1;
  }

  private BinaryNode rotateWithRightChild(BinaryNode k2) {
    BinaryNode k1 = k2.right;
    k2.right = k1.left;
    k1.left = k2;

    return k1;
  }
}
