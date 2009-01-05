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

import java.lang.reflect.Method;

/**
 * Creates Invoker classes according to the Class type passed in.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class InvokerFactory {

  public static Invoker createInvoker(Class paramType, Object o, Method m,
                                      Object value)
          throws IllegalArgumentException {
    Invoker invoker = null;
    if (value instanceof String) {
      String strValue = (String) value;

      if (paramType == String.class) {
        invoker = new StringArgInvoker(o, m, strValue);
      } else if (paramType == Integer.class ||
              paramType.getName().equals("int")) {
        invoker = new IntegerArgInvoker(o, m, strValue);

      } else if (paramType == Float.class ||
              paramType.getName().equals("float")) {
        invoker = new FloatArgInvoker(o, m, strValue);

      } else if (paramType == Double.class ||
              paramType.getName().equals("double")) {
        invoker = new DoubleArgInvoker(o, m, strValue);

      } else if (paramType == Long.class ||
              paramType.getName().equals("long")) {
        invoker = new LongArgInvoker(o, m, strValue);

      } else if (paramType == Boolean.class ||
              paramType.getName().equals("boolean")) {
        invoker = new BooleanArgInvoker(o, m, strValue);

      } else if (paramType == Byte.class ||
              paramType.getName().equals("byte")) {
        invoker = new ByteArgInvoker(o, m, strValue);

      } else if (paramType == Short.class ||
              paramType.getName().equals("short")) {
        invoker = new ShortArgInvoker(o, m, strValue);

      } else {
        throw new IllegalArgumentException("Invalid parameter type");
      }
    } else {
      invoker = new ObjectArgInvoker(o, m, value);
    }


    return invoker;
  }
}
