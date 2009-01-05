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
package uchicago.src.sim.analysis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.SimUtilities;
//import cern.colt.map.OpenIntIntHashMap;

/**
 * The model for a SequenceGraph. This controls all the sequences used by
 * a sequenceGraph.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class OpenSeqStatistic extends PlotModel {

  private ArrayList sequences = new ArrayList();
  java.util.Date date = new java.util.Date();
  private int lastIndex = -1;

  class SequenceWrapper {
    int series;
    Sequence sequence;
    boolean suspended = false;
    int startingX = -1;

    public SequenceWrapper(int series, Sequence s) {
      this.series = series;
      sequence = s;
    }

    public boolean equals(Object o) {
      return sequence.equals(o);
    }
  }



  /**
   * Construct this SequenceStatistic with the specified model
   */
  public OpenSeqStatistic(SimModel model) {
    super(model);
  }

  /**
   * Constructs a SequenceStatistic with the specified title,
   * file name, and file format, model.
   *
   * @param fileName the name of the file to write the sequence data to
   * @param fileFormat the format of the file - i.e. Statistics.CSV
   * @param title the title
   * @param model the model
   */
  public OpenSeqStatistic(String fileName, int fileFormat, String title,
      SimModel model)
  {
    super(fileName, fileFormat, title, model);
  }

  /**
   * Creates a sequence with the specified name, using data from the specified
   * object attained through the specified method name.
   *
   * @param name of the sequence
   * @param feedFrom the object from which to collect the sequence data
   * @param methodName the name of the method to call on the object to get the
   * data. This method must return a number (int etc.) or a Number (Integer etc.)
   */
  public Sequence createSequence(String name, Object feedFrom,
				 String methodName)
  {
    Sequence s = new ObjectSequence(feedFrom, methodName);
    addSequence(name);
    //sequences.addElement(s);
    sequences.add(new SequenceWrapper(sequences.size(), s));
    return s;
  }

  /**
   * Adds the specified sequence with the specified name.
   *
   * @param name the name of the sequence
   * @param sequence the sequence to add
   */
  public Sequence addSequence(String name, Sequence sequence) {
    addSequence(name);
    sequences.add(new SequenceWrapper(sequences.size(),
					     sequence));
    return sequence;
  }

  /**
   * Creates an Average sequence with the specified name, using data
   * from the specified list attained through the specified method
   * name. Each object in the list has the named method called on it,
   * the resulting data is averaged, and this average constitutes the
   * data for the sequence.
   *
   * @param name of the sequence
   * @param feedFrom the list from which to collect the sequence data
   * @param methodName the name of the method to call on the object to
   * get the data. This method must return a number (int etc.) or a
   * Number (Integer etc.)
   */
  public Sequence createAverageSequence(String name, ArrayList feedFrom,
					String methodName)
  {
    Sequence s = new AverageSequence(feedFrom, methodName);
    addSequence(name);
    sequences.add(new SequenceWrapper(sequences.size(), s));
    return s;
  }


  public double getYVal(int sequenceIndex, int xIndex) {
    SequenceWrapper sw = (SequenceWrapper)sequences.get(sequenceIndex);
    if (xIndex - sw.startingX < 0) return Double.NaN;
    return super.getYVal(sequenceIndex, xIndex - sw.startingX);
  }

  /**
   * Calculates the next item in the sequence
   */
  public void record() {
    if (sequences.size() != 0) {
      //double tick = model.getTickCountDouble();
      double tick = model.getTickCount();
      addX(tick);
      double yVal = 0;
      for (int i = 0; i < sequences.size(); i++) {
	SequenceWrapper sw = (SequenceWrapper)sequences.get(i);
	if (sw.startingX == -1 && getXValCount() == 1) sw.startingX = 0;
	else if (sw.startingX == -1) sw.startingX = getXValCount() - 1;
	yVal = sw.sequence.getSValue();
	addY(yVal, i);
      }
    }
  }

  /**
   * Writes the sequence out to a file
   */
  public void writeToFile() {
    BufferedWriter out = null;
    try {

      if (lastIndex == -1) {
        renameFile();
        out = new BufferedWriter(new java.io.FileWriter(fileName, true));
        String dateTime = DateFormat.getDateTimeInstance().format(date);
        out.write(dateTime + "\n");
        out.write(model.getPropertiesValues());
        out.write("\n");
        String header = "\"tick\"";
        ArrayList v = getSequenceNames();

        for (int i = 0; i < v.size(); i++) {
          header += ", \"" + (String)v.get(i) + "\"";
        }
        out.write(header);
        out.write("\n");
	lastIndex = 0;
      }

      if (out == null)
        out = new BufferedWriter(new java.io.FileWriter(fileName, true));

      int xSize = getXValCount();
      int ySize = getSequenceCount();
      for (int i = lastIndex; i < xSize; i++) {
	StringBuffer b = new StringBuffer(String.valueOf(getXVal(i)));
	for (int j = 0; j < ySize; j++) {
	  b.append(", ");
	  double yVal = this.getYVal(j, i);
	  if (Double.isNaN(yVal)) continue;
	  b.append(this.getYVal(j, i));
	}

	out.write(b.toString());
	out.write("\n");
      }

      out.flush();
      lastIndex = xSize;
    } catch (IOException ex) {
      SimUtilities.showError("Unable to write sequence to file", ex);
      ex.printStackTrace();
      try {
        out.close();
      } catch (Exception ex1) {}
      System.exit(0);
    }
  }
}
