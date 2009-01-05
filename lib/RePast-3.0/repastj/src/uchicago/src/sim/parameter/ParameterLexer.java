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

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/*
 * TO DO: read comments so the parameter tool can display them.
 */

/**
 * A lexer for parameter files. Helps parameter reader turn parameter files into
 * <code>Parameter</code>s. For more on parameter files see
 * {@link uchicago.src.sim.parameter.ParameterReader ParameterReader}
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.engine.SimInit
 * @see uchicago.src.sim.parameter.ParameterReader
 */

public class ParameterLexer {

  public static final int INVALID_CHAR = -1;
  public static final int LEFT_BRACE = 0;
  public static final int RIGHT_BRACE = 1;
  public static final int START = 2;
  public static final int INCR = 3;
  public static final int END = 4;
  public static final int RUNS = 5;
  public static final int NUMBER = 6;
  public static final int WORD = 7;
  public static final int SET = 8;
  public static final int SET_LIST = 9;
  public static final int BOOL_SET = 10;
  public static final int BOOL_SET_LIST = 11;
  public static final int STRING_SET = 12;
  public static final int STRING_SET_LIST = 13;
  public static final int BOOL = 14;
  public static final int EOF = 15;
  public static final int LEFT_PAREN = 16;
  public static final int RIGHT_PAREN = 17;
  public static final int IO_INPUT = 18;
  public static final int IO_OUTPUT = 19;


  private StreamTokenizer input;
  //private int lastToken;

  /**
   * Create ParameterLexer using the specified Reader as input.
   */
  public ParameterLexer(Reader in) {
    input = new StreamTokenizer(in);
    input.resetSyntax();
    input.eolIsSignificant(false);
    input.slashSlashComments(true);
    input.slashStarComments(true);
    input.wordChars('a', 'z');
    input.wordChars('A', 'Z');
    input.wordChars('0', '9');
    input.wordChars('<', '>');
    input.wordChars('(', ')');
    input.wordChars('{', '}');
    input.wordChars(':', ':');
    input.wordChars('_', '_');
    input.wordChars('\\', '\\');
    //input.wordChars('/', '/');
    input.whitespaceChars(' ', ' ');
    input.whitespaceChars('\t', '\t');
    input.whitespaceChars('\n', '\n');
    input.whitespaceChars('\r', '\r');
    input.parseNumbers();
  }

  public String getString() {
    return input.sval;
  }

  public double getNumber() {
    return input.nval;
  }

  public int getLineNum() {
    return input.lineno();
  }

  public void pushBack() {
    input.pushBack();
  }

  /**
   * Get the next token in the input stream
   * @return the next token
   */
  public int nextToken() throws IOException {
    int token;

    switch (input.nextToken()) {
      case StreamTokenizer.TT_EOF:
        token = EOF;
        break;

      case StreamTokenizer.TT_WORD:
        if (input.sval.equalsIgnoreCase("start:"))
          token = START;
        else if (input.sval.equalsIgnoreCase("end:"))
          token = END;
        else if (input.sval.equalsIgnoreCase("incr:"))
          token = INCR;
        else if (input.sval.equalsIgnoreCase("{"))
          token = LEFT_BRACE;
        else if (input.sval.equalsIgnoreCase("}"))
          token = RIGHT_BRACE;
        else if (input.sval.equalsIgnoreCase("runs:"))
          token = RUNS;
        else if (input.sval.equalsIgnoreCase("set:"))
          token = SET;
        else if (input.sval.equalsIgnoreCase("set_list:"))
          token = SET_LIST;
        else if (input.sval.equalsIgnoreCase("set_string:"))
          token = STRING_SET;
        else if (input.sval.equalsIgnoreCase("set_string_list:"))
          token = STRING_SET_LIST;
        else if (input.sval.equalsIgnoreCase("set_boolean:"))
          token = BOOL_SET;
        else if (input.sval.equalsIgnoreCase("set_boolean_list:"))
          token = BOOL_SET_LIST;
        else if (input.sval.equalsIgnoreCase("("))
            token = LEFT_PAREN;
        else if (input.sval.equalsIgnoreCase(")"))
            token = RIGHT_PAREN;
        else if (input.sval.equalsIgnoreCase("input"))
            token = IO_INPUT;
        else if (input.sval.equalsIgnoreCase("output"))
            token = IO_OUTPUT;

        else if (input.sval.equalsIgnoreCase("true") ||
                input.sval.equalsIgnoreCase("false"))
          token = BOOL;
        else
          token = WORD;
        break;
      case StreamTokenizer.TT_NUMBER:
        token = NUMBER;
        break;
      default:
        token = INVALID_CHAR;
        break;
    }

    return token;
  }
}



