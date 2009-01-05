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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Factory utility methods for creating ParameterSetters given a specifed
 * file.
 *
 * @version $Revision$ $Date$
 */
public class ParameterSetterFactory {

  /**
   * Creates the appropriate ParameterSetter for the specified file. The first
   * line the file should be "# fully.qualified.name.of.ParameterSetter".
   * This will attempt to instantiate the named class and call its
   * <code>init</code> method with the fileName as an argument.
   *
   * @param fileName the name of the parameter file
   * @return a ParameterSetter suitable for reading the specified file.
   * @throws IOException if unable to read the file or create the
   * appropriate ParameterSetter.
   */
  public static ParameterSetter createParameterSetter(String fileName) throws
          IOException {

    // get the first line of the file
    ParameterSetter setter = null;
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    String line;

    while ((line = reader.readLine()) != null) {
      if (line.trim().length() != 0) {
        break;
      }
    }

    reader.close();
    if (line == null) {
      // let the default parameter setter handle throwing exceptions here
      setter = createDefaultParameterSetter();
      setter.init(fileName);
    } else {
      line = line.trim();
      if (line.startsWith("#")) {
        // parse the class we should create to read the file.
        String className = line.substring(1).trim();
        setter = createParameterSetterFromClassName(className);
        setter.init(fileName);

      } else {
       // use the default parameter setter
      setter = createDefaultParameterSetter();
      setter.init(fileName);

      }
    }

    return setter;
  }

  private static ParameterSetter createParameterSetterFromClassName(String className)
          throws IOException
  {
    Class c = null;
    ParameterSetter s = null;
    try {
      c = Class.forName(className);
      s = (ParameterSetter)c.newInstance();
    } catch (InstantiationException e) {
     throw new IOException("Unable to create ParameterSetter from class " + className);
    } catch (IllegalAccessException e) {
      throw new IOException("Unable to create ParameterSetter from class " + className);
    } catch (ClassNotFoundException e) {
      throw new IOException("Unable to create ParameterSetter from class " + className +
                            ". Class not found.");
    } catch (ClassCastException e) {
      throw new IOException("Unable to create ParameterSetter from class " + className +
                            ". " + className + " does implement ParameterSetter.");
    }

    return s;
  }

  public static ParameterSetter createSingleSetParameterSetter(int runCount) {
    return new SingleSetParameterSetter(runCount);
  }

  /**
   * Creates a DefaultParameterSetter.
   * @return a DefaultParameterSetter.
   */
  public static ParameterSetter createDefaultParameterSetter() {
    return new DefaultParameterSetter();
  }
}
