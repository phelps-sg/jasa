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

import java.awt.Color;

/**
 * Data source for a Sequence created via the gui chart creator. This describes
 * the sequence that will displayed in an OpenSequenceGraph.
 *
 * @version $Revision$ $Date$
 */

public class SequenceSource implements GuiChartDataSource {
  private String name = "A Sequence", methodName, fieldName;
  private Object feedFrom;
  private String feedFromName, sourceName;
  private int markType = 0;
  private Color color = Color.blue;

  /**
   * Creates a SequenceSource whose data is feed from the specified object.
   *
   * @param feedFrom the object from which the data for this source comes
   */

  public SequenceSource(Object feedFrom) {
    this.feedFrom = feedFrom;
    feedFromName = feedFrom.getClass().getName();
    int index = feedFromName.lastIndexOf('.');
    if (index != -1) feedFromName = feedFromName.substring(index + 1);
  }

  /**
   * Returns the field name is the data for this SequenceSource is a field.
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Sets the field name of the field from which this SequenceSource gets
   * its data. This can be null.
   *
   * @param fieldName the name of the field that provides data for this
   * SequenceSource
   */
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
    sourceName = feedFromName + "." + fieldName;
  }

  /**
   * Returns the name of this SequenceSource. This name will be the legend
   * for this sequence.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this SequenceSource. This name will be the legend
   * for this sequence.
   *
   * @param name the name of this SequenceSource
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the name of the method that is the source of  the data for
   * this SequenceSource.
   */
  public String getMethodName() {
    return methodName;
  }

  /**
   * Sets the name of the method that is the source of the data for
   * this SequenceSource
   *
   * @param methodName the name of the method that is the source of the
   * data for this SequenceSource
   */
  public void setMethodName(String methodName) {
    this.methodName = methodName;
    sourceName = feedFromName + "." + methodName;
  }

  /**
   * Returns the Object that provides the data (via a method or a field)
   * for this SequenceSource.
   */
  public Object getFeedFrom() {
    return feedFrom;
  }

  /**
   * Sets the Object that provides the data (via a method or a field)
   * for this SequenceSource.
   *
   * @param feedFrom the object that provides the data (via a method or a field)
   * for this SequenceSource
   */
  public void setFeedFrom(Object feedFrom) {
    this.feedFrom = feedFrom;
  }

  /**
   * Returns the mark style (the shape of the point) for this SequenceSource.
   * The appropriate types are: <ul>
   * FILLED_CIRCLE = 0;
   * CROSS = 1;
   * SQUARE = 2;
   * FILLED_TRIANGLE = 3;
   * DIAMOND = 4;
   * CIRCLE = 5;
   * PLUS_SIGN = 6;
   * FILLED_SQUARE = 7;
   * TRIANGLE = 8;
   * FILLED_DIAMOND = 9;
   * </ul>
   */
  public int getMarkStyle() {
    return markType;
  }

  /**
   * Returns the mark style (the shape of the point) for this SequenceSource.
   * The appropriate types are: <ul>
   * FILLED_CIRCLE = 0;
   * CROSS = 1;
   * SQUARE = 2;
   * FILLED_TRIANGLE = 3;
   * DIAMOND = 4;
   * CIRCLE = 5;
   * PLUS_SIGN = 6;
   * FILLED_SQUARE = 7;
   * TRIANGLE = 8;
   * FILLED_DIAMOND = 9;
   * </ul>
   * @param markType the mark style
   */
  public void setMarkStyle(int markType) {
    this.markType = markType;
  }

  /**
   * Returns the Color of the sequence created by this SequenceSource.
   * @return
   */
  public Color getColor() {
    return color;
  }

  /**
   * Sets the Color of the sequence created by this SequenceSource.
   *
   * @param color the color of the sequence
   */
  public void setColor(Color color) {
    this.color = color;
  }

  // Implements  GuiChartDataSource interface
  public String getFullName() {
    return sourceName;
  }

  public String getShortName() {
    if (methodName == null) return fieldName;
    else return methodName;
  }

  public GuiChartDataSource copy() {
    SequenceSource source = new SequenceSource(feedFrom);
    source.methodName = methodName;
    source.name = name;
    source.fieldName = fieldName;
    source.feedFromName = feedFromName;
    source.sourceName = sourceName;
    source.markType = markType;
    source.color = color;
    return source;
  }

  public String toXML() {
    StringBuffer b = new StringBuffer("<DataSource name=\"");
    b.append(name);
    b.append("\" color=\"");
    b.append(color.getRGB());
    b.append("\" methodName=\"");
    b.append(methodName);
    b.append("\" markStyle=\"");
    b.append(markType);
    b.append("\" />");

    return b.toString();
  }
}
