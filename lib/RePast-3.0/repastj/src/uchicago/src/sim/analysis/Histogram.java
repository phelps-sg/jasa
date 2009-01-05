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
import hep.aida.ref.Converter;
import hep.aida.ref.Histogram1D;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import uchicago.src.sim.analysis.plot.OpenGraph;
import uchicago.src.sim.engine.SimModel;
import cern.colt.list.DoubleArrayList;

/**
 * A histogram plot allowing the user to histogram data from a list of
 * objects. Histogram works on a model / view pattern where the model
 * is the hep.aida.ref.Histogram1D from the colt package. This differs from
 * OpenHistogram in that the xaxis interval is not dynamic, allowing for
 * empty bins. The xaxis interval can be fixed or variable. Note that this
 * Histogram only plots a single dataset.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 *
 * @see hep.aida.ref.Histogram1D
 */

public class Histogram extends OpenGraph {

  /**
   * The actual Histogram model of which this is the view.
   */
  protected Histogram1D histogram;

  protected List histList;

  /**
   * The data source for this Histogram
   */
  protected BinDataSource dataSource;

  /**
   * The number of bins in this Histogram
   */
  protected int numBins;
 
  protected boolean xTickDrawn = false;
  protected boolean showStats = true;
  protected NumberFormat format = NumberFormat.getInstance();

  /**
   * Constructs a histogram with a fixed xaxis interval. The range of the
   * histogram is specified by min and max and the number of bins by
   * numBins. So for example <code>Histogram("sample", 2, 0.0, 20.0)
   * </code> contains two in-range bins and one bin for underflow and one
   * for overflow. The bin boundaries are [Double.NEGATIVE_INFINITY,0.0),
   * [0.0, 10.0), [10.0, 20.0), [20.0, Double.POSITIVE_INFINITY], the first
   * and last bins being for underflow and overflow. The height of the
   * overflow and underflow bins can be seen by setting setStatsVisible
   * to true.
   *
   * @param title the title of the histogram
   * @param numBins the number of bins in the histogram
   * @param min the minimum value of the histogram. Anything lower than
   * this value is put in the underflow bin.
   * @param max the maximum value of the histogram. Anything greater than
   * or equal to this value is put in the overflow bin.
   */
  public Histogram(String title, int numBins, double min, double max) {
    this(title, numBins, min, max, null);
  }

  /**
   * Constructs a histogram with a fixed xaxis interval. The range of the
   * histogram is specified by min and max and the number of bins by
   * numBins. So for example <code>Histogram("sample", 2, 0.0, 20.0, aModel)
   * </code> contains two in-range bins and one bin for underflow and one
   * for overflow. The bin boundaries are [Double.NEGATIVE_INFINITY,0.0),
   * [0.0, 10.0), [10.0, 20.0), [20.0, Double.POSITIVE_INFINITY], the first
   * and last bins being for underflow and overflow. The height of the
   * overflow and underflow bins can be seen by setting setStatsVisible
   * to true.
   *
   * @param title the title of the histogram
   * @param numBins the number of bins in the histogram
   * @param min the minimum value of the histogram. Anything lower than
   * this value is put in the underflow bin.
   * @param max the maximum value of the histogram. Anything greater than
   * or equal to this value is put in the overflow bin.
   * @param model a reference to the model associated with this histogram.
   */
  public Histogram(String title, int numBins, double min, double max,
                   SimModel model) {
    super(title);
    this.model = model;
    this.setXRange(min, max);
    histogram = new Histogram1D(title, numBins, min, max);
    this.numBins = numBins;
    double interval = (max - min) / numBins;
    this.setBars(interval / 2.0, 0);
  }

