/*
 * JASA Java Auction Simulator API
 * Copyright (C) 2001-2005 Steve Phelps
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

package uk.ac.liv.auction.stats;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ac.liv.auction.event.AuctionEvent;
import uk.ac.liv.auction.ui.UserFrame;

import uk.ac.liv.util.Parameterizable;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartUtilities;
import org.jfree.ui.RefineryUtilities;

/**
 *  A report that logs data to JFreeChart graphs.
 * 
 * <p><b>Parameters</b><br></p>
 * <table>
 *
 * <tr><td valign=top><i>base</i><tt>.name</tt><br>
 * <font size=-1>string</font></td>
 * <td valign=top></td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.graph.n</tt><br>
 * <font size=-1>int</font></td>
 * <td valign=top>(the number of JFreeChart graphs to generate)</td><tr>
 *
 * <tr><td valign=top><i>base</i><tt>.savetofile</tt><br>
 * <font size=-1>boolean</font></td>
 * <td valign=top>(whether to save graphs into files as jpg pictures)</td><tr>
 *
 * </table>
 * 
 * @author Jinzhong Niu
 * @version $Revision$
 */

public class FreeChartReport extends AbstractAuctionReport implements
    Parameterizable, Serializable, Cloneable {

  static Logger logger = Logger.getLogger(FreeChartReport.class);

  public static final String P_NAME = "name";

  public static final String P_GRAPH = "graph";

  public static final String P_NUM = "n";

  public static final String P_SAVETOFILE = "savetofile";

  private boolean saveToFile = false;

  private UserFrame frame;

  private FreeChartGraph graphs[];

  public FreeChartReport() {
  }

  public void setup(ParameterDatabase parameters, Parameter base) {

    frame = new UserFrame();
    frame.setup(parameters, base);

    graphs = new FreeChartGraph[parameters.getInt(base.push(P_GRAPH)
        .push(P_NUM))];

    for (int i = 0; i < graphs.length; i++) {
      graphs[i] = (FreeChartGraph) parameters.getInstanceForParameterEq(base
          .push(P_GRAPH).push(String.valueOf(i)), null, FreeChartGraph.class);
      graphs[i].setReport(this);
      graphs[i].setup(parameters, base.push(P_GRAPH).push(String.valueOf(i)));
    }
    
    saveToFile = parameters.getBoolean(base.push(P_SAVETOFILE), null, saveToFile);

    JPanel canvas = new JPanel();

    if (graphs.length > 2)
      canvas.setLayout(new GridLayout(0, 1, 5, 5));
    else
      canvas.setLayout(new GridLayout(0, 1, 5, 5));

    JPanel p; 
    for (int i = 0; i < graphs.length; i++) {
      p = new JPanel();
      p.setLayout(new BorderLayout());
      p.add(BorderLayout.CENTER, graphs[i]);
      canvas.add(p);
    }

    JScrollPane scrollP = new JScrollPane(canvas);
    frame.setContentPane(scrollP);
    //frame.pack();
    RefineryUtilities.centerFrameOnScreen(frame);
    frame.setVisible(true);
  }

  public void eventOccurred(AuctionEvent event) {
    for (int i = 0; i < graphs.length; i++) {
      graphs[i].eventOccurred(event);
    }
  }

  public void produceUserOutput() {
    logger.info("");
    logger.info("Auction statistics");
    logger.info("------------------");
    if (saveToFile) {
      File file = null;
      String name = null;
      for(int i = 0; i < graphs.length; i++){
        name = graphs[i].getChart().getTitle().getText().replace(File.separatorChar, '_')+".jpg";
        file = new File(name);
        try {
          ChartUtilities.saveChartAsJPEG(file, graphs[i].getChart(), graphs[i].getWidth(), graphs[i].getHeight());
        } catch (IOException e) {
          logger.info(e);
        }
      }
    } else {
      logger.info("Output of "+getClass()+" is empty.");
    }
  }

  public Map getVariables() {
    HashMap vars = new HashMap();
    return vars;
  }
}