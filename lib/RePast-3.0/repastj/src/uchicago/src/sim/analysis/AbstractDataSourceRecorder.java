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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Vector;

import uchicago.src.codegen.GeneratorException;
import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.ByteCodeBuilder;
import uchicago.src.sim.util.SimUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 2, 2003
 * Time: 1:29:42 PM
 * To change this template use Options | File Templates.
 */
public abstract class AbstractDataSourceRecorder implements DataSourceRecorder {
  protected SimDataNew data;
  protected Vector sources = new Vector(7);
  protected SimModel model;
  protected DataFileHeader dfHeader;

  public void initData(SimModel model, String headerComment){
    this.model = model;
    dfHeader = new DataFileHeader(model);
    if (headerComment != null) dfHeader.addHeaderComment(headerComment);
    String modelHeader = dfHeader.getFileHeader();

    data = new SimDataNew(modelHeader);

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
      int maxIntegerDigits, int maxFractionDigits)
  {
    data.addToHeader(name);
    DataSource ds = new NumberFormattingDataSource(s, maxIntegerDigits,
                                                  maxFractionDigits);
    sources.add(ds);

  }


  /**
   * Adds a NumericDataSource to this DataRecorder with the specified name.
   * The NumericDataSource generates the data to be recorded.
   *
   * @param name the name of the data (e.g. Number of Agents)
   * @param s the source of the data
   */
  public void addNumericDataSource(String name, final NumericDataSource s) {
    data.addToHeader(name);
    sources.add(new DataSource() {
      public Object execute() {
        return String.valueOf(s.execute());
      }
    });
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
    //DataSource s = new ObjectDataSource(name, feedFrom, methodName);
    DataSource s = null;
    try {
      s = ByteCodeBuilder.generateDataSource(feedFrom, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating ObjectDataSource: " + ex.getMessage(), ex);
      System.exit(0);
    }
    data.addToHeader(name);
    //namedData.put(name, new Pair(new Double(0D), new Double(0D)));
    sources.add(s);
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
				      int maxFractionDigits)
  {
    NumericDataSource s = null;
    try {
      s = ByteCodeBuilder.generateNumericDataSource(feedFrom, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating NumericDataSource: " +
			     ex.getMessage(), ex);
      System.exit(0);
    }
    if(this.data ==null){
      System.out.println("no data");
      System.exit(0);
    }

    this.data.addToHeader(name);
    DataSource ds = new NumberFormattingDataSource(s, maxIntegerDigits,
                                                    maxFractionDigits);
    sources.add(ds);
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
				      String methodName)
  {
    NumericDataSource s = null;
    try {
      s = ByteCodeBuilder.generateNumericDataSource(feedFrom, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating NumericDataSource: " +
			     ex.getMessage(), ex);
      System.exit(0);
    }

    this.addNumericDataSource(name, s);

  }

  /**
   * Adds a DataSource to this DataRecorder with the specified name. The
   * DataSource generates the data that is to be recorded.
   *
   * @param name the name of the data to be recorded
   * @param s the DataSource for the data to be recorded
   */
  public void addObjectDataSource(String name, DataSource s){
    data.addToHeader(name);
    sources.add(s);
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
    DataSource s = new AverageDataSource(name, feedFrom, methodName);
    data.addToHeader(name);
    //namedData.put(name, new Pair(new Double(0D), new Double(0D)));
    sources.add(s);
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
                                      int maxFractionDigits)
  {
    AverageDataSource s = new AverageDataSource(name, feedFrom, methodName);
    data.addToHeader(name);
    DataSource ds = new AverageFormattingDataSource(s, maxIntegerDigits,
                                                    maxFractionDigits);
    sources.add(ds);
  }

  public abstract void record();

  public abstract void writeToFile();

  public abstract void write();

  public abstract void writeEnd();

  // listener interface
  public void simEventPerformed(SimEvent evt) {
    if (evt.getId() == SimEvent.END_EVENT)
      writeEnd();
  }

  /**
   * Sets the tabular data delimiter to the specified string. The default is
   * ",".
   *
   * @param delim the new delimiter.
   */
  public void setDelimeter(String delim) {
    data.setDelimiter(delim);
  }

  abstract class FormattingDataSource implements DataSource {

    protected DecimalFormat format =
      (DecimalFormat)NumberFormat.getNumberInstance();

    public FormattingDataSource(int maxInts, int maxFrac) {
      boolean setMax = false;
      format.setMaximumFractionDigits(340);
      format.setGroupingUsed(false);
      StringBuffer b = new StringBuffer();
      if (maxInts > -1 ) {
        format.setMaximumIntegerDigits(maxInts);
        setMax = true;
      } else {
        b.append("0");
      }

      b.append(format.getDecimalFormatSymbols().getDecimalSeparator());
      if (maxFrac > -1) {
        for (int i = 0; i < maxFrac; i++) b.append("0");
        format.applyPattern(b.toString());
        if (setMax)format.setMaximumIntegerDigits(maxInts);
      }
    }

    public abstract Object execute();
  }

  class AverageFormattingDataSource extends FormattingDataSource {

    private AverageDataSource dataSource;

    public AverageFormattingDataSource(AverageDataSource ds, int maxInts,
              int maxFrac)
    {
      super(maxInts, maxFrac);
      dataSource = ds;
    }

    public Object execute() {
      return format.format(dataSource.execute());
    }
  }

  class NumberFormattingDataSource extends FormattingDataSource {

    private NumericDataSource dataSource;

    public NumberFormattingDataSource(NumericDataSource ds, int maxInts,
              int maxFrac)
    {
      super(maxInts, maxFrac);
      dataSource = ds;
    }

    public Object execute() {
      return format.format(dataSource.execute());
    }
  }


}
