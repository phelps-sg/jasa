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

import hep.aida.IAxis;
import hep.aida.ref.Histogram1D;

import java.text.NumberFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import uchicago.src.codegen.GeneratorException;
import uchicago.src.sim.util.ByteCodeBuilder;
import uchicago.src.sim.util.SimUtilities;

/** An item to be displayed in a histogram. A HistogramItem
 * iterates over a list calling the specified method on each
 * object in the list. This method must return a value that can be
 * cast into a double (i.e. float, int, double, short, etc.). These values
 * are then distributed across the bins according to the number of bins,
 * the lower bound, and the maximum value. For example, given 2 bins,
 * a lower bound of 0 and a maximum value of 4. The first bin will contain
 * all the values from 0 up to but not including 2, and the final bin will
 * contain all the values from 2 up to <em>and</em> including 4. The displayed
 * bin value (i.e. the height of the bar in the chart) is the number of value
 * the fall within this bin. This class is created through the Histogram
 * or HistogramStatistic classes and under normal circumstance should not
 * be created by a user.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 * @see Histogram
 * @see HistogramStatistic
 */

public class HistogramItem {

  protected List list;
  protected int numBins;
  protected long lowerBound;
  protected String[] labels;

  protected BinDataSource source = null;

  private NumberFormat formatter = NumberFormat.getInstance();

  /**
   * Constructs a HistogramItem with the specified list of objects,
   * the specified method to call on these objects, the specified number of
   * bins, and the specified lower bound.
   *
   * @param list the list of objects on which to call the method
   * @param methodName the name of the method to call on the objects
   * @param numBins the number of bins in the histogram
   * @param lowerBound the lower bound of the histogram
   */

  public HistogramItem (List list, String methodName, int numBins,
                        long lowerBound) {
    try {
      source = ByteCodeBuilder.generateNoTargetBinDataSource(list.listIterator().next(),
                                                             methodName);
      this.list = list;
    } catch (GeneratorException ex) {
      SimUtilities.showError ("Error creating HistorgramItem: ", ex);
      ex.printStackTrace ();
      System.exit (0);
    }
    this.numBins = numBins;
    this.lowerBound = lowerBound;
    formatter.setMaximumFractionDigits(340);

  }

  /**
   * Constructs a HistogramItem with the specified list of objects,
   * the specified method to call on these objects, the specified number of
   * bins, and the specified lower bound.
   *
   * @param list the list of objects on which to call the method
   * @param methodName the name of the method to call on the objects
   * @param numBins the number of bins in the histogram
   * @param lowerBound the lower bound of the histogram
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   */

  public HistogramItem (List list, String methodName, int numBins,
                        long lowerBound, int maxIntegerDigits,
                        int maxFractionDigits)
  {
    try {
      source = ByteCodeBuilder.generateNoTargetBinDataSource(list.listIterator().next(),
                                                             methodName);
      this.list = list;
    } catch (GeneratorException ex) {
      SimUtilities.showError ("Error creating HistorgramItem: ", ex);
      ex.printStackTrace ();
      System.exit (0);
    }
    this.numBins = numBins;
    this.lowerBound = lowerBound;
    formatter.setMaximumFractionDigits(340);
    if (maxIntegerDigits != -1) formatter.setMaximumIntegerDigits(maxIntegerDigits);
    if (maxFractionDigits != -1) formatter.setMaximumFractionDigits(maxFractionDigits);
  }


  /**
   * Constructs a HistogramItem whose bins are constructed from the specified
   * list of objects, the specified BinDataSource, number of bins, and the
   * specified lower bound.
   *
   * @param list the list of objects on which to call the method
   * @param source the BinDataSource that gets the bin values from the object
   * in the list
   * @param numBins the number of bins in the histogram
   * @param lowerBound the lower bound of the histogram
   */
  public HistogramItem (List list, BinDataSource source, int numBins, long lowerBound) {
    this.source = source;
    this.numBins = numBins;
    this.list = list;
    this.lowerBound = lowerBound;
    formatter.setMaximumFractionDigits(340);
  }

  /**
   * Constructs a HistogramItem whose bins are constructed from the specified
   * list of objects, the specified BinDataSource, number of bins, and the
   * specified lower bound.
   *
   * @param list the list of objects on which to call the method
   * @param source the BinDataSource that gets the bin values from the object
   * in the list
   * @param numBins the number of bins in the histogram
   * @param lowerBound the lower bound of the histogram
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   */
  public HistogramItem (List list, BinDataSource source, int numBins,
                        long lowerBound, int maxIntegerDigits,
                        int maxFractionDigits)
  {
    this.source = source;
    this.numBins = numBins;
    this.list = list;
    this.lowerBound = lowerBound;
    formatter.setMaximumFractionDigits(340);
    if (maxIntegerDigits != -1) formatter.setMaximumIntegerDigits(maxIntegerDigits);
    if (maxFractionDigits != -1) formatter.setMaximumFractionDigits(maxFractionDigits);
  }

  /**
   * Gets a Vector of the bin values (ie how many items are in each bin).
   *
   * @return a Vector of the bin values.
   */

  public Vector getBinValues () {
    Vector retVal = new Vector(numBins);
    double[] ySums = new double[numBins];
    for (int k = 0; k < numBins; k++) {
      ySums[k] = 0.0;
    }

    double[] yVals = new double[list.size()];
    double max = 0;
    ListIterator li = list.listIterator();
    int i = 0;
    while (li.hasNext()) {
      double yVal = source.getBinValue(li.next());
      if (i == 0) {
        max = yVal;
      } else if (yVal > max) {
        max = yVal;
      }
      yVals[i++] = yVal;
    }

    Histogram1D hist = new Histogram1D("foo", numBins, lowerBound, max);

    for (int j = 0; j < yVals.length; j++) {
      hist.fill(yVals[j]);
    }

    IAxis axis = hist.xAxis();
    labels = new String[numBins];
    for (int j = 0; j < axis.bins(); j++) {
      labels[j] = formatter.format(axis.binLowerEdge(j)) + "-" +
                  formatter.format(axis.binUpperEdge(j));
      retVal.add(new Double(hist.binEntries(j)));
    }

    // need to replace the last bin height with one that includes the overflow
    // because Histogram1D calculates the last bin as exclusive of the max
    // value.
    int lastBin = axis.bins() - 1;
    double val = hist.binEntries(lastBin) + hist.binEntries(Histogram1D.OVERFLOW);
    retVal.add(lastBin, new Double(val));


    return retVal;
  }


  /**
   * Gets the bin labels. The bin labels are calculated each time getBinValues()
   * is called.
   *
   * @return the bin labels
   * @see #getBinValues()
   */

  public String[] getBinLabels () {
    return labels;
  }
}
