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

import java.util.List;
import java.util.Vector;

import ViolinStrings.Strings;


/**
 * Formats AdjacencyMatrices into dl format.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class DlFormatter implements NetworkMatrixFormatter {

  private static String lineSep = "\r\n";
  private String header = "";
  private Vector matrixStrings;

  /**
   * Formats the specified list of matrix labels, matrices and comments.
   * Subsequent calls to getHeader and getMatrices will return the results
   * of this formatting.
   *
   * @param matrixLabels the matrix labels
   * @param matrices a Vector of AdjacencyMatrices
   * @param comment a comment
   */

  public void format(List matrixLabels, Vector matrices, String comment) {
    header = "";
    if (comment.length() > 0) {
      header += "# " + comment + lineSep;
    }

    matrixStrings = new Vector(matrices.size());

    for (int i = 0; i < matrices.size(); i++) {
      AdjacencyMatrix m = (AdjacencyMatrix)matrices.get(i);
      if (i == 0) {
        header += formatHeader(m.getLabels(), matrixLabels);
      }

      String mString = Strings.change(m.matrixToString(), "\n", "\r\n");
      matrixStrings.add(mString + lineSep);
    }
  }

  /**
   * Gets the dl header created by format(...).
   */
  public String getHeader() {
    return header;
  }

  /**
   *  Gets the matrices in dl format (a vector of formatted Strings).
   */
  public Vector getMatrices() {
    return matrixStrings;
  }

  private String formatHeader(List labels, List matrixLabels) {
    StringBuffer buf = new StringBuffer("dl ");
    int size = labels.size();
    boolean doOneLabel = false;
    boolean doMultLabels = false;


    buf.append("n=" + size);

    if (matrixLabels.size() > 0) {
      if (matrixLabels.size() == 1) {
        if (((String)matrixLabels.get(0)).length() != 0) {
          buf.append(", nm = 1");
          doOneLabel = true;
        }
      } else {
        buf.append(", nm = " + matrixLabels.size());
        doMultLabels = true;
      }
    }

    buf.append(lineSep);

    if (((String)labels.get(0)).trim().length() != 0) {
      buf.append("labels:" + lineSep);

      for (int i = 0; i < size; i++) {
        if (i == 0) {
          buf.append("\"" + labels.get(i) + "\"");
        } else {
          buf.append(",\"" + labels.get(i) + "\"");
        }
      }
      buf.append(lineSep);
    }

    if (doOneLabel) {
      String mLabel = (String)matrixLabels.get(0);
      buf.append("matrix labels:" + lineSep);
      buf.append(mLabel + lineSep);
    } else if (doMultLabels) {
      buf.append("matrix labels:" + lineSep);
      for (int i = 0; i < matrixLabels.size(); i++) {
        if (i == 0) {
          buf.append("\"" + matrixLabels.get(i) + "\"");
        } else {
          buf.append(",\"" + matrixLabels.get(i) + "\"");
        }
      }
      buf.append(lineSep);
    }

    buf.append("data:");
    return buf.toString();
  }
}