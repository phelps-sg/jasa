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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import uchicago.src.sim.engine.IController;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.SimUtilities;
import ViolinStrings.Strings;

/**
 * Holds data in tabular format - a vector of vectors. Also provides methods
 * for printing the data to a file.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see DataRecorder
 */

public class SimData {

  // A vector of vectors
  private ArrayList data = new ArrayList();
  //private int lastVectorWritten = -1;
  private String header = "\"tick\"";
  private String modelHeader;
  private String fileName;
  private boolean isBatch = false;
  private boolean writeHeader = true;
  //private boolean nothingWritten = true;
  private IController control;

  private String delimiter = ",";

  /**
   * Constructs this object with the specified fileName, model, and mode.
   *
   * @param fileName the fileName to write to
   * @param model the model from which the data is taken. Used to create
   * the file header.
   * @param isBatch whether the simulation is run in batch mode or not
   * @deprecated
   */

  public SimData(String fileName, String modelHeader, SimModel model,
                 boolean isBatch) {

    this.isBatch = isBatch;
    this.modelHeader = modelHeader;

    try {
      this.fileName = new File(fileName).getCanonicalPath();
    } catch (IOException ex) {
      SimUtilities.showError("Fatal file error", ex);
      System.exit(0);
    }

    if (isBatch) {
      header = "\"run\"" + delimiter + header;
      control = model.getController();
      Boolean b = (Boolean)control.getPersistentObj(this.fileName + DataFileHeader.WRITE_HEADER);
      if (b == null) {
        writeHeader = true;
        control.putPersistentObj(this.fileName + DataFileHeader.WRITE_HEADER, Boolean.TRUE);
      } else {
        writeHeader = b.booleanValue();
      }

    }
  }

  /**
   * Constructs a new SimData object from the model header, model and batch flag
   * @param modelHeader
   * @param model the model from which the data is taken. Used to create
   * the file header.
   * @param isBatch whether the simulation is run in batch mode or not
   */
  public SimData(String modelHeader, SimModel model, boolean isBatch){
    this.isBatch = isBatch;
    this.modelHeader = modelHeader;
  }

  public String getHeader() {
    writeHeader = false;
    return header;
  }

  public boolean writeHeader() {
    return writeHeader;
  }

  public String getModelHeader() {
    return modelHeader;
  }

  /**
   * Sets the column delimiter. Data is written out in tabular format
   * where the columns are separated by the specified delimiter.
   *
   * @param delim the new delimiter
   */
  public void setDelimiter(String delim) {
    header = Strings.change(header, delimiter, delim);
    delimiter = delim;
  }

  /**
   * Adds the specified String to the header associated with this data
   */
  public void addToHeader(String s) {
    if (header.length() == 0) {
      header += "\"" + s + "\"";
    } else {
      header += delimiter + "\"" + s + "\"";
    }
  }

  /**
   * Adds the specified list to the header associated with this data
   */
  public void addToHeader(List l) {
    ListIterator li = l.listIterator();
    while (li.hasNext()) {
      String s = (String) li.next();
      if (header.length() == 0) {
        header += s;
      } else {
        header += delimiter + s;
      }
    }
  }

  /**
   * Add a vector of data to this SimData.
   * @param v the data to add.
   */
  public void addData(List v) {
    data.add(v);
  }

  private void renameFile() throws IOException {
    File oldFile = new File(fileName);
    fileName = oldFile.getCanonicalPath();

    if (oldFile.exists()) {
      int x = 1;
      File newFile;
      String newName = fileName;
      String lastPart = "";

      if (fileName.indexOf(".") != -1) {
        int index = fileName.lastIndexOf(".");
        newName = fileName.substring(0, index);
        lastPart = fileName.substring(index, fileName.length());
      }

      if (isBatch) {
        newName += ".bak";
      }

      do {
        newFile = new File(newName + x + lastPart);
        x++;
      } while (newFile.exists());
      oldFile.renameTo(newFile);
      oldFile.delete();
    }
  }

  /**
   * Writes ending data (current time) to the file
   * <B>Please use getData and the DataRecorder methods instead</B>
   * @deprecated
   */
  public void writeEnd() {
    BufferedWriter out = null;
    try {
      // has not written anything out yet
      if (writeHeader) {
        writeToFile();

        // renameFile();
        // nothingWritten stays true in order that header etc. will
        // get written if caller does write some data
      }

      out = new BufferedWriter(new FileWriter(fileName, true));
      Date date = new Date();
      String dateTime = DateFormat.getDateTimeInstance().format(date);
      out.newLine();
      out.write("End Time: " + dateTime);
      out.flush();
      out.close();
    } catch (IOException ex) {
      SimUtilities.showError("Unable to write footer to file", ex);
      ex.printStackTrace();
      try {
        out.flush();
        out.close();
      } catch (Exception ex1) {
      }
      System.exit(0);
    }
  }

  /**
   * Writes the data (and a file header) out to a file. This flushes all
   * data from the data vector out to a file.
   * <B>Please use getData() instead</B>
   * @deprecated
   */
  public void writeToFile() {
    BufferedWriter out = null;
    try {
      // has not written anything out yet
      //if (nothingWritten) {
      if (writeHeader) {
        renameFile();
        out = new BufferedWriter(new FileWriter(fileName, true));
        out.write(modelHeader);
        out.newLine();
        out.newLine();
        out.write(header);
        out.newLine();
        if (isBatch) {
          control.putPersistentObj(fileName + DataFileHeader.WRITE_HEADER,
                                   Boolean.FALSE);
        }
        writeHeader = false;
      }

      // nothingWritten = false;
      //}

      if (out == null)
        out = new BufferedWriter(new FileWriter(fileName, true));

      // write all the data and clear the vector
      for (int i = 0; i < data.size(); i++) {
        List v = (List) data.get(i);
        for (int j = 0; j < v.size(); j++) {
          if (j == 0)
            out.write(v.get(j).toString());
          else
            out.write(delimiter + v.get(j).toString());
        }
        out.newLine();
      }
      out.flush();
      out.close();
      data.clear();
    } catch (IOException ex) {
      SimUtilities.showError("Unable to write data to file", ex);
      ex.printStackTrace();
      try {
        out.flush();
        out.close();
      } catch (Exception ex1) {
      }
      System.exit(0);
    }
  }

  /**
   * Get the data stored in this SimData object and clear this object.  This is the
   * preferred way of recording data.  Get the data as a String and return it to the
   * recorder object.
   * @return The data currently in the SimData object.
   */
  public String getData(){
    StringBuffer out = new StringBuffer();
    // write all the data and clear the vector

    for (int i = 0; i < data.size(); i++) {
      List v = (List) data.get(i);
      for (int j = 0; j < v.size(); j++) {
        if (j == 0)
          out.append(v.get(j).toString());
        else
          out.append(delimiter + v.get(j).toString());
      }
      out.append("\n");
    }
    data.clear();
    return out.toString();
  }

  public Iterator iterator(){
    return new SimDataIterator();
  }

  public class SimDataIterator implements Iterator{
    private Iterator i;

    SimDataIterator(){
      i = data.iterator();
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
      return i.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next() {
      List v = (List) i.next();
      StringBuffer out = new StringBuffer();
      for (int j = 0; j < v.size(); j++) {
        if (j == 0)
          out.append(v.get(j).toString());
        else
          out.append(delimiter + v.get(j).toString());
      }
      out.append("\n");
      return out.toString();
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.

     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove() {
      i.remove();
    }
  }
}


