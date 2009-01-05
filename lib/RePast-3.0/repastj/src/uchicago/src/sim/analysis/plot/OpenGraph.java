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
package uchicago.src.sim.analysis.plot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import uchicago.src.codegen.GeneratorException;
import uchicago.src.guiUtils.GuiUtilities;
import uchicago.src.sim.analysis.BinDataSource;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.gui.FrameFactory;
import uchicago.src.sim.gui.MediaProducer;
import uchicago.src.sim.gui.MovieMaker;
import uchicago.src.sim.util.ByteCodeBuilder;
import uchicago.src.sim.util.SimUtilities;

/**
 * The base class for the Ptolemy based open source charting
 * components: OpenSequenceGraph and OpenHistogram. OpenGraph encapsulates
 * a RepastPlot object, provides some reasonable defaults. It also implements
 * ZoomListener, and adds a KeyListener to the plot in order to correctly
 * undo a zoom with the 'r' key.
 *
 * @author Nick Collier (Modified by Michael J. North)
 * @version $Revision$ $Date$
 */
public abstract class OpenGraph implements ZoomListener, MediaProducer {

  public static final int SEQUENCE = 0;
  public static final int HISTOGRAM = 1;

  public static final int FILLED_CIRCLE = 0;
  public static final int CROSS = 1;
  public static final int SQUARE = 2;
  public static final int FILLED_TRIANGLE = 3;
  public static final int DIAMOND = 4;
  public static final int CIRCLE = 5;
  public static final int PLUS_SIGN = 6;
  public static final int FILLED_SQUARE = 7;
  public static final int TRIANGLE = 8;
  public static final int FILLED_DIAMOND = 9;

  protected JFrame frame;
  protected boolean inNormalState = true;
  protected double xMin, xMax, yMin, yMax;
  protected double xIncr = 5;
  protected double yIncr = 5;
  protected RepastPlot plot;

  protected SimModel model = null;
  protected Point location;

  protected MovieMaker movieMaker;

  protected String title = "";
  protected String fileName = null;

  /**
   * Creates an OpenGraph with the specified title and sets up some
   * reasonable defaults.
   */
  public OpenGraph(String title) {
    this.title = title;

    plot = new RepastPlot(this);

    plot.setTitle(title);
    xMin = 0;
    xMax = 100;
    yMin = 0;
    yMax = 100;

    addListeners();
  }

