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

import scheme.kernel.ScmObject;
import scheme.kernel.ScmNumber;
import scheme.kernel.ScmInteger;
import scheme.kernel.ScmReal;
import scheme.kernel.ScmSymbol;
import scheme.kernel.ScmPair;

import scheme.extensions.ScmJavaObject;

import uk.ac.liv.ec.gp.func.*;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.Serializable;

/**
 * A temporary place to put some misc ECJ extensions.
 *
 * @author Steve Phelps
 * @version $Revision$
 */

public class GPSchemeIndividual extends GPIndividual implements Serializable {

  protected GPContext context = new GPContext();

  protected boolean misbehaved = false;

  private static final GPNode[] GPNODE_ARR = new GPNode[0];
  
  public GPSchemeIndividual() {
  	super();  	
  }
  
  
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

  public Number evaluateNumberTree( int treeNumber ) {
    misbehaved = false;
    GPGenericData input = new GPGenericData();
    try {
      evaluateTree(treeNumber, input);
    } catch ( ArithmeticException e ) {
      misbehaved = true;
    }    
    if ( input.data instanceof Boolean ) {
    	//TODO
    	System.out.println(input.data);
    }
    return (Number) input.data;
  }

  public GPTree getTree( int treeNumber ) {
    return trees[treeNumber];
  }

  public boolean misbehaved() {
    return misbehaved;
  }
  
  public void illegalResult() {
  	misbehaved = true;
  }

  public void doneEvaluating() {
    evaluated = true;
  }

  public void prepareForEvaluating() {
  }

  public void reset() {
  	misbehaved = false;
		evaluated = false;
  }

  /**
   * Return the given tree as a scheme list.
   *
   * @param treeNumber The number of the tree to convert
   *
   * @returns A scheme list
   */
  public ScmObject treeToScheme( int treeNumber ) {
    return nodeToSchemeList(trees[treeNumber].child);
  }

  /**
   * Return the first tree of this individual as a scheme list.
   */
  public ScmObject toScheme() {
    return treeToScheme(0);
  }

  /**
   * Utility method for converting a terminal GPNode to a scheme atom
   *
   * @param node  The terminal to convert
   *
   * @returns A scheme atom
   */
  public static ScmObject nodeToSchemeAtom( GPNode node ) {
    if ( node instanceof GPSchemeNode ) {
      return ((GPSchemeNode) node).toScheme();
    } else if ( node instanceof DoubleERC ) {
      return new ScmReal(((DoubleERC) node).value);
    } else {
      return new ScmSymbol(node.toString());
    }
  }

  /**
   * Utility method to convert a non-terminal GPNode to a scheme list.
   *
   * @param node  The non-terminal GPNode
   *
   * @returns A scheme a list
   */

  public static ScmObject nodeToSchemeList( GPNode node ) {
    if ( node.children.length == 0 ) {
      return nodeToSchemeAtom(node);
    } else {
      ScmObject result = ScmPair.NULL;
      for( int i=node.children.length-1; i>=0; i-- ) {
        result = new ScmPair(nodeToSchemeList(node.children[i]), result);
      }
      result = new ScmPair(nodeToSchemeAtom(node), result);
      return result;
    }
  }

  /**
   * Set the given tree number by converting a scheme s-expression.
   *
   * @param treeNum   The index of the tree to mute
   * @param scheme    The scheme s-expression
   * @param environment       A scheme list specififying the symbol->fn mapping
   */
  public void setTree( int treeNum, ScmObject scheme, ScmPair environment ) {
    trees[treeNum].child = makeTree(scheme, environment);
    trees[treeNum].child.parent = trees[treeNum];
  }

  public void setTree( ScmObject scheme, ScmPair map ) {
    setTree(0, scheme, map);
  }

  /**
   * Utility method for building an ArrayList from a scheme list
   * according to some mapping between scheme symbols and GPNode classes.
   */
  public static void consToArray( ScmPair list, ArrayList aList, HashMap map,
                                  GPNode parent ) {
    if ( list.getCar() != null ) {
    	GPNode node = buildTreeFromScheme(list.getCar(), map);
    	node.parent = parent;
    	aList.add(node);
    }
    if ( list.getCdr() != null && list.getCdr() != null ) {
      consToArray((ScmPair) list.getCdr(), aList, map, parent);
    }
  }

  public static GPNode[] consToArray( ScmPair list, HashMap map, GPNode parent ) {
    ArrayList aList = new ArrayList();
    consToArray(list, aList, map, parent);
    GPNode[] result = (GPNode[]) aList.toArray( GPNODE_ARR );
    for( byte i=0; i<result.length; i++ ) {
    	result[i].argposition = i;
    }
    return result;
  }

  /**
   * Utility method to convert scheme s-expression into a GPNode.
   */
  public static GPNode buildTreeFromScheme( ScmObject scheme, HashMap map ) {
    GPNode result = new uk.ac.liv.ec.gp.func.Nil();
    if ( scheme instanceof ScmPair ) {
      ScmPair list = (ScmPair) scheme;
      GPNode node = buildNodeFromScheme(list.getCar(), map);
      node.children = consToArray((ScmPair) list.getCdr(), map, node);
      result = node;
    } else {
      result = buildNodeFromScheme(scheme, map);
    }
    return result;
  }

  /**
   * Utility method to convert scheme s-expression into a GP tree.
   *
   * @param environment A list defining a mapping between scheme symbols and
   * GPNode class names.
   */
  public static GPNode makeTree( ScmObject scheme, ScmPair environment ) {
    HashMap hashMap = new HashMap();
    for( ScmPair i = environment; i != ScmPair.NULL; i=(ScmPair) i.getCdr() ) {
      ScmPair pair = (ScmPair) i.getCar();      
      hashMap.put(pair.getCar(), pair.getCdr());
    }
    return buildTreeFromScheme(scheme, hashMap);
  }


  public static GPNode buildNodeFromScheme( ScmObject scheme, HashMap map ) {
    GPNode result = null;
    if ( scheme instanceof ScmSymbol ) {
      ScmJavaObject schemeNode = (ScmJavaObject) map.get(scheme);
      if ( schemeNode == null ) {
      	throw new Error("No node defined for symbol " + scheme);
      }
      try {      	
      	result = (GPNode) ((GPNode) schemeNode.getValue()).protoClone();
      } catch ( CloneNotSupportedException e ) {
      	throw new Error(e);
      }
    } else if ( scheme instanceof ScmNumber ) {    	
    	if ( scheme instanceof ScmInteger ) {    		    		
    		result = buildNodeFromScheme(new ScmSymbol(LongConstant.NAME), map);
    		((LongConstant) result).setValue(((ScmInteger) scheme).getValue());
    	} else {
    		result = buildNodeFromScheme(new ScmSymbol(DoubleConstant.NAME), map);
    		((DoubleConstant) result).setValue(((ScmReal) scheme).getValue());
    	}	    
    } else {
    	throw new Error("Unknown data type " + scheme);
    }
    return result;
  }
  
}
