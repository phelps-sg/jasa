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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ViolinStrings.Strings;

public class GeneratorUtilities {

  public static StringBuffer getBufferWithIndent(int ident) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < ident; i++) {
      b.append(CodeGenerator.I_SPACES);
    }

    return b;
  }

  public static CodeGenerator createCodeGenerator(Node node) {
    String name = node.getNodeName();
    NamedNodeMap attribs = node.getAttributes();

    CodeGenerator cg = null;

    if (name.equals("COMP_UNIT")) {
      String cname = attribs.getNamedItem("NAME").getNodeValue();
      String pack = attribs.getNamedItem("PACKAGE").getNodeValue();
      cg = new CompUnitGenerator(cname, pack);

    } else if (name.equals("CLASS")) {
      String id = attribs.getNamedItem("ID").getNodeValue();
      String cname = attribs.getNamedItem("NAME").getNodeValue();
      cg = new ImportGenerator(id, cname);

    } else if (name.equals("CLASS_DEF")) {
      String cname = attribs.getNamedItem("NAME").getNodeValue();
      String scope = attribs.getNamedItem("SCOPE").getNodeValue();
      String ext = attribs.getNamedItem("EXTENDS").getNodeValue();
      String imp = attribs.getNamedItem("IMPLEMENTS").getNodeValue();
      imp = Strings.change(imp, " ", ", ");
      cg = new ClassGenerator(scope, cname, ext, imp);

    } else if (name.equals("IVAR")) {
      String cname = attribs.getNamedItem("NAME").getNodeValue();
      String scope = attribs.getNamedItem("SCOPE").getNodeValue();
      String val = "";
      if (attribs.getNamedItem("VAL") != null) {
        val = attribs.getNamedItem("VAL").getNodeValue();
      }

      String type = attribs.getNamedItem("TYPE").getNodeValue();

      cg = new IvarGenerator(scope, cname, type, val);

    } else if (name.equals("METHOD")) {
      String cname = attribs.getNamedItem("NAME").getNodeValue();
      String scope = attribs.getNamedItem("SCOPE").getNodeValue();
      String retVal = attribs.getNamedItem("RETVAL").getNodeValue();

      String exp = "";
      if (attribs.getNamedItem("EXP") != null) {
        exp = attribs.getNamedItem("EXP").getNodeValue();
      }

      cg = new MethodGenerator(scope, cname, retVal, exp);

    } else if (name.equals("ARG")) {
      //String cname = attribs.getNamedItem("NAME").getNodeValue();
      String type = attribs.getNamedItem("TYPE").getNodeValue();

      cg = new ArgGenerator(name, type);

    } else if (name.equals("BODY")) {
      cg = new BodyGenerator(node.getNodeValue());
    }

    return cg;
  }
}
