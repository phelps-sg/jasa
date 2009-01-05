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
package uchicago.src.sim.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A RePast specific extension intended to wrap other extensions. Simplifies
 * exception handling.
 */
public class RepastException extends Exception {

  private Exception ex;
  private String message;

  public RepastException(String message, Exception ex) {
  	super(message, ex);
    this.ex = ex;
    this.message = message;
  }
  
  public RepastException(Exception ex, String message) {
  	super(message, ex);
    this.ex = ex;
    this.message = message;
  }

  public RepastException(String message) {
    this.message = message;
  }

  public void printStackTrace() {
    System.out.println(message);
    if (ex != null) ex.printStackTrace();
  }

  public void printStackTrace(PrintStream s) {
    s.println(message);
    if (ex != null) ex.printStackTrace(s);
  }

  public void printStackTrace(PrintWriter w) {
    w.println(message);
    if (ex != null) ex.printStackTrace(w);
  }

  public String getMessage() {
    if (ex != null) return message + "\n" + ex.getMessage();
    return message;
  }
}



