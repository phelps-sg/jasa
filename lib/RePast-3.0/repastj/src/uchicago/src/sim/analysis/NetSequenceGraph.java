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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uchicago.src.guiUtils.GuiUtilities;
import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.gui.FrameFactory;
import uchicago.src.sim.network.NetUtilities;

/**
 * A graph that captures a series of points (sequences) specialized
 * for network statistics. This is specialized for network stats in
 * that it allows you to add the plotting of such stats on the fly. It
 * also contains methods to easily add such sequences, density,
 * component count, etc.  via code. Note that we are currently
 * working on the robustness of these statistics. They should be used
 * only as guides. The actual statistical analysis of your network should
 * be done using established tools.
 *
 * @version $Revision$ $Date$
 */

public class NetSequenceGraph extends OpenSequenceGraph {

  private List nodeList;
  private HashMap tableRows = new HashMap();

  private static final Integer CLUSTER = new Integer(0);
  private static final Integer DENSITY = new Integer(1);
  private static final Integer DIAMETER = new Integer(2);
  private static final Integer COMPONENT = new Integer(3);
  private static final Integer PATH_LENGTH = new Integer(4);
  private static final Integer SYMMETRY = new Integer(5);

  /**
   * Creates a NetSequencePlot with the specified title for the
   * specified model.
   *
   * @param title the title for this graph.
   * @param model the model associated with this graph
   */

  public NetSequenceGraph(String title, SimModel model, List nodeList) {
    super(title, model);
    this.nodeList = nodeList;
    makeTableRows();
  }


  /**
   * Creates a NetSequencePlot with the specified title, model, file name
   * and file format. File name and file format provide are necessary if the
   * data displayed by this graph is to be outputed to a file.
   *
   * @param title the title of the graph.
   * @param model the model associated with this graph
   * @param fileName the file name to be used when this graph is dumped to a
   * file
   * @param fileFormat the format to be used for dumping data to the file. At
   * the moment only PlotModel.CSV (comma delimited) is supported.
   */
  public NetSequenceGraph(String title, SimModel model, String fileName,
                          int fileFormat, List nodeList) {
    super(title, model, fileName, fileFormat);
    this.nodeList = nodeList;
    makeTableRows();
  }

  private void makeTableRows() {
    Sequence s = new Sequence() {
      public double getSValue() {
        return NetUtilities.calcClustCoef(nodeList);
      }
    };
    NetSeqTableRow row = new NetSeqTableRow("Clust Coef", s, Color.red);
    tableRows.put(CLUSTER, row);

    s = new Sequence() {
      public double getSValue() {
        return NetUtilities.calcDensity(nodeList);
      }
    };

    row = new NetSeqTableRow("Density", s, Color.green);
    tableRows.put(DENSITY, row);

    s = new Sequence() {
      public double getSValue() {
        return NetUtilities.calcDiameter(nodeList);
      }
    };

    row = new NetSeqTableRow("Diameter", s, Color.blue);
    tableRows.put(DIAMETER, row);

    s = new Sequence() {
      public double getSValue() {
        return NetUtilities.getComponents(nodeList).size();
      }
    };
    row = new NetSeqTableRow("Components", s, Color.magenta);
    tableRows.put(COMPONENT, row);

    s = new Sequence() {
      public double getSValue() {
        return NetUtilities.calcAvgPathLength(nodeList);
      }
    };

    row = new NetSeqTableRow("Avg. Path Length", s, Color.black);
    tableRows.put(PATH_LENGTH, row);


    s = new Sequence() {
      public double getSValue() {
        return NetUtilities.calcSymmetry(nodeList);
      }
    };

    row = new NetSeqTableRow("Symmetry", s, Color.orange);
    tableRows.put(SYMMETRY, row);
  }


