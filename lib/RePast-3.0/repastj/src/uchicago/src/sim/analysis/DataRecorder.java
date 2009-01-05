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

import java.util.ArrayList;

import uchicago.src.sim.engine.IController;
import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimModel;


/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 2, 2003
 * Time: 1:06:17 PM
 * To change this template use Options | File Templates.
 */
public class DataRecorder implements DataSourceRecorder{
  private DataSourceRecorder record;

  /**
   * Constructs a DataRecorder using the specified file name and model.
   * Defaults to non-batch mode.
   *
   * @param fileName the file to which the data is recorded
   * @param model the model from which the data is drawn. The relevant
   * model parameters (set/get parameters, rng seed) are written to the header
   * of the file.
   */
  public DataRecorder(String fileName, SimModel model){
    this(fileName, model, "");
  }

  /**
   * Constructs a DataRecorder using the specified file name, model,
   * and headerComment.
   *
   * @param fileName the file to which the data is recorded
   * @param model the model from which the data is drawn. The relevant
   * model parameters (set/get parameters, rng seed) are written to the header
   * of the file
   * @param headerComment a comment to prepend to the file header information
   */
  public DataRecorder(String fileName, SimModel model, String headerComment){
    IController control = model.getController();
    //Get the writer object
    Object objWriter = control.getPersistentObj("WRITER");
    if(objWriter != null){
      DataWriter writer = (DataWriter) objWriter;
      if(fileName == null) System.out.println("no fileName");
      if(!writer.isFileSet()){
        writer.setFile(fileName);
      }
      record = new DistributedDataRecorder(fileName, writer, model, headerComment);
    }else{
      record = new LocalDataRecorder(fileName, model, headerComment);
    }
  }

  /**
   * This constructor is deprecated.  Use DataRecorder(String fileName, SimModel model)
   * instead.
   * @param fileName
   * @param model
   * @param batch
   * @deprecated
   */
  public DataRecorder(String fileName, SimModel model, boolean batch){
    record = new LocalDataRecorder(fileName, model,batch);
  }

  /**
   * This constructor is deprecated.   Use
   * DataRecorder(String fileName, SimModel model, String headerComment) instead.
   * @param fileName
   * @param model
   * @param isBatch
   * @param headerComment
   * @deprecated
   */
  public DataRecorder(String fileName, SimModel model, boolean isBatch, String headerComment){
    record = new LocalDataRecorder(fileName, model, isBatch, headerComment);
  }


  /**
   * Adds a NumericDataSource to this DataRecorder with the specified name,
   * and specified number of integral and fractional digits. The
   * NumericDataSource generates the data to be recorded.<p>
   *
   * Specify a maxIntegerDigits of less than 0 to avoid rounding and truncating
   * the integeral portion of the number. Similarly, specify a maxFractionDigits
   * of less than 0 to avoid rounding and truncating the fractional portion
   * of the number.<p>
   *
   * <b>Note that the number is rounded if necessary when
   * trucated. For example, with a maxFactionDigits of 2, the number
   * 99.555 becomes 99.56.</b>
   *
   * @param name the name of the data (e.g. Number of Agents)
   * @param s the source of the data
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point. A value of -1 will record all the digits.
   */

  public void addNumericDataSource(String name, NumericDataSource s,
                                   int maxIntegerDigits, int maxFractionDigits) {
    record.addNumericDataSource(name,s,maxIntegerDigits, maxFractionDigits);
  }

  /**
   * Adds a NumericDataSource to this DataRecorder with the specified name.
   * The NumericDataSource generates the data to be recorded.
   *
   * @param name the name of the data (e.g. Number of Agents)
   * @param s the source of the data
   */
  public void addNumericDataSource(String name, NumericDataSource s) {
    record.addNumericDataSource(name, s);
  }

  /**
   * Creates a DataSource using the specified object and the specified method
   * name. Whenever DataRecorder.record is called, the method of this name is
   * called on this object and the result is recorded.<p>
   *
   * <b>Note</b> the specified method must return an Object, that is, anything
   * but void or a primitive. The method must be public.
   *
   * @param name the name for this data source. This corresponds to the column
   * name when this object is written out to the file
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   */
  public void createObjectDataSource(String name, Object feedFrom, String methodName) {
    record.createObjectDataSource(name, feedFrom, methodName);
  }

