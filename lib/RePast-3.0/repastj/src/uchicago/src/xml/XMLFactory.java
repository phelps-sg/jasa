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
package uchicago.src.xml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

//import org.w3c.dom.*;

public class XMLFactory {

  String xmlName;
  Object target;
  ArrayList propNames;

  public XMLFactory(String name, Object target, ArrayList propNames) {
    xmlName = name;
    this.target = target;
    this.propNames = propNames;
  }
  
  public String getXMLStart() {
    StringBuffer b = new StringBuffer("<");
    b.append(xmlName);
    b.append(" ");
    return b.toString();
  }

  public String getXMLEnd() {
    StringBuffer b = new StringBuffer("</");
    b.append(xmlName);
    b.append(">");
    return b.toString();
  }

  public String getXMLAttributes() throws NoSuchMethodException,
    IllegalAccessException, InvocationTargetException
  {
    Class clazz = target.getClass();
    StringBuffer b = new StringBuffer(" ");
    for (int i = 0; i < propNames.size(); i++) {
      String propName = (String)propNames.get(i);
      b.append(propName);
      b.append("=\"");
      Method method = clazz.getMethod("get" + propName, new Class[]{});
      Object o = method.invoke(target, new Object[]{});
      b.append(o.toString());
      b.append("\" ");
    }

    return b.toString();
  }

  public String getXMLStartAttrib() throws NoSuchMethodException,
    IllegalAccessException, InvocationTargetException
  {
    StringBuffer b = new StringBuffer(getXMLStart());
    b.append(getXMLAttributes());
    return b.toString();
  }

  public String getXML() throws NoSuchMethodException, IllegalAccessException,
    InvocationTargetException
  {
    StringBuffer b = new StringBuffer(getXMLStart());
    b.append(getXMLAttributes());
    b.append("/>");

    return b.toString();
  }
}
