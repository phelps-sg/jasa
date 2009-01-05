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
import java.util.Date;
import java.util.Iterator;

import uchicago.src.sim.util.SimUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Feb 11, 2003
 * Time: 1:28:01 PM
 * To change this template use Options | File Templates.
 */
public class DataWriter{
  String fileName;
  private boolean fileSet = false;


  public DataWriter(){
    fileName = null;
  }

  public DataWriter(String file){
    fileName = file;
  }

  public void setFile(String file){
    fileName = file;
    fileSet = true;
  }

  public boolean isFileSet(){
    return fileSet;
  }

  public synchronized void write(SimDataNew data, boolean writeHeader){
    BufferedWriter out = null;
    try{
      if (writeHeader) {
        renameFile();
        out = new BufferedWriter(new FileWriter(fileName, true));
        out.write(data.getModelHeader());
        out.newLine();
        out.newLine();
        out.write(data.getHeader());
        out.newLine();
        //if (isBatch) {
        //  control.putPersistentObj(writeKey, Boolean.FALSE);
        //}
        writeHeader = false;
      }


      if (out == null)
        out = new BufferedWriter(new FileWriter(fileName, true));

      Iterator i = data.iterator();
      while(i.hasNext()){
        out.write((String) i.next());
      }
      out.close();
    }catch(Exception e){
      e.printStackTrace();
      try{
        out.flush();
        out.close();

      }catch(Exception e1){

      }
      System.exit(0);
    }

  }

  public synchronized void writeEnd(){
    BufferedWriter out = null;
    try {
      // has not written anything out yet
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

      newName += ".bak";
      do {
        newFile = new File(newName + x + lastPart);
        x++;
      } while (newFile.exists());
      oldFile.renameTo(newFile);
      oldFile.delete();
    }
  }

}

