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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import uchicago.src.sim.network.AdjacencyMatrix;
import uchicago.src.sim.util.SimUtilities;


/**
 * Writes network data to ExcelFiles. <b>This is extremely slow and
 * ASCII writing (which is suitable for importing into excel should be used
 * instead.</b> The data is written with a file header (model properties
 * etc.) any block header info (dynamic properties, tick count) and then
 * the actual network data. The network data format is identical to the
 * excel format imported and exported by UCINet. Labels are recorded on
 * the row and column above and preceding the data. The data itself begins
 * in the second row and col cell.
 */

public class ExcelFileWriter implements BlockFileWriter {

  private HSSFWorkbook book;
  private boolean isBatch = false;
  private String fileName;
  private String blockHeader = "";
  private boolean initialized = false;
  private HashSet sheetsHeadersWritten = new HashSet();
  private Hashtable sheetsCurRow;
  private DataFileHeader dfHeader;
  private FileOutputStream out;

  /**
   * Constructs a new ExcelFileWriter to write to the specified file with the
   * specified DataFileHeader.
   *
   * @param fileName the name of the file to write to.
   * @param dfHeader the DataFileHeader for this file.
   */
  public ExcelFileWriter(String fileName, DataFileHeader dfHeader) {
    try {
      this.fileName = new File(fileName).getCanonicalPath();
    } catch (IOException ex) {
      SimUtilities.showError("Fatal file Error", ex);
      System.exit(0);
    }

    this.dfHeader = dfHeader;

    if (dfHeader.isBatch()) {
      sheetsCurRow = (Hashtable) dfHeader.getPersistentObj(this.fileName);
      if (sheetsCurRow == null) {
        sheetsCurRow = new Hashtable();
        dfHeader.putPersistentObj(this.fileName, sheetsCurRow);
      }
    } else {
      sheetsCurRow = new Hashtable();
    }
  }

  /**
   * Sets the block header for subsequent writes.
   *
   * @param blockHeader the block header
   */
  public void setBlockHeader(String blockHeader) {
    this.blockHeader = blockHeader;
  }

  private void init() {

    try {
      renameFile();
      openBook();
    } catch (IOException ex) {
      SimUtilities.showError("Error opening excel file", ex);
      System.exit(0);
    }
    initialized = true;
  }

  private void openBook() {
    book = new HSSFWorkbook();
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

  private void writeHeader(String sheet) {
    HSSFSheet s = book.createSheet(sheet);
    HSSFRow r = null;
    HSSFCell c = null;
    String header = dfHeader.getFileHeader();
    StringTokenizer t = new StringTokenizer(header, "\r\n");
    int row = 0;
    while (t.hasMoreTokens()) {
      String line = t.nextToken();
      r = s.createRow((short) row);
      if (line.indexOf(':') != -1) {
        int index = line.indexOf(':');
        c = r.createCell((short) 0);
        c.setCellValue(line.substring(0, index));
        c = r.createCell((short) 1);
        c.setCellValue(line.substring(index + 1, line.length()));
      } else {
        c = r.createCell((short) 0);
        c.setCellValue(t.nextToken());
      }
      row++;
    }
    row++;

    // this 1 should be changed to the appropriate row
    // after writing the header
    sheetsHeadersWritten.add(sheet);
    sheetsCurRow.put(sheet, new Integer(row));
  }

  private void writeData(String sheet, AdjacencyMatrix m) {
    int row = ((Integer) sheetsCurRow.get(sheet)).intValue();
    HSSFSheet s = book.getSheet(sheet);

    String bHeader = blockHeader + "\n" + dfHeader.getBlockHeader();
    HSSFRow r = null;
    HSSFCell c = null;
    if (bHeader.trim().length() > 0) {
      StringTokenizer t = new StringTokenizer(bHeader, "\r\n");
      while (t.hasMoreTokens()) {
        String line = t.nextToken();
        r = s.createRow((short) row);
        if (line.indexOf(':') != -1) {
          int index = line.indexOf(':');
          c = r.createCell((short) 0);
          c.setCellValue(line.substring(0, index));
          c = r.createCell((short) 1);
          c.setCellValue(line.substring(index + 1, line.length()));
        } else {
          c = r.createCell((short) 0);
          c.setCellValue(line);
        }
        row++;
      }
    }
    if (m.getLabels().size() > 0) {
      r = s.createRow((short) row);
      Vector l = new Vector(m.getLabels());
      for (int i = 1; i <= l.size(); i++) {
        c = r.createCell((short) i);
        c.setCellValue((String) l.get(i - 1));
      }

      for (int i = 0; i < l.size(); i++) {
        r = s.createRow((short) (row + i + 1));
        c = r.createCell((short) 0);
        c.setCellValue((String) l.get(i));
      }

      row++;
    }

    //Vector vals = new Vector();
    for (int i = 0; i < m.rows(); i++) {
      double[] dVals = m.getRow(i).toArray();
      r = s.getRow((short) row);
      c = r.createCell((short) 1);
      for (int j = 0; j < dVals.length; j++) {
        c.setCellValue(dVals[j]);
        c = r.createCell((short) (j + 1));
      }

      row++;
    }

    sheetsCurRow.put(sheet, new Integer(++row));
  }

  /**
   * Writes the specified object (an AjacencyMatrix) to the file. The file
   * header and any block header information will be written if necessary.
   *
   * @param o the AdjacencyMatrix to write to the Excel file
   */
  public void writeToFile(Object o) {
    if (!(o instanceof AdjacencyMatrix)) {
      throw new IllegalArgumentException("Argument to ExcelFileWriter.writeToFile() must be an AdjacencyMatrix");
    }

    if (dfHeader.isBatch()) {
      if (dfHeader.doWriteHeader(fileName)) {
        init();
        dfHeader.setWriteHeader(fileName, false);
      }
    } else {
      if (!initialized) {
        init();
      }
    }

    String sheet = "Sheet1";
    AdjacencyMatrix m = (AdjacencyMatrix) o;

    String matrixName = m.getMatrixLabel();
    if (matrixName.trim().length() > 0) {
      sheet = matrixName;
    }
    //HSSFSheet s = null;
    if (dfHeader.isBatch()) {
      if (dfHeader.doWriteHeader(fileName + sheet)) {
        writeHeader(sheet);
        dfHeader.setWriteHeader(fileName + sheet, false);
      } else {
        book.createSheet(sheet);
      }
    } else {
      if (!sheetsHeadersWritten.contains(sheet)) {
        writeHeader(sheet);
      }
    }

    writeData(sheet, m);
    try {
      out = new FileOutputStream(fileName);
      book.write(out);
    } catch (IOException ioex) {
      SimUtilities.showError("Excel Writer Error: " + ioex, ioex);
    }
  }

  /**
   * Closes the writer.
   */
  public void close() {
    try {
      if (out != null)
        out.close();
    } catch (IOException ioex) {
      SimUtilities.showError("Excel Writer Error: " + ioex, ioex);
      System.exit(0);
    }
  }
}
