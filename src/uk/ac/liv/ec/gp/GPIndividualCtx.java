/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2004 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package uk.ac.liv.ec.gp;

import ec.gp.*;
import ec.EvolutionState;
import ec.Problem;

import com.ibm.jikes.skij.Cons;
import com.ibm.jikes.skij.Symbol;
import com.ibm.jikes.skij.Nil;

import java.util.HashMap;
import java.util.ArrayList;

import uk.ac.liv.ec.gp.func.*;
import uk.ac.liv.util.FastNumber;

import java.io.Serializable;

/**
 * A temporary place to put some misc ECJ extensions.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPIndividualCtx extends GPIndividual implements Serializable {

  GPContext context = new GPContext();

  protected boolean misbehaved = false;

  static final GPNode[] GPNODE_ARR = new GPNode[0];

  public void setGPContext( EvolutionState state, int thread, ADFStack stack,
                        Problem problem ) {
    context.setState(state);
    context.setThread(thread);
    context.setStack(stack);
    context.setProblem(problem);
  }

  public void setGPContext( GPContext context ) {
    this.context = context;
  }

  public GPContext getGPContext() {
    return context;
  }

  public void evaluateTree( int treeNumber, GPData input ) {
    trees[treeNumber].child.eval(context.state, context.thread, input,
                                    context.stack, this, context.problem);
    context.getStack().reset();
  }

  public FastNumber evaluateNumberTree( int treeNumber ) {
    misbehaved = false;
    GPGenericData input = GPGenericDataPool.fetch();
    try {
      evaluateTree(treeNumber, input);
    } catch ( ArithmeticException e ) {
      misbehaved = true;
    }
    GPGenericDataPool.release(input);
    return (FastNumber) input.data;
  }

  public GPTree getTree( int treeNumber ) {
    return trees[treeNumber];
  }

  public boolean misbehaved() {
    return misbehaved;
  }

  public void doneEvaluating() {
    evaluated = true;
  }

  public void prepareForEvaluating() {
  }

  /**
   * Return the given tree as a scheme list.
   *
   * @param treeNumber The number of the tree to convert
   *
   * @returns A scheme list
   */
  public Object treeToScheme( int treeNumber ) {
    return nodeToSchemeList(trees[treeNumber].child);
  }

  /**
   * Return the first tree of this individual as a scheme list.
   */
  public Object toScheme() {
    return treeToScheme(0);
  }

  /**
   * Utility method for converting a terminal GPNode to a scheme atom
   *
   * @param node  The terminal to convert
   *
   * @returns A scheme atom
   */
  public static Object nodeToSchemeAtom( GPNode node ) {
    if ( node instanceof One ) {
      return new Integer(1);
    } else if ( node instanceof DoubleERC ) {
      return new Double(((DoubleERC) node).value);
    } else if ( node instanceof GPArithmeticBinaryOperator ) {
      return Symbol.intern(node.toString());
    } else {
      return Symbol.intern(node.toString());
    }
  }

  /**
   * Utility method to convert a non-terminal GPNode to a scheme list.
   *
   * @param node  The non-terminal GPNode
   *
   * @returns A scheme a list
   */

  public static Object nodeToSchemeList( GPNode node ) {
    if ( node.children.length == 0 ) {
      return nodeToSchemeAtom(node);
    } else {
      Object result = Nil.nil;
      for( int i=node.children.length-1; i>=0; i-- ) {
        result = new Cons(nodeToSchemeList(node.children[i]),result);
      }
      result = new Cons(nodeToSchemeAtom(node),result);
      return result;
    }
  }

  /**
   * Set the given tree number by converting a scheme s-expression.
   *
   * @param treeNum   The index of the tree to mute
   * @param scheme    The scheme s-expression
   * @param map       A scheme list specififying the symbol->fn mapping
   */
  public void setTree( int treeNum, Object scheme, Cons map ) {
    trees[treeNum].child = makeTree(scheme, map);
    trees[treeNum].child.parent = trees[treeNum];
  }

  public void setTree( Object scheme, Cons map ) {
    setTree(0, scheme, map);
  }

  /**
   * Utility method for converting building an ArrayList from a scheme list
   * according to some mapping between scheme symbols and GPNode classes.
   */
  public static void consToArray( Cons list, ArrayList aList, HashMap map,
                                  GPNode parent ) {
    if ( list.car != null ) {
        GPNode node = buildTreeFromScheme(list.car, map);
        node.parent = parent;
    	aList.add(node);
    }
    if ( list.cdr != null && list.cdr != Nil.nil ) {
      consToArray((Cons) list.cdr, aList, map, parent);
    }
  }

  public static GPNode[] consToArray( Cons list, HashMap map, GPNode parent ) {
    ArrayList aList = new ArrayList();
    consToArray(list, aList, map, parent);
    return (GPNode[]) aList.toArray( GPNODE_ARR );
  }

  /**
   * Utility method to convert scheme s-expression into a GPNode.
   */
  public static GPNode buildTreeFromScheme( Object scheme, HashMap map ) {
    GPNode result = new uk.ac.liv.ec.gp.func.Nil();
    if ( scheme instanceof Cons ) {
      Cons list = (Cons) scheme;
      GPNode node = buildNodeFromScheme(list.car, map);
      node.children = consToArray((Cons) list.cdr, map, node);
      result = node;
    } else {
      result = buildNodeFromScheme(scheme, map);
    }
    return result;
  }

  /**
   * Utility method to convert scheme s-expression into a GP tree.
   *
   * @param map A list defining a mapping between scheme symbols and
   * GPNode class names.
   */
  public static GPNode makeTree( Object scheme, Cons map ) {
    HashMap hashMap = new HashMap();
    for( Cons i = map; i != Nil.nil; i=(Cons) i.cdr ) {
      Cons pair = (Cons) i.car;
      hashMap.put(pair.car, pair.cdr);
    }
    return buildTreeFromScheme(scheme, hashMap);
  }


  public static GPNode buildNodeFromScheme( Object scheme, HashMap map ) {
    GPNode result = new uk.ac.liv.ec.gp.func.Nil();
    if ( scheme instanceof Symbol ) {
      Symbol className = (Symbol) map.get(scheme);
      if ( className == null ) {
        throw new Error("Symbol " + scheme + " not bound");
      }
      try {
        Class c = Class.forName(className.toString());
        GPNode node = (GPNode) c.newInstance();
        node.children = new GPNode[0];
        result = node;
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    } else if ( scheme instanceof Integer ) {
      int value = ((Integer) scheme).intValue();
      if ( value == 1 ) {
        result = new One();
        result.children = new GPNode[0];
      } else {
        DoubleERC node = new DoubleERC();
        node.value = (double) value;
        node.children = new GPNode[0];
        result = node;
      }
    } else {
      System.out.println("WARNING: Uknown data type " + scheme);
    }
    return result;
  }

}
