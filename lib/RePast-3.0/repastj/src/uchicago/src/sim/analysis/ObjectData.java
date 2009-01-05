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
import java.util.Vector;

import uchicago.src.sim.util.SimUtilities;

/**
 * Stores simulation data as objects. These objects can be written out
 * to a file as Strings via the Objects toString() method. It is assumed
 * that such data is already approriately formatted.  These objects are
 * from DataSources stored by an ObjectDataRecorder.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ObjectData {

  private Vector data = new Vector();
  private String dataHeader;
  private String fileName;
  private String modelHeader;
  private boolean isBatch;
  //private boolean writeHeader = true;
  private boolean nothingWritten = true;

  /**
   * Creates an ObjectData with the specified fileName, model header, and
   * whether this is a batch run or not.
   *
   * @param filename the name of the file to write the data out to.
   * @param modelHeader the model header (parameters etc.)
   * @param header the header for this data.
   * @param isBatch whether this is a batch run or not.
   */
  public ObjectData(String filename, String modelHeader, String header,
      boolean isBatch)
  {
    this.fileName = filename;
    this.modelHeader = modelHeader;
    this.isBatch = isBatch;
    dataHeader = header;
  }

  /**
   * Adds newData to this ObjectData
   *
   * @param newData the new data to add
   */
  public void addData(Object newData) {
    data.add(newData);
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
   * Writes the data (and a file header) out to a file. This flushes all
   * data from the data vector out to a file.
   */
  public void writeToFile() {
    BufferedWriter out = null;
    try {
      // has not written anything out yet
      if (nothingWritten) {
        renameFile();
        out = new BufferedWriter(new FileWriter(fileName, true));
        out.write(modelHeader);
        out.newLine();
        out.newLine();
        out.write(dataHeader);
        out.newLine();
      }

      nothingWritten = false;

      if (out == null)
        out = new BufferedWriter(new FileWriter(fileName, true));

      for (int i = 0; i < data.size(); i++) {
        out.write(data.get(i).toString());
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
      } catch (Exception ex1) {}
      System.exit(0);
    }
  }
}