  /**
   * Creates a NumericDataSource using the specified object and the
   * specified method name. Whenever DataRecorder.record is called,
   * the method of this name is called on this object and the result
   * is recorded.<p>
   *
   * <b>Note</b> the specified method must explicity return a double, that is,
   * the return value of the method signature must be a double (e.g.
   * public double ...). The method must be public.
   *
   * @param name the name for this data source. This corresponds to the column
   * name when this object is written out to the file
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point. A value of -1 will record all the digits.
   */
  public void createNumericDataSource(String name, Object feedFrom,
                                      String methodName, int maxIntegerDigits,
                                      int maxFractionDigits) {
    record.createNumericDataSource(name,feedFrom, methodName, maxIntegerDigits, maxFractionDigits);
  }

  /**
   * Creates a NumericDataSource using the specified object and the
   * specified method name. Whenever DataRecorder.record is called,
   * the method of this name is called on this object and the result
   * is recorded.<p>
   *
   * <b>Note</b> the specified method must explicity return a double, that is,
   * the return value of the method signature must be a double (e.g.
   * public double ...). The method must be public.
   *
   * @param name the name for this data source. This corresponds to the column
   * name when this object is written out to the file
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   */
  public void createNumericDataSource(String name, Object feedFrom,
                                      String methodName) {
    record.createNumericDataSource(name, feedFrom, methodName);
  }

  /**
   * Adds a DataSource to this DataRecorder with the specified name. The
   * DataSource generates the data that is to be recorded.
   *
   * @param name the name of the data to be recorded
   * @param s the DataSource for the data to be recorded
   */
  public void addObjectDataSource(String name, DataSource s) {
    record.addObjectDataSource(name,s);
  }

  /**
   * Creates an average data source from a specified list using a method with
   * the specified name. Whenever DataRecorder.record is called, this method is
   * called on all the objects in the list, the resulting values are
   * averaged, and this average is recorded.
   *
   * @param name the name for this data source. This corresponds to the column
   * name when this object is written out to the file
   * @param feedFrom the list of object on which to call the method
   * @param methodName the name of the method to call. This method should return
   * some sub class of java.lang.Number
   */
  public void createAverageDataSource(String name, ArrayList feedFrom,
                                      String methodName) {
    record.createAverageDataSource(name,feedFrom,methodName);
  }

  /**
   * Creates an average data source from a specified list using a method with
   * the specified name. Whenever DataRecorder.record is called, this method is
   * called on all the objects in the list, the resulting values are
   * averaged, and this average is recorded.
   *
   * @param name the name for this data source. This corresponds to the column
   * name when this object is written out to the file
   * @param feedFrom the list of object on which to call the method
   * @param methodName the name of the method to call. This method should return
   * some sub class of java.lang.Number
   */
  public void createAverageDataSource(String name, ArrayList feedFrom,
                                      String methodName, int maxIntegerDigits,
                                      int maxFractionDigits) {
    record.createAverageDataSource(name,feedFrom,methodName, maxIntegerDigits, maxFractionDigits);
  }

  /**
   * Records the data for the current tick in tabular format. Each tick is
   * a row, and each column is the name of the data source.
   */
  public void record() {
    record.record();
  }

  /**
   * Writes the recorded data out to a file in tabular format. This also
   * does a flush on the data itself (i.e. the data is no longer stored by
   * repast and exists only in the file). Identical to writeToFile().
   */
  public void write() {
    record.write();
  }

  public void writeToFile(){
    record.writeToFile();
  }

  /**
   * Writes any ending matter to the file. Used internally during a batch run
   * to write the ending time of the entire batch. A model would not
   * typically call this method.
   */

  public void writeEnd() {
    record.writeEnd();
  }

  // listener interface
  public void simEventPerformed(SimEvent evt) {
    record.simEventPerformed(evt);
  }

  /**
   * Sets the column delimiter. Data is written out in tabular format
   * where the columns are separated by the specified delimiter.
   *
   * @param delimeter the new delimiter
   */
  public void setDelimeter(String delimeter) {
    record.setDelimeter(delimeter);
  }
}
