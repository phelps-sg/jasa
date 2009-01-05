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
package uchicago.src.sim.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import uchicago.src.codegen.GeneratorException;
import uchicago.src.sim.util.ByteCodeBuilder;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.util.SimUtilities;

/**
 *
 * Experimental
 *
 * @version $Revision$ $Date$
 */
public class DefaultGroup implements Group, List {

  private List items = new ArrayList();
  private BasicAction stepMethod;
  private String stepMethodName;
  private Map minCalculators = new HashMap();
  private Map maxCalculators = new HashMap();
  private Map avgCalculators = new HashMap();
  private Map minPickers = new HashMap();
  private Map maxPickers = new HashMap();
  private Map methods = new HashMap();
  private Class objClass;


  public DefaultGroup(Class clazz, String stepMethodName) {
    this.stepMethodName = stepMethodName;
    this.objClass = clazz;
  }

  public DefaultGroup(Collection items, String stepMethodName) {
    if (items.size() == 0) throw new IllegalArgumentException("DefaultGroup cannot be created with an empty collection");
    this.items.addAll(items);
    objClass = this.items.get(0).getClass();
    this.stepMethodName = stepMethodName;
  }

  public DefaultGroup(Class clazz, Collection items, String stepMethodName) {
    if (items.size() == 0) throw new IllegalArgumentException("DefaultGroup cannot be created with an empty collection");
    this.items.addAll(items);
    this.stepMethodName = stepMethodName;
    objClass = clazz;
  }

  public boolean add(Object obj) {
    return items.add(obj);
  }

  public boolean remove(Object obj) {
    return items.remove(obj);
  }

  public int size() {
    return items.size();
  }

  public void clear() {
    items.clear();
  }

  public boolean isEmpty() {
    return items.size() == 0;
  }

  public Iterator iterator() {
    return items.iterator();
  }

  public Object getRandomItem() {
    if (items.size() == 0) return null;
    int index = Random.uniform.nextIntFromTo(0, items.size() - 1);
    return items.get(index);
  }

  public void step() {
    if (stepMethod == null) {
      try {
        stepMethod = ByteCodeBuilder.generateBasicActionForList(items, stepMethodName,
                                                                objClass, false);
      } catch (GeneratorException e) {
        SimUtilities.showError("Error executing step method '" + stepMethodName + "'", e);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    stepMethod.execute();
  }

  public void call(String methodName) {
    BasicAction action = (BasicAction) methods.get(methodName);
    if (action == null) {
      try {
        action = ByteCodeBuilder.generateBasicActionForList(items, methodName, objClass,
                                                            false);
        methods.put(methodName, action);
      } catch (GeneratorException ex) {
        SimUtilities.showError("Error calling '" + methodName + "' method", ex);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    action.execute();
  }

  public double min(String methodName) {
    StatCalculator calc = (StatCalculator) minCalculators.get(methodName);
    if (calc == null) {
      try {
        calc = ByteCodeBuilder.generateMinCalculator(objClass, items, methodName);
        minCalculators.put(methodName, calc);
      } catch (GeneratorException e) {
        SimUtilities.showError("Error executing min('" + methodName + "') method", e);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    return calc.calc();
  }

  public double max(String methodName) {
    StatCalculator calc = (StatCalculator) maxCalculators.get(methodName);
    if (calc == null) {
      try {
        calc = ByteCodeBuilder.generateMaxCalculator(objClass, items, methodName);
        maxCalculators.put(methodName, calc);
      } catch (GeneratorException e) {
        SimUtilities.showError("Error executing max('" + methodName + "') method", e);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    return calc.calc();
  }

  public double avg(String methodName) {
    StatCalculator calc = (StatCalculator) avgCalculators.get(methodName);
    if (calc == null) {
      try {
        calc = ByteCodeBuilder.generateAvgCalculator(objClass, items, methodName);
        avgCalculators.put(methodName, calc);
      } catch (GeneratorException e) {
        SimUtilities.showError("Error executing avg('" + methodName + "') method", e);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    return calc.calc();
  }

  public List getItemWithMinValue(String methodName) {
    ObjectPicker objPicker = (ObjectPicker) minPickers.get(methodName);
    if (objPicker == null) {
      try {
        objPicker = ByteCodeBuilder.generateMinObjectPicker(objClass, items, methodName);
        minPickers.put(methodName, objPicker);
      } catch (GeneratorException e) {
        SimUtilities.showError("Error executing getItemWithMinValue('" + methodName + "') method", e);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    return objPicker.pickObjects();

  }


  public List getItemWithMaxValue(String methodName) {
    ObjectPicker objPicker = (ObjectPicker) maxPickers.get(methodName);
    if (objPicker == null) {
      try {
        objPicker = ByteCodeBuilder.generateMaxObjectPicker(objClass, items, methodName);
        maxPickers.put(methodName, objPicker);
      } catch (GeneratorException e) {
        SimUtilities.showError("Error executing getItemWithMaxValue('" + methodName + "') method", e);
        // todo should check if we should exit on exit here
        System.exit(0);
      }
    }

    return objPicker.pickObjects();
  }

  public boolean contains(Object o) {
    return items.contains(o);
  }

  public Object[] toArray() {
    return items.toArray();
  }

  public Object[] toArray(Object a[]) {
    return items.toArray(a);
  }

  public boolean containsAll(Collection c) {
    return items.containsAll(c);
  }

  public boolean addAll(Collection c) {
    return items.addAll(c);
  }

  public boolean removeAll(Collection c) {
    return items.removeAll(c);
  }

  public boolean retainAll(Collection c) {
    return items.retainAll(c);
  }

  public void add(int index, Object element) {
    items.add(index, element);
  }

  public boolean addAll(int index, Collection c) {
    return items.addAll(index, c);
  }

  public Object get(int index) {
    return items.get(index);
  }

  public int indexOf(Object o) {
    return items.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return items.lastIndexOf(o);
  }

  public ListIterator listIterator() {
    return items.listIterator();
  }

  public ListIterator listIterator(int index) {
    return items.listIterator(index);
  }

  public Object remove(int index) {
    return items.remove(index);
  }

  public Object set(int index, Object element) {
    return items.set(index, element);
  }

  public List subList(int fromIndex, int toIndex) {
    return subList(fromIndex, toIndex);
  }
}
