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

public class CompUnitGenerator implements CodeGenerator {

    String name, pack;
  ArrayList imports = new ArrayList();
  ArrayList classes = new ArrayList();

  public CompUnitGenerator(String name, String pack) {
    this.name = name;
    this.pack = pack;
  }

  public void add(String name, Object object) {
    if (name.equals("CLASS")) {
      addImport((ImportGenerator)object);
    } else if (name.equals("CLASS_DEF")) {
      addClassGenerator((ClassGenerator)object);
    }
  }

  public void addImport(ImportGenerator imp) {
    imports.add(imp);
  }

  public void addClassGenerator(ClassGenerator cg) {
    classes.add(cg);
  }

  public String generate(int indent) {
    StringBuffer b = GeneratorUtilities.getBufferWithIndent(indent);
    b.append("package ");
    b.append(pack);
    b.append(";\n\n");
    for (int i = 0; i < imports.size(); i++) {
      b.append(((ImportGenerator)imports.get(i)).generate(indent));
    }

    b.append("\n");

    for (int i = 0; i < classes.size(); i++) {
      ClassGenerator cg = (ClassGenerator)classes.get(i);
      b.append(cg.generate(indent));
      b.append("\n");
    }

    return b.toString();
  }
}

