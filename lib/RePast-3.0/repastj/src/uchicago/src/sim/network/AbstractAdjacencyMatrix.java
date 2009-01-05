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

/**
 * A social network adjacency matrix. This is used as an itermediary data
 * structure when moving between <code>Nodes</code> and <code>Edges</code>
 * and other kinds of network representations. The matrix is assumed to
 * be square and that the rows and columns refer to the same nodes.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see uchicago.src.sim.network.Node
 * @see uchicago.src.sim.network.Edge
 */

import java.util.List;
import java.util.Vector;

//import cern.colt.matrix.impl.DenseDoubleMatrix1D;

public abstract class AbstractAdjacencyMatrix implements AdjacencyMatrix {

  protected List labels = new Vector();
  protected String matrixLabel = "";
  protected String comment = "";


  /**
   * Sets the label for this matrix. This is used to indicated the type
   * of matrix (i.e. kinship etc.).
   *
   * @param mLabel the label for this matrix
   */
  public void setMatrixLabel(String mLabel) {
    matrixLabel = mLabel;
  }

  /**
   * Gets the label for this matrix. This is used to indicated the type
   * of matrix (i.e. kinship etc.).
   */
  public String getMatrixLabel() {
    return matrixLabel;
  }

  /**
   * Associates a comment with this matrix (e.g. the tick count at which
   * it was created.)
   *
   * @param comment the comment
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Gets the comment, if any, associated with this matrix.
   */
  public String getComment() {
    return comment;
  }

  /**
   * Gets the node labels for this matrix.
   */
  public List getLabels() {
    return labels;
  }
}