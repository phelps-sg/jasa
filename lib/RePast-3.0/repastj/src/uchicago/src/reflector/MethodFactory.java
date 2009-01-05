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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import uchicago.src.collection.Pair;

/**
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class MethodFactory {

  //private Hashtable cache = new Hashtable(35);
  private static MethodFactory mf = new MethodFactory();

  private MethodFactory() {

  }

  public static MethodFactory getInstance() {
    return mf;
  }

  public TreeMap getAccessorMethods(Class clazz, String[] props) throws
    IntrospectionException
  {
    TreeMap h = new TreeMap();

    BeanInfo bi = java.beans.Introspector.getBeanInfo(clazz);
    PropertyDescriptor[] pds = bi.getPropertyDescriptors();
    for (int i = 0; i < pds.length; i++) {
      PropertyDescriptor pd = pds[i];
      Pair p = null;
      if (props != null) {
        p = getPropertyPair(pd, props);
      } else {
        p = new Pair(pd.getReadMethod(), pd.getWriteMethod());
      }

      if (p != null) {
        // ensure that the first letter of the property is upper case
        StringBuffer key = new StringBuffer(pd.getName().trim());
        char c = Character.toUpperCase(key.charAt(0));
        key.setCharAt(0, c);
        h.put(key.toString(), p);
      }
    }

    return h;
  }

  private Pair getPropertyPair(PropertyDescriptor pd, String[] props) {
    String name = pd.getName().toLowerCase();

    Pair p = null;

    for (int i = 0; i < props.length; i++) {
      String prop = props[i].toLowerCase();
      if (prop.equals(name)) {
        Method get = pd.getReadMethod();
        Method set = pd.getWriteMethod();
        p = new Pair(get, set);
        break;
      }
    }
    return p;
  }

  public Hashtable findGetMethods(Class clazz, ArrayList list) throws
    IntrospectionException
  {
    String[] props = new String[list.size()];
    list.toArray(props);


    TreeMap map = getAccessorMethods(clazz, props);
    Hashtable h = new Hashtable();
    Iterator i = map.keySet().iterator();
    while (i.hasNext()) {
      String key = (String)i.next();
      Pair p = (Pair)map.get(key);
      if (p.first != null) {
        h.put(key, p.first);
      }
    }

    return h;
  }
}