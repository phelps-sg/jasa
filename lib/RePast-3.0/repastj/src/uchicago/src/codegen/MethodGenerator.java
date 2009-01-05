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
//import java.io.*;

public class MethodGenerator implements CodeGenerator {

  String scope, name, retVal, excep;
  ArrayList args = new ArrayList();
  BodyGenerator body = new BodyGenerator("");



  public MethodGenerator(String scope, String name, String retVal, String excep) {
    this.scope = scope;
    this.name = name;
    if (retVal == null) {
      this.retVal = "void";
    } else {
      this.retVal = retVal;
    }

    if (excep == null) {
      this.excep = "";
    } else if (excep.length() == 0) {
      this.excep = "";
    } else {
      this.excep = excep;
    }
  }

  public void setBody(String body) {
    this.body = new BodyGenerator(body);
  }

    public void setBody(BodyGenerator b) {
    this.body = b;
    }

  public void addArg(String name, String type) {
    args.add(new ArgGenerator(name, type));
  }

  public void addArg(ArgGenerator arg) {
    args.add(arg);
  }

  public void addToBody(String body) {
    this.body.add(body);
  }

  public void add(String name, Object o) {
    if (name.equals("BODY")) {
      setBody((BodyGenerator)o);
    } else if (name.equals("ARG")) {
      addArg((ArgGenerator)o);
    }
  }

  public String generate(int ident) {
    StringBuffer b = GeneratorUtilities.getBufferWithIndent(ident);
    b.append(scope);
    b.append(" ");
    b.append(retVal);
    b.append(" ");
    b.append(name);
    b.append("(");

    for (int i = 0; i < args.size(); i++) {
      ArgGenerator a = (ArgGenerator)args.get(i);
      if (i != 0) {
        b.append(", ");
      }

      b.append(a.generate(ident));
    }

    b.append(") ");
    b.append(excep);
    b.append(" {\n");
    b.append(body.generate(ident));
    for (int i = 0; i < ident; i++) {
      b.append(I_SPACES);
    }

    b.append("}\n");

    return b.toString();
  }
}




