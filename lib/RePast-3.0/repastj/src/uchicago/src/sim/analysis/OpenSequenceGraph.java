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

import uchicago.src.sim.analysis.plot.OpenGraph;
import uchicago.src.sim.engine.SimModel;

/**
 * A graph that displays a connected series of points
 * (a sequence). Encapsulates a sequence statistic class that manages the
 * data. This statistic class can also be set. Typically, a time series
 * updated every tick of the main schedule.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class OpenSequenceGraph extends OpenGraph {

  private OpenSeqStatistic stats;
  private int pointIndex = 0;
  private int sequenceIndex = 0;
  private double xRange, yRange;
  private PolicyConstant xExpandConst = SHOW_ALL;
  private PolicyConstant yExpandConst = SHOW_ALL;

  static interface ExpandPolicy {
    public void expand(double val, OpenSequenceGraph graph);
  }

  static class ShowAllPointsX implements ExpandPolicy {
    public void expand(double xVal, OpenSequenceGraph graph) {
      double[] range = graph.plot.getXRange();
      if (xVal >= range[1]) {
        //setXRange(range[0], range[1] + xIncr);
        graph.setXRange(range[0], xVal + graph.xIncr);
        graph.plot.repaint();
      }
    }
  }

  static class ShowAllPointsY implements ExpandPolicy {
    public void expand(double yVal, OpenSequenceGraph graph) {
      double[] range = graph.plot.getYRange();
      if (yVal >= range[1]) {
        //setYRange(range[0], range[1] + yIncr);
        graph.setYRange(range[0], yVal + graph.yIncr);
        graph.plot.repaint();
      } else if (yVal <= range[0]) {
        graph.setYRange(yVal - graph.yIncr, range[1]);
        graph.plot.repaint();
      }
    }
  }

  static class ShowFirst implements ExpandPolicy {
    public void expand(double val, OpenSequenceGraph graph) {
    }
  }

  static class ShowLastX implements ExpandPolicy {
    public void expand(double xVal, OpenSequenceGraph graph) {
      double[] range = graph.plot.getXRange();
      if (xVal > range[1]) {
        graph.setXRange(xVal - graph.xRange, xVal);
        graph.plot.repaint();
      }
    }
  }

  static class ShowLastY implements ExpandPolicy {
    public void expand(double yVal, OpenSequenceGraph graph) {
      double[] range = graph.plot.getYRange();
      if (yVal > range[1]) {
        //setYRange(range[0], range[1] + yIncr);
        graph.setYRange(yVal - graph.yRange, yVal);
        graph.plot.repaint();
      } else if (yVal < range[0]) {
        graph.setYRange(yVal, yVal + graph.yRange);
        graph.plot.repaint();
      }
    }
  }

  // default to AutoExanding
  private ExpandPolicy xExpand = new ShowAllPointsX();
  private ExpandPolicy yExpand = new ShowAllPointsY();

  // Constant values for the expand policy.
  static interface PolicyConstant {
    public ExpandPolicy createXPolicy();

    public ExpandPolicy createYPolicy();
  }

  public static final PolicyConstant SHOW_ALL = new PolicyConstant() {
    public ExpandPolicy createXPolicy() {
      return new ShowAllPointsX();
    }

    public ExpandPolicy createYPolicy() {
      return new ShowAllPointsY();
    }
  };

  public static final PolicyConstant SHOW_FIRST = new PolicyConstant() {
    public ExpandPolicy createXPolicy() {
      return new ShowFirst();
    }

    public ExpandPolicy createYPolicy() {
      return new ShowFirst();
    }
  };

  public static final PolicyConstant SHOW_LAST = new PolicyConstant() {
    public ExpandPolicy createXPolicy() {
      return new ShowLastX();
    }

    public ExpandPolicy createYPolicy() {
      return new ShowLastY();
    }
  };

  /**
   * Creates an OpenSequenceGraph with the specified title for the
   * specified model.
   * @param title the title for this graph.
   * @param model the model associated with this graph
   */

  public OpenSequenceGraph(String title, SimModel model) {
    super(title);
    this.model = model;
    stats = new OpenSeqStatistic(model);
    plot.setMarksStyle("dots");
    setXRange(0.0, 200);
    xRange = xMax - xMin;
    yRange = yMax - yMin;
  }

  /**
   * Creates an OpenSequenceGraph with the specified title, model, file name
   * and file format. File name and file format provide are necessary if the
   * data displayed by this graph is to be outputed to a file.
   *
   * @param title the title of the graph.
   * @param model the model associated with this graph
   * @param fileName the file name to be used when this graph is dumped to a
   * file
   * @param fileFormat the format to be used for dumping data to the file. At
   * the moment only OpenSeqStatistic.CSV is supported.
   */
  public OpenSequenceGraph(String title, SimModel model, String fileName,
                           int fileFormat) {
    super(title);
    this.model = model;
    //stats = new OpenSequenceModel(fileName, fileFormat, title, model);
    stats = new OpenSeqStatistic(fileName, fileFormat, title, model);
    plot.setMarksStyle("dots");
    setXRange(0.0, 200);
    xRange = xMax - xMin;
    yRange = yMax - yMin;
  }

  /**
   * Adds the specified sequence with the specified name.
   *
   * @param name the name of the sequence
   * @param sequence the sequence to add
   */
  public Sequence addSequence(String name, Sequence sequence) {
    Sequence s = stats.addSequence(name, sequence);
    plot.addLegend(sequenceIndex++, name);
    return s;
  }

  /**
   * Creates and adds a Sequence to this SequenceGraph. The Sequence
   * will have the specified name and wraps a call to the specified
   * methodName on the specified Object. The value returned from this
   * method call will be plotted. The value must be numeric.
   *
   * @param name the name of the sequence
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   */
  public Sequence createSequence(String name, Object feedFrom, String methodName) {
    return addSequence(name, createSequence(feedFrom, methodName));
  }

  /**
   * Adds the specified sequence with the specified name to be drawn in the
   * specific color.
   *
   * @param name the name of the sequence
   * @param sequence the sequence to add
   * @param color the color of the sequence
   */
  public Sequence addSequence(String name, Sequence sequence, Color color) {
    Sequence s = stats.addSequence(name, sequence);
    plot.addLegend(sequenceIndex++, name, color);
    return s;
  }

  /**
   * Creates and adds a Sequence to this SequenceGraph. The Sequence
   * will have the specified name and color. The Sequence itself wraps a call to the specified
   * methodName on the specified Object. The value returned from this
   * method call will be plotted. This return value must be numeric.
   *
   * @param name the name of the sequence
   * @param color the color of the sequence
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   */
  public Sequence createSequence(String name, Color color, Object feedFrom, String methodName) {
    return addSequence(name, createSequence(feedFrom, methodName), color);
  }

  /**
   * Adds the specified sequence with specified name whose points will
   * be drawn in the specified style. The markStyle is one of the
   * following constants: FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE,
   * DIAMOND, CIRCLE, PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   * @param name the name of the sequence
   * @param sequence the sequence to add
   * @param markStyle the shape of the plotted points
   */
  public Sequence addSequence(String name, Sequence sequence, int markStyle) {
    Sequence s = stats.addSequence(name, sequence);
    plot.addLegend(sequenceIndex++, name, markStyle);
    return s;
  }

  /**
   * Creates and adds a Sequence to this SequenceGraph. The Sequence
   * will have the specified name and its points will be drawn in the
   * specified style. The sequence itself wraps a call to the specified
   * methodName on the specified Object. The value returned from this
   * method call will be plotted. This value must be numeric.
   * The markStyle is one of the
   * following constants: FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE,
   * DIAMOND, CIRCLE, PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   * @param name the name of the sequence
   * @param markStyle the shape of the plotted points
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   */
  public Sequence createSequence(String name, int markStyle, Object feedFrom, String methodName) {
    return addSequence(name, createSequence(feedFrom, methodName), markStyle);
  }

  /**
   * Adds the specified sequence with the specified name to be drawn
   * in the specific color, whose points will be drawn in the
   * specified style. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   * @param name the name of the sequence
   * @param sequence the sequence to add
   * @param color the color of the sequence
   * @param markStyle the shape of the plotted points
   */
  public Sequence addSequence(String name, Sequence sequence, Color color,
                              int markStyle) {
    Sequence s = stats.addSequence(name, sequence);
    plot.addLegend(sequenceIndex++, name, color, markStyle);
    return s;
  }

  /**
   * Creates and adds a Sequence to this SequenceGraph. The Sequence
   * will have the specified name and color. Its points will be drawn in the
   * specified style. The sequence itself wraps a call to the specified
   * methodName on the specified Object. The value returned from this
   * method call will be plotted. This return value mustbe numeric.
   * The markStyle is one of the
   * following constants: FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE,
   * DIAMOND, CIRCLE, PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   * @param name the name of the sequence
   * @param color the color of the sequence
   * @param markStyle the shape of the plotted points
   * @param feedFrom the object on which to call the method
   * @param methodName the name of the method to call
   */
  public Sequence createSequence(String name, Color color, int markStyle, Object feedFrom, String methodName) {
    return addSequence(name, createSequence(feedFrom, methodName), color, markStyle);
  }

  /**
   * Sets the initial range of the x-axis. Depending on the view policy for
   * the x-axis this range may change as new points are added to the graph.<p>
   *
   * Under the SHOW_FIRST and SHOW_LAST view policies, this range is the viewable
   * range. For example, given a range of 5, 100, SHOW_FIRST will only show the
   * points that fall into the range from 5 to 100; SHOW_LAST will show all the
   * points that fall into the range of lastPoint - 95 (i.e the "range" of 5 to 100)
   * to the lastPoint.
   *
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   * @see OpenSequenceGraph#setXViewPolicy(PolicyConstant) setXViewPolicy
   */
  public void setXRange(double min, double max) {
    super.setXRange(min, max);
    xRange = xMax - xMin;

  }

  /**
   * Sets the initial range of the y-axis. Depending on the view policy for
   * the y-axis this range may change as new points are added to the graph.<p>
   *
   * Under the SHOW_FIRST and SHOW_LAST view policies, this range is the viewable
   * range. For example, given a range of 5, 100, SHOW_FIRST will only show the
   * points that fall into the range from 5 to 100; SHOW_LAST will show all the
   * points that fall into the range of lastPoint - 95 (i.e the "range" of 5 to 100)
   * to the lastPoint.
   *
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   * @see OpenSequenceGraph#setYViewPolicy(PolicyConstant) setYViewPolicy
   */
  public void setYRange(double min, double max) {
    super.setYRange(min, max);
    yRange = yMax - yMin;
  }

  /**
   * Sets the view policy for the x-axis. The view policy controls which
   * the visibility of points w/r to the x-axis and thus whether graph expands along the
   * x-axis in order for new points to be visible. The appropriate arguments are:
   * <ul>
   * <li> OpenSequenceGraph.SHOW_ALL - The graph will rescale itself along the x-axis
   * so that any points outside of the current x-range will be displayed. This
   * is equivalent to <code>setXAutoExpand(true)</code>
   * <li>  OpenSequenceGraph.SHOW_FIRST - The graph will NOT rescale itself to
   * show out of range points. Only those points falling inside the range set
   * by setXRange(...) will be displayed. This is equivalent to
   * <code>setXAutoExpand(true)</code>
   * <li> OpenSequenceGraph.SHOW_LAST - The graph will rescale itself such that
   * only those points falling within the interval set by setXRange(...) are displayed.
   * The interval is applied from the last recorded point so if the interval is 100 (as set by
   * setXRange(0, 100)) then the graph will rescale to show only those points whose x-coordinate
   * is between that of the last point and last point - 100.<p>
   * </ul>
   *
   * <b>Note</b>Using SHOW_LAST is much slower than the other two as the graph will
   * have to entirely repaint itself whenver a point is added.
   *
   * @param policy determines what points are visible w/r to the
   * x-axis
   */
  public void setXViewPolicy(PolicyConstant policy) {
    xExpand = policy.createXPolicy();
    xExpandConst = policy;
  }

  /**
   * Returns the current view policy for the x-axis.
   */
  public PolicyConstant getXViewPolicy() {
    return xExpandConst;
  }

  /**
   * Sets the view policy for the y-axis. The view policy controls which
   * the visibility of points w/r to the y-axis and thus whether graph expands along the
   * y-axis in order for new points to be visible. The appropriate arguments are:
   * <ul>
   * <li> OpenSequenceGraph.SHOW_ALL - The graph will rescale itself along the y-axis
   * so that any points outside of the current y-range will be displayed. This
   * is equivalent to <code>setYAutoExpand(true)</code>
   * <li>  OpenSequenceGraph.SHOW_FIRST - The graph will NOT rescale itself to
   * show out of range points. Only those points falling inside the range set
   * by setYRange(...) will be displayed. This is equivalent to
   * <code>setYAutoExpand(true)</code>
   * <li> OpenSequenceGraph.SHOW_LAST - The graph will rescale itself such that
   * only those points falling within the interval set by setXYange(...) are displayed.
   * The interval is applied from the last recorded point so if the interval is 100 (as set by
   * setYRange(0, 100)) then the graph will rescale to show only those points whose y-coordinate
   * is between that of the last point and last point - 100.<p>
   * </ul>
   *
   * <b>Note</b>Using SHOW_LAST is much slower than the other two as the graph will
   * have to entirely repaint itself whenver a point is added. Also, this sets the
   * view policy for all the sequences added to this graph. Given that the y-values
   * of sequences can vary by large amounts, setting the y-axis view policy to
   * anything other than SHOW_ALL (the default) can hide some sequences and show others.
   *
   * @param policy determines what points are visible w/r to the
   * y-axis
   */
  public void setYViewPolicy(PolicyConstant policy) {
    yExpand = policy.createYPolicy();
    yExpandConst = policy;
  }

  /**
   * Returns the current view policy for the y-axis.
   */
  public PolicyConstant getYViewPolicy() {
    return yExpandConst;
  }

  /**
   * Sets whether the plot's x-axis scale will expand to include new points
   * or not. Default value is true.
   *
   * @param autoExpand if true, plot scale will expand, if false then
   * it will not
   */
  public void setXAutoExpand(boolean autoExpand) {
    if (autoExpand)
      setXViewPolicy(OpenSequenceGraph.SHOW_ALL);
    else
      setXViewPolicy(SHOW_FIRST);
  }

  /**
   * Returns whether the plot's x-axis scale will expand to include new points
   * that will not fit in the current scale.
   */
  public boolean getXAutoExpand() {
    return xExpandConst == OpenSequenceGraph.SHOW_ALL;
  }

  /**
   * Sets whether the plot's y-axis scale will expand to include new points
   * or not. Default value is true.
   *
   * @param autoExpand if true, plot scale will expand, if false then
   * it will not
   */
  public void setYAutoExpand(boolean autoExpand) {
    if (autoExpand)
      setYViewPolicy(OpenSequenceGraph.SHOW_ALL);
    else
      setYViewPolicy(SHOW_FIRST);
  }

  /**
   * Returns whether the plot's y-axis scale will expand to include new points
   * that will not fit in the current scale.
   */
  public boolean getYAutoExpand() {
    return yExpandConst == OpenSequenceGraph.SHOW_ALL;
  }

  /**
   * Records the data for this graph without updating the display.
   */
  public void record() {
    stats.record();
  }


  /**
   * Updates the display to reflect any new data as captured by record.
   */
  public void updateGraph() {
    int xSize = stats.getXValCount();
    int ySize = stats.getSequenceCount();

    for (int i = pointIndex; i < xSize; i++) {
      double xVal = stats.getXVal(i);
      xExpand.expand(xVal, this);

      for (int j = 0; j < ySize; j++) {
        double yVal = stats.getYVal(j, i);
        if (Double.isNaN(yVal)) continue;
        plot.addPoint(j, xVal, yVal, true);
        yExpand.expand(yVal, this);
      }
    }

    pointIndex = xSize;
  }

  /**
   * Writes this graph to a file.
   */
  public void writeToFile() {
    stats.writeToFile();
  }
}