  /**
   * Constructs a histogram with a variable xaxis interval. The range of the
   * Histogram is specified by the xedges double array. For example
   * <code>Histogram("sample", new double[] { 10.0, 20.0, 22.0}, aModel)
   * </code> contains two in-range bins and one bin for underflow and one
   * bin for overflow. Its boundaries are [Double.NEGATIVE_INFINITY,10.0),
   * [10.0, 20.0), [20.0, 22.0), [22.0, Double.POSITIVE_INFINITY],  the first
   * and last bins being for underflow and overflow. The height of the
   * overflow and underflow bins can be seen by setting setStatsVisible
   * to true.<p>
   *
   * Note that this doesn't display as well as it might. The scale of
   * plot will show the appropriate amount of distance between an
   * xaxis value of 0 and one of 10.
   *
   * @param title the title of the histogram
   * @param xedges the xaxis bin boundaries
   * @param model a reference to the model associated with this histogram.
   */

  public Histogram(String title, double[] xedges, SimModel model) {

    super(title);
    this.model = model;
    this.setXRange(xedges[0], xedges[xedges.length - 1]);
    histogram = new Histogram1D(title, xedges);

    IAxis xaxis = histogram.xAxis();
    this.numBins = xaxis.bins();

    double minWidth = xaxis.upperEdge() - xaxis.lowerEdge();
    for (int i = 0; i < numBins; i++) {
      double width = xaxis.binWidth(i);
      if (width < minWidth) minWidth = width;
    }

    this.setBars(minWidth / 2.0, 0);
  }

  /**
   * Constructs a histogram with a variable xaxis interval. The range of the
   * Histogram is specified by the xedges double array. For example
   * <code>Histogram("sample", new double[] { 10.0, 20.0, 22.0})
   * </code> contains two in-range bins and one bin for underflow and one
   * bin for overflow. Its boundaries are [Double.NEGATIVE_INFINITY,10.0),
   * [10.0, 20.0), [20.0, 22.0), [22.0, Double.POSITIVE_INFINITY],  the first
   * and last bins being for underflow and overflow. The height of the
   * overflow and underflow bins can be seen by setting setStatsVisible
   * to true.
   *
   * Note that this doesn't display as well as it might. The scale of
   * plot will show the appropriate amount of distance between an
   * xaxis value of 0 and one of 10.
   *
   * @param title the title of the histogram
   * @param xedges the xaxis bin boundaries
   */
  public Histogram(String title, double[] xedges) {
    super(title);
    this.setXRange(xedges[0], xedges[xedges.length - 1]);
    histogram = new Histogram1D(title, xedges);

    IAxis xaxis = histogram.xAxis();
    this.numBins = xaxis.bins();
    double minWidth = xaxis.upperEdge() - xaxis.lowerEdge();
    for (int i = 0; i < numBins; i++) {
      double width = xaxis.binWidth(i);
      if (width < minWidth) minWidth = width;
    }
    this.setBars(minWidth / 2.0, 0);

  }

  /**
   * Sets the plot bar width in xaxis coordinates.
   *
   * @param width the width in xaxis coordinates.
   */
  public void setBarWidth(double width) {
    setBars(width, 0);
  }


  /**
   * Sets the statistics display on the plot to visible. The statistics
   * display will display the total number of entries, the bin height
   * of the overflow and underflow bin, the mean of the whole histogram
   * as calculated on filling time, and the rms of the whole histogram
   * as calculated on filling time. These stats are retrieved from method
   * calls on a help.aida.ref.Histogram1D in the colt library. See that
   * for more info.
   *
   * @param isVisible if true the stats will be visible, if not then they
   * won't be visible
   */

  public void setStatsVisible(boolean isVisible) {
    showStats = isVisible;
  }

  /**
   * Returns whether the stats are visible or not.
   */
  public boolean isStatsVisible() {
    return showStats;
  }

  /**
   * Returns the Histogram1D object of which this Histogram is a view. See
   * the help.aida.ref.Histogram1D in the colt library for more info.
   */
  public Histogram1D getHistogram() {
    return histogram;
  }

  /**
   * Creates an item to be histogramed by this Histogram. The HistogramItem
   * iterates over the specified list passing each Object
   * in the list as argument to the getBinValue method of the BinDataSource.
   * This getBinValue method returns a double. All these doubles
   * are then distributed across the bins accordingly.
   *
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param source the BinDataSource used to get the data from the objects
   * in the list.
   */
  public void createHistogramItem(String name, List list,
                                  BinDataSource source) {
    histList = list;
    dataSource = source;
    plot.addLegend(0, name);
    format.setMaximumFractionDigits(340);
  }

