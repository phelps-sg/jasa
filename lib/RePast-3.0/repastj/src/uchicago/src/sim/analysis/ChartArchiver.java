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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.util.SimUtilities;

/**
 * Saves and loads custom charts via their XML descriptions. The data
 * required to create such charts is persisted between application instances
 * via an xml file. The file is stored in $HOME/fully_qualified_model_name_as
 * _dir/charts.xml. So, for example, if the home directory is /home/nick and the
 * fully qualified model name is uchicago.src.sim.heatBugs, then the file path
 * is <br>
 * /home/nick/.repast/uchicago/src/sim/heatBugs/charts.xml. <br>
 *
 * The xml itself is human readable, but is liable to change. An example follows:
 * <p>
 * <pre><code>
 *<?xml version="1.0"?>
 <!-- RePast Custom Chart Description File -->
 <RePast:Charts xmlns:RePast="http://src.uchicago.edu/repast/" >
 <ChartModel type="SequenceChart" title ="A Chart" xAxisTitle="X-Axis"
 yAxisTitle="Y-Axis" xRangeMin="0.0" xRangeMax="100.0" xRangeIncr="5.0"
 yRangeMin="0.0" yRangeMax="100.0" yRangeIncr="5.0" >
 <DataSource name="A Sequence" color="-23296" methodName="getMaxTriggerTime"
 markStyle="0" />

 </ChartModel>
 </RePast:Charts>

 *
 * </code></pre>
 *
 * @version $Revision$ $Date$
 */

// NOTE this only works with OpenSequenceGraphs at the moment

public class ChartArchiver {

  /**
   * Creates chart models from their XML descriptions and returns these
   * models in an ArrayList.
   *
   * @param model the SimModel associated with the chart models
   * @return a list of AbstractChartModel-s
   */
  public static ArrayList loadCharts(SimModel model) {
    String fqModelName = model.getClass().getName();
    String homeDir = System.getProperty("user.home");
    String modelDir = homeDir + File.separator + ".repast" + File.separator +
            fqModelName.replace('.', File.separatorChar);
    String file = modelDir + File.separator + "charts.xml";
    File chartFile = new File(file);
    if (chartFile.exists()) {
      return loadXML(file, model);
    } else {
      return new ArrayList();
    }
  }

  private static ArrayList loadXML(String file, SimModel model) {
    ArrayList charts = new ArrayList();
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = dbf.newDocumentBuilder();

      Document doc = parser.parse(new FileInputStream(file), file);
      Element root = doc.getDocumentElement();
      NodeList list = root.getElementsByTagName("ChartModel");
      for (int i = 0; i < list.getLength(); i++) {
        Element child = (Element) list.item(i);
        String type = child.getAttribute("type");
        if (type.equals("SequenceChart")) {
          charts.add(loadSequenceChart(child, model));
        }
      }
    } catch (Exception ex) {
      SimUtilities.showError("Error loading persistent chart sizes and positions", ex);
      ex.printStackTrace();
    }

    return charts;
  }

  private static SequenceChartModel loadSequenceChart(Element chart, SimModel model) {
    SequenceChartModel sequence = new SequenceChartModel(model);
    sequence.setTitle(chart.getAttribute("title"));
    sequence.setXAxisTitle(chart.getAttribute("xAxisTitle"));
    sequence.setYAxisTitle(chart.getAttribute("yAxisTitle"));
    sequence.setXRangeMin(chart.getAttribute("xRangeMin"));
    sequence.setXRangeMax(chart.getAttribute("xRangeMax"));
    sequence.setXRangeIncr(chart.getAttribute("xRangeIncr"));
    sequence.setYRangeMin(chart.getAttribute("yRangeMin"));
    sequence.setYRangeMax(chart.getAttribute("yRangeMax"));
    sequence.setYRangeIncr(chart.getAttribute("yRangeIncr"));

    NodeList ds = chart.getElementsByTagName("DataSource");
    ArrayList sources = new ArrayList();
    for (int i = 0; i < ds.getLength(); i++) {
      Element source = (Element) ds.item(i);
      // we assume that the feed is from the model for now!!!
      SequenceSource ss = new SequenceSource(model);
      ss.setName(source.getAttribute("name"));
      ss.setColor(new Color(Integer.parseInt(source.getAttribute("color"))));
      ss.setMethodName(source.getAttribute("methodName"));
      ss.setMarkStyle(Integer.parseInt(source.getAttribute("markStyle")));
      sources.add(ss);
    }

    sequence.setDataSources(sources);

    return sequence;
  }

  /**
   * Saves the list of AbstractChartModels. The data
   * required to create such charts is persisted between application instances
   * via an xml file. The file is stored in $HOME/fully_qualified_model_name_as
   * _dir/charts.xml. So, for example, if the home directory is /home/nick and the
   * fully qualified model name is uchicago.src.sim.heatBugs, then the file path
   * is <br>
   * /home/nick/.repast/uchicago/src/sim/heatBugs/charts.xml. <br>
   *
   * The xml itself is human readable, but is liable to change. An example follows:
   * <p>
   * <pre><code>
   * <?xml version="1.0"?>
   * <!-- RePast Custom Chart Description File -->
   * <RePast:Charts xmlns:RePast="http://src.uchicago.edu/repast/" >
   * <ChartModel type="SequenceChart" title ="A Chart" xAxisTitle="X-Axis"
   * yAxisTitle="Y-Axis" xRangeMin="0.0" xRangeMax="100.0" xRangeIncr="5.0"
   * yRangeMin="0.0" yRangeMax="100.0" yRangeIncr="5.0" >
   * <DataSource name="A Sequence" color="-23296" methodName="getMaxTriggerTime"
   * markStyle="0" />
   *
   * </ChartModel>
   * </RePast:Charts>
   * </code></pre>
   *
   * @param fqModelName the fully qualified name of the model associated with
   * the AbstractChartModels
   * @param charts the list of AbstractChartModels to save
   */
  public static void saveCharts(String fqModelName, ArrayList charts) {
    String homeDir = System.getProperty("user.home");
    String modelDir = homeDir + File.separator + ".repast" + File.separator +
            fqModelName.replace('.', File.separatorChar);
    File fModelDir = new File(modelDir);
    if (!fModelDir.exists()) {
      fModelDir.mkdirs();
    }

    String file = modelDir + File.separator + "charts.xml";
    try {
      BufferedWriter out =
              new BufferedWriter(new FileWriter(file));
      out.write("<?xml version=\"1.0\"?>");
      out.newLine();
      out.write("<!-- RePast Custom Chart Description File -->");
      out.newLine();
      out.write("<RePast:Charts xmlns:RePast=\"http://src.uchicago.edu/repast/\" >");
      out.newLine();

      for (int i = 0, n = charts.size(); i < n; i++) {
        AbstractChartModel model = (AbstractChartModel) charts.get(i);
        out.write(model.toXML());
        out.newLine();
      }

      out.write("</RePast:Charts>");

      out.flush();
      out.close();

    } catch (IOException ex) {
      SimUtilities.showError("Error storing persistent chart sizes and positions", ex);
      ex.printStackTrace();
    }
  }
}
