package uk.ac.liv.util;


public class Debug {

  public Debug() {
  }

  public static void assert( String message, boolean condition ) {
    if ( ! condition ) {
      System.err.println("*** ASSERTION FAILED: " + message);
      throw new Error(message);
    }
  }

  public static void assert( boolean condition ) {
    assert("",condition);
  }

  public static void println( String message ) {
    System.out.println("DEBUG: " + message);
  }
}