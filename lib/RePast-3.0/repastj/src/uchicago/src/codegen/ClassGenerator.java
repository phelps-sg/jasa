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

import java.util.ArrayList;

public class ClassGenerator implements CodeGenerator {

  private String name, ext, imp, scope;
  private ArrayList ivars = new ArrayList();
  private ArrayList methods = new ArrayList();
  private ArrayList innerClasses = new ArrayList();

  public ClassGenerator(String scope, String name, String ext, String imp) {
    this.name = name;
    this.scope = scope;

    if (ext != null) {
      if (ext.length() == 0) {
        this.ext = "";
      } else {
        this.ext = " extends " + ext;
      }
    } else {
      this.ext = "";
    }

    if (imp != null) {
      if (imp.length() == 0) {
        this.imp = "";
      } else {
        this.imp = " implements " + imp;
      }
    } else {
      this.imp = "";
    }
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setExtends(String ext) {
    this.ext = ext;
  }

  public void setImplements(String imp) {
    this.imp = imp;
  }

  public void addIvar(IvarGenerator iv) {
    ivars.add(iv);
  }

  public void addMethod(MethodGenerator method) {
    methods.add(method);
  }

  public void add(String name, Object object) {
    if (name.equals("IVAR")) {
      addIvar((IvarGenerator)object);
    } else if (name.equals("METHOD")) {
      addMethod((MethodGenerator)object);
    } else if(name.equals("CLASS_DEF")) {
      innerClasses.add((ClassGenerator)object);
    }
  }

  public String generate(int ident) {
    StringBuffer b = GeneratorUtilities.getBufferWithIndent(ident);
    b.append(scope);
    if (scope.length() == 0) b.append("class ");
    else b.append(" class ");
    b.append(name);
    b.append(ext);
    b.append(imp);
    b.append(" {\n\n");

    for (int i = 0; i < ivars.size(); i++) {
      CodeGenerator cd = (CodeGenerator)ivars.get(i);
      b.append(cd.generate(ident + 1));
    }
    b.append("\n");

    for (int i = 0; i < innerClasses.size(); i++) {
      ClassGenerator cg = (ClassGenerator)innerClasses.get(i);
      b.append(cg.generate(ident + 1));
    }

    if (innerClasses.size() > 0) {
      b.append("\n");
    }

    for (int i = 0; i < methods.size(); i++) {
      CodeGenerator cd = (CodeGenerator)methods.get(i);
      b.append(cd.generate(ident + 1));
      b.append("\n");
    }

    b.append("}\n");

    return b.toString();
  }

  /*
  public static void main(String[] args) {
    String body = "myVal = val;\nif (x == 3) {\n\ty++;\n} else {\ny--;\n}";
    ClassGenerator c = new ClassGenerator("public", "Sample", null, null);
    MethodGenerator m = new MethodGenerator("public", "setVal", "void", null);
    m.addArg("val", "int");
    m.setBody(body);
    c.addMethod(m);
    c.addIvar("private", "myVal", "int", "3");

    System.out.println(c.generate(0));
  }
  */

}
