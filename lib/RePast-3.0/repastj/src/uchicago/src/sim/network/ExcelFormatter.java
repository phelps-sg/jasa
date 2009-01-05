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

/**
 * Formats AdjacencyMatrices into ExcelFormat.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ExcelFormatter implements NetworkMatrixFormatter {

  Vector matrices = null;
  String header = "";

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
    this.matrices = matrices;
    header = comment;

  }

  /**
   * Gets the header information created by a previous call to format(...).
   */
  public String getHeader() {
    return header;

  }

  /**
   * Get the matrices formatted by a previous call to format. In this case,
   * a Vector of AdjacencyMatrices is returned.
   */
  public Vector getMatrices() {
    return matrices;
  }
}