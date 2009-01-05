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

import java.io.File;
import java.util.Vector;

import uchicago.src.sim.engine.SimModel;

/**
 * Statistics: base class for the statistics classes that form the model (MVC)
 * for SimGraphs.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public abstract class OpenStats {

  public static final int CSV = 0;

  protected OpenArrayData data = new OpenArrayData();
  protected String fileName;
  protected int fileFormat;
  protected long lastPrinted = -1;
  protected String title = "";
  protected SimModel model;

  protected int lastColUpdate = 0;

  /**
   * Construct a Statistics class with the specified model
   */
  public OpenStats(SimModel model) {
    this("statFile.txt", CSV, "", model);
  }

  /**
   * Constructs a Statistic with the specified title, file name, and file
   * format, and model.
   *
   * @param fileName the name of the file to write the sequence data to
   * @param fileFormat the format of the file - i.e. Statistics.CSV
   * @param title the title
   * @param model the model
   */
  public OpenStats(String fileName, int fileFormat, String title, SimModel model) {
    this.model = model;
    this.fileName = fileName;
    this.fileFormat = fileFormat;
    this.title = title;
  }

  /**
   * Calculates the next item in the sequence
   */
  public abstract void record();

  public Vector getDataTable() {
    return data.getDataTable();
  }

  protected void renameFile() throws java.io.IOException {
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

      do {
        newFile = new File(newName + x + lastPart);
        x++;
      } while (newFile.exists());
      oldFile.renameTo(newFile);
      oldFile.delete();
    }
  }


  /**
   * Writes the data stored by this call to a file specified in the constructor.
   */
  public abstract void writeToFile();

  /**
   * Sets the simulation model.
   */
  public void setSimModel(SimModel model) {
    this.model = model;
  }

  public Object getDataItem(int row, int col) {
    return data.getDataItem(row, col);
  }

  public String getName() {
    return data.getName();
  }

  public int getNumRows() {
    return data.getNumRows();
  }

  public String[] getPointLabels() {
    return data.getPointLabels();
  }

  public Vector getRow(int row) {
    return data.getRow(row);
  }

  public String getSeriesLabel(int parm1) {
    return data.getSeriesLabel(parm1);
  }

  public String getSeriesName(int parm1) {
    return data.getSeriesName(parm1);
  }
}




