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
package uchicago.src.sim.parameter;

import java.util.Iterator;

/**
 * Defines some common code to be used by implementations of ParameterSetter.
 *
 * @version $Revision$ $Date$
 */

public abstract class AbstractParameterSetter implements ParameterSetter {

  /**
   * Captializes the specified String.
   *
   * @param str the String to capitalize.
   * @return the capitalized String.
   */
  protected String capitalize(String str) {
    char[] chars = str.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  protected static class ParamNameIterator implements Iterator {

    Iterator iter;

    public ParamNameIterator(Iterator iter) {
      this.iter = iter;
    }

    public boolean hasNext() {
      return iter.hasNext();
    }

    public Object next() {
      DefaultParameterSetter.ParamName pn = (DefaultParameterSetter.ParamName)iter.next();
      return pn.name;
    }

    public void remove() {
      iter.remove();
    }
  }

  /**
   * Represents a parameter name as both its "given name" and as fully lower
   * case. A particular ParamName is equals to another if the name is the
   * same regardeless of case.
   */
  protected static class ParamName {

    public String name;
    public String lcName;

    public ParamName(String name) {
      this.name = name;
      lcName = name.toLowerCase();
    }

    public boolean equals(Object o) {
      if (o instanceof DefaultParameterSetter.ParamName) {
        return ((DefaultParameterSetter.ParamName)o).lcName.equals(lcName);
      }

      return false;
    }

    public int hashCode() {
      return lcName.hashCode();
    }

    public String getCapName() {
      char[] chars = name.toCharArray();
      chars[0] = Character.toUpperCase(chars[0]);
      return new String(chars);
    }
  }
}
