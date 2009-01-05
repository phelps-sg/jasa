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
package uchicago.src.sim.analysis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Factory for creating data sources for gui created charts.
 *
 * @version $Revision$ $Date$
 */
public class ChartSourceFactory {

  private static HashSet numberSet = new HashSet();

  static {
    numberSet.add(int.class);
    numberSet.add(float.class);
    numberSet.add(long.class);
    numberSet.add(double.class);
    numberSet.add(byte.class);
    numberSet.add(short.class);
  }

  private ChartSourceFactory() {}

  public static ArrayList createSequenceSources(Object obj) {
    Class clazz = obj.getClass();
    ArrayList list = new ArrayList();
    Method[] methods = clazz.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      Class retType = method.getReturnType();
      if (numberSet.contains(retType) || Number.class.isAssignableFrom(retType))
      {
        SequenceSource source = new SequenceSource(obj);
        source.setMethodName(method.getName());
        list.add(source);
      }
    }

    Field[] fields = clazz.getFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      Class retType = field.getType();
      if (numberSet.contains(retType) || Number.class.isAssignableFrom(retType))
      {
        SequenceSource source = new SequenceSource(obj);
        source.setFieldName(field.getName());
        list.add(source);
      }
    }

    return list;
  }
}
