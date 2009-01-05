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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cern.jet.random.Uniform;

/**
 * Static Utility methods for RePast simulations.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class SimUtilities {

  private static double[] sinArray = new double[360];
  private static double[] cosArray = new double[360];
  public static final String newLine;
  
  static {
  	String lineSep = System.getProperty("line.separator");
  	if (lineSep.equals(""))
  		lineSep = "\n";
  	
  	newLine = lineSep;
  	
    //System.out.println("setting up trig arrays");
    for (int i = 0; i < 360; i++) {
      sinArray[i] = Math.sin(i * (Math.PI / 180));
      cosArray[i] = Math.cos(i * (Math.PI / 180));
    }
  }

  private SimUtilities() {}

  
  /**
   * Captializes the specified String.
   *
   * @param str the String to capitalize.
   * @return the capitalized String.
   */
  public static String capitalize(String str) {
    char[] chars = str.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }
  
  /**
   * Gets a file based on a certain name.  The way this works is it
   * checks the current directory for the file, then data/ for the file,
   * then demo/data/ for the file.  This is useful especially for demos that
   * will be in the demos/ directory and ran through the Repast GUI.
   * 
   * @param filename	the name of the file to get
   * 
   * @return a file object corresponding to the filename or null if the file
   * 			wasn't found
   */
  public static File getDataFile(String filename) {
  	File file = new File(filename);
  	if (file.exists())
  		return file;
  	
  	file = new File("./data/" + filename);
  	if (file.exists())
  		return file;
  	
  	file = new File("./demos/data/" + filename);
  	if (file.exists())
  		return file;
  	
  	file = new File("./repast/demo/data/" + filename);
  	if (file.exists())
  		return file;

  	file = new File("./demos/" + filename);
  	if (file.exists())
  		return file;
  	
  	return null;
  }
  
  /**
   * Gets a file based on a certain name.  The way this works is it
   * checks the current directory for the file, then data/ for the file,
   * then demo/data/ for the file.  This is useful especially for demos that
   * will be in the demos/ directory and ran through the Repast GUI.
   * 
   * @param filename	the name of the file to get
   * 
   * @return a String corresponding to the absolute path of the file or null 
   * 			if the file wasn't found
   */
  public static String getDataFileName(String filename) {
  	File file = getDataFile(filename);
  	
  	if (file == null)
  		return null;
  	
  	return file.getAbsolutePath();
  }
  
  /**
   * Shuffles the specified list using Random.uniform. This list
   * is shuffled by iterating backwards through the list and
   * swapping the current item with a randomly chosen item. This
   * randomly chosen item will occur before the current item in
   * the list.
   *
   * @param list the list to shuffle
   *
   * @see uchicago.src.sim.util.Random
   */
  public static void shuffle(List list) {
	if (Random.uniform == null) {
	  Random.createUniform();
	}

    for (int i = list.size(); i > 1; i--)
      swap(list, i-1, Random.uniform.nextIntFromTo(0, i - 1));
  }

  /**
   * Shuffles the specified list using the specifid Uniform
   * rng.  This list
   * is shuffled by iterating backwards through the list and
   * swapping the current item with a randomly chosen item. This
   * randomly chosen item will occur before the current item in
   * the list.
   *
   * @param list the List to shuffle
   * @param rng the random number generator to use for
   * the shuffle
   */
  public static void shuffle(List list, Uniform rng) {
    for (int i = list.size(); i > 1; i--)
      swap(list, i - 1, rng.nextIntFromTo(0, i - 1));
  }

   /**
   * Shuffles the specified double[] using the specifid Uniform
   * rng.  This list
   * is shuffled by iterating backwards through the list and
   * swapping the current item with a randomly chosen item. This
   * randomly chosen item will occur before the current item in
   * the list.
   *
   * @param array the double[] to shuffle
   * @param rng the random number generator to use for
   * the shuffle
   */
  public static void shuffle(double[] array, Uniform rng) {
    for (int i = array.length; i > 1; i--)
      swap(array, i - 1, rng.nextIntFromTo(0, i - 1));
  }

  /*
   * Swaps the two specified elements in the specified list.
   */
  private static void swap(List list, int i, int j) {
    Object tmp = list.get(i);
    list.set(i, list.get(j));
    list.set(j, tmp);
  }

  private static void swap(double[] array, int i, int j) {
    double tmp = array[i];
    array[i] = array[j];
    array[j] = tmp;
  }

  /**
   * Gets an x, y coordinate as a double[] of length 2, given a heading (0-359)
   * and a distance. The point of origin is 0, 0 and the returned coordinate
   * is relative to this distance. (An agent can calculate a new coordinate
   * by adding the returned coordinates to its own x, y values.) This assumes
   * that north = 0 degrees, east = 90 and so on.
   *
   * @param heading the heading in degrees
   * @param distance the distance to travel along the heading
   * @return a double[] with the calculated coordinate
   */
  public static double[] getPointFromHeadingAndDistance(int heading, int distance) {
    double y = sinArray[heading] * distance;
    double x = cosArray[heading] * distance;

    double[] retVal = new double[2];
    retVal[0] = x;
    retVal[1] = y;
    return retVal;
  }

  /**
   * Normalize the specified value to the specified size
   *
   * @param val the value to normalize
   * @param size the size to normalize the value to
   * @return the normalized value
   */
  public static int norm(int val, int size) {
    if (val < 0 || val > size - 1) {
      while (val < 0) val += size;
      return val % size;
    }

    return val;
  }

  /**
   * Displays a message in a dialog box.
   */
  public static void showMessage(String msg) {

    // internal error will be thrown if we try to create a frame
    // when running and no display is available.
    try {
      JFrame f = new JFrame();
      JOptionPane.showMessageDialog(f, msg, "Repast Message",
                            JOptionPane.INFORMATION_MESSAGE);
      f.dispose();
    } catch (InternalError ex) {
      System.out.println("Repast Message: " + msg);
    }
  }

  /**
   * Displays an error message on the screen and prints the message
   * and the exception message and stack trace to a ./repast_error.log
   */
  public static void showError(String msg, Exception ex) {
    SimUtilities.logException(msg, ex);

    // if running remotely in batch mode a frame for the dialog cannot
    // be displayed, and an InternalError will be thrown.
    try {
      JFrame f = new JFrame();
      String displayMsg = msg + "\nSee repast.log for details";
      JOptionPane.showMessageDialog(f, displayMsg, "Repast Error",
                              JOptionPane.ERROR_MESSAGE);
      f.dispose();
    } catch (InternalError e) {
	  // thrown when no display is available
      System.out.println("Repast Error: see ./repast.log for details");

    }
    System.out.println(msg);
    ex.printStackTrace();
  }

  /**
   * Logs the specified message and exception to
   * ./repast.log.
   *
   * @param msg the message to log
   * @param ex the exception to log
   */
  public static void logException(String msg, Exception ex) {

    BufferedWriter out;
    PrintWriter pOut = null;

    try {
      out = new BufferedWriter(new FileWriter("./repast.log"));
      pOut = new PrintWriter(out);
      pOut.println(msg);
      ex.printStackTrace(pOut);
      pOut.flush();
      pOut.close();
      if (pOut.checkError()) {
        pOut.flush();
        pOut.close();
        System.err.println("Error while writing error log");

      }
    } catch (Exception ex1) {
      try {
        if (pOut != null) {
          pOut.flush();
          pOut.close();
        }
        System.err.println("Error while writing error log");
        ex1.printStackTrace();
      } catch (Exception ex2) {}
    }
  }


}
