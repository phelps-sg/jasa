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
package uchicago.src.codegen;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;

import com.go.trove.classfile.ClassFile;

public class RSClassLoader extends URLClassLoader {

  private HashMap classDefs = new HashMap();
  private HashMap classes = new HashMap();
  private HashSet paths = new HashSet();

  class ClassData {
    byte[] bytecode;
    String name;

    public ClassData(String name, byte[] bytecode) {
      this.name = name;
      this.bytecode = bytecode;
    }
  }

  public RSClassLoader(ClassLoader parent) {
    super(new URL[]{}, parent);
  }

  public void addClass(String name, ClassFile cf) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      cf.writeTo(out);
      out.close();
    } catch (java.io.IOException ex) {
      ex.printStackTrace();
    }

    classDefs.put(name, new ClassData(name, out.toByteArray()));
  }

  public void addPath(String path) throws MalformedURLException {
    path.replace(File.separatorChar, '/');
    path = "file://" + path;
    if (!paths.contains(path)) {
      URL url = new URL(path);
      addURL(url);
      paths.add(path);
   }
  }

  public void writeClass(String name, String dir) {
    ClassData cd = (ClassData) classDefs.get(name);

    // change the name to something appropriate for a file name by
    // removing any package info.
    int index = name.lastIndexOf(".");
    if (index != -1) name = name.substring(index + 1, name.length());

    if (cd == null) throw new RuntimeException("Class " + name + " not found");
    try {

      BufferedOutputStream out =
              new BufferedOutputStream(
                      new FileOutputStream(dir + java.io.File.separator + name + ".class"));
      out.write(cd.bytecode);
      out.flush();
      out.close();
    } catch (java.io.IOException ex) {
      ex.printStackTrace();
    }
  }

  protected Class findClass(String name) throws ClassNotFoundException {
    //System.out.println("looking for class " + name);
    Class c = (Class) classes.get(name);
    if (c == null) {
      ClassData cd = (ClassData) classDefs.get(name);
      if (cd == null) {
        c = super.findClass(name);
        return c;
      }
      c = this.defineClass(name, cd.bytecode, 0, cd.bytecode.length);
    }

    return c;
  }
}
