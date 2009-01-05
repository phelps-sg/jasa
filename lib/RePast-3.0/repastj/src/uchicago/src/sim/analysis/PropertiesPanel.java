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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import layout.TableLayout;
import uchicago.src.guiUtils.DoubleDocument;

/**
 *
 * @version $Revision$ $Date$
 */
public class PropertiesPanel extends JPanel implements ChartGuiPanel {

  JComboBox cmbType;
  JTextField txtXAxisTitle = new JTextField("x - axis");
  JTextField txtTitle = new JTextField("A Chart");
  JTextField txtXRangeMin = new JTextField(new DoubleDocument(), "0", 4);
  JTextField txtXRangeMax = new JTextField(new DoubleDocument(), "100", 4);
  JTextField txtXIncr = new JTextField(new DoubleDocument(), "5", 4);
  JTextField txtYAxisTitle = new JTextField("y - axis");
  JTextField txtYRangeMin = new JTextField(new DoubleDocument(), "0", 4);
  JTextField txtYRangeMax = new JTextField(new DoubleDocument(), "100", 4);
  JTextField txtYIncr = new JTextField(new DoubleDocument(), "5", 4);

  AbstractChartModel model;

  public PropertiesPanel(AbstractChartModel model) {
    this.model = model;
    String[] types = {"OpenSequenceGraph"}; //, "NetSequenceGraph", "OpenHistogram", "Histogram"};
    cmbType = new JComboBox(types);
    txtXAxisTitle.setText(model.getXAxisTitle());
    txtTitle.setText(model.getTitle());
    cmbType.setSelectedItem(model.getType());
    txtXRangeMin.setText(String.valueOf(model.getXRangeMin()));
    txtXRangeMax.setText(String.valueOf(model.getXRangeMax()));
    txtXIncr.setText(String.valueOf(model.getXRangeIncr()));
    txtYAxisTitle.setText(model.getYAxisTitle());
    txtYRangeMin.setText(String.valueOf(model.getYRangeMin()));
    txtYRangeMax.setText(String.valueOf(model.getYRangeMax()));
    txtYIncr.setText(String.valueOf(model.getYRangeIncr()));

    this.setBorder(BorderFactory.createTitledBorder("Chart Properties"));
    int hborder = 5;
    int hiborder = 8;
    int vborder = 8;

    double[][] sizes = {{hborder, .33, hiborder, .33, hiborder, .33, hborder},
                        {vborder, .083, .083, vborder, .083, .083, vborder,
                         .083, .083, vborder, .083, .083, vborder, .083, .083, vborder, .083,
                         .083, vborder}};
    setLayout(new TableLayout(sizes));
    add(new JLabel("Type:"), "1, 1");
    add(cmbType, "1, 2, 5, 2");
    add(new JLabel("Title:"), "1, 4");
    add(txtTitle, "1, 5, 5, 5");
    add(new JLabel("X-Axis Title:"), "1, 7");
    add(txtXAxisTitle, "1, 8, 5, 8");
    add(new JLabel("X-Range Min:"), "1, 10");
    add(txtXRangeMin, "1, 11");
    add(new JLabel("X-Range Max:"), "3, 10");
    add(txtXRangeMax, "3, 11");
    add(new JLabel("X-Range Incr:"), "5, 10");
    add(txtXIncr, "5, 11");

    add(new JLabel("Y-Axis Title:"), "1, 13");
    add(txtYAxisTitle, "1, 14, 5, 14");
    add(new JLabel("Y-Range Min:"), "1, 16");
    add(txtYRangeMin, "1, 17");
    add(new JLabel("Y-Range Max:"), "3, 16");
    add(txtYRangeMax, "3, 17");
    add(new JLabel("Y-Range Incr:"), "5, 16");
    add(txtYIncr, "5, 17");
  }

  public AbstractChartModel updateModel() {
    model.setTitle(txtTitle.getText());
    model.setXAxisTitle(txtXAxisTitle.getText());
    model.setXRangeMin(txtXRangeMin.getText());
    model.setXRangeMax(txtXRangeMax.getText());
    model.setXRangeIncr(txtXIncr.getText());
    model.setYAxisTitle(txtYAxisTitle.getText());
    model.setYRangeMin(txtYRangeMin.getText());
    model.setYRangeMax(txtYRangeMax.getText());
    model.setYRangeIncr(txtYIncr.getText());

    return model;
  }

  public void setModel(AbstractChartModel model) {
    this.model = model;
  }
}
