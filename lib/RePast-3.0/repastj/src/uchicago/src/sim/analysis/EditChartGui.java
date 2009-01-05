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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uchicago.src.guiUtils.GuiUtilities;

/**
 * Gui for editing an AbstractChartModel.
 *
 * @version $Revision$ $Date$
 */

public class EditChartGui extends JPanel {

  JButton btnDelete = new JButton("Delete");
  JButton btnOK = new JButton("OK");
  JButton btnCancel = new JButton("Cancel");
  JTabbedPane tabs = new JTabbedPane();
  JDialog dialog;
  ChartGuiPanel curPanel;
  AbstractChartModel model;

  /**
   * Creates an EditChartGui for the editing the specified AbstractChartModel.
   *
   * @param model the AbstractChartModel to edit
   */

  public EditChartGui(AbstractChartModel model) {
    super(true);
    setLayout(new BorderLayout());
    this.model = model;
    curPanel = new PropertiesPanel(model);
    tabs.addTab("Properties", (JPanel)curPanel);
    tabs.addTab("Data Sources", new DataSourcesPanel());
    tabs.addTab("Display Properties", new DataSourcePropPanel());
    add(tabs, BorderLayout.CENTER);

    JPanel bottom = new JPanel();
    bottom.add(btnOK);
    bottom.add(btnCancel);
    add(bottom, BorderLayout.SOUTH);

    addListeners();
  }

  private void addListeners() {
    btnOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dialog.dispose();
        model = curPanel.updateModel();
      }
    });

    tabs.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        model = curPanel.updateModel();
        curPanel = (ChartGuiPanel)tabs.getSelectedComponent();
        curPanel.setModel(model);
      }
    });

    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dialog.dispose();
        model = null;
      }
    });
  }

  /**
   * Returns the AbstractChartModel edited by this EditChartGui.
   * @return
   */
  public AbstractChartModel getModel() {
    return model;
  }

  /**
   * Displays this EditChartGui
   * @param parent the JDialog for which this EditChartGui is a sub-dialog
   */
   public void display(JDialog parent) {
    dialog = new JDialog(parent, "Edit Chart", true);
    dialog.setSize(400, 450);
    dialog.setContentPane(this);
    GuiUtilities.centerComponentOnScreen(dialog);
    dialog.setVisible(true);
  }







}
