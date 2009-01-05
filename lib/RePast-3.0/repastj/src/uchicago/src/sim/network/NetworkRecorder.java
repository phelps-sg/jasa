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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import uchicago.src.sim.analysis.BlockFileWriter;
import uchicago.src.sim.analysis.DataFileHeader;
import uchicago.src.sim.analysis.DataFileWriter;
import uchicago.src.sim.analysis.ExcelFileWriter;
import uchicago.src.sim.engine.SimModel;

/**
 * Records a network as a matrix or matrices in the appropriate format.
 * NetworkRecorder is used to take a list of Nodes and write the corresponding
 * adjacency matrix to a file. Three file formats are supported: UCINet's
 * dl, Excel, and ASCII. The actual network matrix is written out in this
 * format, although RePast records relevant non-matrix information as well:
 * a file header containing the constant parameters for the model and a
 * timestamp, and a block header that contains header information relevant
 * to the network data recorded beneath it. The block header will contain
 * the value of any dynamic model parameters at the time a network
 * was recorded, as well as any user comments (typically the current tick
 * count).<p>
 *
 * The actual format of the file is thus:
 * <code><pre>
 * file header
 *
 * block_header_1
 * network data in the specified format
 *
 * block_header_2
 * network data in the specified format
 *
 * ...
 * </pre></code>
 *
 * Specifying the format then specifies the format for the network data. The
 * rest (file header and block header) will remain the same for all formats.
 * The actual network data can easily be cut and pasted out of the file.<p>
 *
 * ASCII format will record the matrix and the matrix label in comma
 * delimited plain text. Excel format will store it in an Excel file
 * in a format suitable for importation into UCINet.<p>
 *
 * <b>Note</b>: Excel format should not be used as it is very time consuming
 * and error prone to write to an Excel file. Use ASCII instead which
 * is a plain text format suitable for importation into excel.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class NetworkRecorder implements NetworkConstants {

  private NetworkMatrixFormatter formatter;
  private String fileName;
  private ArrayList dataList = new ArrayList(7);
  private BlockFileWriter writer = null;

  private DataFileHeader dfHeader;


  // holds one or more AdjacencyMatrices, together with their
  // the labels of these matrices. The point here is that
  // the same nodes may participate in more than one type of
  // network so more than one matrix for same nodes
  public class MatrixData {
    Vector matrices = new Vector();
    Vector matrixLabels = new Vector();
    String comment = "";
  };


  /**
   * Constructs a NetworkRecorder with the specified format and file name.
   * Networks will be recorded in the format specified by format to
   * the file specified by fileName. The format can be one of
   * NetworkRecorder.DL (UCINet dl), NetworkRecorder.EXCEL (UCINet Excel
   * format), or NetworkRecorder.ASCII (plain text comma-delimeted). See
   * the class description above for more information on each format
   * and the actual format of the resulting file.
   *
   * @param fileType the network file format (NetworkRecorder.DL,
   * NetworkRecorder.EXCEL, or NetworkRecorder.ASCII).
   * @param fileName the name of the file to write the recorded network(s) to.
   * @param model the model associated with this network
   * @param isBatch whether this is a batch run or not
   */
  public NetworkRecorder(int format, String fileName, SimModel model)
  {
    this.fileName = fileName;
    if (model == null) dfHeader = new DataFileHeader();
    else dfHeader = new DataFileHeader(model);

    if (format == NetworkRecorder.DL) {
      formatter = new DlFormatter();
      // header info can be put in if necessary
      writer = new DataFileWriter(this.fileName, dfHeader);
    } else if (format == NetworkRecorder.EXCEL) {
      formatter = new ExcelFormatter();
      writer = new ExcelFileWriter(this.fileName, dfHeader);
    } else if (format == NetworkRecorder.ASCII) {
      formatter = new ASCIIFormatter();
      writer = new DataFileWriter(this.fileName, dfHeader);
    } else {
      throw new IllegalArgumentException("Illegal file type");
    }
  }

  /**
   * Records the network described by the Nodes in nodeList. It is assumed
   * that each Node appears only once in the nodeList.
   *
   * @param nodeList the list of nodes that describe the network to record
   * @deprecated use <code>record(List, String, int)</code> instead.
   */
  public void record(List nodeList) {
    record(nodeList, "");
  }

  /**
   * Records the network described by the Nodes in nodeList. It is assumed
   * that each Node appears only once in the nodeList.<p>
   *
   * The matrixType parameter refers to the size of the matrix elements, the
   * ij values. A matrix of type of NetworkConstants.BINARY contains on 0 or 1
   * as elements. A matrix of type of NetworkConstants.SMALL contains values
   * of -127 - 127 as elements. And a matrix of type NetworkConstants.LARGE
   * contains anything else. Matrix ij values are assigned the edge strength
   * of an edge, so unless you have explicitly set an edge strength of greater
   * than 1, use NetworkConstants.BINARY.
   *
   * @param nodeList the list of nodes that describe the network to record
   * @param matrixType the type of the matrix. type refers to the size of the
   * matrix elements (ij values) and can be one of NetworkConstants.BINARY,
   * NetworkConstants.LARGE, NetworkConstants.SMALL
   */
  public void record(List nodeList, int matrixType) {
    record(nodeList, "", matrixType);
  }

  /**
   * Records the network described by the Nodes in nodeList. It is assumed
   * that each Node appears only once in the nodeList. The comment is
   * prepended to the formatted network data corresponding to the nodeList,
   * and can be used to specify the tick at which the network was recorded,
   * for example.
   *
   * @param nodeList the list of nodes that describe the network to record
   * @param comment a comment associated with the network
   * @deprecated use <code>record(List nodeList, String comment, int matrixType)
   * </code> instead.
   */
  public void record(List nodeList, String comment) {
    record(nodeList, comment, NetworkConstants.LARGE);
  }

  /**
   * Records the network described by the Nodes in nodeList. It is assumed
   * that each Node appears only once in the nodeList. The comment is
   * prepended to the formatted network data corresponding to the nodeList,
   * and can be used to specify the tick at which the network was recorded,
   * for example.
   *
   * The matrixType parameter refers to the size of the matrix elements, the
   * ij values. A matrix of type of NetworkConstants.BINARY contains on 0 or 1
   * as elements. A matrix of type of NetworkConstants.SMALL contains values
   * of -127 - 127 as elements. And a matrix of type NetworkConstants.LARGE
   * contains anything else. Matrix ij values are assigned the edge strength
   * of an edge, so unless you have explicitly set an edge strength of greater
   * than 1, use NetworkConstants.BINARY.
   *
   * @param nodeList the list of nodes that describe the network to record
   * @param comment a comment associated with the network
   * @param matrixType the type of the matrix. type refers to the size of the
   * matrix elements (ij values) and can be one of NetworkConstants.BINARY,
   * NetworkConstants.LARGE, NetworkConstants.SMALL
   */
  public void record(List nodeList, String comment, int matrixType) {
    Vector v = NetworkConvertor.nodesToMatrices(nodeList, matrixType);

    MatrixData md = new MatrixData();
    md.comment = comment;
    for (int i = 0; i < v.size(); i++) {
      AdjacencyMatrix m = (AdjacencyMatrix)v.get(i);
      if (!md.matrixLabels.contains(m.getMatrixLabel())) {
        md.matrixLabels.add(m.getMatrixLabel());
      }

      md.matrices.add(m);
    }

    dataList.add(md);
  }

  /**
   * Writes the networks recorded by record(...) to the file specified in
   * the constructor. See the class description above for the file format.
   */
  public void write() {
    for (int i = 0; i < dataList.size(); i++) {
      MatrixData md = (MatrixData)dataList.get(i);
      formatter.format(md.matrixLabels, md.matrices, md.comment);
      String header = formatter.getHeader();
      writer.setBlockHeader(header);

      Vector v = formatter.getMatrices();
      for (int j = 0; j < v.size(); j++) {
        writer.writeToFile(v.get(j));
      }
    }
    dataList.clear();
  }
}
