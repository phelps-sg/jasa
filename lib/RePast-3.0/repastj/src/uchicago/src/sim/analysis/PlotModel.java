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
import java.util.ArrayList;

import uchicago.src.sim.engine.SimModel;
import cern.colt.list.DoubleArrayList;

/**
 * 
 *
 * @author Nick Collier
 * @version $Revision $ $Date$
 */
public abstract class PlotModel {

  public static final int CSV = 0;

  private DoubleArrayList xVals = new DoubleArrayList();
  private ArrayList data = new ArrayList();
  private ArrayList sequenceNames = new ArrayList();
  protected String fileName;
  protected int fileFormat;
  protected String title = "";
  protected SimModel model;

  protected int lastColUpdate = 0;

  /**
   * Construct a Statistics class with the specified model
   */
  public PlotModel(SimModel model) {
    this("statFile.txt", CSV, "", model);
  }

  /**
   * Constructs a Statistic with the specified title, file name, and file
   * format, and model.
   *
   * @param fileName the name of the file to write the sequence data to
   * @param fileFormat the format of the file - i.e. PlotModel.CSV
   * @param title the title
   * @param model the model
   */
  public PlotModel(String fileName, int fileFormat, String title,
		   SimModel model) {
    this.model = model;
    this.fileName = fileName;
    this.fileFormat = fileFormat;
    this.title = title;
  }

  public void addSequence(String name) {
    data.add(new DoubleArrayList());
    sequenceNames.add(name);
  }

  public ArrayList getSequenceNames() {
    return sequenceNames;
  }
  
  public void addX(double xVal) {
    xVals.add(xVal);
  }

  public void addY(double yVal, int yIndex) {
    ((DoubleArrayList)data.get(yIndex)).add(yVal);
  }

  public double getXVal(int xIndex) {
    return xVals.getQuick(xIndex);
  }

  public double getYVal(int sequenceIndex, int index) {
    return ((DoubleArrayList)data.get(sequenceIndex)).getQuick(index);
  }

  public int getSequenceCount() {
    return data.size();
  }

  public int getXValCount() {
    return xVals.size();
  }

  /**
   * Calculates the next item in the sequence
   */
  public abstract void record();

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
   * Writes the data stored by this call to a file specified in the constructor
   */
  public abstract void writeToFile();
}



