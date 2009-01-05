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

import java.util.Vector;

import uchicago.src.sim.engine.SimModel;

/**
 * Records Objects to files as Strings. ObjectDataRecorder will
 * record Objects together with an optional String comment. Typically, this
 * comment will be the tick count, but need not be.<p>
 *
 * In non-batch mode, a file header will be written that includes all the
 * model's properties and the values of these properties. These values
 * will be the value at the time the first call to write is made, allowing the
 * user to manipulate the parameters after setting up but prior to running the
 * model. Consequently, your initial model parameters should only be
 * changeable via the user or through a parameter file. If not, the file header
 * may be inaccurate. The actual data will be written as a block where the
 * block includes the optional comment as a header, followed by the String
 * representation of the Object itself.<p>
 *
 * In batch mode, any constant model parameters will be written in the
 * file header. The data block will include the value of any dynamic
 * parameters, the run number, as well as the optional comment (again,
 * typically the tick count), together with the String representation of the
 * Object to be recorded.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class ObjectDataRecorder {

  protected Vector data = new Vector(57);
  protected DataFileHeader dfHeader;
  protected String lineSep;
  protected DataFileWriter writer;

  protected class ObjectStringPair {
    Object object;
    String blockHeader;

    public ObjectStringPair(Object o, String s) {
      object = o;
      blockHeader = s;
    }
  }

  /**
   * Creates a new ObjectDataRecorder.
   *
   * @param fileName the name of the file to record objects to.
   * @param model the model associated with this ObjectDataRecorder
   */
  public ObjectDataRecorder(String fileName, SimModel model) {
    dfHeader = new DataFileHeader(model);
    lineSep = System.getProperty("line.separator");
    writer = new DataFileWriter(fileName, dfHeader);
  }

  /**
   * Creates a new ObjectDataRecorder.
   *
   * @param fileName the name of the file to record objects to.
   * @param modelHeader user provided header
   */
  public ObjectDataRecorder(String fileName, String modelHeader) {
    dfHeader = new DataFileHeader(modelHeader);
    lineSep = System.getProperty("line.separator");
    writer = new DataFileWriter(fileName, dfHeader);
  }

  /**
   * Records the specified Object together with the comment. The String
   * returned by Object.toString() is what is actuall recorded.
   *
   * @param o the object to record
   * @param comment a comment about the object
   */
  public void record(Object o, String comment) {
    data.add(new ObjectStringPair(o, comment));
  }

  /**
   * Records the specified object. The String
   * returned by Object.toString() is what is actuall recorded.
   *
   * @param o the object to record
   */
  public void record(Object o) {
    record(o, "");
  }

  /**
   * Writes all the recorded data (ie Objects) to the file specified in
   * the constructor. See the general class description for the specifics of
   * the file format. The Objects are recorded via their toString() method.
   */
  public void write() {
    for (int i = 0; i < data.size(); i++) {
      ObjectStringPair p = (ObjectStringPair)data.get(i);
      writer.setBlockHeader(p.blockHeader);
      writer.writeToFile(p.object);
    }

    data.clear();
  }
}
