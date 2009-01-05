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
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Reads a model's parameters from a parameter file for a batch run. A typical
 * user should have no need to use this class directly.<p>
 *
 * A parameter file has the following format: <pre><code>
 * runs: x
 * Parameter (input|output) {
 *  value_definition
 * }
 * </code></pre>
 * where x is some number and Parameter is the name of some model parameter
 * accessible through get and set methods. Runs specifies the number of runs
 * to execute for the current parameter value. The value_definition is composed
 * of one or more keywords and corresponding values. Parameters can be either input or
 * output. Parameters are input by default and need not be explicitly specified. Output parameters
 * have to be declared using the keyword output enclosed in parenthesis. For output parameters
 * value_definition should be omitted.<p>
 *
 * The multi-keyword value definitions:
 *
 * <ul>
 * <li> start: the starting numerical value of a parameter.</li>
 * <li> end: the ending numerical value of a parameter.</li>
 * <li> incr: the amount to increment the current value of the parameter.</li>
 * </ul>
 * The start, end, and incr keywords together provide a value defintion and
 * must always occur together. For a batch simulation they define
 * a parameter space which will be automatically iterated through. start:
 * defines the initial parameter. end: the parameter up to and including
 * ending parameter, and incr: the amount to increment the start: value and
 * any succeeding values to reach the end: parameters. For a gui simulation
 * the start: value is taken as the default parameter and the remaining
 * keywords are ignored.<p>
 *
 * Single keyword value_definitions:
 * <ul>
 * <li> set: defines a single numerical value as a constant for the entire collection
 * of batch runs.</li>
 * <li> set_list: defines a space separated list of numerical values. A batch
 * simluation will iterate through the list.</li>
 * <li> set_boolean: defines a boolean value as a constant for the entire batch
 * simulation. Allowed values are "true" and "false" (without the quotes).</li>
 * <li> set_string: defines a string value as a constant for the entire batch
 * simluation. The string value must not contain any white space.</li>
 * <li> set_boolean_list: same as set_list but list of boolean values (true
 * or false).</li>
 * <li> set_string_list: same as set_list but list of string values.</li>
 * </ul>
 *
 * All these work using the get and set accessor method pattern. So regardless
 * of how the value is defined, the model must have the appropriate
 * get and set methods matching the parameter name.<p>
 *
 * Some examples,
 * <pre><code>
 * runs: 10
 * Food {
 *  start: 10
 *  end: 30
 *  incr: 10
 * }
 * </code></pre>
 *
 * This means start with a food value of 10 and run the simulation 10 times
 * using this value. Increment the food value by 10 and run the simulation 10
 * times with a food value of 20 (start 10 + incr 10). Increment the food value
 * by another 10, and run another 10 times with the food value of 30 (start 10 +
 * incr 10 + incr 10). <em>This example assumes that the model has getFood()
 * and setFood() methods.</em><p>
 *
 * More than one parameter can be specified, so for example,
 * <pre><code>
 * runs: 10
 * Food {
 *  start: 10
 *  end: 30
 *  incr: 10
 * }
 *
 * MaxAge {
 *  start: 10
 *  end: 30
 *  incr: 10
 * }
 * </code></pre>
 * Where both food and max age are incremented as described above. If using more
 * than one parameter it is important to synchronize them, as whenever a
 * parameter's current value is greater than its end value, the simulation will
 * exit.<p>
 *
 * Parameters can also be nested. For example,
 * <pre><code>
 * runs: 1
 * Food {
 *  start: 10
 *  end: 30
 *  incr: 10
 *  {
 *    runs: 10
 *    MaxAge {
 *      start: 0
 *      end: 40
 *      incr: 1
 *    }
 *  }
 * }
 * </code></pre>
 * This example means starting with a food value of 10 run the simulation 10
 * times with a MaxAge of 0. Increment MaxAge by 1 and run the simulation 10
 * times, and so on until the value of MaxAge is greater than 40. At this point,
 * increment Food by 10 and run the simulation 10 times with a MaxAge of 0.
 * Increment MaxAge by 1 and run the simulation 10 times. This continues until
 * the value of Food is greater than 30. Multiple levels of nesting are possible.
 * <p>
 *
 * <pre><code>
 * runs: 1
 * Food {
 *  start: 10
 *  end: 30
 *  incr: 10
 *  {
 *    runs: 10
 *    MaxAge {
 *      start: 0
 *      end: 40
 *      incr: 1
 *    }
 *  }
 * }
 * RngSeed {
 *  set: 1
 * }
 * </code></pre>
 * RngSeed is parameter of every model and can be manipulated like any other
 * parameter. And here it is set to one and this value will remain constant
 * over all the individual batch runs.<p>
 *
 *  <pre><code>
 * runs: 1
 * Food {
 *  start: 10
 *  end: 30
 *  incr: 10
 *  {
 *    runs: 10
 *    MaxAge {
 *      set_list: 1.2 3 10 12 84
 *    }
 *  }
 * }
 * RngSeed {
 *  set: 1
 * }
 * </code></pre>
 * This is the same as above except that maxAge will be incremented via the list.
 * So first run with maxAge as 1.2, do this for 10 runs. Then set maxAge to 3 and
 * run with this value for 10 times. Continue until the end of the list, then
 * increment Food and start and the beginning of the MaxAge list, and so on
 * until the Food parameter is greater than 30.<p>
 *
 * The boolean and string keywords work in the identical manner, but set boolean
 * and string values instead of numeric ones.<p>
 *
 * Parameter files can contain comments delimited by the standard c/c++/java
 * comment markers: '//' and so on.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 *
 */

