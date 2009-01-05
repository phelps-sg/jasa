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

import java.util.ArrayList;

import javax.swing.Icon;

import uchicago.src.sim.analysis.plot.OpenGraph;
import uchicago.src.sim.engine.SimModel;

/**
 * Model (MVC) used for GUI creation of OpenSequenceGraphs.
 *
 * @version $Revision$ $Date$
 */
public class SequenceChartModel extends AbstractChartModel {

  private String type = "OpenSequenceGraph";

  /**
   * Creates a SequenceChartModel associated with the specified SimModel.
   *
   * @param simModel the SimModel for this SequenceChartModel
   */
  public SequenceChartModel(SimModel simModel) {
    super(simModel);
  }

  /**
   * Returns a copy of this SequenceChartModel.
   */
  public AbstractChartModel copy() {
    SequenceChartModel copy = new SequenceChartModel(simModel);
    copy.type = type;
    return copy(copy);
  }

  /**
   * Returns "OpenSequenceGraph".
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the GuiChartDataSources for this SequenceChartModel.
   */
  public ArrayList getModelDataSources() {
    return ChartSourceFactory.createSequenceSources(simModel);
  }

  /**
   * Returns the Icon representing this type of AbstractChartModel.
   */
  public Icon getIcon() {
    return AbstractChartModel.SEQUENCE_ICON;
  }

  /**
   * Returns an XML representation of this SequenceChartModel.
   */
  public String toXML() {
    StringBuffer b = new StringBuffer("<ChartModel type=\"SequenceChart\" ");
    b.append(super.getXML());
    b.append("\n");
    b.append("</ChartModel>");
    return b.toString();
  }

  /**
   * Creates a OpenSequenceGraph from this SequenceChartModel.
   */ 
  public OpenGraph createChart() {
    OpenSequenceGraph graph = new OpenSequenceGraph(title, simModel);
    graph.setAxisTitles(xAxisTitle, yAxisTitle);
    graph.setXIncrement(xRangeIncr);
    graph.setXRange(xRangeMin, xRangeMax);
    graph.setYIncrement(yRangeIncr);
    graph.setYRange(yRangeMin, yRangeMax);

    for (int i = 0; i < dataSources.size(); i++) {
      SequenceSource source = (SequenceSource) dataSources.get(i);
      graph.createSequence(source.getName(), source.getColor(),
                           source.getMarkStyle(), source.getFeedFrom(),
                           source.getMethodName());

    }

    return graph;
  }
}
