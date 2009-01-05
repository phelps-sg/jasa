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
package uchicago.src.reflector;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import uchicago.src.collection.Pair;
import uchicago.src.sim.util.SimUtilities;

/**
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class Introspector {

  public static final int GET = 0;
  public static final int SET = 1;

  private TreeMap getSetMethods = new TreeMap();
  private Object spectee = null;
  private HashSet stringables = new HashSet();

  public Introspector() {
    stringables.add(Integer.class);
    stringables.add(Long.class);
    stringables.add(String.class);
    stringables.add(Float.class);
    stringables.add(Byte.class);
    stringables.add(Double.class);
    stringables.add(Boolean.class);
  }

  public void introspect(Object o, String[] props) throws IntrospectionException {
    spectee = o;
    Class cls = spectee.getClass();
    getSetMethods = MethodFactory.getInstance().getAccessorMethods(cls, props);
  }

  public Method getAccessorMethod(String propName, int type) {
    Pair p = (Pair)getSetMethods.get(propName);
    if (p == null)
      throw new IllegalArgumentException("Accessor Method for: " + propName + " not found");

    if (type == GET)
      return (Method)p.first;
    else if (type == SET)
      return (Method)p.second;
    else
      throw new IllegalArgumentException("Illegal type");
  }

  public void printProperties() {
    Iterator i = getSetMethods.keySet().iterator();
    while (i.hasNext()) {
      System.out.println(i.next());
    }
  }

  public Iterator getPropertyNames() {
    return getSetMethods.keySet().iterator();
  }

  public Object getPropertyValue(String propertyName)
          throws InvocationTargetException, IllegalAccessException
  {
    Pair p = (Pair)getSetMethods.get(propertyName);
    if (p == null)
      throw new IllegalArgumentException("Property: " + propertyName + " not found");

    Method m =  (Method)p.first;
    return m.invoke(spectee, new Object[]{});
  }

  /**
   * Is the specified property amenable to being turned into a String -
   * i.e. is it a String or a wrapper around a primitive.
   */
  public boolean isPropertyStringable(String propertyName)
          throws InvocationTargetException, IllegalAccessException
  {
    Object result = getPropertyValue(propertyName);
    if (result != null) return stringables.contains(result.getClass());
    return false;
  }
  
  /**
   * Returns the class of the named property.
   * @param propertyName
   * @return
   */ 
  public Class getPropertyClass(String propertyName) {
    return getAccessorMethod(propertyName, GET).getReturnType();
  }

  /**
   * Is the specified property boolean.
   */
  public boolean isPropertyBoolean(String propertyName)
          throws InvocationTargetException, IllegalAccessException
  {
    Method m = getAccessorMethod(propertyName, GET);
    return m.getReturnType().equals(Boolean.class) || m.getReturnType().equals(boolean.class);
  }

  public String getPropertyAsString(String propertyName)
          throws InvocationTargetException, IllegalAccessException
  {
    Object result = getPropertyValue(propertyName);
    return result.toString();
  }

  public boolean hasProperty(String propName) {
    return getSetMethods.containsKey(propName);
  }

  /**
   * Returns a String of "property: value" for all the properties.
   */
  public String getPropertiesValues() throws InvocationTargetException,
          IllegalAccessException
  {
    Iterator i = getSetMethods.keySet().iterator();
    String retVal = "";
    String lineSep = System.getProperty("line.separator");
    while (i.hasNext()) {
      String property = (String)i.next();
      String value = getPropertyAsString(property);
      retVal += property + ": " + value + lineSep;
    }
    return retVal;
  }

  /**
   * Get the property value pairs as a Hashtable. The key is the property as
   * a String and the value is the value.
   */
  public Hashtable getPropValues() throws InvocationTargetException,
          IllegalAccessException
  {
    Hashtable h = new Hashtable();
    Iterator i = getSetMethods.keySet().iterator();
    while (i.hasNext()) {
      String property = (String)i.next();
      h.put(property, getPropertyValue(property));
    }
    return h;
  }

  public void invokeSetMethod(String propertyName, Object param)
          throws InvocationTargetException, IllegalAccessException
  {
    //System.out.println("in invokeSetMethod");
    //System.out.println("Prop Name: " + propertyName + ", Param: " + param);
    //System.out.println(getPropertiesValues());

    // ensure that the first letter of the property is upper case
    StringBuffer key = new StringBuffer(propertyName);
    char c = Character.toUpperCase(key.charAt(0));
    key.setCharAt(0, c);

    Pair p = (Pair)getSetMethods.get(key.toString());
    Method m = (Method)p.second;
    Class[] paramTypes = m.getParameterTypes();
    Class paramType = paramTypes[0];
    Invoker invoker = InvokerFactory.createInvoker(paramType, spectee, m, param);
    try {
      invoker.execute();
    } catch (InvokerException ex) {
      SimUtilities.showError("Invalid Parameter for " + propertyName + "\n" +
              ex.getMessage(), ex);
    }
  }

  public boolean isReadOnly(String propertyName) {
    Pair p = (Pair)getSetMethods.get(propertyName);
    if (p == null)
      throw new IllegalArgumentException("Property: " + propertyName + " not found");
    if (p.second == null)
      return true;

    return false;
  }

  public boolean isWriteOnly(String propertyName) {
    Pair p = (Pair)getSetMethods.get(propertyName);
    if (p == null)
      throw new IllegalArgumentException("Property: " + propertyName + " not found");
    if (p.first == null)
      return true;

    return false;
  }

  public void reset(){
    getSetMethods = new TreeMap();
    spectee = null;
    stringables = new HashSet();
  }

}
