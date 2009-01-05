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

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Reads and parses xml format parameter files.
 */

public class XMLParameterReader {

  private Document doc;
  private Vector params = new Vector();
  private Vector tempVec = new Vector();
  private Hashtable methodTable = new Hashtable();

  public XMLParameterReader(String fileName) throws IOException {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = dbf.newDocumentBuilder();

      doc = parser.parse(new FileInputStream(fileName), fileName);
      parser.parse(fileName);
    } catch (Exception ex) {
      throw new IOException(ex.getMessage());
    }
  }

  private Parameter parseElement(Element element, Parameter parent, long runs) {
    NamedNodeMap attribs = element.getAttributes();
    String name = attribs.getNamedItem("name").getNodeValue();

    NumericParameter current = new NumericParameter();
    current.setNumRuns(runs);
    current.setName(name);
    String parentName = "";
    if (parent != null) {
      current.setParent(parent);
      parent.addChild(current);
      parentName = parent.getName();
    }


    // parse the type and call the appropriate method in the method table
    String type = "";
    if (attribs.getNamedItem("type") != null)
    {
	    type = attribs.getNamedItem("type").getNodeValue().toLowerCase();
	    Method m = (Method)methodTable.get(type);
	    if (m != null) {
	      try {
	        m.invoke(this, new Object[]{current, attribs});
	      } catch (Exception ex) {
	        ex.printStackTrace();
	      }
	    }
    }

    //parse io type; remember it is optional for input
    if (attribs.getNamedItem("io") != null) {
        String iotype = attribs.getNamedItem("io").getNodeValue();
        if(iotype.equalsIgnoreCase("input")) {
        	current.setInput(true);
        } else {
        	current.setInput(false);
        }
    } else {
    	current.setInput(true);
    }
    
    System.out.println(name + " " + type + " runs - " + runs + " parent - " + parentName);
    tempVec.add(current);
    return current;
  }

  private long parseParamBlock(Element element) {
    NamedNodeMap attribs = element.getAttributes();
    Node runs = attribs.getNamedItem("runs");
    return Long.parseLong(runs.getNodeValue());
  }

  private void doParse(Node node, Parameter parent, long runs) {

    switch (node.getNodeType()) {
      case Node.DOCUMENT_NODE:
        Document doc = (Document)node;
        doParse(doc.getDocumentElement(), parent, 0);
        break;

      case Node.ELEMENT_NODE:
        String name = node.getNodeName();
        if (name.equalsIgnoreCase("Repast:ParamBlock")) {
          runs = parseParamBlock((Element)node);
        } else if (name.equalsIgnoreCase("Repast:Param")) {
          parent = parseElement((Element)node, parent, runs);
        }

        NodeList children = node.getChildNodes();
        if (children != null) {
          for (int i = 0; i < children.getLength(); i++) {
            doParse(children.item(i), parent, runs);
          }
        }

        break;
      default:
        break;
    }

    parent = null;
  }

  /**
   * Parses the Document tree creating repast parameter objects from the
   * elements.
   */
  public void parse() {
    createMethodLookupTable();
    doParse(doc, null, 0);

    // Only the "top-level" parameters, those with no parents, are put
    // in the parameter list.
    for (int i = 0; i < tempVec.size(); i++) {
      Parameter p = (Parameter)tempVec.get(i);
      if (p.getParent() == null) {
        params.add(p);
      }
    }
  }

  public Vector getParameters() {
    return params;
  }

  public NumericParameter handleIncr(NumericParameter me, NamedNodeMap attribs)
    throws IOException
  {
    Double start = getNumber(me, "start",
                            attribs.getNamedItem("start").getNodeValue());
    Double end = getNumber(me, "end", attribs.getNamedItem("end").getNodeValue());
    Double incr = getNumber(me, "incr", attribs.getNamedItem("incr").getNodeValue());

    me.setStart(start);
    me.setEnd(end);
    me.setIncr(incr);

    //System.out.println("handling incr");

    return me;

  }

  public NumericParameter handleList(NumericParameter me, NamedNodeMap attribs)
    throws IOException
  {
    Vector v = new Vector();
    String list = attribs.getNamedItem("value").getNodeValue();
    StringTokenizer tok = new StringTokenizer(list, " ");
    while (tok.hasMoreTokens()) {
      v.add(getNumber(me, "value", tok.nextToken()));
    }

    me.setList(v);
    return me;
  }

  public NumericParameter handleConst(NumericParameter me, NamedNodeMap attribs)
    throws IOException
  {
    String val = attribs.getNamedItem("value").getNodeValue();
    me.setConstVal(getNumber(me, "value", val));
    return me;
  }

  public NumericParameter handleBooleanList(NumericParameter me,
    NamedNodeMap attribs) throws IOException
  {
    Vector v = new Vector();
    String list = attribs.getNamedItem("value").getNodeValue();
    StringTokenizer tok = new StringTokenizer(list, " ");
    while (tok.hasMoreTokens()) {
      v.add(getBoolean(me, "value", tok.nextToken()));
    }

    me.setList(v);
    return me;
  }

  public NumericParameter handleBooleanConst(NumericParameter me, NamedNodeMap attribs)
    throws IOException
  {
    String val = attribs.getNamedItem("value").getNodeValue();
    me.setConstVal(getBoolean(me, "value", val));
    return me;
  }

  public NumericParameter handleStringList(NumericParameter me,
    NamedNodeMap attribs) throws IOException
  {
    Vector v = new Vector();
    String list = attribs.getNamedItem("value").getNodeValue();
    StringTokenizer tok = new StringTokenizer(list, " ");
    while (tok.hasMoreTokens()) {
      v.add(tok.nextToken());
    }

    me.setList(v);
    return me;
  }

  public NumericParameter handleStringConst(NumericParameter me, NamedNodeMap attribs)
    throws IOException
  {
    String val = attribs.getNamedItem("value").getNodeValue();
    me.setConstVal(val);
    return me;
  }


  /*
   * Gratuitious use of reflection to avoid typing a long if then statement.
   */
  private void createMethodLookupTable() {
    Class cls = uchicago.src.sim.parameter.XMLParameterReader.class;
    try {
      Method m = cls.getMethod("handleIncr", new Class[] {NumericParameter.class,
                                                          NamedNodeMap.class});
      methodTable.put("incr", m);

      m = cls.getMethod("handleList", new Class[] {NumericParameter.class,
                                                  NamedNodeMap.class});
      methodTable.put("list", m);

      m = cls.getMethod("handleConst", new Class[] {NumericParameter.class,
                                                    NamedNodeMap.class});
      methodTable.put("const", m);

      m = cls.getMethod("handleBooleanList", new Class[] {NumericParameter.class,
                                                          NamedNodeMap.class});
      methodTable.put("boolean_list", m);

      m = cls.getMethod("handleBooleanConst", new Class[] {NumericParameter.class,
                                                            NamedNodeMap.class});
      methodTable.put("boolean_const", m);

      m = cls.getMethod("handleStringList", new Class[] {NumericParameter.class,
                                                          NamedNodeMap.class});
      methodTable.put("string_list", m);

      m = cls.getMethod("handleStringConst", new Class[] {NumericParameter.class,
                                                          NamedNodeMap.class});
      methodTable.put("string_const", m);

    } catch (NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  private Double getNumber(NumericParameter p, String attrib, String val)
    throws IOException
  {
    Double d;
    try {
      d = new Double(val);
      return d;
    } catch (NumberFormatException ex) {
      String error = "Attribute '" + attrib + "' for parameter \'" + p.getName()
                      + "' must be a number";
      throw new IOException(error);
    }
  }

  private Boolean getBoolean(NumericParameter p, String attrib, String val)
    throws IOException
  {
    if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
      return new Boolean(val);
    } else {
      String error = "Attribute '" + attrib + "' for parameter \'" + p.getName()
                      + "' must be 'true' or 'false'";
      throw new IOException(error);
    }
  }

  public static void main(String[] args) {
    try {
      XMLParameterReader reader = new XMLParameterReader("/home/nick/src/uchicago/src/sim/engine/pFile.xml");
      reader.parse();
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
  }
}