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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import uchicago.src.guiUtils.Wizard;
import uchicago.src.sim.engine.SimModel;

/**
 * Gui for adding custom charts.
 *
 * @version $Revision$ $Date$
 */

public class AddChartGui extends Wizard {

  private ChartGuiPanel curPanel;
  private ArrayList panels = new ArrayList();
  private int panelIndex = 0;
  private AbstractChartModel model;
  private boolean finished;

  public AddChartGui(SimModel simModel) {
    super(false);
    model = new SequenceChartModel(simModel);
    curPanel = new PropertiesPanel(model);
    panels.add(curPanel);
    panels.add(new DataSourcesPanel());
    panels.add(new DataSourcePropPanel());
    this.setTopPanel((JPanel)curPanel);
    this.setBackEnabled(false);
    addListeners();
  }

  private void addListeners() {
    btnNext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        btnBack.setEnabled(true);
        if (finished) close();

        if (panelIndex < panels.size() - 1) {
          model = curPanel.updateModel();
          curPanel = (ChartGuiPanel)panels.get(++panelIndex);
          curPanel.setModel(model);
          setTopPanel((JPanel)curPanel);

          if (panelIndex == panels.size() - 1) {
            btnNext.setText("Finished");
            finished = true;
          }
        }
      }
    });

    btnBack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        btnNext.setText("Next");
        finished = false;
        panelIndex--;
        model = curPanel.updateModel();
        curPanel = (ChartGuiPanel)panels.get(panelIndex);
        curPanel.setModel(model);
        setTopPanel((JPanel)curPanel);
        if (panelIndex == 0) btnBack.setEnabled(false);
      }
    });

    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        close();
        model = null;
      }
    });
  }

  public void display(JDialog owner, String title, boolean centerOnOwner) {
    setSize(400, 450);
    super.display(owner, title, centerOnOwner);
  }

  /**
   * Returns null on cancel.
   * @return
   */
  public AbstractChartModel getModel() {
    return model;
  }
}
