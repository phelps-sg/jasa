package uchicago.src.sim.parameter.rpl;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Exception thrown by the RPLCompiler.
 *
 * @version $Revision$ $Date$
 */

public class CompilerException extends RuntimeException {

  private Exception ex;
  private String message, fileName, code;
  private int line;

  public CompilerException(String message, Exception ex) {
    this.ex = ex;
    this.message = message;
  }

  public CompilerException(String message) {
    this.message = message;
  }

  public void resetMessage(String message) {
    this.message = message;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setFileName(String file) {
    fileName = file;
  }

  private String formatMessage() {
    StringBuffer b = new StringBuffer("File: ");
    b.append(fileName);
    b.append(", line: ");
    b.append(line);
    b.append("\nCompiler Error: ");
    b.append(message);
    b.append("\n  ");
    b.append(code);
    return b.toString();
  }

  /**
   * Because of the way Throwable works we need an explicit method for
   * getting the actual stack trace.
   */
  public void printSuperStackTrace() {
    super.printStackTrace(System.err);
  }

  public void printStackTrace() {
    System.out.println(formatMessage());
    if (ex != null) ex.printStackTrace();
  }

  public void printStackTrace(PrintStream s) {
    s.println(formatMessage());
    if (ex != null) ex.printStackTrace(s);
  }

  public void printStackTrace(PrintWriter w) {
    w.println(formatMessage());
    if (ex != null) ex.printStackTrace(w);
  }

  public String getMessage() {
    if (ex != null) return formatMessage() + "\n" + ex.getMessage();
    return formatMessage();
  }

}
