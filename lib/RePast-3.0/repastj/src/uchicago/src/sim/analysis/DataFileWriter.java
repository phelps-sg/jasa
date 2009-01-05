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

//import java.util.Vector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import uchicago.src.sim.util.SimUtilities;

/**
 * Writes objects to a file via their toString() method. DataFileWriter
 * is used by recording objects to write data to a file.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */

public class DataFileWriter implements BlockFileWriter {

  //private String dataHeader;
  private String fileName;
  private String blockHeader;
  private boolean nothingWritten = true;
  private boolean writeBlockHeader = false;
  private DataFileHeader dfHeader;

  /**
   * Constructs a DataFileWriter to write to the specified file
   * using the specified DataFileHeader.
   *
   * @param filename the name of the file to write the data out to.
   * @param dfHeader the DataFileHeader for this file.
   */
  public DataFileWriter(String fileName, DataFileHeader dfHeader)
  {
    try {
      this.fileName = new File(fileName).getCanonicalPath();
    } catch (IOException ex) {
      SimUtilities.showError("Fatal file error", ex);
      System.exit(0);
    }

    this.dfHeader = dfHeader;
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

      if (dfHeader.isBatch()) {
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
   * Sets a block header. Once this has been called the next call to
   * writeToFile will write the block header to the file. The block header
   * will be the header for any data then written out to the file via
   * writeToFile(). Subsequent calls to setBlockHeader will again force
   * the new block header to be written.<br/>
   * If the block header is either null or "" this will disable the writing
   * of the block header
   *
   * @param blockHeader the header for a block of data
   */
  public void setBlockHeader(String blockHeader) {
  	this.blockHeader = blockHeader;
  	
  	if (blockHeader == null || blockHeader.equals(""))
  		writeBlockHeader = false;
    else
    	writeBlockHeader = true;
  }

  /**
   * Writes the specifed object out to a file. The
   * String representation of the object (via Object.toString()) is written.
   *
   * @param obj the object whose String representation will be written to
   * a file.
   */
  public void writeToFile(Object obj) {
    String data = obj.toString();

    BufferedWriter out = null;
    try {
      // has not written anything out yet
      if (nothingWritten) {
        if (dfHeader.doWriteHeader(fileName)) {
          renameFile();
          out = new BufferedWriter(new FileWriter(fileName, true));
          out.write(dfHeader.getFileHeader());
          out.newLine();
          out.newLine();
          dfHeader.setWriteHeader(fileName, false);
        }
        nothingWritten = false;
      }

      if (out == null)
        out = new BufferedWriter(new FileWriter(fileName, true));

      if (writeBlockHeader) {
        out.write(dfHeader.getBlockHeaderAsComments());
        out.write(blockHeader);
        out.newLine();
        writeBlockHeader = false;
      }

      out.write(data);
      out.newLine();

      out.flush();
      out.close();
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


