/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2003 Steve Phelps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */


package uk.ac.liv.ai.learning;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import uk.ac.liv.util.io.DataSeriesWriter;
import uk.ac.liv.util.io.DataWriter;

import JSci.awt.*;
import JSci.swing.*;

import javax.swing.*;
/**
 * @author Steve Phelps
 * @version $Revision$
 */

public class GraphLearnerMonitor extends DataSeriesWriter
    implements LearnerMonitor {

  GraphFrame graph;

  public GraphLearnerMonitor() {
    super();
    graph = new GraphFrame(this);
  }

  public void setup( ParameterDatabase parameters, Parameter base ) {
    //TODO
  }

  public void startRecording() {
    clear();
  }

  public void finishRecording() {
    graph.update();
  }

}

class GraphFrame extends JFrame {

  JLineGraph graph;

  DefaultGraph2DModel model;

  public GraphFrame( DataSeriesWriter series ) {
    model = new DefaultGraph2DModel();
    model.addSeries(series);
    graph = new JLineGraph(model);
    getContentPane().add(graph);
    pack();
    setVisible(true);
  }

  public void update() {
//    SwingUtilities.invokeAndWait(new Runnable() {
//      public void run() {
        //TODO
//        model.fireDataChanged()
//      }});
  }
}