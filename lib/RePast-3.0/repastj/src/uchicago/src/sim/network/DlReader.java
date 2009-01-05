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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import uchicago.src.sim.util.SimUtilities;

/**
 * Matrix reader for UCINET dl format files. This will only read
 * square matrices whose rows and columns refer to the same nodes.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class DlReader implements NetworkMatrixReader {

  private BufferedReader reader;
  private long numNodes = -1;
  private int numMatrices = 1;
  private ArrayList labels;
  private ArrayList matrixLabels;
  private boolean hasMatrixLabels = false;
  //private int type = NetworkConstants.LARGE;

  /**
   * Creates a DlReader to read the specified file.
   *
   * @param filename the name of the file to read (in dl format)
   */
  public DlReader (String filename) {
    try {
      reader = new BufferedReader(new FileReader(filename));
    } catch (IOException ex) {
      try {
        if (reader != null) reader.close();
      } catch (IOException ex1) {}
      SimUtilities.showError("Error reading network file: " + filename, ex);
      System.exit(0);
    }
  }

  /**
   * Creates a DlRedaer to read from the specified InputStream.
   *
   * @param stream the InputStream to read from.
   */
  public DlReader(InputStream stream) {
    reader = new BufferedReader(new InputStreamReader(stream));
  }

  /**
   * Gets the matrix or matrices from the dl formatted file as a Vector
   * of AdjancencyMatrices.
   *
   * @deprecated use <code>getMatrices(int matrixType)</code> instead.
   * @return a Vector of AdjacencyMatrices.
   */
  public Vector getMatrices() throws IOException {
    return getMatrices(NetworkConstants.LARGE);
  }

  /**
   * Gets the matrix or matrices from the dl formatted file as a Vector
   * of AdjancencyMatrices.<p>
   *
   *The matrixType parameter refers to the size of the matrix elements, the
   * ij values. A matrix of type of NetworkConstants.BINARY contains on 0 or 1
   * as elements. A matrix of type of NetworkConstants.SMALL contains values
   * of -127 - 127 as elements. And a matrix of type NetworkConstants.LARGE
   * contains anything else. Choose the type appropriate to the values in the
   * matrix you are importing.
   *
   * @param  matrixType the type of the matrix. type refers to the size of the
   * matrix elements (ij values) and can be one of NetworkConstants.BINARY,
   * NetworkConstants.LARGE, NetworkConstants.SMALL
   * @return a Vector of AdjacencyMatrices.
   */
  public Vector getMatrices(int matrixType) throws IOException {
    //type = matrixType;
    readHeader();
    readLabels();
    if (hasMatrixLabels) {
      readMatrixLabels();
    }

    Vector v = new Vector(numMatrices);

    for (int i = 0; i < numMatrices; i++) {
      AdjacencyMatrix m = null;
      if (labels.size() > 0) {
        m = AdjacencyMatrixFactory.createAdjacencyMatrix(labels, matrixType);
      } else {
        m = AdjacencyMatrixFactory.createAdjacencyMatrix((int)this.numNodes, (int)this.numNodes, matrixType);
      }

      if (hasMatrixLabels) {
        m.setMatrixLabel((String)matrixLabels.get(i));
      }
      v.add(loadData(m));
    }

    //reader.close();

    return v;
  }

  private void readHeader() throws IOException {

    // all this is rather sloppy and naive.

    String header = null;

    while ((header = reader.readLine()) != null) {
      header = header.trim();
      if (header.length() > 0)
        break;
    }

    if (header == null) {
      throw new IOException("File is not a valid dl file");
    }


    if (!header.startsWith("dl")) {
      throw new IOException("File is not a valid dl file");
    }

    parseForNumNodesMatrices(header);
  }

  private void parseForNumNodesMatrices(String header) throws IOException {

    StringTokenizer tok = new StringTokenizer(header, " ");

    // get the 'dl'
    tok.nextToken();
    numNodes = parseForValue("n", tok, 1);


    if (tok.hasMoreTokens()) {
      numMatrices = (int)parseForValue("nm", tok, 2);
    }

    if (numNodes < 0) {
      throw new IOException("File is not a valid dl file");
    }
  }

  private long parseForValue(String lookFor, StringTokenizer tok, int length) throws
    IOException
  {
    if (tok.hasMoreTokens()) {
      String token = tok.nextToken();
      if (!token.startsWith(lookFor)) {
        throw new IOException("File is not a valid dl file");
      }

      if (token.length() > length) {
        // next better be an '=' or a ','
        char[] c = token.toCharArray();
        if (c[length] == '=' ||  c[length] == ',') {
          if (c.length > 1) {
            try {
              if (token.endsWith(",")) {
                token = token.substring(0, token.length() - 1);
              }
              return Long.parseLong(token.substring(length + 1, token.length()));
            } catch (NumberFormatException ex) {
              throw new IOException("File is not a valid dl file");
            }
          } else {
            throw new IOException("File is not a valid dl file");
          }
        } else {
          throw new IOException("File is not a valid dl file");
        }
      // token length == 1 so should more tokens
      } else {
        if (tok.hasMoreTokens()) {
          token = tok.nextToken();
          if (token.startsWith("=")) {
            if (token.length() > 1) {
              try {
                if (token.endsWith(",")) {
                  token = token.substring(0, token.length() - 1);
                }
                return Long.parseLong(token.substring(1, token.length()));
              } catch (NumberFormatException ex) {
                throw new IOException("File is not a valid dl file");
              }
            } else {
              if (tok.hasMoreTokens()) {
                try {
                  token = tok.nextToken();
                  if (token.endsWith(",")) {
                    token = token.substring(0, token.length() - 1);
                  }
                  return Long.parseLong(token);
                } catch (NumberFormatException ex) {
                  throw new IOException("File is not a valid dl file");
                }
              } else {
                throw new IOException("File is not a valid dl file");
              }
            }
          } else {
            try {
              if (token.endsWith(",")) {
                token = token.substring(0, token.length() - 1);
              }
              return Long.parseLong(token);
            } catch (NumberFormatException ex) {
              throw new IOException("File is not a valid dl file");
            }
          }
        } else {
          throw new IOException("File is not a valid dl file");
        }
      }
    } else {
      throw new IOException("File is not a valid dl file");
    }
  }

  private void parseLineForLabel(String line, ArrayList labelArray) throws IOException {
    char[] array = line.trim().toCharArray();
    int labelStart = -1;
    boolean labelStarted = false;

    for (int i = 0; i < array.length; i++) {
      if (array[i] == '\"') {
        labelStart = ++i;
        while (i < array.length) {
          if (array[i] == '\"') {
            labelArray.add(line.substring(labelStart, i));
            labelStart = -1;
            break;
          } else {
            i++;
          }
        }
        if (labelStart != -1) {
          // if here then never found the other '"' so throw error
          throw new IOException("File is not valid dl file");
        }
      } else if (array[i] == ' ' || array[i] == ',' || array[i] == '\r' ||
                array[i] == '\n') {
        if (labelStarted) {
          labelArray.add(line.substring(labelStart, i));
          labelStarted = false;
          labelStart = -1;
        }
      } else if (!labelStarted && array[i] != '\"') {
        System.out.println(array[i]);
        labelStart = i;
        labelStarted = true;
      }
    }

    if (labelStart != -1) {
      throw new IOException("File not a valid dl file");
    }
  }

  private void readMatrixLabels() throws IOException {
    matrixLabels = new ArrayList();
    String line = "";
    while ((line = reader.readLine()) != null) {
      if (line.equals("data:")) {
        break;
      }

      parseLineForLabel(line, matrixLabels);
    }
  }

  private void readLabels() throws IOException {
    labels = new ArrayList();

    // might not be any labels if so return null for getLabels;
    String line = reader.readLine();
    if (!line.equals("labels:")) {
      labels = new ArrayList();
      if (line.equals("matrix labels:")) {
        hasMatrixLabels = true;
      }
      return;
    }

    while ((line = reader.readLine()) != null) {
      if (line.equals("data:")) {
        break;
      }

      if (line.equals("matrix labels:")) {
        hasMatrixLabels = true;
        break;
      }

      parseLineForLabel(line, labels);
    }
  }

  private AdjacencyMatrix loadData(AdjacencyMatrix matrix) throws IOException {
    String line = reader.readLine();

    // remove whitespace
    while (line.trim().length() == 0) {
      line = reader.readLine();
    }

    // now should have data
    for (int i = 0; i < numNodes; i++) {
      StringTokenizer t = new StringTokenizer(line, " ");
      int j = 0;
      while (t.hasMoreTokens()) {
        String val = t.nextToken();
        matrix.set(i, j, Double.parseDouble(val));
        j++;
      }
      line = reader.readLine();
    }

    return matrix;
  }

  /**
   * Closes the reader.
   */
  public void close() {
    try {
      reader.close();
    } catch (IOException ex) {

    }
  }
}