  /**
   * Creates an item to be histogramed by this Histogram. The HistogramItem
   * iterates over the specified list passing each Object
   * in the list as argument to the getBinValue method of the BinDataSource.
   * This getBinValue method returns a double. All these doubles
   * are then distributed across the bins accordingly.
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
  public void createHistogramItem(String name, List list,
                                  BinDataSource source, int maxIntegerDigits,
                                  int maxFractionDigits)
  {
    histList = list;
    dataSource = source;
    plot.addLegend(0, name);
    format.setMaximumFractionDigits(340);
    if (maxIntegerDigits != -1) format.setMaximumIntegerDigits(maxIntegerDigits);
    if (maxFractionDigits != -1) format.setMaximumFractionDigits(maxFractionDigits);
  }

  /**
   * Creates an item to be histogrammed by this Histogram. The HistogramItem
   * iterates over the specified list passing each Object
   * in the list as an argument to the name method called on the specified
   * object. This method returns a double and all these doubles
   * are then distributed across the bins accordingly.
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param bdsTarget the target of the named method
   * @param bdsMethodName the name of the method to call on the target
   *
   */
  public void createHistogramItem(String name, List list, Object bdsTarget,
                                  String bdsMethodName) {
    BinDataSource bds = createBinDataSource(bdsTarget, bdsMethodName);
    createHistogramItem(name, list, bds);
  }

  /**
   * Creates an item to be histogrammed by this Histogram. The HistogramItem
   * iterates over the specified list passing each Object
   * in the list as an argument to the name method called on the specified
   * object. This method returns a double and all these doubles
   * are then distributed across the bins accordingly.
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param bdsTarget the target of the named method
   * @param bdsMethodName the name of the method to call on the target
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   */
  public void createHistogramItem(String name, List list, Object bdsTarget,
                                  String bdsMethodName,  int maxIntegerDigits,
                                  int maxFractionDigits)
  {
    BinDataSource bds = createBinDataSource(bdsTarget, bdsMethodName);
    createHistogramItem(name, list, bds);
    format.setMaximumFractionDigits(340);
    if (maxIntegerDigits != -1) format.setMaximumIntegerDigits(maxIntegerDigits);
    if (maxFractionDigits != -1) format.setMaximumFractionDigits(maxFractionDigits);
  }

  /**
   * Creates an item to be histogrammed by this Histogram. The HistogramItem
   * iterates over the specified list calling the specified method on
   * each object in the list. This method must return a double or
   * a primitive value that can be cast to a double. All these doubles
   * are then distributed across the bins accordingly.<p>
   *
   * <b>Note</b> this method assumes that the specified list is not
   * empty when this method is called.
   *
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param listObjMethodName the name of the method to be called on each
   * object in the list, the results of which are then histogrammed
   */
  public void createHistogramItem(String name, List list,
                                  String listObjMethodName) {
    BinDataSource bds = createListBinDataSource(list.iterator().next(),
                                                listObjMethodName);
    createHistogramItem(name, list, bds);
  }

  /**
   * Creates an item to be histogrammed by this Histogram. The HistogramItem
   * iterates over the specified list calling the specified method on
   * each object in the list. This method must return a double or
   * a primitive value that can be cast to a double. All these doubles
   * are then distributed across the bins accordingly.<p>
   *
   * <b>Note</b> this method assumes that the specified list is not
   * empty when this method is called.
   *
   * @param name the name of the item
   * @param list the list of objects that provided the data for the item
   * @param listObjMethodName the name of the method to be called on each
   * object in the list, the results of which are then histogrammed
   * @param maxIntegerDigits the maximum number of digits before the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   * @param maxFractionDigits the maximum number of digits after the
   * decimal point in the bin labels. A value of -1 will record all the digits.
   */
  public void createHistogramItem(String name, List list,
                                  String listObjMethodName, int maxIntegerDigits,
                                  int maxFractionDigits)
  {
    BinDataSource bds = createListBinDataSource(list.iterator().next(),
                                                listObjMethodName);
    createHistogramItem(name, list, bds);
    format.setMaximumFractionDigits(340);
    if (maxIntegerDigits != -1) format.setMaximumIntegerDigits(maxIntegerDigits);
    if (maxFractionDigits != -1) format.setMaximumFractionDigits(maxFractionDigits);
  }