  /**
   * Displays this graph.
   */
  public void display() {
    if (frame == null) {
      frame = FrameFactory.createFrame(title);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.getContentPane().setLayout(new BorderLayout());
      final JTabbedPane tb = new JTabbedPane();
      frame.getContentPane().add(tb, BorderLayout.CENTER);
      tb.add("Plot", plot);
      ArrayList l = new ArrayList(tableRows.values());
      NetSequenceSetupPanel nsp = new NetSequenceSetupPanel(l);
      tb.add("Setup", nsp);
      frame.setTitle(title);

      tb.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent evt) {
          if (tb.getSelectedIndex() == 0) checkAddSequences();
        }
      });

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

  private void checkAddSequences() {
    java.util.Iterator i = tableRows.values().iterator();
    while (i.hasNext()) {
      NetSeqTableRow row = (NetSeqTableRow) i.next();
      row.addSequence(this);
    }

    step();
  }

  private NetSeqTableRow getTableRow(Integer key) {
    return (NetSeqTableRow) tableRows.get(key);
  }

  /**
   * Plots the density of the network. The default color and mark style
   * will be used.
   *
   * @param legend the legend to display for the density sequence
   */
  public void plotDensity(String legend) {
    NetSeqTableRow row = getTableRow(DENSITY);
    row.setShow(true);
    row.addSequence(this, legend);
  }

  /**
   * Plots the density of the network. The default mark style
   * will be used.
   *
   * @param legend the legend to display for the density sequence
   * @param color the color of the density sequence plot
   */
  public void plotDensity(String legend, Color color) {
    NetSeqTableRow row = getTableRow(DENSITY);
    row.setShow(true);
    row.addSequence(this, legend, color);
  }

  /**
   * Plots the density of the network. The default color will be used.
   *
   * @param legend the legend to display for the density sequence
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotDensity(String legend, int markStyle) {
    NetSeqTableRow row = getTableRow(DENSITY);
    row.setShow(true);
    row.addSequence(this, legend, markStyle);
  }

  /**
   * Plots the density of the network using the specified parameters.
   *
   * @param legend the legend to display for the density sequence
   * @param color the color of the sequence plot
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotDensity(String legend, Color color, int markStyle) {
    NetSeqTableRow row = getTableRow(DENSITY);
    row.setShow(true);
    row.addSequence(this, legend, color, markStyle);
  }

  /**
   * Plots the component size of the network. The default color and mark style
   * will be used.
   *
   * @param legend the legend to display for the component sequence
   */
  public void plotComponentCount(String legend) {
    NetSeqTableRow row = getTableRow(COMPONENT);
    row.setShow(true);
    row.addSequence(this, legend);
  }

  /**
   * Plots the component size of the network. The default mark style
   * will be used.
   *
   * @param legend the legend to display for the component sequence
   * @param color the color of the component sequence plot
   */
  public void plotComponentCount(String legend, Color color) {
    NetSeqTableRow row = getTableRow(COMPONENT);
    row.setShow(true);
    row.addSequence(this, legend, color);
  }

  /**
   * Plots the component size of the network. The default color will be used.
   *
   * @param legend the legend to display for the component sequence
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotComponentCount(String legend, int markStyle) {
    NetSeqTableRow row = getTableRow(COMPONENT);
    row.setShow(true);
    row.addSequence(this, legend, markStyle);
  }

  /**
   * Plots the component size of the network using the specified parameters.
   *
   * @param legend the legend to display for the component sequence
   * @param color the color of the sequence plot
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotComponentCount(String legend, Color color, int markStyle) {
    NetSeqTableRow row = getTableRow(COMPONENT);
    row.setShow(true);
    row.addSequence(this, legend, color, markStyle);
  }

  /**
   * Plots the cluster coefficient of the network. The default color
   * and mark style will be used.
   *
   * @param legend the legend to display for the cluster coefficient
   * sequence
   */
  public void plotClusterCoefficient(String legend) {
    NetSeqTableRow row = getTableRow(CLUSTER);
    row.setShow(true);
    row.addSequence(this, legend);
  }

  /**
   * Plots the cluster coefficient of the network. The default mark style
   * will be used.
   *
   * @param legend the legend to display for the cluster coefficient
   * sequence
   * @param color the color of the cluster coefficient sequence plot
   */
  public void plotClusterCoefficient(String legend, Color color) {
    NetSeqTableRow row = getTableRow(CLUSTER);
    row.setShow(true);
    row.addSequence(this, legend, color);
  }

  /**
   * Plots the cluster coefficient of the network. The default color
   * will be used.
   *
   * @param legend the legend to display for the cluster coefficient
   * sequence
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotClusterCoefficient(String legend, int markStyle) {
    NetSeqTableRow row = getTableRow(CLUSTER);
    row.setShow(true);
    row.addSequence(this, legend, markStyle);
  }

  /**
   * Plots the cluster coefficient of the network using the specified
   * parameters.
   *
   * @param legend the legend to display for the cluster coefficient
   * sequence
   * @param color the color of the sequence plot
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotClusterCoefficient(String legend, Color color,
                                     int markStyle) {
    NetSeqTableRow row = getTableRow(CLUSTER);
    row.setShow(true);
    row.addSequence(this, legend, color, markStyle);
  }

  /**
   * Plots the diameter of the network. The default color and mark style
   * will be used.
   *
   * @param legend the legend to display for the diameter sequence
   */
  public void plotDiameter(String legend) {
    NetSeqTableRow row = getTableRow(DIAMETER);
    row.setShow(true);
    row.addSequence(this, legend);
  }

  /**
   * Plots the diameter of the network. The default mark style
   * will be used.
   *
   * @param legend the legend to display for the diameter sequence
   * @param color the color of the diameter sequence plot
   */
  public void plotDiameter(String legend, Color color) {
    NetSeqTableRow row = getTableRow(DIAMETER);
    row.setShow(true);
    row.addSequence(this, legend, color);
  }

  /**
   * Plots the diameter of the network. The default color will be used.
   *
   * @param legend the legend to display for the diameter sequence
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotDiameter(String legend, int markStyle) {
    NetSeqTableRow row = getTableRow(DIAMETER);
    row.setShow(true);
    row.addSequence(this, legend, markStyle);
  }

  /**
   * Plots the diameter of the network using the specified parameters.
   *
   * @param legend the legend to display for the diameter sequence
   * @param color the color of the sequence plot
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotDiameter(String legend, Color color, int markStyle) {
    NetSeqTableRow row = getTableRow(DIAMETER);
    row.setShow(true);
    row.addSequence(this, legend, color, markStyle);
  }

  /**
   * Plots the average path length of the network. The default color
   * and mark style will be used.
   *
   * @param legend the legend to display for the average path length
   * sequence
   */
  public void plotAvgPathLength(String legend) {
    NetSeqTableRow row = getTableRow(PATH_LENGTH);
    row.setShow(true);
    row.addSequence(this, legend);
  }

  /**
   * Plots the average path length of the network. The default mark style
   * will be used.
   *
   * @param legend the legend to display for the average path length sequence
   * @param color the color of the average path length sequence plot
   */
  public void plotAvgPathLength(String legend, Color color) {
    NetSeqTableRow row = getTableRow(PATH_LENGTH);
    row.setShow(true);
    row.addSequence(this, legend, color);
  }

  /**
   * Plots the average path length of the network. The default color
   * will be used.
   *
   * @param legend the legend to display for the average path length
   * sequence
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotAvgPathLength(String legend, int markStyle) {
    NetSeqTableRow row = getTableRow(PATH_LENGTH);
    row.setShow(true);
    row.addSequence(this, legend, markStyle);
  }

  /**
   * Plots the average path length of the network using the specified
   * parameters.
   *
   * @param legend the legend to display for the average path length sequence
   * @param color the color of the sequence plot
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotAvgPathLength(String legend, Color color, int markStyle) {
    NetSeqTableRow row = getTableRow(PATH_LENGTH);
    row.setShow(true);
    row.addSequence(this, legend, color, markStyle);
  }

  /**
   * Plots the symmetry of the network. The default color and mark style
   * will be used.
   *
   * @param legend the legend to display for the symmetry sequence
   */
  public void plotSymmetry(String legend) {
    NetSeqTableRow row = getTableRow(SYMMETRY);
    row.setShow(true);
    row.addSequence(this, legend);
  }

  /**
   * Plots the symmetry of the network. The default mark style
   * will be used.
   *
   * @param legend the legend to display for the symmetry sequence
   * @param color the color of the symmetry sequence plot
   */
  public void plotSymmetry(String legend, Color color) {
    NetSeqTableRow row = getTableRow(SYMMETRY);
    row.setShow(true);
    row.addSequence(this, legend, color);
  }

  /**
   * Plots the symmetry of the network. The default color will be used.
   *
   * @param legend the legend to display for the symmetry sequence
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotSymmetry(String legend, int markStyle) {
    NetSeqTableRow row = getTableRow(SYMMETRY);
    row.setShow(true);
    row.addSequence(this, legend, markStyle);
  }

  /**
   * Plots the symmetry of the network using the specified parameters.
   *
   * @param legend the legend to display for the symmetry sequence
   * @param color the color of the sequence plot
   * @param markStyle the style (shape etc.) of the point mark to
   * use. The markStyle is one of the following constants:
   * FILLED_CIRCLE, CROSS, SQUARE, FILLED_TRIANGLE, DIAMOND, CIRCLE,
   * PLUS_SIGN, FILLED_SQUARE, TRIANGLE, FILLED_DIAMOND.
   *
   */
  public void plotSymmetry(String legend, Color color, int markStyle) {
    NetSeqTableRow row = getTableRow(SYMMETRY);
    row.setShow(true);
    row.addSequence(this, legend, color, markStyle);
  }

}
