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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import layout.TableLayout;
import uchicago.src.guiUtils.GuiUtilities;
import uchicago.src.sim.engine.SimModel;

/**
 * GUI for creating, editing and deleting custom charts. This is essentially
 * the view (MVC) of AbstractChartModels.
 *
 * @version $Revision$ $Date$
 */


public class CustomChartGui extends JPanel {

  JButton btnAdd = new JButton("Add");
  JButton btnEdit = new JButton("Edit");
  JButton btnDelete = new JButton("Delete");
  JButton btnOK = new JButton("OK");
  JButton btnCancel = new JButton("Cancel");
  JDialog dialog;
  SimModel simModel;
  JList chartList = new JList();
  private ArrayList modelList = new ArrayList();

  /**
   * Creates the gui for the specified model and list of AbstractChartModels.
   * Any charts created will be associated withe specified SimModel. Any
   * AbstractChartModel-s passed in will be displayed in the gui.
   *
   * @param simModel the SimModel to associate with any AbstractChartModel-s
   * created by this gui
   * @param models the list of AbstractChartModels to work with in this
   * gui
   */
  public CustomChartGui(SimModel simModel, ArrayList models) {
    super(new BorderLayout(), true);
    this.simModel = simModel;
    int border = 10;
    chartList.setModel(new DefaultListModel());

    double[][] sizes = {{border, .75, border, .25, border},
                        {border, border, .2, border, .2, border, .2, border,
                         .2, .2}};
    JPanel center = new JPanel(new TableLayout(sizes));
    JPanel listPanel = new JPanel(new BorderLayout());
    listPanel.setBorder(BorderFactory.createTitledBorder("Charts"));
    listPanel.add(new JScrollPane(chartList), BorderLayout.CENTER);
    center.add(listPanel, "1, 1, 0, 9");
    center.add(btnAdd, "3, 2");
    center.add(btnEdit, "3, 4");
    center.add(btnDelete, "3, 6");
    this.add(center, BorderLayout.CENTER);

    JPanel bottom = new JPanel();
    bottom.add(btnOK);
    bottom.add(btnCancel);
    this.add(bottom, BorderLayout.SOUTH);
    addListeners();

    DefaultListModel m = (DefaultListModel)chartList.getModel();
    for (int i = 0, n = models.size(); i < n; i++) {
      m.addElement(models.get(i));
    }
  }

  private void addListeners() {
    btnOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        dialog.dispose();
        modelList.clear();
        DefaultListModel m = (DefaultListModel)chartList.getModel();
        for (int i = 0; i < m.size(); i++) {
          modelList.add(m.get(i));
        }
      }
    });

    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        modelList = null;
        dialog.dispose();
      }
    });

    btnAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        AddChartGui addgui = new AddChartGui(simModel);
        addgui.display(dialog, "Add Chart", true);
        if (addgui.getModel() != null) {
          DefaultListModel m = (DefaultListModel)chartList.getModel();
          m.addElement(addgui.getModel());
        }
      }
    });

    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        int selectedIndex = chartList.getSelectedIndex();
        if (selectedIndex != -1) {
          DefaultListModel m = (DefaultListModel)chartList.getModel();
          m.remove(selectedIndex);
        }
      }
    });

    btnEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        int selectedIndex = chartList.getSelectedIndex();
        if (selectedIndex != -1) {
          AbstractChartModel model = (AbstractChartModel)chartList.getSelectedValue();
          EditChartGui edit = new EditChartGui(model.copy());
          edit.display(dialog);
          model = edit.getModel();
          if (model != null) {
            DefaultListModel m = (DefaultListModel)chartList.getModel();
            m.remove(selectedIndex);
            if (selectedIndex >= m.size()) m.addElement(model);
            else m.insertElementAt(model, selectedIndex);
          }
        }
      }
    });

    chartList.setCellRenderer(new DefaultListCellRenderer() {
      public Component getListCellRendererComponent(JList list, Object value,
                                                    int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                                                               cellHasFocus);
        AbstractChartModel model = (AbstractChartModel)value;
        l.setIcon(model.getIcon());
        l.setText(model.getTitle());
        return l;
      }
    });
  }

  /**
   * Display this gui.
   * @param parent the parent JFrame for which this gui is a dialog
   */
  public void display(JFrame parent) {
    dialog = new JDialog(parent, "Dynamic Charts", true);
    dialog.setSize(336, 238);
    dialog.setContentPane(this);
    GuiUtilities.centerComponentOnScreen(dialog);
    dialog.setVisible(true);
  }

  /**
   * Returns a list of all the charts (AbstractChartModel-s) created
   * by this gui.
   */
  public ArrayList getModels() {
    return modelList;
  }
}