  /**
   * Histograms the data from the HistogramItems without updating the display.
   */

  public void record() {
    histogram.reset();
    int size = histList.size();
    for (int i = 0; i < size; i++) {
      histogram.fill(dataSource.getBinValue(histList.get(i)));
    }
  }

  /**
   * Histograms the specified data without updating the display.
   *
   * @param data the double-s to histogram
   */
  public void record(double[] data) {
    histogram.reset();
    int size = data.length;
    for (int i = 0; i < size; i++) {
      histogram.fill(data[i]);
    }
  }

  /**
   * Histograms the specified data without updating the display.
   *
   * @param data the double-s to histogram
   */
  public void record(DoubleArrayList data) {
    histogram.reset();
    int size = data.size();
    for (int i = 0; i < size; i++) {
      histogram.fill(data.get(i));
    }
  }

  /**
   * Histograms the data from the histogram items and updates the displayed
   * graph.
   */
  public void step() {
    record();
    updateGraph();
  }

  /**
   * Histograms the specified double array and updates the graph.
   *
   * @param data the array of doubles to histogram
   */
  public void step(double[] data) {
    record(data);
    updateGraph();
  }

  /**
   * Histograms the specified DoubleArrayList and updates the graph.
   *
   * @param data the DoubleList of doubles to histogram
   */
  public void step(DoubleArrayList data) {
    record(data);
    updateGraph();
  }

  /**
   * Returns a String representation of the histogram. This includes
   * a tabular represenation of the bin heights.
   */
  public String toString() {
    Converter converter = new Converter();
    String lineSep = System.getProperty("line.separator");
    StringBuffer b = new StringBuffer(Calendar.getInstance().getTime().
                                      toString());
    b.append(lineSep);
    if (model != null) b.append(model.getPropertiesValues());

    b.append(lineSep);
    b.append(converter.toString(histogram));
    return b.toString();
  }

  /**
   * Updates the graph.
   */
  public void updateGraph() {

    plot.clearPoints();

    if (!xTickDrawn) {
      IAxis axis = histogram.xAxis();

      for (int i = 0; i < numBins; i++) {
        StringBuffer b = new StringBuffer();
        b.append(format.format(axis.binLowerEdge(i)));
        b.append("-");
        b.append(format.format(axis.binUpperEdge(i)));
        updateXTick(axis.binCentre(i), b.toString(), i);
      }
      xTickDrawn = true;
    }

    IAxis xAxis = histogram.xAxis();
    for (int i = 0; i < numBins; i++) {
      plot.addPoint(0, xAxis.binCentre(i), histogram.binHeight(i), false);
    }

    if (showStats) showStats();
    plot.fillPlot();
  }

  // shows the stats.
  private void showStats() {
    plot.clearDetailStrings();
    NumberFormat format = NumberFormat.getInstance();
    StringBuffer b = new StringBuffer("All Entries: ");

    b.append(format.format(histogram.allEntries()));
    plot.addDetailString(b.toString());
    b.setLength(0);

    b.append("Underflow: ");
    b.append(format.format(histogram.binHeight(Histogram1D.UNDERFLOW)));
    plot.addDetailString(b.toString());
    b.setLength(0);

    b.append("Overflow: ");
    b.append(format.format(histogram.binHeight(Histogram1D.OVERFLOW)));
    plot.addDetailString(b.toString());
    b.setLength(0);

    b.append("Mean: ");
    b.append(format.format(histogram.mean()));
    plot.addDetailString(b.toString());
    b.setLength(0);

    b.append("Rms: ");
    b.append(format.format(histogram.rms()));
    plot.addDetailString(b.toString());
  }
}
