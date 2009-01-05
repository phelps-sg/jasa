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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CompUnitParser {

  private Stack stack = new Stack();
  private Document doc;

  public CompUnitParser(String fileName) throws IOException {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileName), fileName);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  private void doParse(Node node) {

    CodeGenerator cg = GeneratorUtilities.createCodeGenerator(node);
    if (cg != null) {
      stack.push(cg);

      NodeList children = node.getChildNodes();
      if (children != null) {
        for (int i = 0; i < children.getLength(); i++) {
          Node child = children.item(i);
          String name = child.getNodeName();
          if (child.getNodeType() == Node.ELEMENT_NODE) {
            doParse(child);
            Object o = stack.pop();
            //System.out.println("adding " + o + " to " + cg);
            cg.add(name, o);
          } else if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
            // cg must be a BodyGenerator
            cg.add("BODY_TEXT", child.getNodeValue());
          }
        }
      }
    }
  }


  /**
   * Parses the document tree and creates the CompUnitGenerator.
   */
  public CompUnitGenerator parse() {
    doParse(doc.getDocumentElement());

    return (CompUnitGenerator)stack.pop();
  }


  public static void main(String[] args) {
    try {
      CompUnitParser p = new CompUnitParser(args[0]);
      CompUnitGenerator c = p.parse();
      System.out.println(c.generate(0));
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
  }
}
