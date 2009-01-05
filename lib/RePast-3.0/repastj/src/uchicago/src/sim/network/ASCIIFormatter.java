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
package uchicago.src.sim.network;

import java.text.NumberFormat;
import java.util.List;
import java.util.Vector;

//import ViolinStrings.Strings;

/**
 * Formats AdjacencyMatrices into a comma delimited plain text format. This
 * can be used by a NetworkRecorder to record network in a
 * plain text format. In this format network data looks like:
 * <code><pre>
 * matrix label: _label_
 *             ,node_label_0, node_label_1, ...
 * node_label_0, data_00, data_01, ...
 * node_label_1, data_10, data_11, ...
 * ...
 * </pre></code>
 *
 * If a network has no label names, comma-delimited empty strings will be used.
 * For example,
 * <code>
 * matrix label: mLabel
 * ,,,,,
 * ,0,1,0,0,0
 * ,0,0,1,0,0
 * ,0,0,0,1,0
 * ,0,0,0,0,1
 * ,0,0,0,0,0
 * </code>
 *
 * This format intended as a substitute for the excel format given the
 * exceedingly long time it takes to write to an excel file. Data in this
 * format is easily imported into excel such that the actual matrix will begin
 * in cell B:2.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */



public class ASCIIFormatter implements NetworkMatrixFormatter {

  private String lineSep;
  private Vector matrixStrings;
  private String header;
  private NumberFormat nFormat = NumberFormat.getNumberInstance();

  /**
   * Formats the specified matrices together with the labels, and comment
   *
   * @param matrixLabels the labels of the matrices
   * @param matrices a Vector of AdjacencyMatrices
   * @param comment an optional comment
   */
  public void format(List matrixLabels, Vector matrices, String comment) {

    lineSep = System.getProperty("line.separator");
    header = "# " + comment + lineSep;
    matrixStrings = new Vector(matrices.size());

    for (int i = 0; i < matrices.size(); i++) {
      StringBuffer b = new StringBuffer();
      AdjacencyMatrix m = (AdjacencyMatrix)matrices.get(i);
      List labels = m.getLabels();
      String mLabels = "";
      for (int j = 0; j < labels.size(); j++) {
        mLabels += "," + labels.get(j);
      }

      if (mLabels.length() == 0) {
        mLabels = ",";
      }

      String mLabel = "matrix label: " + m.getMatrixLabel() + lineSep;
      b.append(mLabel);

      if (mLabels.length() > 0) {
        b.append(mLabels);
        b.append(lineSep);
      }

      String mString = formatMatrix(m);
      b.append(mString);
      b.append(lineSep);

      matrixStrings.add(b.toString());
    }
  }


  private String formatMatrix(AdjacencyMatrix m) {
    StringBuffer b = new StringBuffer();
    List labels = m.getLabels();

    for (int i = 0; i < m.rows(); i++) {
      double[] vals = m.getRow(i).toArray();
      if (labels != null) {
        b.append(labels.get(i));
      }
      for (int j = 0; j < vals.length; j++) {
        b.append(",");
        b.append(nFormat.format(vals[j]));
      }
      b.append(lineSep);
    }

    return b.toString();
  }

  /**
   * Returns the block header for the matrices formatter in comment.
   */
  public String getHeader() {
    return header;
  }

  /**
   * Returns a Vector of appropriately Strings representing the matrices.
   */
  public Vector getMatrices() {
    return matrixStrings;
  }
}
