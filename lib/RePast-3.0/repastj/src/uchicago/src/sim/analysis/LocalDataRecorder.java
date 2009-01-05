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
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import uchicago.src.sim.engine.IController;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.SimUtilities;

/**
 * The primary means of recording data during a simulation. The recorded
 * data is drawn from objects in the simulation via the DataSources added with
 * DataRecorder's addObjectDataSource method.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 */

public class LocalDataRecorder extends AbstractDataSourceRecorder {

  protected String fileName;
  protected boolean isBatch;
  protected Hashtable dynParams;
  protected boolean writeHeader = true;
  protected IController control;
  protected String writeKey;

  /**
   * Constructs a DataRecorder using the specified file name and model.
   * Defaults to non-batch mode.
   *
   * @param fileName the file to which the data is recorded
   * @param model the model from which the data is drawn. The relevant
   * model parameters (set/get parameters, rng seed) are written to the header
   * of the file.
   */
  public LocalDataRecorder(String fileName, SimModel model) {
    this(fileName, model, null);
  }

  /**
   * Constructs a DataRecorder using the specified file name, model,
   * and headerComment.
   *
   * @param fileName the file to which the data is recorded
   * @param model the model from which the data is drawn. The relevant
   * model parameters (set/get parameters, rng seed) are written to the header
   * of the file
   * @param headerComment a comment to prepend to the file header information
   */
  public LocalDataRecorder(String fileName, SimModel model, String headerComment) {
    initData(model, headerComment);
    isBatch = model.getController().isBatch();
    this.fileName = fileName;
    if (isBatch) {
      control = model.getController();
      control.addSimEventListener(this);
      dynParams = dfHeader.getDynParamMethod();
      //this had been in SimData, it is related to the header
      data.addToHeader("run");
      writeKey = this.fileName + DataFileHeader.WRITE_HEADER;
      Boolean b = null;
      b = (Boolean)control.getPersistentObj(writeKey);

      if (b == null) {
        writeHeader = true;
        control.putPersistentObj(writeKey, Boolean.TRUE);
      } else {
        writeHeader = b.booleanValue();
      }
      //end of code from SimData
      Enumeration e = dynParams.keys();
      HashSet numbers = new HashSet();
      numbers.add(int.class);
      numbers.add(float.class);
      numbers.add(double.class);
      numbers.add(byte.class);
      numbers.add(long.class);
      numbers.add(char.class);

      while (e.hasMoreElements()) {
        String key = (String) e.nextElement();
        //System.out.println(key);
        Method m = (Method) dynParams.get(key);
        if (Object.class.isAssignableFrom(m.getReturnType())) {
          createObjectDataSource(key, this.model, m.getName());
        } else if (numbers.contains(m.getReturnType())) {
          createNumericDataSource(key, this.model, m.getName(), -1, -1);
        } else {
          DataSource s = new ObjectDataSource(key, model,
                  (Method) dynParams.get(key));
          sources.add(s);
          data.addToHeader(key + "***0");
        }
      }
    }
  }

  /**
   * Constructs a DataRecorder using the specified file name, model, and mode.
   *
   * @param fileName the file to which the data is recorded
   * @param model the model from which the data is drawn. The relevant
   * model parameters (set/get parameters, rng seed) are written to the header
   * of the file
   * @param isBatch whether this model is run in batch mode
   * @deprecated
   */
  public LocalDataRecorder(String fileName, SimModel model, boolean isBatch) {
    this(fileName, model, isBatch, null);
  }

  /**
   * Constructs a DataRecorder using the specified file name, model, and mode.
   *
   * @param fileName the file to which the data is recorded
   * @param model the model from which the data is drawn. The relevant
   * model parameters (set/get parameters, rng seed) are written to the header
   * of the file
   * @param isBatch whether this model is run in batch mode
   * @param headerComment a comment to prepend to the file header information
   * @deprecated
   */
  public LocalDataRecorder(String fileName, SimModel model, boolean isBatch,
                           String headerComment) {
    this(fileName, model, headerComment);
    isBatch = false;
  }

  /**
   * Records the data for the current tick in tabular format. Each tick is
   * a row, and each column is the name of the data source.
   */
  public void record() {
    ArrayList v = new ArrayList();
    Double tick = new Double(model.getTickCount());
    v.add(tick);
    for (int i = 0; i < sources.size(); i++) {
      DataSource s = (DataSource) sources.elementAt(i);
      if (s instanceof ListDataSource) {
        v.addAll((List) s.execute());
      } else {
        Object d = s.execute();
        v.add(d);
      }
    }
    if(isBatch){
      v.add(0, new Long(control.getRunCount()));
    }
    data.addData(v);
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
   * Writes the recorded data out to a file in tabular format. This also
   * does a flush on the data itself (i.e. the data is no longer stored by
   * repast and exists only in the file). Identical to write().
   */
  public void writeToFile() {
    BufferedWriter out = null;
    try {
      if (writeHeader) {
        renameFile();
        out = new BufferedWriter(new FileWriter(fileName, true));
        out.write(data.getModelHeader());
        out.newLine();
        out.newLine();
        out.write(data.getHeader());
        out.newLine();
        if (isBatch) {
          control.putPersistentObj(writeKey, Boolean.FALSE);
        }
        writeHeader = false;
      }

      if (out == null)
        out = new BufferedWriter(new FileWriter(fileName, true));

      Iterator i = data.iterator();
      while(i.hasNext()){
        out.write((String) i.next());
      }
      out.flush();
      out.close();
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
    data.clearData();
  }

  /**
   * Writes the recorded data out to a file in tabular format. This also
   * does a flush on the data itself (i.e. the data is no longer stored by
   * repast and exists only in the file). Identical to writeToFile().
   */
  public void write() {
    writeToFile();
  }

  /**
   * Writes any ending matter to the file. Used internally during a batch run
   * to write the ending time of the entire batch. A model would not
   * typically call this method.
   */

  public void writeEnd() {
    BufferedWriter out = null;
    try {
      // has not written anything out yet
      if (writeHeader) {
        writeToFile();
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
}

