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
//import java.util.ArrayList;

/**
 * A collection class used by the Statistic classes to
 * hold their data. Implements the jclass.chart.Chartable interface
 * in order that the data can be charted by a JClass chart.
 * The data here is in tabular form (a Vector of Vectors) as x,y pairs.
 * The first row holds the x values, while the next holds the y values that
 * correspond to these x values. Any additional rows are additional y values
 * that also correspond to the x values. Each x, y row pair is a series.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class OpenArrayData  {

  private Vector labels = new Vector();
  private Vector table = new Vector(13);
  private Vector xVals = new Vector(213);

  public String[] ptLabels = {};

  /**
   * Constructs this ArrayData object
   */
  public OpenArrayData() {
    table.addElement(xVals);
  }

  /**
   * Adds an x value
   *
   * @param x the x value to add
   */
  public void addX(double x) {
    xVals.add(new Double(x));
  }

  /**
   * Adds a Y value to the specified series
   *
   * @param the y value to add
   * @param series the series to add the y value to.
   */

  public void addY(double y, int series) {
    Vector v;
    if (table.size() < series + 2) {
      v = new Vector(213);
      table.add(series + 1, v);
    } else {
      v = (Vector)table.get(series + 1);
    }
    v.add(new Double(y));

  }

  /**
   * Sets a Vector of y values as a series
   *
   * @param yVals the y values to add
   * @param series the series of these yVals
   */

  public void setYValues(Vector yVals, int series) {
    table.setElementAt(yVals, series + 1);
  }

  /**
   * Adds a series with the specified name to the data table.
   *
   * @param label the label for the series
   */
  public void addSeries(String label) {
    labels.addElement(label);
    table.add(new Vector(213));
  }

  /**
   * Sets the point labels (labels for each x value)
   *
   * @param labels the labels to set
   */

  public void setPointLabels(String[] labels) {
    ptLabels = labels;
  }

  /**
   * Gets the names (labels) of the series
   *
   * @return the series names.
   */

  public Vector getSeriesLabels() {
    return labels;
  }

  /**
   * Gets the underlying table (Vector of Vectors).
   *
   * @return the underlying table (Vector of Vectors).
   */
  Vector getDataTable() {
    return table;
  }

  /**
   * Gets the current column size.
   */
  public int getColSize() {
    return xVals.size();
  }

  public Object getDataItem(int row, int col) {
    return ((Vector)table.elementAt(row)).get(col);
  }

  public String getName() {
    return "";
  }

  public int getNumRows() {
    return table.size();
  }

  public String[] getPointLabels() {
    return ptLabels;
  }

  public Vector getRow(int row) {
    return (Vector)table.elementAt(row);
  }

  public String getSeriesLabel(int parm1) {
    return (String)labels.elementAt(parm1);
  }

  public String getSeriesName(int parm1) {
    return null;
  }
}
