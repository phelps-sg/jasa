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

import java.util.List;
import java.util.Vector;

/**
 * The Statistics class for histogram data. Operates as the backend for a
 * Histogram, manipulating the HistogramItems. Can be created without a
 * Histogram class to generate histogram data without displaying such.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see Histogram
 * @see HistogramItem
 */
public class OpenHistogramStat extends OpenStats {

  private Vector items = new Vector();
  private int numBins = 0;
  private long lowerBound;
  //private double interval;

  /**
   * Constructs a HistogramStatistic with the specified number of bins and the
   * specified lower bound.
   *
   * @param numBins the number of bins
   * @param lowerBound the lower bound
   */
  public OpenHistogramStat(int numBins, long lowerBound) {
    super(null);
    this.numBins = numBins;
    this.lowerBound = lowerBound;
    for (int i = 0; i < numBins; i++) {
      data.addX(i);
    }
  }

  /**
   * Creates a HistogramItem. The HistogramItem
   * iterates over the specified list calling the specified method on each
   * object in the list. This method must return a value that can be
   * cast into a double (i.e. float, int, double, short, etc.). These values
   * are then distributed across the bins according to the number of bins,
   * the lower bound, and the maximum value. For example, given 2 bins,
   * a lower bound of 0 and a maximum value of 4. The first bin will contain
   * all the values from 0 up to but not including 2, and the final bin will
   * contain all the values from 2 up to <em>and</em> including 4. The displayed
   * bin value (i.e. the height of the bar in the chart) is the number of values
   * that fall within this bin.<p>
   *
   * @param name the name of this item
   * @param list the list of object on which the specified method is called
   * @param methodName the name of the method to call on the objects. Should
   * return a Number value.
   * @see HistogramItem
   */

  public void createHistogramItem(String name, List list, String methodName) {
    HistogramItem item = new HistogramItem(list, methodName, numBins, lowerBound);
    data.addSeries(name);
    items.addElement(item);
  }

  /**
   * Creates a HistogramItem. The HistogramItem
   * iterates over the specified list calling the specified method on each
   * object in the list. This method must return a value that can be
   * cast into a double (i.e. float, int, double, short, etc.). These values
   * are then distributed across the bins according to the number of bins,
   * the lower bound, and the maximum value. For example, given 2 bins,
   * a lower bound of 0 and a maximum value of 4. The first bin will contain
   * all the values from 0 up to but not including 2, and the final bin will
   * contain all the values from 2 up to <em>and</em> including 4. The displayed
   * bin value (i.e. the height of the bar in the chart) is the number of values
   * that fall within this bin.<p>
   *
   * @param name the name of this item
   * @param list the list of object on which the specified method is called
   * @param methodName the name of the method to call on the objects. Should
   * return a Number value.
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @see HistogramItem
   */

  public void createHistogramItem(String name, List list, String methodName,
                                  int maxIntegerDigits, int maxFractionDigits) {
    HistogramItem item = new HistogramItem(list, methodName, numBins,
                                           lowerBound, maxIntegerDigits,
                                           maxFractionDigits);
    data.addSeries(name);
    items.addElement(item);
  }

  /**
   * Creates a HistogramItem with specified name, list and BinDataSource.
   * The HistogramItem iterates over the specified list passing each Object
   * in the list as argument to the getBinValue method of the BinDataSource.
   * This getBinValue method returns a double. All these doubles
   * are then distributed across the bins according to the number of bins,
   * the lower bound, and the maximum value. For example, given 2 bins,
   * a lower bound of 0 and a maximum value of 4. The first bin will contain
   * all the values from 0 up to but not including 2, and the final bin will
   * contain all the values from 2 up to <em>and</em> including 4. The displayed
   * bin value (i.e. the height of the bar in the chart) is the number of values
   * that fall within this bin.<p>
   *
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param source the BinDataSource used to get the data from the objects
   * in the list.
   */
  public void createHistogramItem(String name, List list, BinDataSource source) {
    HistogramItem item = new HistogramItem(list, source, numBins, lowerBound);
    data.addSeries(name);
    items.addElement(item);
  }

  /**
   * Creates a HistogramItem with specified name, list and BinDataSource.
   * The HistogramItem iterates over the specified list passing each Object
   * in the list as argument to the getBinValue method of the BinDataSource.
   * This getBinValue method returns a double. All these doubles
   * are then distributed across the bins according to the number of bins,
   * the lower bound, and the maximum value. For example, given 2 bins,
   * a lower bound of 0 and a maximum value of 4. The first bin will contain
   * all the values from 0 up to but not including 2, and the final bin will
   * contain all the values from 2 up to <em>and</em> including 4. The displayed
   * bin value (i.e. the height of the bar in the chart) is the number of values
   * that fall within this bin.<p>
   *
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param source the BinDataSource used to get the data from the objects
   * in the list.
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   */
  public void createHistogramItem(String name, List list, BinDataSource source,
                                  int maxIntegerDigits, int maxFractionDigits) {
    HistogramItem item = new HistogramItem(list, source, numBins, lowerBound,
                                           maxIntegerDigits, maxFractionDigits);
    data.addSeries(name);
    items.addElement(item);
  }

  /**
   * Calculates a new histogram over the data supplied by a HistogramItem
   */
  public void record() {
    Vector yVals;
    for (int i = 0; i < items.size(); i++) {
      HistogramItem item = (HistogramItem) items.elementAt(i);
      yVals = item.getBinValues();
      data.setYValues(yVals, i);
      data.setPointLabels(item.getBinLabels());
    }
  }

  /**
   * Sets the histogram labels (the x - axis legend).
   */
  public void setPointLabels(String[] labels) {
    data.setPointLabels(labels);
  }

  /**
   * Write the histogram data to a file. This method is not yet implemented.
   */
  public void writeToFile() {
    throw new UnsupportedOperationException("This method is not yet implemented");

  }
}