public class ParameterReader {

  private Vector params = new Vector();
  private boolean start = true;
  private long numRuns = -1;

  private Hashtable errorMsgs = new Hashtable(8);
  private XMLParameterReader xmlReader;

  public ParameterReader() {
    fillErrorHash();
  }

  /**
   * Constructs a ParameterReader with specified parameter file.
   * If the reader cannot read the file for some reason, the simulation
   * will exit.
   *
   * @param fileName the name of the parameter file
   */
  public ParameterReader(String fileName) throws IOException {
    this();
    if (isXMLFormat(fileName)) {
      xmlReader = new XMLParameterReader(fileName);
      xmlReader.parse();
    } else {
      read(fileName);
    }
  }

  private boolean isXMLFormat(String fileName) throws IOException {
    BufferedReader in = null;
    boolean retVal = false;
    try {
      in = new BufferedReader(new FileReader(fileName));
      String line = "";
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() != 0) {
          break;
        }
      }

      if (line == null) {
        // read through file but nothing there
        throw new IOException("File is empty");
      } else {
        if (line.indexOf("<?xml") != -1 ||
            line.indexOf("<?XML") != -1)
        {
          retVal = true;
        }
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) {}
      }
    }

    return retVal;
  }

  public Vector read(String fileName) throws IOException {
    BufferedReader in = null;

    try {
      //StringBuffer b = new StringBuffer();
      in = new BufferedReader(new FileReader(fileName));
      // forward past the # comment identifying the class used to read
      // this file, if necessary.
      String line = "";
      in.mark(1000);
      while ((line = in.readLine()) != null) {
        line = line.trim();
        if (line.length() != 0 && !(line.startsWith("#"))) {
          break;
        } else if (line.startsWith("#")) {
          in.mark(1000);
        }
        in.mark(1000);
      }

      in.reset();

      parse(in);
      in.close();
    } catch (IOException ex) {
      try {
        if (in != null)
          in.close();
      } catch (Exception ex1) {}

      throw ex;
    }

    return params;
  }

  private void parse(Reader in) throws IOException {
    ParameterLexer lexer = new ParameterLexer(in);
    NumericParameter current = new NumericParameter();
    Vector expecting = new Vector(5);
    expecting.add(new Integer(ParameterLexer.RUNS));

    // brace count increments on { and decrements on }
    int braceCount = 0;
    int tokenID;

    while ((tokenID = lexer.nextToken()) != ParameterLexer.EOF) {
      if (!expecting.contains(new Integer(tokenID))) {
        String error = "Illegally formatted parameter file at line: " +
                        lexer.getLineNum() + "\n";
        for (int i = 0; i < expecting.size(); i++) {
          error += (String)errorMsgs.get(expecting.elementAt(i)) + "\n";
        }
        throw new IOException(error);
      }
      switch (tokenID) {
        case ParameterLexer.WORD:
          if (braceCount == 0) {
            current = new NumericParameter();
            current.setName(lexer.getString());

          } else if (braceCount % 2 == 0) {
            NumericParameter p = new NumericParameter();
            current.addChild(p);
            p.setParent(current);
            long longRuns = current.getSubRuns();
            current = p;
            current.setName(lexer.getString());
            current.setNumRuns(longRuns);
          } else {
            // attribute should only occur if brace count is even
            String error = "Illegally formatted parameter file at line: " +
                        lexer.getLineNum() + "\n";
            error += "Missing '}'\n";
            throw new IOException(error);
          }
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.LEFT_BRACE));
          expecting.add(new Integer(ParameterLexer.LEFT_PAREN));
          break;
        case ParameterLexer.LEFT_BRACE:
          braceCount++;
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.START));
          expecting.add(new Integer(ParameterLexer.RUNS));
          expecting.add(new Integer(ParameterLexer.SET));
          expecting.add(new Integer(ParameterLexer.SET_LIST));
          expecting.add(new Integer(ParameterLexer.STRING_SET_LIST));
          expecting.add(new Integer(ParameterLexer.BOOL_SET_LIST));
          expecting.add(new Integer(ParameterLexer.STRING_SET));
          expecting.add(new Integer(ParameterLexer.BOOL_SET));
          break;

        case ParameterLexer.RIGHT_BRACE:
          braceCount--;
          if (!current.isComplete()) {
            String error = "Illegally formatted parameter file at line: " +
                        lexer.getLineNum() + "\n";
            error += "Parameter definition is incomplete";
            throw new IOException(error);
          }

          if (current.getParent() != null) {
            current = (NumericParameter)current.getParent();
          } else {
            if (!params.contains(current))
              params.add(current);
          }
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.RUNS));
          expecting.add(new Integer(ParameterLexer.WORD));
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;
        case ParameterLexer.START:
          current.setStart(getNumber(lexer));
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.END));
          break;
        case ParameterLexer.END:
          current.setEnd(getNumber(lexer));
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.INCR));
          break;
        case ParameterLexer.INCR:
          current.setIncr(getNumber(lexer));
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.LEFT_BRACE));
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;
        case ParameterLexer.SET:
          double val = getNumber(lexer);
          current.setConstVal(new Double(val));
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;

        case ParameterLexer.SET_LIST:
          Vector valList = new Vector();
          // loop until token is not a number
          // then pushBack();
          while (lexer.nextToken() == ParameterLexer.NUMBER) {
            valList.add(new Double(lexer.getNumber()));
          }
          current.setList(valList);

          lexer.pushBack();
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.LEFT_BRACE));
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;

        case ParameterLexer.BOOL_SET:
          int btoken = lexer.nextToken();
          if (btoken != ParameterLexer.BOOL) {
            String message = "Expected boolean value on line " +
                            lexer.getLineNum();
            throw new IOException(message);
          }
          current.setConstVal(lexer.getString());
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;

        case ParameterLexer.BOOL_SET_LIST:
          Vector boolList = new Vector();
          while (lexer.nextToken() == ParameterLexer.BOOL) {
            boolList.add(new Boolean(lexer.getString()));
          }

          current.setList(boolList);
          lexer.pushBack();
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.LEFT_BRACE));
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;

        case ParameterLexer.STRING_SET:
          String s;

          // this allows numbers to be treated as strings
          if (lexer.nextToken() == ParameterLexer.WORD) {
            s = lexer.getString();
          } else {
            s = String.valueOf(lexer.getNumber());
          }

          current.setConstVal(s);
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;

        case ParameterLexer.STRING_SET_LIST:
          Vector stringList = new Vector();
          while (lexer.nextToken() == ParameterLexer.WORD) {
            stringList.add(lexer.getString());
          }

          current.setList(stringList);

          lexer.pushBack();
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.LEFT_BRACE));
          expecting.add(new Integer(ParameterLexer.RIGHT_BRACE));
          break;

        case ParameterLexer.RUNS:
          long num = (long)getNumber(lexer);
          if (start) {
            numRuns = num;
            start = false;
          } else {
            current.setSubRuns(num);
          }
          expecting.clear();
          expecting.add(new Integer(ParameterLexer.WORD));
          break;
        case ParameterLexer.LEFT_PAREN:
            if (lexer.nextToken()==ParameterLexer.IO_INPUT) {
                current.setInput(true);
            } else {
                current.setInput(false);
            }
            expecting.clear();
            expecting.add(new Integer(ParameterLexer.RIGHT_PAREN));
            break;
        case ParameterLexer.RIGHT_PAREN:
            expecting.clear();
            expecting.add(new Integer(ParameterLexer.LEFT_BRACE));
            expecting.add(new Integer(ParameterLexer.WORD));
            break;
        default:
          System.out.println(lexer.getString());
          System.out.println(lexer.getNumber());
          throw new IOException("Invalid token: " + tokenID);

      } // end switch
    } // end while

    for (int i = 0; i < params.size(); i++) {
      Parameter p = (Parameter)params.elementAt(i);
      p.setNumRuns(numRuns);
    }
  } // end parse


  /**
   * Gets the parameters read by this Parameter reader
   *
   * @return the read parameters
   */
  public Vector getParameters() {
    if (xmlReader == null) {
      return params;
    } else {
      return xmlReader.getParameters();
    }
  }

  private double getNumber(ParameterLexer lexer) throws IOException {
    int token = lexer.nextToken();
    if (token != ParameterLexer.NUMBER) {
      String error = "Illegally formatted parameter file at line: " +
                        lexer.getLineNum() + "\n";
      error += "Expected a number";
      throw new IOException(error);
    } else {
      return lexer.getNumber();
    }
  }

  private void fillErrorHash() {
    errorMsgs.put(new Integer(ParameterLexer.END), "Expected 'end:'");
    errorMsgs.put(new Integer(ParameterLexer.INCR), "Expected 'incr:'");
    errorMsgs.put(new Integer(ParameterLexer.LEFT_BRACE), "Expected '{'");
    errorMsgs.put(new Integer(ParameterLexer.NUMBER), "Expected number");
    errorMsgs.put(new Integer(ParameterLexer.RIGHT_BRACE), "Expected '}'");
    errorMsgs.put(new Integer(ParameterLexer.RUNS), "Expected 'runs:'");
    errorMsgs.put(new Integer(ParameterLexer.START), "Expected 'start:'");
    errorMsgs.put(new Integer(ParameterLexer.WORD), "Expected attribute");
  }

  /*
  public static void main(String[] args) {
    try {
      ParameterReader p = new ParameterReader("h:/paramTest.txt");
      Vector v = (Vector)p.getParameters();
      System.out.println("Number of main params: " + v.size());
      for (int i = 0; i < v.size(); i++) {
        Parameter pm = (Parameter)v.elementAt(i);
        pm.printToScreen();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(0);
    }
  }
  */
}



