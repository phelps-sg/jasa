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
 * A generic point plotting class. This can be used to plot multiple points,
 * plotting a function whose input is some simulation data. Use the
 * plotPoint method to plot the points, updateGraph to display those points,
 * and clear to clear the graph of all the points.
 *
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class Plot extends OpenGraph {

  /**
   * Creates a Plot with the specified title.
   *
   * @param title the title of the plot
   */
  public Plot(String title) {
    super(title);
    plot.setMarksStyle("dots");

    // batch the updates so updateGraph must be called
    plot.setDrawOnAddPoint(false);
    setXRange(0.0, 100);
    setYRange(0.0, 100);
  }

  /**
   * Creates a Plot with the specified title and associated with the
   * specified SimModel. If you want take a snapshot of this Plot you need
   * to use this constructor.
   *
   * @param title the title of this Plot
   * @param model the associated model.
   */
  public Plot(String title, SimModel model) {
    this(title);
    this.model = model;
  }

  /**
   * Whether or not all the points should be connected.
   *
   * @param connected if true the points are connected
   */
  public void setConnected(boolean connected) {
    plot.setConnected(connected);
  }


  /**
   * Whether or not the points specific to some dataset should be
   * connected.
   *
   * @param connected if true the points will be connected
   * @param dataset the dataset whose points should be connected
   */
  public void setConnected(boolean connected, int dataset) {
    plot.setConnected(connected, dataset);
  }

  /**
   * Plots a point on the graph. This point will not appear on the graph
   * untill updateGraph is called. Points are specific to datasets. Multiple
   * datasets allow you to plot multiple functions etc. on a single chart.
   *
   * @param x the x coordinate for the point to be plotted
   * @param y the y coordinate for the point to be plotted
   * @param dataset the dataset for the point to be plotted
   */
  public void plotPoint(double x, double y, int dataset) {
    plot.addPoint(dataset, x, y, plot.getConnected());
  }

  /**
   * Empty method that does nothing.
   */
  public void record() {}

  /**
   * Updates the graph by redrawing the points.
   */
  public void updateGraph() {
    plot.repaint();
  }

  /**
   * Clears all the points from the specified dataset from the graph. This
   * will update the graph.
   *
   * @param dataset the number of the dataset to clear.
   */
  public void clear(int dataset) {
    plot.clear(dataset);
    plot.repaint();
  }

  /**
   * Adds a legend for the specified dataset.
   *
   * @param dataset the number of the dataset to add a legend for.
   * @param legend the legend to add.
   */
  public void addLegend(int dataset, String legend) {
	plot.addLegend(dataset, legend);
  }

  /**
   * Adds a legend for the specified dataset. The legend and the plot for
   * this dataset will be drawn in the specified color.
   *
   * @param dataset the number of the dataset to add a legend for.
   * @param legend the legend to add.
   * @param color the color of the legend / plot
   */
  public void addLegend(int dataset, String legend, Color color) {
    plot.addLegend(dataset, legend, color);
  }

  /**
   * Adds a legend for the specified dataset. The plotted points for
   * this dataset will be drawn in the specified style. markStyle is
   * one of the following constants: FILLED_CIRCLE, CROSS, SQUARE,
   * FILLED_TRIANGLE, DIAMOND, CIRCLE, PLUS_SIGN, FILLED_SQUARE,
   * TRIANGLE, FILLED_DIAMOND.
   
   * @param dataset the number of the dataset to add a legend for.
   * @param legend the legend to add.
   * @param markStyle the style of the plotted points
   */
  public void addLegend(int dataset, String legend, int markStyle) {
    plot.addLegend(dataset, legend, markStyle);
  }

  /**
   * Adds a legend for the specified dataset. The legend and the plot
   * for this dataset will be drawn in the specified color. The
   * plotted points for this dataset will be drawn in the specified
   * style. markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   * @param dataset the number of the dataset to add a legend for.
   * @param legend the legend to add.
   * @param color the color of the legend / plot
   * @param markStyle the style of the plotted points
   */
  public void addLegend(int dataset, String legend, Color color,
			int markStyle)
  {
    plot.addLegend(dataset, legend, color, markStyle);
  }

  /**
   * Shrinks or enlarges the scale so that the plotted points all fit.
   * This will update the graph.
   */
  public void fillPlot() {
    plot.fillPlot();
  }

  /**
   * Adds a String to the plot. The string will be displayed to the right
   * of the box. Additional statistics or more information about the graph
   * can be displayed here.
   */
  public void addString(String string) {
	plot.addDetailString(string);
  }

  /**
   * Clears any added strings from the plot.
   */
  public void clearStrings() {
	plot.clearDetailStrings();
  }

  /**
   * Tests Plot
   */
  public static void main(String[] args) {
    try {
      Plot aPlot = new Plot("Test Plot");
      /*
      aPlot.f.addWindowListener(new java.awt.event.WindowAdapter () {
        public void windowClosing(java.awt.event.WindowEvent evt) {
          System.exit(0);
        }
      });
      */
      aPlot.display();
      aPlot.setConnected(true);

      for (double i = 0; i < 100; i++) {
        aPlot.plotPoint(i, Math.sin(i), 0);
      }
      aPlot.updateGraph();
      aPlot.fillPlot();
      try {
        Thread.sleep(2000);
      } catch (Exception ex) {}

      aPlot.plotPoint(3.0, 4.0, 1);
      aPlot.plotPoint(5.0, 1.4, 1);
      aPlot.fillPlot();

	  aPlot.addString("hello");

    } catch (Exception ex) {
      System.exit(0);
    }
  }
}