  private void addListeners() {

    // this used to work, but now it appears that the key listener needs
    // to be added to the frame.
    plot.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();
        if (!inNormalState && key == KeyEvent.VK_R) {
          plot.setYRange(yMin, yMax);
          plot.setXRange(xMin, xMax);
          inNormalState = true;
          plot.repaint();
        }
      }
    });
  }

  /**
   * Sets the initial range of the X-axis. This range sill change if
   * subsequenly plotted points are outside of the initial range.
   *
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   */
  public void setXRange(double min, double max) {
    xMin = min;
    xMax = max;
    if (xMin >= xMax) throw new IllegalArgumentException("Min. value must be less than max. value");
    plot.setXRange(min, max);
  }

  /**
   * Returns the range of the x-axis as a double[] where the min
   * is the 0th elements and the max the 1st.
   */
  public double[] getXRange() {
    return plot.getXRange();
  }

  /**
   * Sets the initial range of the y-axis. This range will change if
   * any subsequently plotted points are outside of the initial
   * range.
   *
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   */
  public void setYRange(double min, double max) {
    yMin = min;
    yMax = max;
    if (yMin >= yMax) throw new IllegalArgumentException("Min. value must be less than max. value");
    plot.setYRange(min, max);
  }

  /**
   * Gets the range of the y-axis as a double[] where the min
   * is the 0th elements and the max the 1st.
   */
  public double[] getYRange() {
    return plot.getYRange();
  }

  /**
   * Sets the size of the plot.
   * @param width the width of the plot
   * @param height the height of the plot
   */
  public void setSize(int width, int height) {
    plot.setSize(width, height);
  }

  /**
   * Gets the size of the plot.
   */
  public Dimension getSize() {
    return plot.getSize();
  }

  /**
   * Sets the screen location for this OpenGraph.
   *
   * @param x the x screen coordinate
   * @param y the y screen coordinate
   */
  public void setLocation(int x, int y) {
    location = new Point(x, y);
    if (frame != null) frame.setLocation(location);
  }

  /**
   * Returns the title of this graph.
   * @return the title of this graph
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the amount to increase or decrease the y-axis scale when a y
   * value is out of the range of the current scale. The new scale is
   * calculated to include the out of range point plus or minus the
   * increment. <b>Note that this only works with an OpenSequenceGraph
   * and not with an OpenHistogram.</b>
   */
  public void setYIncrement(double incr) {
    yIncr = incr;
  }

  /**
   * Get the the amount to increase or decrease the y-axis scale when a
   * y value is out of the range of the current scale.
   */
  public double getYIncrement() {
    return yIncr;
  }

  /**
   * Sets the amount to increase or decrease the x-axis scale when a
   * x value is out of the range of the current scale. The new scale is
   * calculated to include the out of range point plus or minus the
   * increment.<b>Note that this
   * only works with an OpenSequenceGraph and not with an OpenHistogram.</b>
   */
  public void setXIncrement(double incr) {
    xIncr = incr;
  }

  /**
   * Get the the amount to increase or decrease the x-axis scale when a
   * x value is out of the range of the current scale.
   */
  public double getXIncrement() {
    return xIncr;
  }

  /**
   * Sets the axis titles.
   * @param xAxis the title for the xAxis
   * @param yAxis the title for the yAxis
   */
  public void setAxisTitles(String xAxis, String yAxis) {
    plot.setXLabel(xAxis);
    plot.setYLabel(yAxis);
  }

  /**
   * Returns the axis titles as a String[] where the first element is
   * x-axis label and the second is the y-axis label.
   */
  public String[] getAxisTitles() {
    String[] titles = new String[2];
    titles[0] = plot.getXLabel();
    titles[1] = plot.getYLabel();
    return titles;
  }

  /**
   * Associates a label with a particular x-axis tick.
   *
   * @param position the position of the tick
   * @param label the label for that tick
   */
  public void setXTick(double position, String label) {
    plot.addXTick(label, position);
  }

  /**
   * Updates an x-axis tick label with a new one.
   */
  public void updateXTick(double position, String label, int index) {
    plot.updateXTick(label, position, index);
  }

  /**
   * Sets the width and offset of the bars. Width and offset are in
   * terms of the x-axis scale and <b>not</b> in pixels.
   */
  public void setBars(double width, double offset) {
    plot.setBars(width, offset);
  }

  /**
   * Records and updates the graph display.
   */
  public void step() {
    record();
    if (frame.getState() != JFrame.ICONIFIED) updateGraph();
  }

  public abstract void record();

  public abstract void updateGraph();

  /**
   * Zoom listener interface. Called whenever a user zooms in or out on
   * the graph.
   */
  public void zoom(int x, int y) {
    inNormalState = false;
  }

  /**
   * Displays this graph.
   */
  public void display() {
    if (frame == null) {
      frame = FrameFactory.createFrame(title);

      // we used to add the keylistener to the plot itself, but that doesn't
      // work anymore so we add it to the frame now.
      frame.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent evt) {
          int key = evt.getKeyCode();
          if (!inNormalState && key == KeyEvent.VK_R) {
            plot.setYRange(yMin, yMax);
            plot.setXRange(xMin, xMax);
            inNormalState = true;
            plot.repaint();
          }
        }
      });

      frame.addWindowListener(new WindowAdapter() {
        public void windowDeiconified(WindowEvent e) {
          updateGraph();
        }
      });

      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(plot, BorderLayout.CENTER);
      frame.setTitle(title);
      frame.pack();
      Rectangle bounds = FrameFactory.getBounds(title);
      if (location != null) {
        // explicitly set location takes precedence over persistent location
        frame.setLocation(location);
        if (bounds != null) frame.setSize(bounds.width, bounds.height);
      } else if (bounds != null) {
        frame.setBounds(bounds);
      } else
        GuiUtilities.centerComponentOnScreen(frame);
      frame.setVisible(true);
    }
  }

  /**
   * Dispose of this graph.
   */
  public void dispose() {
    if (frame != null) {
      frame.dispose();
      frame = null;
    }
  }

  /**
   * Dynamically creates a BinDataSource wrapping the method call
   * to the specified methodName on the specified target in
   * BinDataSource.getBinValue(Object obj).
   *
   * @param target the target of the method call
   * @param methodName the name of the method to call
   * @return a BinDataSource object
   * @see uchicago.src.sim.analysis.BinDataSource
   */
  protected BinDataSource createBinDataSource(Object target, String methodName) {
    BinDataSource bds = null;
    try {
      bds = ByteCodeBuilder.generateBinDataSource(target, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating BinDataSource: " +
                             ex.getMessage(), ex);
      System.exit(0);
    }

    return bds;
  }

  protected BinDataSource createListBinDataSource(Object listObj,
                                                  String listObjMethodName) {
    BinDataSource bds = null;
    try {
      bds = ByteCodeBuilder.generateNoTargetBinDataSource(listObj,
                                                          listObjMethodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating ListBinDataSource: " +
                             ex.getMessage(), ex);
      System.exit(0);
    }

    return bds;
  }


  /**
   * Creates a Sequence, wrapping a call of specified methodName on
   * the feedFrom object in Sequence.getSValue().
   *
   * @param feedFrom the target of the method call
   * @param methodName the name of the method to call
   * @return a Sequence object
   * @see uchicago.src.sim.analysis.Sequence
   */
  protected Sequence createSequence(Object feedFrom, String methodName) {
    Sequence s = null;
    try {
      s = ByteCodeBuilder.generateSequence(feedFrom, methodName);
    } catch (GeneratorException ex) {
      SimUtilities.showError("Error creating Sequence: " +
                             ex.getMessage(), ex);
      System.exit(0);
    }

    return s;
  }

  public void takeSnapshot() {
    if (fileName == null) {
      SimUtilities.showMessage("Snapshot filename is not set");
      return;
    }

    if (model == null) {
      SimUtilities.showMessage("No model associated with this graph.");
      return;
    }

    // make sure the plot is up to date if it is minimized
    if (frame.getState() == JFrame.ICONIFIED)
    	updateGraph();
    
    BufferedImage bi = plot.getImage();
    //String file = fileName + model.getTickCountDouble() + ".gif";
    String file = fileName + model.getTickCount() + ".png";

    try {
      DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
      ImageIO.write(bi, "png", os);
      os.close();
      /*
      GifEncoder encoder = new GifEncoder(bi, os);
      encoder.encode();
      encoder = null;
      */
    } catch (java.io.IOException ex) {
      SimUtilities.showError("Error writing graph to file", ex);
    }
  }

  public void setSnapshotFileName(String name) {
    fileName = name;
  }

  /**
   * Sets the name and type of a movie. Currently type can only be
   * DisplaySurface.QUICK_TIME.
   *
   * @param fileName the name of the movie
   * @param movieType the type of movie (e.g. DisplaySurface.QUICK_TIME)
   */
  public void setMovieName(String fileName, String movieType) {
    Dimension d = getSize();
    if (movieType.equals(QUICK_TIME)) {
      fileName = fileName + ".mov";
      movieMaker = new MovieMaker(d.width, d.height, 1, fileName, movieType);
    } else {
      SimUtilities.showMessage("Movie type " + movieType + " is unsupported");
    }
  }

  /**
   * Adds the currently displayed image as frame to a movie. setMovieName must
   * be called before this method is called.
   */
  public void addMovieFrame() {
    if (movieMaker == null) {
      SimUtilities.showMessage("Unable to create frame - use setMovieFileName first");
      return;
    }

    BufferedImage bufImage = plot.getGraphicsConfiguration().
            createCompatibleImage(plot.getWidth(), plot.getHeight());
    Graphics g = bufImage.getGraphics();
    plot.paint(g);
    movieMaker.addImageAsFrame(bufImage);
    g.dispose();
  }

  /**
   * Closes the movie, writing any remaining frames to the file. This must
   * be called if making a movie.
   */
  public void closeMovie() {
    if (movieMaker != null) {
      movieMaker.cleanUp();
    }
  }
}
